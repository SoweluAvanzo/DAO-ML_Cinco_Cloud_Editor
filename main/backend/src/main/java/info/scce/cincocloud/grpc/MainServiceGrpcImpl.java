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

        final var projectOptional = PyroProjectDB.findByIdOptional(projectId);
        if (projectOptional.isEmpty()) {
            return Uni.createFrom().item(CincoCloudProtos.CreateImageReply.newBuilder()
                    .setProjectId(projectId)
                    .setSuccess(false)
                    .setMessage("project not found")
                    .build());
        } else {
            // TODO: save archive, send build job to artemis mq
            return Uni.createFrom().item(CincoCloudProtos.CreateImageReply.newBuilder()
                    .setProjectId(projectId)
                    .setSuccess(true)
                    .setMessage("success")
                    .build());
        }
    }
}
