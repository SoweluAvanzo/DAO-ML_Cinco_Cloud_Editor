package info.scce.cincocloud;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Collections;
import java.util.Map;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class PostgresResource implements QuarkusTestResourceLifecycleManager {

  @Container
  static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:13-alpine")
      .withDatabaseName("cc")
      .withUsername("cc")
      .withPassword("cc");

  @Override
  public Map<String, String> start() {
    database.start();
    return Collections.singletonMap(
        "quarkus.datasource.jdbc.url", database.getJdbcUrl()
    );
  }

  @Override
  public void stop() {
    database.stop();
  }
}
