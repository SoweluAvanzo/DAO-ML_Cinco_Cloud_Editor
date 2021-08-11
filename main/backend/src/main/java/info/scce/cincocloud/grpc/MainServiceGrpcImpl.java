package info.scce.cincocloud.grpc;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.mutiny.core.buffer.Buffer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import info.scce.cincocloud.config.VertxService;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroWorkspaceImageBuildJobDB;
import info.scce.cincocloud.mq.WorkspaceImageBuildJobMessage;
import info.scce.cincocloud.mq.WorkspaceMQProducer;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.proto.MutinyMainServiceGrpc;

@Singleton
public class MainServiceGrpcImpl extends MutinyMainServiceGrpc.MainServiceImplBase {

    private static final Logger LOGGER = Logger.getLogger(MainServiceGrpcImpl.class.getName());

    @ConfigProperty(name = "cincocloud.data.dir")
    String dataDirectory;

    @Inject
    WorkspaceMQProducer workspaceMQProducer;

    @Inject
    VertxService vertxService;

    @Override
    @Transactional
    public Uni<CincoCloudProtos.CreateImageReply> createImageFromArchive(CincoCloudProtos.CreateImageRequest request) {
        final var projectId = request.getProjectId();
        final var archiveInBytes = request.getArchive();

        LOGGER.log(Level.INFO, "createImageFromArchive(projectId: {0}, archive: {1})", new Object[]{projectId, archiveInBytes.size()});

        if (projectId <= 0) {
            throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
                    .withDescription("projectId must be > 0"));
        } else if (archiveInBytes.isEmpty()) {
            throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
                    .withDescription("archive must not be empty"));
        }

        final var vertx = vertxService.getVertx();
        final var path = getArchiveDirectoryPath(projectId);
        final var file = getArchiveFilePath(projectId);

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

        return Uni.createFrom().item(() -> {
            final var project = (PyroProjectDB) PyroProjectDB.findByIdOptional(projectId).orElseThrow(() -> new StatusRuntimeException(
                    Status.fromCode(Status.Code.INVALID_ARGUMENT).withDescription("project not found")
            ));

            PyroWorkspaceImageBuildJobDB.findByProjectId(projectId).stream()
                .filter(job -> job.status.equals(PyroWorkspaceImageBuildJobDB.Status.BUILDING))
                .findFirst()
                .orElseThrow(() -> new StatusRuntimeException(
                        Status.fromCode(Status.Code.ALREADY_EXISTS).withDescription("a build job for the project already exists")
                ));

            return createBuildJob(project).orElseThrow(() -> new StatusRuntimeException(
                    Status.fromCode(Status.Code.INTERNAL).withDescription("failed to create a build job")
            ));
        })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .flatMap(job -> {
                    final var project = job.project;
                    LOGGER.log(Level.INFO, "Save archive (projectId: {0}, archive: {1})", new Object[]{project, file});
                    return vertx.fileSystem().writeFile(file.toString(), Buffer.buffer(archiveInBytes.toByteArray()))
                            .map(v -> {
                                LOGGER.log(Level.INFO, "Create build image job (projectId: {0}, user: {1}, archive: {2})", new Object[]{project, project.owner.username, file});
                                workspaceMQProducer.send(new WorkspaceImageBuildJobMessage(projectId, job.id, project.owner.username, project.name));
                                return createImageReply(projectId);
                            });
                });
    }

    @Override
    @Transactional
    public Uni<CincoCloudProtos.GetGeneratedAppArchiveReply> getGeneratedAppArchive(CincoCloudProtos.GetGeneratedAppArchiveRequest request) {
        final var projectId = request.getProjectId();

        LOGGER.log(Level.INFO, "getGeneratedAppArchive(projectId: {0})", new Object[]{projectId});

        if (projectId <= 0) {
            throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
                    .withDescription("projectId must be > 0"));
        }

        final var file = getArchiveFilePath(projectId);
        if (!Files.exists(file)) {
            throw new StatusRuntimeException(Status.fromCode(Status.Code.INTERNAL)
                    .withDescription("the archive does not exist"));
        }

        return Uni.createFrom().item(() -> PyroProjectDB.findByIdOptional(projectId))
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
    public Uni<CincoCloudProtos.BuildJobStatus> getBuildJobStatus(CincoCloudProtos.GetBuildJobStatusRequest request) {
        LOGGER.log(Level.INFO, "getBuildJobStatus(jobId: {0})", new Object[]{request.getJobId()});

        return Uni.createFrom().item(() -> PyroWorkspaceImageBuildJobDB.findByIdOptional(request.getJobId()))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .map(jobOptional -> {
                    if (jobOptional.isEmpty()) {
                        throw new StatusRuntimeException(Status.fromCode(Status.Code.NOT_FOUND)
                                .withDescription("job not found"));
                    } else {
                        final var job = (PyroWorkspaceImageBuildJobDB) jobOptional.get();
                        return CincoCloudProtos.BuildJobStatus.newBuilder()
                                .setJobId(job.id)
                                .setStatus(jobStatusToProtoJobStatus(job.status))
                                .build();
                    }
                });
    }

    @Override
    @Transactional
    public Uni<CincoCloudProtos.BuildJobStatus> setBuildJobStatus(CincoCloudProtos.BuildJobStatus request) {
        LOGGER.log(Level.INFO, "setBuildJobStatus(jobId: {0}, status: {1})", new Object[]{request.getJobId(), request.getStatus()});

        return Uni.createFrom().item(() -> {
            final var job = (PyroWorkspaceImageBuildJobDB) PyroWorkspaceImageBuildJobDB.findByIdOptional(request.getJobId()).orElseThrow(
                    () -> new StatusRuntimeException(Status.fromCode(Status.Code.NOT_FOUND).withDescription("job not found"))
            );
            job.status = protoJobStatusToJobStatus(request.getStatus());
            job.persist();
            return job;
        })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .map(job -> CincoCloudProtos.BuildJobStatus.newBuilder()
                        .setJobId(job.id)
                        .setStatus(jobStatusToProtoJobStatus(job.status))
                        .build()
                );
    }

    private Optional<PyroWorkspaceImageBuildJobDB> createBuildJob(PyroProjectDB project) {
        final var buildJob = new PyroWorkspaceImageBuildJobDB(
                project,
                PyroWorkspaceImageBuildJobDB.Status.PENDING
        );
        buildJob.persist();
        return Optional.of(buildJob);
    }

    private CincoCloudProtos.BuildJobStatus.Status jobStatusToProtoJobStatus(PyroWorkspaceImageBuildJobDB.Status status) {
        switch (status) {
            case PENDING: return CincoCloudProtos.BuildJobStatus.Status.PENDING;
            case BUILDING: return CincoCloudProtos.BuildJobStatus.Status.BUILDING;
            case ABORTED: return CincoCloudProtos.BuildJobStatus.Status.ABORTED;
            case FINISHED_WITH_SUCCESS: return CincoCloudProtos.BuildJobStatus.Status.FINISHED_WITH_SUCCESS;
            case FINISHED_WITH_FAILURE: return CincoCloudProtos.BuildJobStatus.Status.FINISHED_WITH_FAILURE;
        }
        throw new IllegalArgumentException("unknown job status enum type: " + status);
    }

    private PyroWorkspaceImageBuildJobDB.Status protoJobStatusToJobStatus(CincoCloudProtos.BuildJobStatus.Status status) {
        switch (status.getNumber()) {
            case CincoCloudProtos.BuildJobStatus.Status.PENDING_VALUE: return PyroWorkspaceImageBuildJobDB.Status.PENDING;
            case CincoCloudProtos.BuildJobStatus.Status.BUILDING_VALUE: return PyroWorkspaceImageBuildJobDB.Status.BUILDING;
            case CincoCloudProtos.BuildJobStatus.Status.ABORTED_VALUE: return PyroWorkspaceImageBuildJobDB.Status.ABORTED;
            case CincoCloudProtos.BuildJobStatus.Status.FINISHED_WITH_SUCCESS_VALUE: return PyroWorkspaceImageBuildJobDB.Status.FINISHED_WITH_SUCCESS;
            case CincoCloudProtos.BuildJobStatus.Status.FINISHED_WITH_FAILURE_VALUE: return PyroWorkspaceImageBuildJobDB.Status.FINISHED_WITH_FAILURE;
        }
        throw new IllegalArgumentException("unknown proto job status type: " + status);
    }

    private CincoCloudProtos.GetGeneratedAppArchiveReply getGeneratedAppArchiveReply(long projectId, ByteString byteString) {
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
}
