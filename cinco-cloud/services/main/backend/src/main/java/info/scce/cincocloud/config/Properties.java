package info.scce.cincocloud.config;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

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

  @ConfigProperty(name = "auth.private-key")
  String authPrivateKey;

  @ConfigProperty(name = "cincocloud.data.dir")
  String dataDir;

  @ConfigProperty(name = "archetype.image")
  Optional<String> archetypeImage;

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

  public String getAuthPrivateKey() {
    return authPrivateKey;
  }

  public String getDataDir() {
    return dataDir;
  }

  public Optional<String> getArchetypeImage() {
    return archetypeImage;
  }
}
