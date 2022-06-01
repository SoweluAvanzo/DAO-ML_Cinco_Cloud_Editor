package info.scce.cincocloud.config;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Properties {

  @ConfigProperty(name = "minio.host")
  String minioHost;

  @ConfigProperty(name = "minio.port")
  String minioPort;

  @ConfigProperty(name = "minio.access-key")
  String minioAccessKey;

  @ConfigProperty(name = "minio.secret-key")
  String minioSecretKey;

  public String getMinioHost() {
    return minioHost;
  }

  public String getMinioPort() {
    return minioPort;
  }

  public String getMinioAccessKey() {
    return minioAccessKey;
  }

  public String getMinioSecretKey() {
    return minioSecretKey;
  }
}
