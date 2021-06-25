package de.jabc.cinco.meta.plugin.pyro.backend.core

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class InitializeSettingsBean extends Generatable {

	new(GeneratorCompound gc) {
		super(gc)
		
	}

	def filename() '''InitializeSettingsBean.java'''

	def content() '''	
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
						«IF !gc.initialOrganizations.empty && !gc.cpd.adminUsers.empty»
							
							java.util.LinkedList<entity.core.PyroOrganizationDB> initialOrganizations = new LinkedList<>();
						«ENDIF»
						«FOR a:gc.initialOrganizations»
							{
								entity.core.PyroOrganizationDB org = this.organizationController.createOrganization("«a»","",null)
								«IF !gc.initialOrganizations.empty && !gc.cpd.adminUsers.empty»
									initialOrganizations.add(org);
								«ENDIF»
							}
						«ENDFOR»
						«IF gc.cpd.hasClosedRegistration»
							
							«IF !gc.initialOrganizations.empty && !gc.cpd.adminUsers.empty»
								java.util.LinkedList<entity.core.PyroUserDB> initialUsers = new LinkedList<>();
							«ENDIF»
							«FOR u:gc.cpd.adminUsers»
								{
									String password = passwordEncoder.encode("«u»");
									java.util.LinkedList<PyroSystemRoleDB> systemRoles = new java.util.LinkedList<>();
									systemRoles.add(PyroSystemRoleDB.ADMIN);
									systemRoles.add(PyroSystemRoleDB.ORGANIZATION_MANAGER);
									entity.core.PyroUserDB user = entity.core.PyroUserDB.add(
										"«u»",
										"«u»",
										password,
										systemRoles
									);
									«IF gc.organizationPerUser»
										//organization per user enabled
										this.organizationController.createOrganization(user.username+" Organization","",user);
									«ENDIF»
									«IF !gc.initialOrganizations.empty && !gc.cpd.adminUsers.empty»
										initialUsers.add(user);
									«ENDIF»
								}
							«ENDFOR»
							«IF !gc.initialOrganizations.empty && !gc.cpd.adminUsers.empty»
								
								// add created initialUsers to initialOrganizations
								for(entity.core.PyroOrganizationDB org: initialOrganizations) {
									for(entity.core.PyroUserDB user: initialUsers) {
										organizationController.addOrganizationOwner(user, org);
									}
								}
							«ENDIF»
						«ENDIF»
						«FOR a:gc.rootPostCreate.indexed BEFORE "\n"»
						    «a.value» hook«a.key» = new «a.value»();
						    hook«a.key».execute(settings);
						«ENDFOR»
					}
					
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	'''
}
