package de.jabc.cinco.meta.plugin.pyro.backend.service.rest

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import productDefinition.Annotation

class ServiceRestTO extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(Annotation t)'''«t.value.get(1).fuEscapeJava».java'''
	
	def content(Annotation t) {
		'''
			package «projectServiceRestPackage»;
			
			/**
			 * Author zweihoff
			 */
			
			@com.fasterxml.jackson.annotation.JsonTypeName("«t.projectServiceRestFQN»")
			public class «t.projectServiceName»
			{
				«FOR attr:t.projectServiceAttributes»
					private String «attr.escapeJava»;
				«ENDFOR»
				«FOR attr:t.projectServiceAttributes SEPARATOR "\n"»
					
					@com.fasterxml.jackson.annotation.JsonProperty("«attr.escapeJava»")
					public String get«attr.fuEscapeJava»() {
						return this.«attr.escapeJava»;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("«attr.escapeJava»")
					public void set«attr.escapeJava»(final String «attr.escapeJava») {
						this.«attr.escapeJava» = «attr.escapeJava»;
					}
				«ENDFOR»
			}
		'''
	}
	
	
}