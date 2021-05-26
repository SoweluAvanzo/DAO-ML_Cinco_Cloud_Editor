package info.scce.cincocloud.core;

import io.quarkus.runtime.StartupEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.db.PyroSettingsDB;
import info.scce.cincocloud.db.PyroStyleDB;

@ApplicationScoped
@Transactional
public class InitializeSettingsBean {

    @Inject
    PBKDF2Encoder passwordEncoder;

    @Inject
    OrganizationController organizationController;

    void onStart(@Observes StartupEvent ev) {
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
}
