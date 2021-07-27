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
		
		
		@ApplicationScoped
		@javax.transaction.Transactional
		public class InitializeSettingsBean {
			
			@javax.inject.Inject
			@org.eclipse.microprofile.rest.client.inject.RestClient
			info.scce.pyro.style.MainAppStyleClient styleClient;
		
		
			void onStart(@Observes StartupEvent ev) {
				try {
						entity.core.PyroStyleDB style = entity.core.PyroStyleDB.fromPOJO(styleClient.getStyle());
						entity.core.PyroSettingsDB settings = new entity.core.PyroSettingsDB();
						settings.style = style;
						«FOR a:gc.rootPostCreate.indexed BEFORE "\n"»
						    «a.value» hook«a.key» = new «a.value»();
						    hook«a.key».execute(settings);
						«ENDFOR»
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	'''
}
