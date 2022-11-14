package info.scce.cincocloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.proto.MainServiceGrpc;
import io.minio.GetObjectArgs;
import io.minio.UploadObjectArgs;
import io.quarkus.grpc.GrpcClient;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.validation.Validator;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class WorkspaceImageBuilder {

  private static final Integer PROCESS_EXIT_SUCCESS_STATUS = 0;

  private static final Integer LOG_BUFFER_SIZE = 10;

  private final Logger logger = LoggerFactory.getLogger(WorkspaceImageBuilder.class);

  /**
   * Saves if the build process has been aborted by a user.
   */
  private final AtomicBoolean aborted = new AtomicBoolean(false);

  @Inject
  @GrpcClient("main")
  MainServiceGrpc.MainServiceBlockingStub mainService;

  @Inject
  Validator validator;

  @Inject
  @Channel("workspaces-jobs-results")
  @OnOverflow(value = OnOverflow.Strategy.NONE)
  Emitter<BuildResult> buildResultEmitter;

  @Inject
  MinioService minio;

  /**
   * The build job that is currently dealt with.
   */
  private BuildJob job;

  @Incoming("workspaces-jobs-abort-queue")
  public CompletionStage<Void> abort(Message<JsonObject> message) {
    logger.info("receive message: {}", message);

    return CompletableFuture.runAsync(() -> {
      try {
        // the process has to be alive to be aborted
        if (!aborted.get() && job != null) {
          final var abortMessage = DatabindCodec.mapper().readValue(
              message.getPayload().toString(), AbortBuildJobMessage.class);

          // only abort the current process if the message is for the build job
          // that is dealt with in the workspace builder instance
          if (abortMessage.jobId.equals(job.jobId)) {
            destroySystemProcesses();
          }
        }
      } catch (Exception e) {
        logger.error("failed to abort build job.", e);
      } finally {
        message.ack();
      }
    });
  }

  @Incoming("workspaces-jobs-queue")
  public CompletionStage<Void> build(Message<JsonObject> message) {
    logger.info("receive message: {}", message);

    // reset worker to initial state
    job = null;
    aborted.set(false);

    return CompletableFuture.runAsync(() -> {
      try {
        job = DatabindCodec.mapper().readValue(message.getPayload().toString(), BuildJob.class);
      } catch (JsonProcessingException e) {
        logger.error("message payload is not formatted properly.", e);
        buildResultEmitter.error(e);
        message.ack();
        return;
      }

      try {
        final var jobInDB = mainService.getBuildJobStatus(CincoCloudProtos.GetBuildJobStatusRequest.newBuilder()
            .setJobId(job.jobId)
            .build());

        if (jobInDB.getStatus().getNumber() == CincoCloudProtos.BuildJobStatus.Status.ABORTED_VALUE) {
          logger.info("image was was already aborted and will not be build.");
          message.ack();
          return;
        }
      } catch (Exception e) {
        logger.error("could not fetch job from main service.", e);
        message.ack();
        return;
      }

      try {
        for (var cv : validator.validate(job)) {
          throw new ValidationException(cv.getMessage());
        }

        mainService.setBuildJobStatus(CincoCloudProtos.BuildJobStatus.newBuilder()
            .setStatus(CincoCloudProtos.BuildJobStatus.Status.BUILDING)
            .setJobId(job.jobId)
            .build());

        final var result = build(job);
        logger.info("image was build successfully: {}", result);
        buildResultEmitter.send(result);
      } catch (Exception e) {
        logger.error("image build failed.", e);
        final var result = new BuildResult(job.projectId, job.jobId, false, e.getMessage(), job.uuid);
        buildResultEmitter.send(result);
      } finally {
        message.ack();
      }
    });
  }

  private BuildResult build(BuildJob job) throws Exception {
    // create the tmp directory where the sources put to
    final var tmpImageDir = Files.createTempDirectory("image");

    sendLogLines(
        "*** Starting build job ***",
        "*** Retrieving pyro-server sources... ***"
    );

    // fetch archive
    final var archive = Files.createTempFile("sources", ".zip").toFile();
    final var archiveBytes = minio.getClient().getObject(GetObjectArgs.builder()
        .bucket(MinioBuckets.PROJECTS_KEY)
        .object("project-" + job.projectId + "-pyro-server-sources.zip")
        .build())
        .readAllBytes();
    FileUtils.writeByteArrayToFile(archive, archiveBytes);

    sendLogLines("*** Pyro-server sources downloaded ***");
    logger.info("received archive with pyro sources for (projectId: {})", job.projectId);

    // unzip and delete archive
    new ZipFile(archive).extractAll(tmpImageDir.toString());
    FileUtils.deleteQuietly(archive);

    // build image and push to registry
    buildImage(job.projectId, tmpImageDir, job.getImageTag());

    return new BuildResult(job.projectId, job.jobId, true, "success", job.uuid);
  }

  private void buildImage(Long projectId, Path sourceDir, String tag) throws Exception {
    logger.info("build image (projectId: {}, archive: {}, tag: {})", projectId, sourceDir, tag);

    try {
      sendLogLines("*** Start building pyro-server binaries... ***");
      executeCommand("cp /app/scripts/build.sh " + sourceDir);
      executeCommand("cd " + sourceDir + " && sh build.sh");
    } catch (Exception e) {
      sendLogLines("", "*** Failed to build pyro-server binaries ***", "");
      throw e;
    }
    sendLogLines("", "*** Pyro-server binaries build successfully ***");

    // the build.sh script creates an archive 'pyro-server.zip` which we upload to minio
    final var zip = sourceDir.resolve("pyro-server.zip").toFile();
    final var objectName = "project-" + projectId + "-pyro-server-binaries.zip";
    minio.getClient().uploadObject(UploadObjectArgs.builder()
        .bucket(MinioBuckets.PROJECTS_KEY)
        .object(objectName)
        .filename(zip.getAbsolutePath())
        .build());

    sendLogLines(
        "*** Archive " + objectName + " uploaded ***",
        "*** Build job terminated successfully ***"
    );

    logger.info("upload file (projectId: {}, archive: {}, tag: {})", projectId, sourceDir, tag);
    FileUtils.deleteQuietly(zip);
  }

  private void executeCommand(String command) throws Exception {
    logger.info("execute command: (command: {})", command);

    if (aborted.get()) {
      throw new Exception("Failed to execute command: " + command + ". Process has already been aborted.");
    }

    final var process = new ProcessBuilder()
        .command("sh", "-c", command)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectErrorStream(true)
        .start();

    // redirect process output to main service
    final var br = new BufferedReader(new InputStreamReader(process.getInputStream()));
    final var bufferedLines = new ArrayList<String>();
    String line;
    try {
      while ((line = br.readLine()) != null) {
        logger.info("[command]: {}", line);
        bufferedLines.add(line);
        if (bufferedLines.size() == LOG_BUFFER_SIZE) {
          this.sendLogLines(bufferedLines.toArray(new String[] {}));
          bufferedLines.clear();
        }
      }

      if (!bufferedLines.isEmpty()) {
        this.sendLogLines(bufferedLines.toArray(new String[] {}));
      }
    } catch (IOException e) {
      logger.error("Error while reading redirected process output.");
    }

    final var exitValue = process.waitFor();
    if (exitValue != PROCESS_EXIT_SUCCESS_STATUS) {
      throw new Exception("Failed to execute command: " + command);
    }
  }

  private void sendLogLines(String ...logLines) {
    this.mainService.sendWorkspaceBuilderLogMessage(CincoCloudProtos.WorkspaceBuilderLogMessage.newBuilder()
        .setProjectId(this.job.projectId)
        .setJobId(this.job.jobId)
        .addAllLogMessages(List.of(logLines))
        .build());
  }

  private void destroySystemProcesses() {
    logger.info("forcefully abort build job: {}", job.jobId);

    // delete all buildah related process and child processes that are spawned from it
    ProcessHandle
        .allProcesses()
        .filter(p -> p.info().commandLine().map(c ->
            c.contains("dart") || c.contains("clean package -DskipTests")).orElse(false))
        .findFirst()
        .ifPresent(p -> {
          p.descendants().forEach(ProcessHandle::destroy);
          p.destroy();
        });

    aborted.set(true);
  }
}
