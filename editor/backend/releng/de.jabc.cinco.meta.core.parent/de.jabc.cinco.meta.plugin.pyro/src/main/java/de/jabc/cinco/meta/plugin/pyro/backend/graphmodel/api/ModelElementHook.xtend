package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api

import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import mgl.ModelElement

class ModelElementHook {
	
	protected extension MGLExtension mglExtension
	
	
	def postCreate(ModelElement em,String elementName, GeneratorCompound gc) {
		postCreate(em,elementName,gc,false) 
	}
	
	def postCreate(ModelElement em,String elementName, GeneratorCompound gc,boolean isTransient) {
		if(isTransient) {
			postCreate(em, elementName, "null", gc)			
		} else {
			postCreate(em, elementName, "cmdExecuter", gc)	
		}
	}
	
	def postCreate(ModelElement em,String elementName, String commandExecuter, GeneratorCompound gc) {
		mglExtension = gc.mglExtension
		val postCreates = em.resolvePostCreate;
		'''
			«IF !postCreates.empty»
				// postCreateHooks
				«FOR postCreate:postCreates»
					{
						«postCreate» hook = new «postCreate»();
						hook.init(«commandExecuter»);
						hook.postCreate(«elementName»);
					}
				«ENDFOR»
			«ENDIF»
		'''
	}
}
