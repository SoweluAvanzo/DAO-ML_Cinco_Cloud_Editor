package info.scce.cincocloud.mq;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

@ApplicationScoped
public class WorkspaceMQProducer {

  private static final Logger LOGGER = Logger.getLogger(WorkspaceMQProducer.class.getName());

  @Inject
  @Channel("workspaces-jobs-queue")
  @OnOverflow(value = OnOverflow.Strategy.NONE)
  Emitter<WorkspaceImageBuildJobMessage> workspaces;

  public void send(WorkspaceImageBuildJobMessage job) {
    LOGGER.log(Level.INFO, "Send message to workspaces.jobs.queue: {0}",
        new Object[] {job.toString()});
    workspaces.send(job);
  }
}
