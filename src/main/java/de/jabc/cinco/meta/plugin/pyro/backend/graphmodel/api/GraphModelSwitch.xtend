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
	
	def createSwitchCase(String type)
	'''if(element instanceof graphmodel.«type») {
		result = case«type»((graphmodel.«type»)element);
		if(result != null) {
			return result;
		}
	}'''
	
	def createSwitchCase(String type, String packageFQN, CharSequence typeName)
	'''if("«typeName»".equals(TypeRegistry.getTypeOf(element))) {
		result = case«type»((«packageFQN».«type»)element);
		if(result != null) {
			return result;
		}
	}'''
	
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
					«FOR e:g.elementsAndTypesAndGraphModels SEPARATOR " else "
					»«e.name.escapeJava.createSwitchCase(e.modelPackage.apiFQNBase.toString, e.typeName)»«
					ENDFOR»
					else «"GraphModel".createSwitchCase»
					else «"Container".createSwitchCase»
					else «"Node".createSwitchCase»
					else «"Edge".createSwitchCase»
					else «"ModelElementContainer".createSwitchCase»
					else «"ModelElement".createSwitchCase»
					else «"IdentifiableElement".createSwitchCase»
					return defaultCase(element);
				}
				
				«FOR e:g.elementsAndTypesAndGraphModels»
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
