package info.scce.cincocloud.grpc;

import com.google.protobuf.ByteString;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.smallrye.mutiny.Uni;
import java.io.File;
import java.io.IOException;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.proto.MutinyMainServiceGrpc;

@Singleton
public class MainServiceGrpcImpl extends MutinyMainServiceGrpc.MainServiceImplBase {

    @Override
    @Transactional
    public Uni<CincoCloudProtos.CreateImageReply> createImageFromArchive(CincoCloudProtos.CreateImageRequest request) {
        final var projectId = request.getProjectId();
        final var archiveInBytes = request.getArchive();

        if (projectId < 0) {
            return createFailure(Code.INVALID_ARGUMENT, "projectId must be > 0");
        } else if (archiveInBytes.isEmpty()) {
            return createFailure(Code.INVALID_ARGUMENT, "archive must not be empty");
        }

        final var projectOptional = PyroProjectDB.findByIdOptional(projectId);
        if (projectOptional.isEmpty()) {
            return createFailure(Code.NOT_FOUND, "project not found");
        } else {
            // TODO: save archive, send build job to artemis mq
            return Uni.createFrom().item(createImageReply(projectId));
        }
    }

    @Override
    @Transactional
    public Uni<CincoCloudProtos.GetGeneratedAppArchiveReply> getGeneratedAppArchive(CincoCloudProtos.GetGeneratedAppArchiveRequest request) {

        final var projectId = request.getProjectId();

        if (projectId < 0) {
            return createFailure(Code.INVALID_ARGUMENT, "projectId must be > 0");
        }

        final var projectOptional = PyroProjectDB.findByIdOptional(projectId);
        if (projectOptional.isEmpty()) {
            return createFailure(Code.NOT_FOUND, "project not found");
        } else {
            try {
                // TODO: load archive
                final var bytes = FileUtils.readFileToByteArray(new File(""));
                final var byteString = ByteString.copyFrom(bytes);
                return Uni.createFrom().item(getGeneratedAppArchiveReply(projectId, byteString));
            } catch (IOException e) {
                return createFailure(Code.INTERNAL, "failed to read archive");
            }
        }
    }

    private <T> Uni<T> createFailure(Code code, String message) {
        return Uni.createFrom().failure(() -> new GrpcException(
                Status.newBuilder()
                        .setCode(code.getNumber())
                        .setMessage(message)
                        .build()
        ));
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
