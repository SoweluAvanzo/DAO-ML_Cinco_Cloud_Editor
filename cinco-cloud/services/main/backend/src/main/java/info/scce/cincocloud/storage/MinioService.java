package info.scce.cincocloud.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Startup
public class MinioService {

  private static final Logger LOGGER = Logger.getLogger(MinioService.class.getName());

  @ConfigProperty(name = "minio.host")
  String host;

  @ConfigProperty(name = "minio.port")
  String port;

  @ConfigProperty(name = "minio.access-key")
  String accessKey;

  @ConfigProperty(name = "minio.secret-key")
  String secretKey;

  private MinioClient client;

  public void startup(@Observes StartupEvent event) throws Exception {
    this.client =
        MinioClient.builder()
            .endpoint("http://" + host + ":" + port)
            .credentials(accessKey, secretKey)
            .build();

    initMinioBuckets();
  }

  private void initMinioBuckets() throws Exception {
    LOGGER.log(Level.INFO, "Init minio buckets");
    final var exists = client.bucketExists(BucketExistsArgs.builder()
        .bucket(MinioBuckets.PROJECTS_KEY)
        .build());

    if (!exists) {
      client.makeBucket(MakeBucketArgs.builder().bucket("projects").build());
      LOGGER.log(Level.INFO, "Minio bucket 'projects' created.");
    } else {
      LOGGER.log(Level.INFO, "Minio bucket 'projects' already exists.");
    }
  }

  public MinioClient getClient() {
    return client;
  }
}
