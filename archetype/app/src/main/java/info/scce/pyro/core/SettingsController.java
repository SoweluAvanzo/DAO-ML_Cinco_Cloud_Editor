package info.scce.pyro.core;

import info.scce.pyro.core.rest.types.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.ws.rs.Path("/settings")
@javax.transaction.Transactional
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.enterprise.context.RequestScoped
public class SettingsController {

	    
	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.inject.Inject
	@org.eclipse.microprofile.rest.client.inject.RestClient
	info.scce.pyro.style.MainAppStyleClient styleClient;
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/public")
	@javax.annotation.security.PermitAll()
	public javax.ws.rs.core.Response get() {				
		entity.core.PyroStyleDB style = entity.core.PyroStyleDB.fromPOJO(styleClient.getStyle());
		entity.core.PyroSettingsDB settings = new entity.core.PyroSettingsDB();
		settings.style = style;
		return javax.ws.rs.core.Response.ok(PyroSettings.fromEntity(settings, objectCache)).build();	
	}
	
	
}
