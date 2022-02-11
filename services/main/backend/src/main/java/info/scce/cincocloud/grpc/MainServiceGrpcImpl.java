package info.scce.cincocloud.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import info.scce.cincocloud.core.rest.tos.GraphModelTypeSpecTO;
import info.scce.cincocloud.core.rest.tos.GraphModelTypeTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.db.GraphModelTypeDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.mq.WorkspaceImageBuildJobMessage;
import info.scce.cincocloud.mq.WorkspaceMQProducer;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.proto.MutinyMainServiceGrpc;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.sync.ProjectWebSocket;
import info.scce.cincocloud.sync.ProjectWebSocket.Messages;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
public class MainServiceGrpcImpl extends MutinyMainServiceGrpc.MainServiceImplBase {

  private static final Logger LOGGER = Logger.getLogger(MainServiceGrpcImpl.class.getName());

  @ConfigProperty(name = "cincocloud.data.dir")
  String dataDirectory;

  @Inject
  WorkspaceMQProducer workspaceMQProducer;

  @Inject
  ProjectWebSocket projectWebSocket;

  @Inject
  ObjectCache objectCache;

  @Inject
  ObjectMapper objectMapper;

  @Override
  @Transactional
  public Uni<CincoCloudProtos.CreateImageReply> createImageFromArchive(
      CincoCloudProtos.CreateImageRequest request) {
    final var projectId = request.getProjectId();
    final var archiveInBytes = request.getArchive();

    LOGGER.log(Level.INFO, "createImageFromArchive(projectId: {0}, archive: {1})",
        new Object[] {projectId, archiveInBytes.size()});

    if (projectId <= 0) {
      throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
          .withDescription("projectId must be > 0"));
    } else if (archiveInBytes.isEmpty()) {
      throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
          .withDescription("archive must not be empty"));
    }

    final var path = getArchiveDirectoryPath(projectId);
    final var file = getArchiveFilePath(projectId);

    removeExistingArchive(path, file);

    return Uni.createFrom().item(() -> {
      final var project = (ProjectDB) ProjectDB.findByIdOptional(projectId)
          .orElseThrow(() -> new StatusRuntimeException(
              Status.fromCode(Status.Code.INVALID_ARGUMENT)
                  .withDescription("project not found")));

      WorkspaceImageBuildJobDB.findByProjectId(projectId).stream()
          .filter(job -> job.status.equals(WorkspaceImageBuildJobDB.Status.BUILDING))
          .findFirst()
          .ifPresent((job) -> {
            throw new StatusRuntimeException(
                Status.fromCode(Status.Code.ALREADY_EXISTS)
                    .withDescription("a build job for the project already exists"));
          });

      try {
        LOGGER.log(Level.INFO, "Save archive (projectId: {0}, archive: {1})", new Object[] {project, file});
        Files.write(file, archiveInBytes.toByteArray());
      } catch (IOException e) {
        e.printStackTrace();
        throw new StatusRuntimeException(
            Status.fromCode(Code.INTERNAL)
                .withDescription("failed to persist archive file"));
      }

      final var toolSpec = readToolSpecJsonFromArchive(file);
      mergeGraphModelTypesInProject(project, toolSpec);

      LOGGER.log(
          Level.INFO,
          "Create build image job and send it to the message queue (projectId: {0}, user: {1}, archive: {2})",
          new Object[] {project, project.owner.username, file}
      );

      final var job = createBuildJob(project);
      final var message = new WorkspaceImageBuildJobMessage(projectId, job.id, project.owner.username, project.name);
      workspaceMQProducer.send(message);
      return job;
    })
        .runSubscriptionOn(Infrastructure.getDefaultExecutor())
        .map(job -> createImageReply(job.project.id));
  }

  @Override
  @Transactional
  public Uni<CincoCloudProtos.GetGeneratedAppArchiveReply> getGeneratedAppArchive(
      CincoCloudProtos.GetGeneratedAppArchiveRequest request) {
    final var projectId = request.getProjectId();

    LOGGER.log(Level.INFO, "getGeneratedAppArchive(projectId: {0})", new Object[] {projectId});

    if (projectId <= 0) {
      throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
          .withDescription("projectId must be > 0"));
    }

    final var file = getArchiveFilePath(projectId);
    if (!Files.exists(file)) {
      throw new StatusRuntimeException(Status.fromCode(Status.Code.INTERNAL)
          .withDescription("the archive does not exist"));
    }

    return Uni.createFrom().item(() -> ProjectDB.findByIdOptional(projectId))
        .runSubscriptionOn(Infrastructure.getDefaultExecutor())
        .map(projectOptional -> {
          if (projectOptional.isEmpty()) {
            throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
                .withDescription("project not found"));
          } else {
            try {
              final var bytes = FileUtils.readFileToByteArray(file.toFile());
              final var byteString = ByteString.copyFrom(bytes);
              return getGeneratedAppArchiveReply(projectId, byteString);
            } catch (IOException e) {
              throw new StatusRuntimeException(Status.fromCode(Status.Code.INTERNAL)
                  .withDescription("failed to read archive")
                  .withCause(e));
            }
          }
        });
  }

  @Override
  @Transactional
  public Uni<CincoCloudProtos.BuildJobStatus> getBuildJobStatus(
      CincoCloudProtos.GetBuildJobStatusRequest request) {
    LOGGER.log(Level.INFO, "getBuildJobStatus(jobId: {0})", new Object[] {request.getJobId()});

    return Uni.createFrom()
        .item(() -> WorkspaceImageBuildJobDB.findByIdOptional(request.getJobId()))
        .runSubscriptionOn(Infrastructure.getDefaultExecutor())
        .map(jobOptional -> {
          if (jobOptional.isEmpty()) {
            throw new StatusRuntimeException(Status.fromCode(Status.Code.NOT_FOUND)
                .withDescription("job not found"));
          } else {
            final var job = (WorkspaceImageBuildJobDB) jobOptional.get();
            return CincoCloudProtos.BuildJobStatus.newBuilder()
                .setJobId(job.id)
                .setStatus(jobStatusToProtoJobStatus(job.status))
                .build();
          }
        });
  }

  @Override
  @Transactional
  public Uni<CincoCloudProtos.BuildJobStatus> setBuildJobStatus(
      CincoCloudProtos.BuildJobStatus request) {
    LOGGER.log(Level.INFO, "setBuildJobStatus(jobId: {0}, status: {1})",
        new Object[] {request.getJobId(), request.getStatus()});

    return Uni.createFrom().item(() -> {
      final var job = (WorkspaceImageBuildJobDB) WorkspaceImageBuildJobDB
          .findByIdOptional(request.getJobId()).orElseThrow(
              () -> new StatusRuntimeException(
                  Status.fromCode(Status.Code.NOT_FOUND).withDescription("job not found"))
          );
      job.status = protoJobStatusToJobStatus(request.getStatus());
      job.persist();
      final var buildJob = WorkspaceImageBuildJobTO.fromEntity(job, objectCache);
      projectWebSocket.send(job.project.id, Messages.updateBuildJobStatus(buildJob));
      return job;
    })
        .runSubscriptionOn(Infrastructure.getDefaultExecutor())
        .map(job -> CincoCloudProtos.BuildJobStatus.newBuilder()
            .setJobId(job.id)
            .setStatus(jobStatusToProtoJobStatus(job.status))
            .build()
        );
  }

  private WorkspaceImageBuildJobDB createBuildJob(ProjectDB project) {
    final var buildJob = new WorkspaceImageBuildJobDB(
        project,
        WorkspaceImageBuildJobDB.Status.PENDING
    );
    buildJob.startedAt = Instant.now();
    buildJob.persist();
    return buildJob;
  }

  private CincoCloudProtos.BuildJobStatus.Status jobStatusToProtoJobStatus(
      WorkspaceImageBuildJobDB.Status status) {
    switch (status) {
      case PENDING:
        return CincoCloudProtos.BuildJobStatus.Status.PENDING;
      case BUILDING:
        return CincoCloudProtos.BuildJobStatus.Status.BUILDING;
      case ABORTED:
        return CincoCloudProtos.BuildJobStatus.Status.ABORTED;
      case FINISHED_WITH_SUCCESS:
        return CincoCloudProtos.BuildJobStatus.Status.FINISHED_WITH_SUCCESS;
      case FINISHED_WITH_FAILURE:
        return CincoCloudProtos.BuildJobStatus.Status.FINISHED_WITH_FAILURE;
      default:
        throw new IllegalArgumentException("unknown job status enum type: " + status);
    }
  }

  private WorkspaceImageBuildJobDB.Status protoJobStatusToJobStatus(
      CincoCloudProtos.BuildJobStatus.Status status) {
    switch (status.getNumber()) {
      case CincoCloudProtos.BuildJobStatus.Status.PENDING_VALUE:
        return WorkspaceImageBuildJobDB.Status.PENDING;
      case CincoCloudProtos.BuildJobStatus.Status.BUILDING_VALUE:
        return WorkspaceImageBuildJobDB.Status.BUILDING;
      case CincoCloudProtos.BuildJobStatus.Status.ABORTED_VALUE:
        return WorkspaceImageBuildJobDB.Status.ABORTED;
      case CincoCloudProtos.BuildJobStatus.Status.FINISHED_WITH_SUCCESS_VALUE:
        return WorkspaceImageBuildJobDB.Status.FINISHED_WITH_SUCCESS;
      case CincoCloudProtos.BuildJobStatus.Status.FINISHED_WITH_FAILURE_VALUE:
        return WorkspaceImageBuildJobDB.Status.FINISHED_WITH_FAILURE;
      default:
        throw new IllegalArgumentException("unknown proto job status type: " + status);
    }
  }

  private CincoCloudProtos.GetGeneratedAppArchiveReply getGeneratedAppArchiveReply(long projectId,
      ByteString byteString) {
    return CincoCloudProtos.GetGeneratedAppArchiveReply.newBuilder()
        .setProjectId(projectId)
        .setArchive(byteString)
        .build();
  }

  private CincoCloudProtos.CreateImageReply createImageReply(long projectId) {
    return CincoCloudProtos.CreateImageReply.newBuilder()
        .setProjectId(projectId)
        .build();
  }

  private Path getArchiveDirectoryPath(Long projectId) {
    return Path.of(dataDirectory, "projects", String.valueOf(projectId));
  }

  private Path getArchiveFilePath(Long projectId) {
    return getArchiveDirectoryPath(projectId).resolve("archive.zip");
  }

  private void removeExistingArchive(Path path, Path file) {
    try {
      if (!Files.exists(path)) {
        Files.createDirectories(path);
      }

      if (Files.exists(file)) {
        Files.delete(file);
      }
    } catch (Exception e) {
      throw new StatusRuntimeException(Status.fromCode(Status.Code.INTERNAL)
          .withDescription("failed to save archive."));
    }
  }

  private GraphModelTypeSpecTO readToolSpecJsonFromArchive(Path file) {
    try {
      final var zip = new ZipFile(file.toFile());
      final var entries = zip.entries();

      // find the spec.json file in the archive
      // and parse its contents
      while (entries.hasMoreElements()) {
        final var entry = entries.nextElement();
        if (entry.getName().equals("spec.json")) {
          try (final var is = zip.getInputStream(entry)) {
            final var json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return objectMapper.readValue(json, GraphModelTypeSpecTO.class);
          }
        }
      }

      throw new StatusRuntimeException(Status.fromCode(Status.Code.INTERNAL)
          .withDescription("spec.json file not found in archive."));
    } catch (Exception e) {
      e.printStackTrace();
      throw new StatusRuntimeException(Status.fromCode(Status.Code.INTERNAL)
          .withDescription("failed to read spec.json file from archive."));
    }
  }

  private void mergeGraphModelTypesInProject(ProjectDB project, GraphModelTypeSpecTO spec) {
    // Remove GraphModelTypes that do not exist anymore. Here, we remove all
    // those entries from graphModelTypes list, where the typeName does not
    // exist in the parsed spec.json file anymore.
    final var gmtSpecSet = spec.graphModelTypes.stream()
        .map(GraphModelTypeTO::gettypeName)
        .collect(Collectors.toSet());

    final var graphModelTypesToDelete = project.graphModelTypes.stream()
        .filter(g -> !gmtSpecSet.contains(g.typeName))
        .collect(Collectors.toList());

    project.graphModelTypes.removeAll(graphModelTypesToDelete);
    project.persist();

    graphModelTypesToDelete.forEach(PanacheEntityBase::delete);

    // Add new GraphModelTypes to project. A GraphModelType is considered new,
    // if its typeName does not exist as an entry in the graphModelTypes list
    // of a project.
    final var projectGmtSet = project.graphModelTypes.stream()
        .map(g -> g.typeName)
        .collect(Collectors.toSet());

    spec.graphModelTypes.stream()
        .filter(g -> !projectGmtSet.contains(g.gettypeName()))
        .forEach(g -> {
          final var db = new GraphModelTypeDB();
          db.typeName = g.gettypeName();
          db.fileExtension = g.getfileExtension();
          db.project = project;
          db.persist();
          project.graphModelTypes.add(db);
        });

    project.persist();
  }
}
