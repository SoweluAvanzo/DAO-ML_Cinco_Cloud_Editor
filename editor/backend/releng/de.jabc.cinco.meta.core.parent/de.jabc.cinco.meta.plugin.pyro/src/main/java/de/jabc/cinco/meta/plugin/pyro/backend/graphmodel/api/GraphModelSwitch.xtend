  package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel
import mgl.MGLModel

class GraphModelSwitch extends Generatable {
	
	protected extension ModelElementHook = new ModelElementHook
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.toCamelCase.fuEscapeJava»Switch.java'''
	
	
	def createSwitchCase(String type)'''
		if(element instanceof graphmodel.«type») {
			result = case«type»((graphmodel.«type»)element);
			if(result != null) {
				return result;
			}
		}
	'''
	
	def createSwitchMethod(String type)'''
		protected T case«type»(graphmodel.«type» element) {
			return null;
		}
	'''
	
	def content(GraphModel g)
	{
		val modelPackage = g.modelPackage as MGLModel
	'''
		package «modelPackage.apiFQNBase».util;
		
		public class «g.name.toCamelCase.fuEscapeJava»Switch<T> {
			
				protected T doSwitch(graphmodel.IdentifiableElement element) {
					T result = null;
					«FOR e:g.elements»
						if(element instanceof «e.apiFQN») {
							result = case«e.name.escapeJava»((«e.apiFQN»)element);
							if(result != null) {
								return result;
							}
						}
					«ENDFOR»
					«"GraphModel".createSwitchCase»
					«"Container".createSwitchCase»
					«"Node".createSwitchCase»
					«"Edge".createSwitchCase»
					«"ModelElementContainer".createSwitchCase»
					«"ModelElement".createSwitchCase»
					«"IdentifiableElement".createSwitchCase»
					return defaultCase(element);
				}
				
				«FOR e:g.elements»
					protected T case«e.name.escapeJava»(«e.apiFQN» element) {
						return null;
					}
				«ENDFOR»
				
				«"GraphModel".createSwitchMethod»
				«"Container".createSwitchMethod»
				«"Node".createSwitchMethod»
				«"Edge".createSwitchMethod»
				«"ModelElementContainer".createSwitchMethod»
				«"ModelElement".createSwitchMethod»
				«"IdentifiableElement".createSwitchMethod»
				
				protected T defaultCase(graphmodel.IdentifiableElement object) {
					return null;
				}
		}
	'''
	}
	
	
}
