package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.modules.checks

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel

class Module extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.fuEscapeJava»Check.java'''
	
	
	def content(GraphModel g)
	'''
	package «g.MGLModel.package».mcam.modules.checks;
	
	import de.jabc.cinco.meta.plugin.mcam.runtime.core.CincoCheckModule;
	import «g.apiFQN».«g.name.fuEscapeJava»;
	import «g.MGLModel.package».mcam.adapter.«g.name.fuEscapeJava»Id;
	import «g.MGLModel.package».mcam.adapter.«g.name.fuEscapeJava»Adapter;
	
	public abstract class «g.name.fuEscapeJava»Check extends CincoCheckModule<«g.name.fuEscapeJava»Id, «g.name.fuEscapeJava», «g.name.fuEscapeJava»Adapter> {
		
	}


	
	'''
	
}
