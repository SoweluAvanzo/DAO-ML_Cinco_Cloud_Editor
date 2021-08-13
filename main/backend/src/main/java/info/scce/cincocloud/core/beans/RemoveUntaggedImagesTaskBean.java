package info.scce.cincocloud.core.beans;

import io.quarkus.scheduler.Scheduled;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class RemoveUntaggedImagesTaskBean {

    @Transactional
    @Scheduled(every = "120s", identity = "remove-untagged-images-task")
    void schedule() {
        // TODO
    }
}
