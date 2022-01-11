package info.scce.cincocloud.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.protobuf.ByteString;
import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.proto.MainServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.runtime.annotations.GrpcService;
import io.quarkus.test.junit.QuarkusTest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
public class MainServiceGrpcImplTest extends AbstractCincoCloudTest {

  @GrpcService("main")
  MainServiceGrpc.MainServiceBlockingStub client;

  @ParameterizedTest(name = "ProjectID {0} is not valid.")
  @ValueSource(longs = {-1, 0})
  public void createImageFromArchive_projectIdInvalid_3(Long projectId) {
    try {
      client.createImageFromArchive(CincoCloudProtos.CreateImageRequest.newBuilder()
          .setProjectId(projectId)
          .setArchive(ByteString.copyFrom("test", StandardCharsets.UTF_8))
          .build());
    } catch (StatusRuntimeException e) {
      assertEquals(Status.Code.INVALID_ARGUMENT, e.getStatus().getCode());
      assertEquals("projectId must be > 0", e.getStatus().getDescription());
    }
  }
}
