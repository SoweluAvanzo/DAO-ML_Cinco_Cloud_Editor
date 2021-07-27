package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.cli

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.NodeContainer
import mgl.GraphModel

class ContainmentCheck extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.fuEscapeJava»ContainmentCheck.java'''
	
	
	def content(GraphModel g)
	{
	val nodes = g.nodesTopologically.filter(NodeContainer).filter[containableElements.exists[lowerBound>0]]
	'''
	package «g.MGLModel.package».mcam.modules.checks;
	
	
	import «g.apiFQN».«g.name.fuEscapeJava»;
	
	
	public class «g.name.fuEscapeJava»ContainmentCheck extends «g.name.fuEscapeJava»Check {
	
		public void check(«g.name.fuEscapeJava» g) {
			//check lower bounds
			«IF !g.containableElements.nullOrEmpty»
				«FOR con:g.containableElements.filter[lowerBound>0]»
				{
					int amount = 0;
					«IF con.types.nullOrEmpty»
						if(g.getModelElements().size()<«con.lowerBound») {
							addError(g,"at least «con.lowerBound» nodes required");
						}
					«ELSE»
						«FOR containableType:con.types»
						 	amount += g.getModelElements(«g.apiFQN».«containableType.name.fuEscapeJava».class).stream()«IF !containableType.isAbstract».filter(c->c.getClass().getName().equals(«g.apiFQN».impl.«containableType.name.fuEscapeJava»Impl.class.getName()))«ENDIF».count();
						«ENDFOR»
						if(amount < «con.lowerBound»){
							addError(g,"at least «con.lowerBound» of [«con.getGroupContainables(g.MGLModel).toSet.map[name].join(",")»] required");
						}
					«ENDIF»
				}
				«ENDFOR»
			«ENDIF»
			«IF !nodes.empty»
			g.getAllNodes().forEach((n)->{
				«FOR n:nodes»
				if(n instanceof «g.apiFQN».«n.name.fuEscapeJava») {
					«g.apiFQN».«n.name.fuEscapeJava» container = («g.apiFQN».«n.name.fuEscapeJava»)n;
					
					«FOR group:n.containableElements.filter[lowerBound>0]»
						{
							//check if type can be contained in group
							int amount = 0;
							«IF group.types.nullOrEmpty»
							if(container.getModelElements().size()<«group.lowerBound») {
								addError(n,"at least «group.lowerBound» nodes required");
							}
							«ELSE»
								«FOR containableType:group.types»
								 	amount += container.getModelElements(«g.apiFQN».«containableType.name.fuEscapeJava».class).stream().filter(c->c.getClass().getName().equals(«g.apiFQN».impl.«containableType.name.fuEscapeJava»Impl.class.getName())).count();
								«ENDFOR»
								if(amount < «group.lowerBound»){
									addError(n,"at least «group.lowerBound» of [«group.getGroupContainables(g.MGLModel).toSet.map[name].join(",")»] required");
								}
							«ENDIF»
						}
					«ENDFOR»
						
				}
				«ENDFOR»
			});
			«ENDIF»
		}
	
	}

	
	'''
	}
	
}
