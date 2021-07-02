package info.scce.cincocloud.grpc;

import io.smallrye.mutiny.Uni;
import javax.inject.Singleton;
import javax.transaction.Transactional;
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
            return Uni.createFrom().item(createImageReply(projectId, false, "projectId must be > 0"));
        } else if (archiveInBytes.isEmpty()) {
            return Uni.createFrom().item(createImageReply(projectId, false, "archive must not be empty"));
        }

        final var projectOptional = PyroProjectDB.findByIdOptional(projectId);
        if (projectOptional.isEmpty()) {
            return Uni.createFrom().item(createImageReply(projectId, false, "project not found"));
        } else {
            // TODO: save archive, send build job to artemis mq
            return Uni.createFrom().item(createImageReply(projectId, true, "success"));
        }
    }

    private CincoCloudProtos.CreateImageReply createImageReply(long projectId, boolean success, String message) {
        return CincoCloudProtos.CreateImageReply.newBuilder()
                .setProjectId(projectId)
                .setSuccess(success)
                .setMessage(message)
                .build();
    }
}
