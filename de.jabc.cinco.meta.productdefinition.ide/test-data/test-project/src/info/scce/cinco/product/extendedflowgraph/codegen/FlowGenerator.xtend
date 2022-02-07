
package info.scce.cinco.product.extendedflowgraph.codegen

import de.jabc.cinco.meta.plugin.generator.runtime.IGenerator
import info.scce.cinco.product.extendedflowgraph.extendedflowgraph.ExtendedFlowGraph

/**
 *  Example class that generates code for a given FlowGraph model. As different
 *  feature examples might or might not be included (e.g. the external component
 *  library or swimlanes), this generator only does stupidly enumerate all
 *  nodes and prints some general information about them.
 *
 */
class FlowGenerator extends IGenerator<ExtendedFlowGraph> {
	
	override generate(ExtendedFlowGraph model) {
		if (model.getModelName().nullOrEmpty)
			throw new RuntimeException("Model's name must be set.")
		
		val fileName = "generated_" + model.getModelName() + ".test"
		val code = generateCode(model).toString
		
		createFile(fileName,code)
	}

	private def generateCode(ExtendedFlowGraph model) '''
		=== «model.modelName» ===
		
		The model contains «model.activitys.size» activities. Here's some general information about them:
		
		«FOR node : model.activitys»
			* node «node.id» of type '«node.getClass().toString()»' with «node.activitySuccessors.size» successors and «node.activityPredecessors.size» predecessors
		«ENDFOR»
	'''

}
	
