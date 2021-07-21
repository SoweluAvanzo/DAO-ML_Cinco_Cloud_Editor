package info.scce.cincocloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.grpc.runtime.annotations.GrpcService;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.validation.Validator;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.proto.MainServiceGrpc;

@ApplicationScoped
public class WorkspaceImageBuilder {

    private static final Integer PROCESS_EXIT_SUCCESS_STATUS = 0;

    private final Logger logger = LoggerFactory.getLogger(WorkspaceImageBuilder.class);

    @Inject
    @GrpcService("main")
    MainServiceGrpc.MainServiceBlockingStub mainService;

    @ConfigProperty(name = "info.scce.cincocloud.docker.registry.host")
    String dockerRegistryHost;

    @ConfigProperty(name = "info.scce.cincocloud.docker.registry.port")
    Integer dockerRegistryPort;

    @Inject
    Validator validator;

    @Inject
    @Channel("workspaces-jobs-results")
    @OnOverflow(value = OnOverflow.Strategy.NONE)
    Emitter<BuildResult> buildResultEmitter;

    @Incoming("workspaces-jobs-queue")
    public CompletionStage<Void> process(Message<JsonObject> message) {
        logger.info("receive message: {}", message);

        return CompletableFuture.runAsync(() -> {
            final BuildJob job;
            try {
                job = DatabindCodec.mapper().readValue(message.getPayload().toString(), BuildJob.class);
            } catch (JsonProcessingException e) {
                logger.error("message payload is not formatted properly.", e);
                buildResultEmitter.error(e);
                message.ack();
                return;
            }

            try {
                for (var cv: validator.validate(job)) {
                    throw new ValidationException(cv.getMessage());
                }

                final var result = build(job);
                logger.info("image was build successfully: {}", result);
                buildResultEmitter.send(result);
            } catch (Exception e) {
                logger.error("image build failed.", e);
                final var result = new BuildResult(job.projectId, false, e.getMessage(), null);
                buildResultEmitter.send(result);
            } finally {
                message.ack();
            }
        });
    }

    private BuildResult build(BuildJob job) throws Exception {
        // create the tmp directory where the sources put to
        final var tmpImageDir = Files.createTempDirectory("image");

        // fetch archive
        final var response = mainService.getGeneratedAppArchive(CincoCloudProtos.GetGeneratedAppArchiveRequest.newBuilder()
                .setProjectId(job.projectId)
                .build());

        logger.info("received archive.zip for (projectId: {})", response.getProjectId());

        final var archive = Path.of(tmpImageDir.toString()).resolve("archive.zip").toFile();
        FileUtils.writeByteArrayToFile(archive, response.getArchive().toByteArray());

        // unzip and delete archive
        new ZipFile(archive).extractAll(tmpImageDir.toString());
        FileUtils.deleteQuietly(archive);

        // build image and push to registry
        buildImage(response.getProjectId(), tmpImageDir, job.getImageTag());
        pushImageToRegistry(response.getProjectId(), job.getImageTag());

        return new BuildResult(job.projectId, true, "success", job.getImageTag());
    }

    private void buildImage(Long projectId, Path sourceDir, String tag) throws Exception {
        final var buildCommand = "cd " + sourceDir.toString() + " && buildah bud -t " + tag + " .";

        logger.info("build image for (projectId: {}, command: {})", projectId, buildCommand);

        final var process = new ProcessBuilder()
                .command("sh", "-c", buildCommand)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        int exitValue = process.waitFor();

        if (exitValue != PROCESS_EXIT_SUCCESS_STATUS) {
            throw new Exception("Failed to build image.");
        }
    }

    private void pushImageToRegistry(Long projectId, String tag) throws Exception {
        final var dockerRegistryLoginUrl = dockerRegistryHost + ":" + dockerRegistryPort;
        final var dockerRegistryPushUrl = dockerRegistryHost + ":" + dockerRegistryPort + "/" + tag + ":latest";

        final var loginCommand = "buildah login --tls-verify=false " + dockerRegistryLoginUrl;
        final var pushCommand = "buildah push --tls-verify=false " + tag + " " + dockerRegistryPushUrl;
        final var command = loginCommand + " && " + pushCommand;

        logger.info("push image to registry (projectId: {}, command: {})", projectId, command);

        final var process = new ProcessBuilder()
                .command("sh", "-c", loginCommand + " && " + pushCommand)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        int exitValue = process.waitFor();

        if (exitValue != PROCESS_EXIT_SUCCESS_STATUS) {
            throw new Exception("Failed to push image to registry.");
        }
    }
}
