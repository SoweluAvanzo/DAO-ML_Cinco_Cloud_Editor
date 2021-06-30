package de.jabc.cinco.meta.plugin.pyro.backend.service.rest

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import productDefinition.Annotation

class ServiceRestTO extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(Annotation t)'''«t.value.get(1).fuEscapeJava».java'''
	
	def content(Annotation t)
	'''
	package info.scce.pyro.service.rest;
	
	/**
	 * Author zweihoff
	 */
	
	@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
	@com.fasterxml.jackson.annotation.JsonTypeInfo(
		use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS,
		include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,
		property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE
	)
	@com.fasterxml.jackson.annotation.JsonIdentityInfo(generator = com.voodoodyne.jackson.jsog.JSOGGenerator.class)
	public class «t.value.get(1).fuEscapeJava»
	{
		«FOR attr:t.value.subList(2,t.value.size) SEPARATOR "\n"»
			private String «attr.escapeJava»;
			
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