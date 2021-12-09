package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.cli

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.Edge
import mgl.GraphModel

class IncomingCheck extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.fuEscapeJava»IncomingCheck.java'''
	
	
	def content(GraphModel g)
	{
	val nodes = g.nodes.filter[incomingEdgeConnections.exists[lowerBound >0]]
	'''
	package «g.MGLModel.package».mcam.modules.checks;
	
	
	import «g.apiFQN»;
	
	
	public class «g.name.fuEscapeJava»IncomingCheck extends «g.name.fuEscapeJava»Check {
	
		public void check(«g.name.fuEscapeJava» g) {
			//check incoming
			«IF !nodes.empty»
			g.getAllNodes().forEach((n)->{
				«FOR n:nodes»
				if(n instanceof «n.apiFQN») {
					«n.apiFQN» node = («n.apiFQNWithoutName».impl.«n.name.fuEscapeJava»Impl)n;
					
					«FOR group:n.incomingEdgeConnections.filter[lowerBound>0]»
						{
							//check if type can be contained in group
							int amount = 0;
							«IF group.connectingEdges.map[subTypesAndType(it.name,g)].flatten.nullOrEmpty»
							if(node.getIncoming().size()<«group.lowerBound») {
								addError(n,"at least «group.lowerBound» incoming required");
							}
							«ELSE»
								«FOR containableType:group.connectingEdges.map[subTypesAndType(it.name,g)].flatten.filter(Edge).filter[!isIsAbstract]»
								 	amount += node.getIncoming(«containableType.apiFQN».class).stream().filter(c->c.getClass().getName().equals(«containableType.apiFQNWithoutName».impl.«containableType.name.fuEscapeJava»Impl.class.getName())).count();
								«ENDFOR»
								if(amount < «group.lowerBound»){
									addError(n,"at least «group.lowerBound» of [«group.connectingEdges.map[name].join(",")»] incoming required");
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
