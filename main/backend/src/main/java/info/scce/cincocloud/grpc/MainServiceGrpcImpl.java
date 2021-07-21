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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import info.scce.cincocloud.config.VertxService;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.mq.WorkspaceImageBuildJob;
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

        return Uni.createFrom().item(() -> PyroProjectDB.findByIdOptional(projectId))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .flatMap(projectOptional -> {
                    if (projectOptional.isEmpty()) {
                        throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
                                .withDescription("project not found"));
                    } else {
                        final var project = (PyroProjectDB) projectOptional.get();
                        LOGGER.log(Level.INFO, "Save archive (projectId: {0}, archive: {1})", new Object[]{project, file});
                        return vertx.fileSystem().writeFile(file.toString(), Buffer.buffer(archiveInBytes.toByteArray()))
                                .map(v -> {
                                    LOGGER.log(Level.INFO, "Create build image job (projectId: {0}, user: {1}, archive: {2})", new Object[]{project, project.owner.username, file});
                                    workspaceMQProducer.send(new WorkspaceImageBuildJob(projectId, project.owner.username, project.name));
                                    return createImageReply(projectId);
                                });
                    }
                });
    }

    @Override
    @Transactional
    public Uni<CincoCloudProtos.GetGeneratedAppArchiveReply> getGeneratedAppArchive(CincoCloudProtos.GetGeneratedAppArchiveRequest request) {
        final var projectId = request.getProjectId();

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
