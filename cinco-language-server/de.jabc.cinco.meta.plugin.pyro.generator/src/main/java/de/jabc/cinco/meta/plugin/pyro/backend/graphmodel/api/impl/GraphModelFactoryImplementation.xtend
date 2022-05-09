package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl

import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.command.GraphModelCommandExecuter
import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel
import mgl.MGLModel
import mgl.UserDefinedType

class GraphModelFactoryImplementation extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)
	'''«g.apiFactoryImpl».java'''
	
	def content(GraphModel g, boolean isTransient) {
		val modelPackage = g.modelPackage as MGLModel
		'''
			package «modelPackage.apiImplFQNBase»;
			
			import «g.apiFactoryFQN»;
			import «g.apiFQN»;
			«IF !isTransient»
				import «g.commandExecuterFQN»;
				import «modelPackage.typeRegistryFQN»;
			«ENDIF»
			
			/**
			 * Author zweihoff
			 */
			@javax.enterprise.context.RequestScoped
			public class «g.apiFactoryImpl» implements «g.apiFactory» {
				«IF !isTransient»
					
					private «g.commandExecuter» executer;
					
					public void warmup(
						         «g.commandExecuter» executer
						    ) {
						        this.executer = executer;
					}
				«ENDIF»
			
			    public static «g.apiFactory» init() {
			    	return new «g.apiFactoryImpl»();
			    }
			
				public «g.apiFQN» create«g.name.fuEscapeJava»(String projectRelativePath, String filename) {
					«IF isTransient»
						«g.apiFQN» newGraphApi = new «g.apiImplFQN»();
					«ELSE»
					    final «g.entityFQN» newGraph =  new «g.entityFQN»();
					    newGraph.filename = filename;
					    newGraph.extension = "«g.fileExtension»";
					    «new GraphModelCommandExecuter(gc).setDefault('''newGraph''', g, true)»
					    newGraph.persist();
					    «g.apiFQN» newGraphApi = new «g.apiImplFQN»(newGraph, executer);
					«ENDIF»
					«IF g.containsPostCreateHook»
						«{
							val postCreateHooks = g.resolvePostCreate
							'''
								«IF !postCreateHooks.empty»
									// postMoveHooks
									«FOR anno:postCreateHooks»
										{
											«anno» ca = new «anno»();
											ca.init(«IF isTransient»null«ELSE»executer«ENDIF»);
											ca.postCreate(newGraphApi);
										}
									«ENDFOR»
								«ENDIF»
							'''
						}»
					«ENDIF»
					return newGraphApi;
			    }
				«FOR udt:g.elements.filter(UserDefinedType).filter[!isAbstract]»
					
					public «udt.apiFQN» create«udt.name.fuEscapeJava»() {
						«IF isTransient»
							return new «udt.apiImplFQN»();
						«ELSE»
							«udt.entityFQN» entity = new «udt.entityFQN»();
							entity.persist();
								«udt.apiFQN» apiEntity = new «udt.apiImplFQN»(
									entity,
							    	executer,
							    	null,
							    	null
								);
							return apiEntity;
					    «ENDIF»
					}
				«ENDFOR»
			}
		'''
	}
	
}
