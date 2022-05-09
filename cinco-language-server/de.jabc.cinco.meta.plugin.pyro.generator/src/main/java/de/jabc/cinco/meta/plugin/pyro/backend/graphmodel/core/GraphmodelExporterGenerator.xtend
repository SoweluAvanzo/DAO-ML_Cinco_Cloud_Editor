package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.core

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel

class GraphmodelExporterGenerator extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.fuEscapeJava»Exporter.xtend'''
	
	def content(GraphModel g)
	'''
	package info.scce.pyro.core.export
	
	public class «g.name.fuEscapeJava»Exporter {
		new() {}
		
		def String getContent(«g.entityFQN» graph) {
			//TODO
			«" ''' "»
				No Content in here
			«" ''' "»
		}
	}
	'''
	
}