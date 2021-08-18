package info.scce.cincocloud.config;

import io.quarkus.runtime.Startup;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import info.scce.cincocloud.grpc.MainServiceGrpcImpl;

@Singleton
@Startup
public class StartupBean {

    private static final Logger LOGGER = Logger.getLogger(StartupBean.class.getName());

    @ConfigProperty(name = "cincocloud.data.dir")
    String dataDirectory;

    @Inject
    MainServiceGrpcImpl mainServiceGrpc;

    @PostConstruct
    public void initDataDirectory() throws IOException {
        final var dir = Path.of(dataDirectory);
        LOGGER.log(Level.INFO, dir.toAbsolutePath().toString());
        if (!Files.exists(dir)) {
            LOGGER.log(Level.INFO, "Create data directory.");
            Files.createDirectories(dir);
        }
    }
}
