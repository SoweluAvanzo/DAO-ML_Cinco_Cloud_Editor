package info.scce.pyro.core;

import info.scce.pyro.core.rest.types.*;

@javax.ws.rs.Path("/settings")
@javax.transaction.Transactional
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.enterprise.context.RequestScoped
public class SettingsController {
	    
	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/public")
	@javax.annotation.security.PermitAll()
	public javax.ws.rs.core.Response get() {		
		entity.core.PyroSettingsDB settings = new entity.core.PyroSettingsDB();
		settings.style = entity.core.PyroStyleDB.getDefault();
		return javax.ws.rs.core.Response.ok(PyroSettings.fromEntity(settings, objectCache)).build();	
	}
}
