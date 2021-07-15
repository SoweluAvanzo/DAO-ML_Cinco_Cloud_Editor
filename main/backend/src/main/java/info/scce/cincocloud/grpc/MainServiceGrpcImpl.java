package info.scce.cincocloud.grpc;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.proto.MutinyMainServiceGrpc;

@Singleton
public class MainServiceGrpcImpl extends MutinyMainServiceGrpc.MainServiceImplBase {

    private static final Logger LOGGER = Logger.getLogger(MainServiceGrpcImpl.class.getName());

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

        return Uni.createFrom().item(() -> PyroProjectDB.findByIdOptional(projectId))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .onItem()
                .transform(projectOptional -> {
                    if (projectOptional.isEmpty()) {
                        throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
                                .withDescription("project not found"));
                    } else {
                        return createImageReply(projectId);
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

        return Uni.createFrom().item(() -> PyroProjectDB.findByIdOptional(projectId))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .onItem()
                .transform(projectOptional -> {
                    if (projectOptional.isEmpty()) {
                        throw new StatusRuntimeException(Status.fromCode(Status.Code.INVALID_ARGUMENT)
                                .withDescription("project not found"));
                    } else {
                        try {
                            // TODO replace with real file
                            final var bytes = FileUtils.readFileToByteArray(new File(""));
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
}
