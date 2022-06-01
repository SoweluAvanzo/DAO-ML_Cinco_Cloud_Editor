package info.scce.cincocloud.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.storage.MinioBuckets;
import info.scce.cincocloud.storage.MinioService;
import info.scce.cincocloud.core.rest.tos.GraphModelTypeSpecTO;
import info.scce.cincocloud.core.rest.tos.GraphModelTypeTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.db.GraphModelTypeDB;
import info.scce.cincocloud.db.GitInformationDB;
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
import io.minio.GetObjectArgs;
import io.minio.UploadObjectArgs;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
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

  @Inject
  WorkspaceMQProducer workspaceMQProducer;

  @Inject
  ProjectWebSocket projectWebSocket;

  @Inject
  ObjectCache objectCache;

  @Inject
  ObjectMapper objectMapper;

  @Inject
  MinioService minio;

  @Override
  @Blocking
  @Transactional
  public Uni<CincoCloudProtos.CreateImageReply> createImageFromArchive(
      CincoCloudProtos.CreateImageRequest request) {
    final var projectId = request.getProjectId();

    LOGGER.log(Level.INFO, "createImageFromArchive(projectId: {0})",
        new Object[] {projectId});

    if (projectId <= 0) {
      throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
          .withDescription("projectId must be > 0"));
    }

    return Uni.createFrom().item(() -> {
      final var project = (ProjectDB) ProjectDB.findByIdOptional(projectId)
          .orElseThrow(() -> new StatusRuntimeException(
              Status.fromCode(Code.INVALID_ARGUMENT)
                  .withDescription("project not found")));

      WorkspaceImageBuildJobDB.findByProjectId(projectId).stream()
          .filter(job -> job.status.equals(WorkspaceImageBuildJobDB.Status.BUILDING))
          .findFirst()
          .ifPresent((job) -> {
            throw new StatusRuntimeException(
                Status.fromCode(Code.ALREADY_EXISTS)
                    .withDescription("a build job for the project already exists"));
          });

      Path file;
      try {
        LOGGER.log(Level.INFO, "fetch archive (projectId: {0})", new Object[] {project.id});
        final var archiveInBytes = minio.getClient().getObject(GetObjectArgs.builder()
                .bucket(MinioBuckets.PROJECTS_KEY)
                .object("project-" + projectId + "-pyro-server-sources.zip")
                .build())
            .readAllBytes();

        file = Files.createTempFile("sources",".zip");
        FileUtils.writeByteArrayToFile(file.toFile(), archiveInBytes);
      } catch (Exception e) {
        e.printStackTrace();
        throw new StatusRuntimeException(
            Status.fromCode(Code.INTERNAL)
                .withDescription("failed to read archive file"));
      }

      final var toolSpec = readToolSpecJsonFromArchive(file);
      mergeGraphModelTypesInProject(project, toolSpec);

      LOGGER.log(
          Level.INFO,
          "Create build image job and send it to the message queue (projectId: {0})",
          new Object[] {project.id}
      );

      final var job = createBuildJob(project);
      final var message = new WorkspaceImageBuildJobMessage(UUID.randomUUID(), projectId, job.id);
      workspaceMQProducer.send(message);
      file.toFile().delete();

      return job;
    })
        .runSubscriptionOn(Infrastructure.getDefaultExecutor())
        .map(job -> createImageReply(job.project.id));
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

  @Override
  @Transactional
  public Uni<CincoCloudProtos.GetGitInformationReply> getGitInformation (
          CincoCloudProtos.GetGitInformationRequest request) {
    final var projectId = request.getProjectId();

    LOGGER.log(Level.INFO, "getGitInformation(projectId: {0})", new Object[] {projectId});

    return Uni.createFrom()
        .item(() -> GitInformationDB.findByProjectId(request.getProjectId()).firstResultOptional())
        .runSubscriptionOn(Infrastructure.getDefaultExecutor())
        .map(gitInformationOptional -> {
          if (gitInformationOptional.isEmpty()) {
            return CincoCloudProtos.GetGitInformationReply.newBuilder()
                    .setProjectId(projectId)
                    .setType(CincoCloudProtos.GetGitInformationReply.Type.NONE)
                    .build();
          } else {
            final var gitInformation = gitInformationOptional.get();
            var rb = CincoCloudProtos.GetGitInformationReply.newBuilder()
                    .setProjectId(projectId)
                    .setType(gitInformation.type)
                    .setRepositoryUrl(gitInformation.repositoryUrl)
                    .setUsername(gitInformation.username)
                    .setPassword(gitInformation.password);

            if (gitInformation.branch != null) {
              rb = rb.setBranch(gitInformation.branch);
            }
            if (gitInformation.genSubdirectory != null) {
              rb = rb.setGenSubdirectory(gitInformation.genSubdirectory);
            }

            return rb.build();
          }
        });
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

  private CincoCloudProtos.CreateImageReply createImageReply(long projectId) {
    return CincoCloudProtos.CreateImageReply.newBuilder()
        .setProjectId(projectId)
        .build();
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
