package info.scce.cincocloud.core;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;
import info.scce.cincocloud.db.PyroSettingsDB;
import info.scce.cincocloud.db.PyroStyleDB;
import info.scce.cincocloud.db.StopProjectPodsTask;

@ApplicationScoped
@Transactional
public class InitializeSettingsBean {

    void onStart(@Observes StartupEvent ev) {
        initSettings();
        removeDanglingPods();
    }

    void initSettings() {
        try {
            if (PyroStyleDB.listAll().isEmpty()) {
                PyroStyleDB style = new PyroStyleDB();
                style.navBgColor = "525252";
                style.navTextColor = "afafaf";
                style.bodyBgColor = "313131";
                style.bodyTextColor = "ffffff";
                style.primaryBgColor = "007bff";
                style.primaryTextColor = "ffffff";
                style.persist();

                PyroSettingsDB settings = new PyroSettingsDB();
                settings.style = style;
                settings.persist();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void removeDanglingPods() {
        final KubernetesClient client = new DefaultKubernetesClient();

        client.apps().statefulSets().list().getItems().stream()
                .filter(s -> s.getMetadata().getName().startsWith("project"))
                .map(s -> s.getMetadata().getLabels().get("project"))
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .forEach(id -> {
                    final var task = new StopProjectPodsTask();
                    task.setProjectId(id);
                    task.setCreatedAt(Instant.now());
                    task.persist();
                });
    }
}
