package info.scce.cincocloud;

import static org.junit.Assert.assertEquals;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.runtime.annotations.GrpcService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.proto.MainServiceGrpc;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
public class MainServiceGrpcImplTest {

    @GrpcService("main")
    MainServiceGrpc.MainServiceBlockingStub client;

    @ParameterizedTest(name = "ProjectID {0} is not valid.")
    @ValueSource(longs = {-1, 0})
    public void createImageFromArchive_projectIdInvalid_3(Long projectId) {
        try {
            final var reply = client.createImageFromArchive(CincoCloudProtos.CreateImageRequest.newBuilder()
                    .setProjectId(projectId)
                    .setArchive(ByteString.copyFrom("test", StandardCharsets.UTF_8))
                    .build());
        } catch (StatusRuntimeException e) {
            assertEquals(Status.Code.INVALID_ARGUMENT, e.getStatus().getCode());
            assertEquals("projectId must be > 0", e.getStatus().getDescription());
        }
    }
}
