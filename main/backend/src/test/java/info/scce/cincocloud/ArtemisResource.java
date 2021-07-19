package info.scce.cincocloud;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

public class ArtemisResource implements QuarkusTestResourceLifecycleManager {

    private static final String ARTEMIS_USERNAME = "cc";
    private static final String ARTEMIS_PASSWORD = "cc";
    private static final Integer ARTEMIS_PORT = 5672;

   @Container
   static GenericContainer<?> artemis = new GenericContainer<>("vromero/activemq-artemis:2.16.0-alpine")
           .withExposedPorts(ARTEMIS_PORT)
           .waitingFor(Wait.forLogMessage(".*?HTTP Server started at.*?", 1))
           .withEnv(Map.of(
                   "ARTEMIS_USERNAME", ARTEMIS_USERNAME,
                   "ARTEMIS_PASSWORD", ARTEMIS_PASSWORD
                   ));

    @Override
    public Map<String, String> start() {
        artemis.start();
        return Map.of(
                "amqp-host", artemis.getHost(),
                "amqp-port", ARTEMIS_PORT.toString(),
                "amqp-username", ARTEMIS_USERNAME,
                "amqp-password", ARTEMIS_PASSWORD
        );
    }

    @Override
    public void stop() {
        artemis.stop();
    }
}
