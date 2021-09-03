package info.scce.cincocloud.core.beans;

import io.quarkus.runtime.StartupEvent;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import info.scce.cincocloud.core.ProjectDeploymentService;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.StopProjectPodsTaskDB;

@ApplicationScoped
public class StopProjectPodsTaskBean {

    private static final Logger LOGGER = Logger.getLogger(StopProjectPodsTaskBean.class.getName());

    /**
     * How long a pod can stay alive without interaction in seconds.
     */
    private static final int POD_IDLE_TIMEOUT = 300;

    @Inject
    Scheduler scheduler;

    @Inject
    ProjectDeploymentService projectDeploymentService;

    void onStart(@Observes StartupEvent event) throws SchedulerException {
        final var job = JobBuilder.newJob(StopProjectPodsJob.class)
                .withIdentity("task-job", "cc")
                .build();

        final var trigger = TriggerBuilder.newTrigger()
                .withIdentity("task-job-trigger", "cc")
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMinutes(1)
                                .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);
    }

    void performTask() {
        LOGGER.log(Level.INFO, "stop inactive pods.");

        final List<StopProjectPodsTaskDB> tasks = StopProjectPodsTaskDB.findAll().list();
        final var now = Instant.now();

        tasks.stream()
                .filter(t -> now.isAfter(t.getCreatedAt().plusSeconds(POD_IDLE_TIMEOUT)))
                .collect(Collectors.toList())
                .forEach(t -> {
                    final Optional<PyroProjectDB> projectOptional = PyroProjectDB.findByIdOptional(t.getProjectId());
                    projectOptional.ifPresent(projectDeploymentService::stop);
                    t.delete();
                });
    }

    public static class StopProjectPodsJob implements Job {

        @Inject
        StopProjectPodsTaskBean taskBean;

        @Transactional
        public void execute(JobExecutionContext context) {
            taskBean.performTask();
        }
    }
}
