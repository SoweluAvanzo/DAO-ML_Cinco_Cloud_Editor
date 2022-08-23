package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.cli

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.Edge
import mgl.GraphModel

class OutgoingCheck extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.fuEscapeJava»OutgoingCheck.java'''
	
	
	def content(GraphModel g)
	{
	val nodes = g.nodes.filter[outgoingEdgeConnections.exists[lowerBound >0]]
	'''
	package «g.MGLModel.package».mcam.modules.checks;
	
	
	import «g.apiFQN»;
	
	
	public class «g.name.fuEscapeJava»OutgoingCheck extends «g.name.fuEscapeJava»Check {
	
		public void check(«g.name.fuEscapeJava» g) {
			//check outgoing
			«IF !nodes.empty»
			g.getAllNodes().forEach((n)->{
				«FOR n:nodes»
				if(n instanceof «n.apiFQN») {
					«n.apiFQN» node = («n.apiFQN») n;
					
					«FOR group:n.outgoingEdgeConnections.filter[lowerBound>0]»
						{
							//check if type can be contained in group
							int amount = 0;
							String errorPrefix = «group.lowerBound» != «group.upperBound» ? "at least " : "";
							«IF group.connectingEdges.map[subTypesAndType(it.name,g)].flatten.nullOrEmpty»
								if(node.getOutgoing().size()<«group.lowerBound») {
									addError(n, errorPrefix + "«group.lowerBound» outgoing required");
								}
							«ELSE»
								«FOR containableType:group.connectingEdges.map[subTypesAndType(it.name,g)].flatten.filter(Edge).filter[!isIsAbstract]»
								 	amount += node.getOutgoing(«containableType.apiFQN».class).stream()
								 		.filter(c ->
								 			c.getClass().getName().equals(«containableType.apiImplFQN».class.getName())
								 		).count();
								«ENDFOR»
								if(amount < «group.lowerBound»){
									addError(n, errorPrefix + "«group.lowerBound» of [«group.connectingEdges.map[name].join(",")»] outgoing required");
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
