package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl

import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.command.GraphModelCommandExecuter
import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel
import mgl.MGLModel
import mgl.UserDefinedType
import mgl.ModelElement

class GraphModelFactoryImplementation extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)
	'''«g.apiFactoryImpl».java'''
	
	def content(GraphModel g) {
		val modelPackage = g.modelPackage as MGLModel
		'''
			package «modelPackage.apiImplFQNBase»;
			
			import «g.apiFactoryFQN»;
			import «g.apiFQN»;
			import «g.commandExecuterFQN»;
			import «modelPackage.typeRegistryFQN»;
			
			/**
			 * Author zweihoff
			 */
			@javax.enterprise.context.RequestScoped
			public class «g.apiFactoryImpl» implements «g.apiFactory» {
			
			    private «g.commandExecuter» executer;
			    
			    public void warmup(
			    	         «g.commandExecuter» executer
			    	    ) {
			    	        this.executer = executer;
			   }
			
			    public static «g.apiFactory» init() {
			    	return new «g.apiFactoryImpl»();
			    }
			
			    public «g.name.fuEscapeJava» create«g.name.fuEscapeJava»(String projectRelativePath, String filename)
			    {
			        final «g.entityFQN» newGraph =  new «g.entityFQN»();
			        newGraph.filename = filename;
			        newGraph.extension = "«g.fileExtension»";
			        «new GraphModelCommandExecuter(gc).setDefault('''newGraph''', g, true)»
			        newGraph.persist();
			        
			        «IF (g as ModelElement).hasPostCreateHook»
			        	
			        	«(g as ModelElement).postCreateHook» ca = new «(g as ModelElement).postCreateHook»();
			        	ca.init(executer);
			        	«g.apiFQN» newGraphApi = («g.apiFQN») «typeRegistryName».getDBToApi(newGraph, executer);
			        	ca.postCreate(newGraphApi);
			        «ENDIF»
			        
			        return new «g.apiImplFQN»(newGraph,executer);
			    }
			
				«FOR udt:g.elementsAndTypes.filter(UserDefinedType).filter[!isAbstract]»
					
					public «udt.apiFQN» create«udt.name.fuEscapeJava»() {
						«udt.entityFQN» entity = new «udt.entityFQN»();
						entity.persist();
							«udt.apiFQN» apiEntity = new «udt.apiImplFQN»(
								entity,
						    	executer«IF !udt.isType»,
						    	null,
						    	null
						    	«ENDIF»
					    	);
					    return apiEntity;
					}
				«ENDFOR»
			}
		'''
	}
	
}
