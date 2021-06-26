package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.adapter

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel

class McamId extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.fuEscapeJava»Id.java'''
	
	
	def content(GraphModel g)
	'''
	package «g.MGLModel.package».mcam.adapter;

	import de.jabc.cinco.meta.plugin.mcam.runtime.core._CincoId;
	
	public class «g.name.fuEscapeJava»Id extends _CincoId {
		
		public «g.name.fuEscapeJava»Id(graphmodel.IdentifiableElement element) {
			super(element);
		}
	}
	
	'''
	
}
