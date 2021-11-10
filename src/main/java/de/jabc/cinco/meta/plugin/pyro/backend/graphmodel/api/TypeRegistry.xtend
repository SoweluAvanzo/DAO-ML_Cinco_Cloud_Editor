  package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import java.util.List
import java.util.Set
import java.util.function.BiFunction
import java.util.function.Function
import mgl.GraphModel
import mgl.MGLModel
import mgl.ModelElement
import org.eclipse.emf.ecore.EPackage

/**
 * pm := packageModel
 * pe := packageElement
 * 
 * @author Sami Mitwalli
 */
class TypeRegistry extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename()'''TypeRegistry.java'''
	
	def content(MGLModel modelPackage)
	{
		val primeReferencedModels = modelPackage.primeReferencedGraphModels.toSet
		val ecoreModels = modelPackage.ecorePrimeRefsModels
		'''
		package «modelPackage.apiFQNBase».util;
		
		import «dbTypeFQN»;
		import «commandExecuterFQN»;
		«FOR graphModel: modelPackage.graphmodels.filter[!isAbstract]»
			import «graphModel.commandExecuterFQN»;
		«ENDFOR»
		
		public class TypeRegistry {
			
			/**
			 * PACKAGE-SPECIFIC FUNCTIONS
			 */
			
			public static String getTypeOf(graphmodel.IdentifiableElement e) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(e instanceof «e.apiFQN») {
					return "«e.typeName»";
				}«
				ENDFOR»
				«modelPackage.onPrimeReferencedTypeRegistry(
					primeReferencedModels,
					"getTypeOf",
					[pm, pe | '''(e instanceof «pe.apiFQN»)'''],
					true
				)»
				«/* TODO: ECORE if needed */»
				return null;
			}
			
			public static String getTypeOf(«dbTypeName» e) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(e instanceof «e.entityFQN») {
					return "«e.typeName»";
				}«
				ENDFOR»
				«onPrimeReferencedTypeRegistry(
					modelPackage,
					primeReferencedModels,
					"getTypeOf",
					[pm, pe | '''(e instanceof «pe.entityFQN»)'''],
					true
				)»
				«/* TODO: ECORE if needed */»
				return null;
			}
			
			public static String getTypeOf(info.scce.pyro.core.graphmodel.IdentifiableElement e) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(e instanceof «e.restFQN») {
					return "«e.typeName»";
				}«
				ENDFOR»
				«onPrimeReferencedTypeRegistry(
					modelPackage,
					primeReferencedModels,
					"getTypeOf",
					[pm, pe | '''(e instanceof «pe.restFQN»)'''],
					true
				)»
				«/* TODO: ECORE if needed */»
				return null;
			}

			public static info.scce.pyro.core.graphmodel.IdentifiableElement getApiToRest(graphmodel.IdentifiableElement e) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(e instanceof «e.apiFQN») {
					«e.apiFQN» apiE = («e.apiFQN») e;
					return getDBToRest(apiE.getDelegate());
				}«
				ENDFOR»
				«onPrimeReferencedTypeRegistry(
					modelPackage,
					primeReferencedModels,
					"getApiToRest",
					[pm, pe | '''(e instanceof «pe.apiFQN»)'''],
					false
				)»
				«/* TODO: ECORE if needed */»
				return null;
			}

			public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRest(«dbTypeName» e) {
				return getDBToRest(e, new info.scce.pyro.rest.ObjectCache(), false);
			}
			
			public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRest(«dbTypeName» e, info.scce.pyro.rest.ObjectCache cache) {
				return getDBToRest(e, cache, false);
			}

			public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRest(«dbTypeName» e, info.scce.pyro.rest.ObjectCache cache, boolean onlyProperties) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(e instanceof «e.entityFQN») {
					«e.entityFQN» en = («e.entityFQN») e;
					if(onlyProperties) {
						return «e.restFQN».fromEntityProperties(en, cache);
					} else {
						return «e.restFQN».fromEntity(en, cache);
					}
				}«
				ENDFOR»
				return getDBToRestPrime(e, cache, onlyProperties);
			}
			
			public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRestPrime(«dbTypeName» e, info.scce.pyro.rest.ObjectCache cache, boolean onlyProperties) {
				«onEcoreReferenceDBToRest(ecoreModels, '''cache''')»
				«onPrimeReferencedTypeRegistry(modelPackage,
					primeReferencedModels,
					"getDBToRest",
					[pm, pe | '''(e instanceof «pe.apiFQN»)'''],
					false,
					ecoreModels.empty
				)»
				return null;
			}
	
			public static «dbTypeName» getApiToDB(graphmodel.IdentifiableElement e) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(e instanceof «e.apiFQN») {
					«e.apiFQN» apiE = («e.apiFQN») e;
					return apiE.getDelegate();
				}«
				ENDFOR»
				«onPrimeReferencedTypeRegistry(
					modelPackage,
					primeReferencedModels,
					"getApiToDB",
					[pm, pe | '''(e instanceof «pe.apiFQN»)'''],
					false
				)»
				«/* TODO: ECORE if needed */»
				return null;
			}
			
			public static «dbTypeName» findAbstract(long id, Class<?> c) {
				if(graphmodel.IdentifiableElement.class.isAssignableFrom(c)) {
					return findAbstractEntityByApi(id, c);
				} else if (info.scce.pyro.core.graphmodel.IdentifiableElement.class.isAssignableFrom(c)){
					return findAbstractEntityByRest(id, c);
				} else if («dbTypeName».class.isAssignableFrom(c)){
					return findAbstractEntityByEntity(id, c);
				}
				return null;
			}
			
			public static «dbTypeName» findAbstractEntityByApi(long id, Class<?> entityClass) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(«e.apiFQN».class.equals(entityClass)) {
					return «e.entityFQN».findById(id);
				}«
				ENDFOR»
				«onPrimeReferencedTypeRegistry(
					modelPackage,
					primeReferencedModels,
					"findAbstractEntityByApi",
					"id, entityClass",
					[pm, pe | '''«pe.apiFQN».class.equals(entityClass)'''],
					false
				)»
				«/* TODO: ECORE if needed */»
				return null;
			}
			
			public static «dbTypeName» findAbstractEntityByRest(long id, Class<?> entityClass) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(«e.restFQN».class.equals(entityClass)) {
					return «e.entityFQN».findById(id);
				}«
				ENDFOR»
				«onPrimeReferencedTypeRegistry(
					modelPackage,
					primeReferencedModels,
					"findAbstractEntityByRest",
					"id, entityClass",
					[pm, pe | '''«pe.restFQN».class.equals(entityClass)'''],
					false
				)»
				«/* TODO: ECORE if needed */»
				return null;
			}
			
			public static «dbTypeName» findAbstractEntityByEntity(long id, Class<?> entityClass) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(«e.entityFQN».class.equals(entityClass)) {
					return «e.entityFQN».findById(id);
				}«
				ENDFOR»
				«onPrimeReferencedTypeRegistry(
					modelPackage,
					primeReferencedModels,
					"findAbstractEntityByEntity",
					"id, entityClass",
					[pm, pe | '''«pe.entityFQN».class.equals(entityClass)'''],
					true
				)»
				«/* TODO: ECORE if needed */»
				return null;
			}
			
			/**
			 * This should only be used if no other information can be derived or resolved!
			 * It iterates over all type-tables of the GraphModel with bruteforce and tries
			 * to find the associated entity by the given id.
			 *
			 * (utilizing this method is bad style!)
			 */
			public static «dbTypeName» findById(long id) {
				«dbTypeName» found = null;
				«FOR e:modelPackage.elements.filter[!isIsAbstract]»
					try {
						found = «e.entityFQN».findById(id);
						if(found != null) {
							return found;
						}
					} catch(Exception e) {
						System.out.println("the id is not associated with «e.entityFQN»...");
						e.printStackTrace();
					}
				«ENDFOR»
				«/* TODO: PRIME & ECORE */»
				return null;
			}
			
			public static «dbTypeName» findByType(String type, long id) {
				«FOR e:modelPackage.elements.filter[!isIsAbstract] SEPARATOR " else "
				»if(type.equals("«e.typeName»") ){
					return «e.entityFQN».findById(id);
				}«
				ENDFOR»
				«/* TODO: ECORE if needed */»
				return null;
			}
			
			/**
			 * GRAPHMODEL FUNCTIONS
			 */
			
			public static graphmodel.IdentifiableElement getDBToApi(
							«dbTypeName» e,
							«commandExecuterClass» executer
			) {
				return getDBToApi(e, executer, null, null);
			}
			
			public static graphmodel.IdentifiableElement getDBToApi(
				«dbTypeName» e,
				«commandExecuterClass» executer,
				graphmodel.IdentifiableElement parent,
				info.scce.pyro.core.graphmodel.IdentifiableElement prev
			) {
				«FOR e:modelPackage.elements.filter[!isAbstract] SEPARATOR " else "
				»if(e instanceof «e.entityFQN») {
					«e.entityFQN» en = («e.entityFQN») e;
					return new «e.apiImplFQN»(en, executer);
				}«
				ENDFOR»
				return getDBToApiPrime(e, executer, parent, prev);
			}
			
			public static graphmodel.IdentifiableElement findApiByType(
				String type,
				long id,
				«commandExecuterClass» cmdExecuter
			) {
				«modelPackage.commandExecuterSwitch([cmdExecuter|
					'''
						return findApiByType(type, id, «cmdExecuter», null, null);
					'''
				])»
				return null;
			}
			
			public static graphmodel.IdentifiableElement findApiByType(
				String type,
				long id,
				«commandExecuterClass» executer,
				graphmodel.IdentifiableElement parent,
				info.scce.pyro.core.graphmodel.IdentifiableElement prev
			) {
				«FOR e:modelPackage.elements.filter[!isIsAbstract] SEPARATOR " else "
				»if(type.equals("«e.typeName»") ){
					«e.entityFQN» e = «e.entityFQN».findById(id);
					return getDBToApi(
						e,
						executer,
						parent,
						prev
					);
				}«
				ENDFOR»
				return findApiByTypePrime(type, id, executer, parent, prev);
			}
			
			public static graphmodel.IdentifiableElement getDBToApiPrime(
				«dbTypeName» e,
				«commandExecuterClass» cmdExecuter,
				graphmodel.IdentifiableElement parent,
				info.scce.pyro.core.graphmodel.IdentifiableElement prev
			) {
				«modelPackage.commandExecuterSwitch([cmdExecuter|
					'''
						return getDBToApiPrime«cmdExecuter»(e, «cmdExecuter», parent, prev);
					'''
				])»
				return null;
			}
			
			public static graphmodel.IdentifiableElement findApiByTypePrime(
				String type,
				long id,
				«commandExecuterClass» cmdExecuter,
				graphmodel.IdentifiableElement parent,
				info.scce.pyro.core.graphmodel.IdentifiableElement prev
			) {
				«modelPackage.commandExecuterSwitch([cmdExecuter|
					'''
						return findApiByTypePrime«cmdExecuter»(type, id, «cmdExecuter», parent, prev);
					'''
				])»
				return null;
			}
			
			/**
			 * GRAPHMODEL-SPECIFIC FUNCTIONS
			 */
			
			«FOR gM: modelPackage.graphmodels.filter[!isAbstract]»
				public static graphmodel.IdentifiableElement getDBToApiPrime«gM.commandExecuterVar»(
					«dbTypeName» e,
					«gM.commandExecuter» executer,
					graphmodel.IdentifiableElement parent,
					info.scce.pyro.core.graphmodel.IdentifiableElement prev
				) {
					«onEcoreReferenceDBToAPI(ecoreModels, '''executer''')»
					«onPrimeReferencedTypeRegistry(
						modelPackage,
						primeReferencedModels,
						"getDBToApi",
						[graphModel | '''«graphModel.commandExecuter»'''],
						[pm | '''e, «pm.commandExecuter», parent, prev'''],
						[pm, pe | '''(e instanceof «pe.entityFQN»)'''],
						true,
						ecoreModels.empty
					)»
					return null;
				}
				
				public static graphmodel.IdentifiableElement findApiByTypePrime«gM.commandExecuterVar»(
					String type,
					long id,
					«gM.commandExecuter» executer,
					graphmodel.IdentifiableElement parent,
					info.scce.pyro.core.graphmodel.IdentifiableElement prev
				) {
					«onPrimeReferencedTypeRegistry(
						modelPackage,
						primeReferencedModels,
						"findApiByType",
						[graphModel | '''«graphModel.commandExecuter»'''],
						[pm | '''type, id, «pm.commandExecuter», parent, prev'''],
						[pm, pe | '''"«pe.typeName»".equals(type)'''],
						true,
						true
					)»
					«/* TODO: ECORE if needed */»
					return null;
				}
				
			«ENDFOR»
		}
		'''
	}
	
	def onPrimeReferencedTypeRegistry(MGLModel g, Set<GraphModel> primeReferencedModels, String functionCall, BiFunction<MGLModel, ModelElement, CharSequence> predicate, boolean filterAbstract) {
		onPrimeReferencedTypeRegistry(g, primeReferencedModels, functionCall, null, null, predicate, filterAbstract, false)
	}
	
	def onPrimeReferencedTypeRegistry(MGLModel g, Set<GraphModel> primeReferencedModels, String functionCall, BiFunction<MGLModel, ModelElement, CharSequence> predicate, boolean filterAbstract, boolean startWithIf) {
		onPrimeReferencedTypeRegistry(g, primeReferencedModels, functionCall, null, null, predicate, filterAbstract, startWithIf)
	}
	
	def onPrimeReferencedTypeRegistry(MGLModel g, Set<GraphModel> primeReferencedModels, String functionCall, String parameter, BiFunction<MGLModel, ModelElement, CharSequence> predicate, boolean filterAbstract) {
		onPrimeReferencedTypeRegistry(g, primeReferencedModels, functionCall, null, [pm | parameter], predicate, filterAbstract, false)
	}
	
	def onPrimeReferencedTypeRegistry(MGLModel g, Set<GraphModel> primeReferencedModels, String functionCall, Function<GraphModel, CharSequence> parameter, BiFunction<MGLModel, ModelElement, CharSequence> predicate, boolean filterAbstract) {
		onPrimeReferencedTypeRegistry(g, primeReferencedModels, functionCall, null, parameter, predicate, filterAbstract, false)
	}
	
	def onPrimeReferencedTypeRegistry(MGLModel g, Set<GraphModel> primeReferencedModels, String functionCall, Function<GraphModel, String> executerName, Function<GraphModel, CharSequence> parameter, BiFunction<MGLModel, ModelElement, CharSequence> predicate, boolean filterAbstract, boolean startWithIf) {
		if(primeReferencedModels === null || primeReferencedModels.empty)
			return ''''''
		'''
			// prime referenced graph-models
			«IF !startWithIf
			»else «
			ENDIF»«
			FOR referencedModel:primeReferencedModels.filter[!equals(g)] SEPARATOR " else "
			»if(«val modelPackage = referencedModel.modelPackage as MGLModel»
				«{
					var elements = modelPackage.elements
					if(filterAbstract) {
						elements = elements.filter[!isAbstract]
					}
					'''
						«FOR pe: elements SEPARATOR "\n|| "
						»«predicate.apply(modelPackage, pe)»«
						ENDFOR»
					'''
				}»
			) {
				«IF executerName !== null»
					«referencedModel.commandExecuter» «executerName.apply(referencedModel)» = executer.get«referencedModel.commandExecuter»();
				«ENDIF»
				return «modelPackage.typeRegistryFQN».«functionCall»(«IF parameter !== null»«parameter.apply(referencedModel)»«ELSE»e«ENDIF»);
			}«
			ENDFOR»
		'''
	}
	
	def onEcoreReferenceDBToRest(List<EPackage> ecoreModels, CharSequence cache) {
		if(ecoreModels === null || ecoreModels.empty)
			return ''''''
		'''
			// prime referenced ecore-models
			«FOR ecore:ecoreModels SEPARATOR " else "
			»«{
				val packageRef = '''«ecore.entityFQN»'''
				'''
					if(e instanceof «packageRef») {
						«packageRef» en = («packageRef») e;
						return «ecore.restFQN».fromEntity(en, «cache»);
					}
					«FOR eC: ecore.elements.filter[!abstract]»
						«{
							val packageRefChild = '''«eC.entityFQN»'''
							'''
								else if(e instanceof «packageRefChild») {
									«packageRefChild» en = («packageRefChild») e;
									if(onlyProperties) {
										return «eC.restFQN».fromEntityProperties(en, «cache»);
									} else {
										return «eC.restFQN».fromEntity(en, «cache»);
									}
								}
							'''
						}»
					«ENDFOR»
				'''
			}»«
			ENDFOR»
		'''
	}
	
	def onEcoreReferenceDBToAPI(List<EPackage> ecoreModels, CharSequence executer) {
		if(ecoreModels === null || ecoreModels.empty)
			return ''''''
		'''
			// prime referenced ecore-models
			«FOR ecore:ecoreModels SEPARATOR " else "
			»«{
				val packageRef = '''«ecore.entityFQN»'''
				'''
					if(e instanceof «packageRef») {
						«packageRef» en = («packageRef») e;
						return new «ecore.apiImplFQN»(en);
					}
					«FOR eC: ecore.elements.filter[!abstract]»
						«{
							val packageRefChild = '''«eC.entityFQN»'''
							'''
								else if(e instanceof «packageRefChild») {
									«packageRefChild» en = («packageRefChild») e;
									return new «eC.apiImplFQN»(en);
								}
							'''
						}»
					«ENDFOR»
				'''
			}»«
			ENDFOR»
		'''
	}
}
