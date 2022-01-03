package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.ModelElement
import mgl.UserDefinedType
import mgl.GraphModel

class GraphModelFactoryTransientImplementation extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)
	'''«g.apiFactoryImpl».java'''
	
	def content(GraphModel g)
	'''
		package «g.modelPackage.apiImplFQNBase»;
		import «g.apiFactoryFQN»;
		
		/**
		 * Author zweihoff
		 */
		@javax.enterprise.context.RequestScoped
		public class «g.apiFactoryImpl» implements «g.apiFactory» {
		
		
		    public static «g.apiFactory» init() {
		    	return new «g.apiFactoryImpl»();
		    }
		
		    public «g.apiFQN» create«g.name.fuEscapeJava»(String projectRelativePath, String filename)
		    {
		        return create«g.name.fuEscapeJava»();
		    }
		    
		    public «g.apiFQN» create«g.name.fuEscapeJava»() {
		    	«g.apiFQN» ce = new «g.apiImplFQN»();
		    	«IF g.containsPostCreateHook»
		    	
		        	«{
						val postCreateHooks = g.resolvePostCreate
						'''
							«IF !postCreateHooks.empty»
								// postMoveHooks
								«FOR anno:postCreateHooks»
									{
										«anno» ca = new «anno»();
										ca.init(executer);
										ca.postCreate(ce);
									}
								«ENDFOR»
							«ENDIF»
						'''
					}»
		        «ENDIF»
		       	
		        return ce;
		    }
			«FOR udt:g.elementsAndTypes.filter(UserDefinedType).filter[!isAbstract]»
				
				public «udt.apiFQN» create«udt.name.fuEscapeJava»() {
					return new «udt.apiImplFQN»();
				}
			«ENDFOR»
		}
	'''
}