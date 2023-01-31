package info.scce.cincocloud.core.services;

import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobLogTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuilderLogMessageTO;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.storage.MinioBuckets;
import info.scce.cincocloud.storage.MinioService;
import info.scce.cincocloud.storage.MinioUtils;
import info.scce.cincocloud.sync.ProjectWebSocket;
import info.scce.cincocloud.util.ZipUtils;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.quarkus.runtime.Startup;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Startup
@ApplicationScoped
public class WorkspaceImageBuildJobLogFileService {

  @Inject
  ProjectWebSocket projectWebSocket;

  @Inject
  MinioService minio;

  private final Map<Long, ReentrantLock> fileLocks = new ConcurrentHashMap<>();

  private final Map<Long, RandomAccessFile> fileAccessors = new ConcurrentHashMap<>();

  private final Map<Long, Integer> logNumbers = new ConcurrentHashMap<>();

  private final Map<Long, Path> tempLogFiles = new ConcurrentHashMap<>();

  private static final Logger LOGGER = Logger.getLogger(WorkspaceImageBuildJobLogFileService.class.getName());

  public void handleLogMessage(CincoCloudProtos.WorkspaceBuilderLogMessage msg) {
    LOGGER.info("Received Message - JobId: " + msg.getJobId()
        + " - ProjectId: " + msg.getProjectId()
        + " - Message: " + msg.getLogMessagesList());

    final var fileLock = fileLocks.computeIfAbsent(msg.getJobId(), k -> new ReentrantLock(true));
    fileLock.lock();

    var raf = fileAccessors.get(msg.getJobId());
    if (raf == null) {
      try {
        final var tempLogFile = Files.createTempFile("log-" + msg.getJobId(), "txt");
        this.tempLogFiles.put(msg.getJobId(), tempLogFile);
        raf = new RandomAccessFile(tempLogFile.toFile(), "rw");
        fileAccessors.put(msg.getJobId(), raf);
        logNumbers.put(msg.getJobId(), 0);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Could not write build log, file is missing.", e);
        fileLock.unlock();
        return;
      }
    }

    final var logMessages = new ArrayList<String>();
    try {
      logMessages.addAll(msg.getLogMessagesList());
      raf.writeBytes(String.join(System.lineSeparator(), logMessages) + System.lineSeparator());
      logNumbers.put(msg.getJobId(), logNumbers.get(msg.getJobId()) + msg.getLogMessagesCount());
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Could not write build log.", e);
      return;
    } finally {
      fileLock.unlock();
    }

    final var wsm = new WorkspaceImageBuilderLogMessageTO();
    wsm.setJobId(msg.getJobId());
    wsm.setLogMessages(logMessages);

    final var m = ProjectWebSocket.Messages.workspaceImageBuilderLogMessage(wsm);
    projectWebSocket.send(msg.getProjectId(), m);
  }

  public Optional<WorkspaceImageBuildJobLogTO> getBuildLog(long projectId, long jobId) {
    final var fileLock = fileLocks.get(jobId);
    if (fileLock != null) {
      fileLock.lock();
    }

    // return partial log
    final var raf = fileAccessors.get(jobId);
    if (raf != null) {

      // read contents of file
      final var sb = new StringBuilder();
      try {
        raf.seek(0);
        String line;
        while ((line = raf.readLine()) != null) {
          sb.append(line).append(System.lineSeparator());
        }
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Could not read from build log file.", e);
        return Optional.empty();
      } finally {
        fileLock.unlock();
      }

      final var buildLog = new WorkspaceImageBuildJobLogTO();
      buildLog.setJobId(jobId);
      buildLog.setLogStatus(WorkspaceImageBuildJobLogTO.Status.PARTIAL);
      buildLog.setLog(sb.toString());
      return Optional.of(buildLog);
    }

    try {
      final var logStream = minio.getClient().getObject(GetObjectArgs.builder()
          .bucket(MinioBuckets.BUILD_JOB_LOGS_KEY)
          .object(getMinioLogZipFileName(projectId, jobId))
          .build());

      // get log file from zip archive
      final var baos = new ByteArrayOutputStream();
      ZipUtils.readFileFromZip(logStream, getMinioLogFileName(projectId, jobId), baos);
      final var content = baos.toString(StandardCharsets.UTF_8);
      logStream.close();
      baos.close();

      final var buildLog = new WorkspaceImageBuildJobLogTO();
      buildLog.setJobId(jobId);
      buildLog.setLogStatus(WorkspaceImageBuildJobLogTO.Status.COMPLETE);
      buildLog.setLog(content);
      return Optional.of(buildLog);
    } catch (Exception e) {
      if (e instanceof ErrorResponseException
          && MinioUtils.objectDoesNotExistByException((ErrorResponseException) e)) {
        // return MISSING if no log file exists
        final var buildLog = new WorkspaceImageBuildJobLogTO();
        buildLog.setJobId(jobId);
        buildLog.setLogStatus(WorkspaceImageBuildJobLogTO.Status.MISSING);
        return Optional.of(buildLog);
      } else {
        LOGGER.log(Level.SEVERE, "Failed to fetch build log from Minio.", e);
        return Optional.empty();
      }
    }
  }

  public void finalizeTransmission(long jobId, long projectId) {
    final var fileLock = fileLocks.get(jobId);
    if (fileLock != null) {
      fileLock.lock();
    }

    final var raf = fileAccessors.get(jobId);
    if (raf != null) {
      try {
        fileAccessors.remove(jobId);
        logNumbers.remove(jobId);
        raf.close();

        // zip log before sending
        final var zip = Files.createTempFile("log", ".zip");
        ZipUtils.writeFileToZip(tempLogFiles.get(jobId).toFile(), getMinioLogFileName(projectId, jobId), zip.toFile());

        // upload to minio
        minio.getClient().uploadObject(UploadObjectArgs.builder()
            .bucket(MinioBuckets.BUILD_JOB_LOGS_KEY)
            .object(getMinioLogZipFileName(projectId, jobId))
            .filename(zip.toString())
            .build());

        Files.delete(zip);
        this.deleteLocalLogFile(projectId, jobId);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Could not finalize build log transmission.", e);
      } finally {
        fileLock.unlock();
      }
    }
  }

  private String getMinioLogFileName(long projectId, long jobId) {
    return "project-" + projectId + "-job-" + jobId + ".log";
  }

  private String getMinioLogZipFileName(long projectId, long jobId) {
    return getMinioLogFileName(projectId, jobId) + ".zip";
  }

  public void deleteLogFile(long projectId, long jobId) {
    try {
      minio.getClient().removeObject(RemoveObjectArgs.builder()
          .bucket(MinioBuckets.BUILD_JOB_LOGS_KEY)
          .object(getMinioLogZipFileName(projectId, jobId))
          .build());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,"Failed to delete log file (project: " + projectId + ", job: " + jobId + ")", e);
    }
  }

  private void deleteLocalLogFile(long projectId, long jobId) {
    try {
      final var file = tempLogFiles.get(jobId);
      if (file != null) Files.delete(file);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to delete log file (project: " + projectId + ", job: " + jobId + ")", e);
    } finally {
      tempLogFiles.remove(jobId);
    }
  }
}
