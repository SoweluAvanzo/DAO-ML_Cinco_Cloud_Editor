package de.jabc.cinco.meta.plugin.pyro.backend.connector

import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.Generatable

class PyroGraphModelType extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}

	def fileName() '''PyroGraphModelTypeDB.java'''

	def content() '''
	package entity.core;
	
	public enum PyroGraphModelTypeDB {
	    
	    «FOR g:gc.graphMopdels SEPARATOR ","
	    »«g.name.toUnderScoreCase»«
	    ENDFOR»
	}
	'''
}