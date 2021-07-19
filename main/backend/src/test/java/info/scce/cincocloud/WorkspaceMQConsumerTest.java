package info.scce.cincocloud;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
@QuarkusTestResource(ArtemisResource.class)
public class WorkspaceMQConsumerTest {
}
