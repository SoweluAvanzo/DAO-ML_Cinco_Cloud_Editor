package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.UserDefinedType
import mgl.GraphModel

class GraphModelFactoryInterface extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)
	'''«g.name.toCamelCase.fuEscapeJava»Factory.java'''
	
	def content(GraphModel g, boolean isTransient)
	'''
		package «g.modelPackage.apiFQNBase»;
		
		/**
		 * Author zweihoff
		 */
		public interface «g.apiFactory» {
			public «g.apiFactory» eINSTANCE = «g.apiFactoryImplFQN».init();
			public «g.name.fuEscapeJava» create«g.name.fuEscapeJava»(String projectRelativePath, String filename);
			«IF !isTransient»
				public void warmup(
					«g.commandExecuterFQN» executer
				);
			«ENDIF»
			«FOR udt:g.elementsAndTypes.filter(UserDefinedType).filter[!isAbstract]»
				«udt.apiFQN» create«udt.name.fuEscapeJava»();
			«ENDFOR»
		}
	'''
}
