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
                final var jobInDB = mainService.getBuildJobStatus(CincoCloudProtos.GetBuildJobStatusRequest.newBuilder()
                        .setJobId(job.jobId)
                        .build());

                if (jobInDB.getStatus().getNumber() == CincoCloudProtos.BuildJobStatus.Status.ABORTED_VALUE) {
                    logger.info("image was was already aborted and will not be build.");
                    message.ack();
                    return;
                }
            } catch (Exception e) {
                logger.error("could not fetch job from main service.", e);
                message.ack();
                return;
            }

            try {
                for (var cv: validator.validate(job)) {
                    throw new ValidationException(cv.getMessage());
                }

                mainService.setBuildJobStatus(CincoCloudProtos.BuildJobStatus.newBuilder()
                        .setStatus(CincoCloudProtos.BuildJobStatus.Status.BUILDING)
                        .setJobId(job.jobId)
                        .build());

                final var result = build(job);
                logger.info("image was build successfully: {}", result);
                buildResultEmitter.send(result);
            } catch (Exception e) {
                logger.error("image build failed.", e);
                final var result = new BuildResult(job.projectId, job.jobId, false, e.getMessage(), null);
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

        return new BuildResult(job.projectId, job.jobId, true, "success", job.getImageTag());
    }

    private void buildImage(Long projectId, Path sourceDir, String tag) throws Exception {
        logger.info("build image (projectId: {}, archive: {}, tag: {})", projectId, sourceDir, tag);
        executeCommand("cd " + sourceDir.toString() + " && buildah --storage-driver vfs bud -t " + tag + " .");
        executeCommand("buildah --storage-driver vfs images");

        logger.info("push image to registry (projectId: {}, tag: {})", projectId, tag);
        final var registryUrl = dockerRegistryHost + ":" + dockerRegistryPort;
        final var registryPushUrl = dockerRegistryHost + ":" + dockerRegistryPort + "/" + tag;
        final var loginCommand = "buildah --storage-driver vfs login --tls-verify=false -u= -p= " + registryUrl;
        final var pushCommand = "buildah --storage-driver vfs push --tls-verify=false " + tag + " " + registryPushUrl;
        executeCommand(loginCommand + " && " + pushCommand);

        final var logoutCommand = "buildah --storage-driver vfs logout " + registryUrl;
        executeCommand(logoutCommand);

        final var deleteLocalImageCommand = "buildah --storage-driver vfs rmi -f " + tag;
        executeCommand(deleteLocalImageCommand);
    }

    private void executeCommand(String command) throws Exception {
        logger.info("execute command: (command: {})", command);

        final var process = new ProcessBuilder()
                .command("sh", "-c", command)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        int exitValue = process.waitFor();

        if (exitValue != PROCESS_EXIT_SUCCESS_STATUS) {
            throw new Exception("Failed to execute command: " + command);
        }
    }
}
