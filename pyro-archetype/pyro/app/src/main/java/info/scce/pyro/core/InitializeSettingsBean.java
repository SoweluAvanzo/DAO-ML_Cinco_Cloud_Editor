package info.scce.pyro.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
@javax.transaction.Transactional
public class InitializeSettingsBean {
	
	@javax.inject.Inject
	@org.eclipse.microprofile.rest.client.inject.RestClient
	info.scce.pyro.style.MainAppStyleClient styleClient;


	void onStart(@Observes StartupEvent ev) {
		entity.core.PyroStyleDB style;
		try {
			style = entity.core.PyroStyleDB.fromPOJO(styleClient.getStyle());
		} catch (Exception e) {
			System.out.println("Could not fetch styling. Falling back to default.");
			style = entity.core.PyroStyleDB.getDefault();
		}
		entity.core.PyroSettingsDB settings = new entity.core.PyroSettingsDB();
		settings.style = style;
		
	}
}
