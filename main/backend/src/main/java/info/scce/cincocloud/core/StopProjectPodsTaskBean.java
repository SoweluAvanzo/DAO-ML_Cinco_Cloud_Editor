package info.scce.cincocloud.core;

import io.quarkus.scheduler.Scheduled;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.StopProjectPodsTaskDB;

@ApplicationScoped
public class StopProjectPodsTaskBean {

    /**
     * How long a pod can stay alive without interaction in seconds.
     */
    private static final int POD_IDLE_TIMEOUT = 300;

    @Inject
    ProjectDeploymentService projectDeploymentService;

    @Transactional
    @Scheduled(every = "120s", identity = "task-job")
    void schedule() {
        final List<StopProjectPodsTaskDB> tasks = StopProjectPodsTaskDB.findAll().list();
        final var now = Instant.now();

        tasks.stream()
                .filter(t -> now.isAfter(t.getCreatedAt().plusSeconds(POD_IDLE_TIMEOUT)))
                .collect(Collectors.toList())
                .forEach(t -> {
                    final Optional<PyroProjectDB> projectOptional = PyroProjectDB.findByIdOptional(t.getProjectId());
                    projectOptional.ifPresent(p -> projectDeploymentService.stop(p));
                    t.delete();
                });
    }
}
