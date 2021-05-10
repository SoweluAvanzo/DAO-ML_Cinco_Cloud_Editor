package info.scce.pyro.core;

import info.scce.pyro.util.DefaultColors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;
import info.scce.pyro.auth.PBKDF2Encoder;
import entity.core.PyroSystemRoleDB;
@ApplicationScoped
@javax.transaction.Transactional
public class InitializeSettingsBean {


	@javax.inject.Inject
	private OrganizationController organizationController;
	
	@javax.inject.Inject
	PBKDF2Encoder passwordEncoder;

	void onStart(@Observes StartupEvent ev) {
		try {
			if(entity.core.PyroStyleDB.listAll().isEmpty()) {
				
				entity.core.PyroStyleDB style = new entity.core.PyroStyleDB();
				style.navBgColor = "525252";
				style.navTextColor = "afafaf";
				style.bodyBgColor = "313131";
				style.bodyTextColor = "ffffff";
				style.primaryBgColor = "007bff";
				style.primaryTextColor = "ffffff";
				style.persist();
				entity.core.PyroSettingsDB settings = new entity.core.PyroSettingsDB();
				settings.style = style;
				settings.persist();
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
