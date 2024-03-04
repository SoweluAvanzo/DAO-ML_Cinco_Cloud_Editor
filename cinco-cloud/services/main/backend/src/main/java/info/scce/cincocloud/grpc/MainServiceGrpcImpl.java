package info.scce.cincocloud.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.core.services.WorkspaceImageBuildJobLogFileService;
import info.scce.cincocloud.core.services.WorkspaceImageService;
import info.scce.cincocloud.db.GitInformationDB;
import info.scce.cincocloud.db.GraphModelTypeDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.proto.MutinyMainServiceGrpc;
import info.scce.cincocloud.proto.CincoCloudProtos.CreateBuildJobMessage;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.storage.MinioService;
import info.scce.cincocloud.sync.ProjectWebSocket;
import info.scce.cincocloud.sync.ProjectWebSocket.Messages;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.minio.GetObjectArgs;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;

@GrpcService
public class MainServiceGrpcImpl extends MutinyMainServiceGrpc.MainServiceImplBase {

  private static final Logger LOGGER = Logger.getLogger(MainServiceGrpcImpl.class.getName());

  @Inject
  ProjectWebSocket projectWebSocket;

  @Inject
  ObjectCache objectCache;

  @Inject
  ObjectMapper objectMapper;

  @Inject
  WorkspaceImageBuildJobLogFileService logFileService;

  @Inject
  MinioService minioService;

  @Inject
  WorkspaceImageService workspaceImageService;

  @Override
  @Transactional
  public Uni<CincoCloudProtos.BuildJobStatus> setBuildJobStatus(
      CincoCloudProtos.BuildJobStatus request) {
    LOGGER.log(Level.INFO, "setBuildJobStatus(jobId: {0}, status: {1})",
        new Object[]{request.getJobId(), request.getStatus()});

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
  public Uni<CincoCloudProtos.GetGitInformationReply> getGitInformation(
      CincoCloudProtos.GetGitInformationRequest request) {
    final var projectId = request.getProjectId();

    LOGGER.log(Level.INFO, "getGitInformation(projectId: {0})", new Object[]{projectId});

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

  @Override
  public Uni<CincoCloudProtos.Empty> sendBuildJobLogMessage(
      CincoCloudProtos.BuildJobLogMessage request) {
    logFileService.handleLogMessage(request);
    return Uni.createFrom().item(() -> CincoCloudProtos.Empty.newBuilder().build());
  }

  @Override
  @Transactional
  public Uni<CincoCloudProtos.BuildJobStatus> createBuildJob(CreateBuildJobMessage request) {
    final var projectId = request.getProjectId();
    final ProjectDB project = ProjectDB.findById(projectId);
    final var buildJob = createBuildJob(project);
    
    return Uni.createFrom().item(() -> 
      CincoCloudProtos.BuildJobStatus.newBuilder()
        .setJobId(buildJob.id)
        .setStatus(jobStatusToProtoJobStatus(buildJob.status))
        .build()
      ).runSubscriptionOn(Infrastructure.getDefaultExecutor())
      .map(bjob -> {
          final var minioClient = minioService.getClient();
          try {
            // read spec.json files from generated files
            final var buildObject = GetObjectArgs.builder().bucket("projects").object(projectId + ".zip").build();
            final var inputStream = minioClient.getObject(buildObject);
            final var zip = Files.createTempFile("project", "zip");
            FileUtils.copyInputStreamToFile(inputStream, zip.toFile());
            final var specs = readToolSpecJsonFromArchive(zip);
            for (var spec: specs) {
              mergeGraphModelTypesInProject(project, spec);
            }
            Files.deleteIfExists(zip);
          } catch (Exception e) {
            LOGGER.log(Level.INFO, "Failed to read spec.json file", e);
          }

          try {
            // create or update workspace image
            createOrUpdateWorkspaceImage(project);
          } catch (Exception e) {
            LOGGER.log(Level.INFO, "Failed merge graph model types with existing ones", e);
          }

          return bjob;
      });
  }

  private void createOrUpdateWorkspaceImage(ProjectDB project) {
    WorkspaceImageDB image = project.image;
    if (Objects.nonNull(image)) {
      image.updatedAt = Instant.now();
      image.persist();
      LOGGER.log(Level.INFO, "Image {0} updated.", new Object[]{image.toString()});
    } else {
      image = new WorkspaceImageDB();
      image.imageVersion = "latest";
      image.published = false;
      image.project = project;
      image.persist();

      project.image = image;
      project.persist();

      LOGGER.log(Level.INFO, "Image {0} created.", new Object[]{image.toString()});
    }
  }

  private WorkspaceImageBuildJobDB createBuildJob(ProjectDB project) {
    final var buildJob = new WorkspaceImageBuildJobDB(
        project,
        WorkspaceImageBuildJobDB.Status.FINISHED_WITH_SUCCESS
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

  private List<GraphModelTypeSpec> readToolSpecJsonFromArchive(Path file) {
    try {
      final var zip = new ZipFile(file.toFile());
      final var entries = zip.entries();

      final var foundSpecs = new ArrayList<GraphModelTypeSpec>();

      // find the spec.json file in the archive
      // and parse its contents
      while (entries.hasMoreElements()) {
        final var entry = entries.nextElement();
        if (entry.getName().endsWith(".json")) {
          try (final var is = zip.getInputStream(entry)) {
            final var json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            final var spec = objectMapper.readValue(json, GraphModelTypeSpec.class);
            foundSpecs.add(spec);
          } catch (Exception ignored) {
          }
        }
      }

      if (foundSpecs.isEmpty()) {
        throw new StatusRuntimeException(Status.fromCode(Status.Code.INTERNAL)
            .withDescription("No spec.json file not found in archive."));
      }

      return foundSpecs;
    } catch (Exception e) {
      LOGGER.log(Level.INFO, "Failed to read spec.json file from archive.", e);
      throw new StatusRuntimeException(Status.fromCode(Status.Code.INTERNAL)
          .withDescription("Failed to read spec.json file from archive."));
    }
  }

  private void mergeGraphModelTypesInProject(ProjectDB project, GraphModelTypeSpec spec) {
    // Remove GraphModelTypes that do not exist anymore. Here, we remove all
    // those entries from graphModelTypes list, where the typeName does not
    // exist in the parsed spec.json file anymore.
    final var gmtSpecSet = spec.graphTypes.stream()
        .map(t -> t.elementTypeId)
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

    spec.graphTypes.stream()
        .filter(g -> !projectGmtSet.contains(g.elementTypeId))
        .forEach(g -> {
          final var db = new GraphModelTypeDB();
          db.typeName = g.elementTypeId;
          db.fileExtension = g.diagramExtension;
          db.project = project;
          db.persist();
          project.graphModelTypes.add(db);
        });

    project.persist();
  }
}