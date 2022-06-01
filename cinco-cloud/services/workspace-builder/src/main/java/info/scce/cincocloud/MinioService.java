package info.scce.cincocloud;

import io.minio.MinioClient;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import okhttp3.OkHttpClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Startup
public class MinioService {

  @ConfigProperty(name = "minio.host")
  String host;

  @ConfigProperty(name = "minio.port")
  String port;

  @ConfigProperty(name = "minio.access-key")
  String accessKey;

  @ConfigProperty(name = "minio.secret-key")
  String secretKey;

  private MinioClient client;

  public void startup(@Observes StartupEvent event) {
    this.client =
        MinioClient.builder()
            .endpoint("http://" + host + ":" + port)
            .credentials(accessKey, secretKey)
            .httpClient(new OkHttpClient())
            .build();
  }

  public MinioClient getClient() {
    return client;
  }
}
