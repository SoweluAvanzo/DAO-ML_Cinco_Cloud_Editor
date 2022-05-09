package de.jabc.cinco.meta.plugin.pyro.util

import mgl.MGLModel

class Generatable {
	protected final GeneratorCompound gc
	protected extension Escaper = new Escaper
	protected extension MGLExtension mglExtension
	
	new(GeneratorCompound gc){
		this.gc = gc
		this.mglExtension = gc.mglExtension
	}
	
	def getNodes(MGLModel g) {
		gc.mglExtension.nodes(g)
	}
}
