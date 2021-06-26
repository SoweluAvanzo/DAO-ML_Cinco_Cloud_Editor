package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.core.rest

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.MGLModel

class RegistrationGenerator extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(MGLModel g)'''RegistrationController.java'''
	
	def content(MGLModel g)
	'''
	package info.scce.pyro.core.rest;
	
	import info.scce.pyro.core.rest.types.PyroUserRegistration;
	import javax.ws.rs.core.SecurityContext;
	import info.scce.pyro.auth.PBKDF2Encoder;
	import info.scce.pyro.core.rest.types.FindPyroUser;
	import info.scce.pyro.core.OrganizationController;
	import info.scce.pyro.core.rest.types.PyroUser;
	
	@javax.transaction.Transactional
	@javax.ws.rs.Path("/register/")
	public class RegistrationController {
		
		private static final int MIN_PASSWORD_LENGTH = 5;
	
	
		@javax.inject.Inject
		PBKDF2Encoder passwordEncoder;
		
		@javax.inject.Inject
		info.scce.pyro.rest.ObjectCache objectCache;
		
		@javax.inject.Inject
		info.scce.pyro.core.OrganizationController organizationController;
		«IF gc.cpd.hasClosedRegistration»
			
			@javax.ws.rs.POST
			@javax.ws.rs.Path("new/private")
			@javax.annotation.security.PermitAll
			@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
			@org.jboss.resteasy.annotations.GZIP
			public javax.ws.rs.core.Response createUser(@javax.ws.rs.core.Context SecurityContext securityContext,PyroUserRegistration pyroUserRegistration) {
				if(pyroUserRegistration.getemail()==null) {
					return javax.ws.rs.core.Response.status(
							javax.ws.rs.core.Response.Status.FORBIDDEN).build();
				}
				if(pyroUserRegistration.getemail().isEmpty()) {
					return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
				}
				final java.util.List<entity.core.PyroUserDB> users = entity.core.PyroUserDB.list("email",pyroUserRegistration.getemail());
				if(users.isEmpty()) {
					String password = passwordEncoder.encode(pyroUserRegistration.getpassword());
					entity.core.PyroUserDB user = entity.core.PyroUserDB.add(
						pyroUserRegistration.getemail(),
						pyroUserRegistration.getusername(),
						password
					);
					return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(user,objectCache)).build();
				}
				return javax.ws.rs.core.Response.status(
						javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
			}
		«ENDIF»
		
		@javax.ws.rs.POST
		@javax.ws.rs.Path("new/public")
		@javax.annotation.security.PermitAll
		@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
		public javax.ws.rs.core.Response registerUser(@javax.ws.rs.core.Context SecurityContext securityContext,PyroUserRegistration pyroUserRegistration) {
	
			if(
				pyroUserRegistration.getemail()==null||
				pyroUserRegistration.getusername()==null||
				pyroUserRegistration.getname()==null||
				pyroUserRegistration.getpassword()==null
			) {
				return javax.ws.rs.core.Response.status(
					javax.ws.rs.core.Response.Status.FORBIDDEN
				).build();
			}
			
			if(
				pyroUserRegistration.getemail().isEmpty()||
				pyroUserRegistration.getusername().isEmpty()||
				pyroUserRegistration.getname().isEmpty()||
				pyroUserRegistration.getpassword().isEmpty()
			) {
				return javax.ws.rs.core.Response.status(
					javax.ws.rs.core.Response.Status.FORBIDDEN
				).build();
			}
	
			if(pyroUserRegistration.getpassword().length() < MIN_PASSWORD_LENGTH){
				return javax.ws.rs.core.Response.status(
					javax.ws.rs.core.Response.Status.FORBIDDEN
				).build();
			}
	
			final java.util.List<entity.core.PyroUserDB> users = entity.core.PyroUserDB.list("email",pyroUserRegistration.getemail());
			
			boolean userShouldBeAdmin = entity.core.PyroUserDB.count() <=0;
			if(«IF gc.cpd.hasClosedRegistration»!«ENDIF»users.isEmpty()){
				«IF gc.cpd.hasClosedRegistration»
					entity.core.PyroUserDB user = users.get(0);
				«ELSE»
					entity.core.PyroUserDB user = entity.core.PyroUserDB.add(
						pyroUserRegistration.getemail(),
						pyroUserRegistration.getusername(),
						passwordEncoder.encode(pyroUserRegistration.getpassword())
					);
				«ENDIF»
				if (userShouldBeAdmin) {
					user.systemRoles.add(entity.core.PyroSystemRoleDB.ADMIN);
					user.systemRoles.add(entity.core.PyroSystemRoleDB.ORGANIZATION_MANAGER);
					«FOR a:gc.initialOrganizations»
						organizationController.addOrganizationOwner(user,organizationController.getOrganization("«a»"));
					«ENDFOR»
				}«IF !gc.initialOrganizations.empty» else {
					«FOR a:gc.initialOrganizations»
						organizationController.addOrganizationMember(user,organizationController.getOrganization("«a»"));
					«ENDFOR»
				}«ENDIF»
				user.persist();
					
				// TODO: SAMI: send activation mail
				user.isActivated = true; // TODO: SAMI: remove this later (for development use)
				«IF gc.organizationPerUser»
					
					//organization per user enabled
					organizationController.createOrganization(user.username+" Organization","",user);
				«ENDIF»
	
				return javax.ws.rs.core.Response.ok("Activation mail send").build();
			}
	
			return javax.ws.rs.core.Response.status(
					javax.ws.rs.core.Response.Status.FORBIDDEN).build();
		}
	}

	'''
	

	
}