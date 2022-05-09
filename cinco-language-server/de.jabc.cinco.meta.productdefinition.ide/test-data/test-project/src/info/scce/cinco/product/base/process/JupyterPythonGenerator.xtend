package info.scce.cinco.product.base.process

import info.scce.cinco.product.base.process.baseprocess.BaseProcess
import info.scce.cinco.product.base.process.baseprocess.Constant
import info.scce.cinco.product.base.process.baseprocess.DataSource
import info.scce.cinco.product.base.process.baseprocess.EndSIB
import info.scce.cinco.product.base.process.baseprocess.FunctionSIB
import info.scce.cinco.product.base.process.baseprocess.ProcessSIB
import info.scce.cinco.product.base.process.baseprocess.SIB
import info.scce.cinco.product.base.process.baseprocess.Variable
import java.util.List
import java.util.LinkedList

class JupyterPythonGenerator extends ProcessGenerator {

	override generate(BaseProcess bp) {
		bp.generateProcesses
	}

	private def CharSequence generateProcesses(BaseProcess bp)
	{
		val allProcesses = bp.findDeeply(new LinkedList)
		'''
			«FOR p:allProcesses»
				«generateProcess(p)»
			«ENDFOR»
			process«bp.id.escape»()
		'''
	}

	private def Iterable<BaseProcess> findDeeply(BaseProcess bp,List<BaseProcess> cache) {
		if(cache.contains(bp)) {
			return #[]
		}
		cache.add(bp)
		val found = new LinkedList(#[bp])
		found.addAll(bp.processSIBs.map[proMod.findDeeply(cache)].flatten)
		found
	}

	private def generateProcess(BaseProcess bp)
	'''
		def process«bp.id.escape»(**kwargs):
			#imports
			«FOR i:bp.functionSIBs.groupBy[importStatement].entrySet»
				«i.key»
			«ENDFOR»
			# declare variables
			context = {
				"endsib":None
				«FOR v:bp.variables BEFORE "," SEPARATOR ","»
					"var_«v.name»":None
				«ENDFOR»
				
			}
			# write inputs to variables
			«FOR input:bp.startSIBs.get(0).outputPorts»
				«FOR suc:input.variableSuccessors»
					«suc.write» = kwargs.get('«input.name»',None)
				«ENDFOR»
			«ENDFOR»
			# sib definitions
			«FOR s:bp.SIBs»
				def sib_«s.id.escape»(context,sibs):
					«s.executeSIB»
			«ENDFOR»
			
			sibs = {
				«FOR s:bp.SIBs SEPARATOR ","»
					"sib_«s.id.escape»": sib_«s.id.escape»
				«ENDFOR»
			}
			# Execute process
			«bp.startSIBs.get(0).SIBSuccessors.get(0).callSIB»
			# return result
			«FOR e:bp.endSIBs»
				if(context["endsib"] == "«e.name»"):
					return { 
						"name":"«e.name»",
						"ports": {
							«FOR output:e.inputPorts SEPARATOR ","»
								"«output.name»":«output.dataSourcePredecessors.get(0).read»
							«ENDFOR»
						}
					}
			«ENDFOR»
	'''

	private def read(DataSource ds)'''«IF ds instanceof Constant»«ds.value»«ELSE»context["var_«ds.name»"]«ENDIF»'''
	private def write(Variable ds)'''context["var_«ds.name»"]'''

	private def dispatch callSIB(SIB s)'''sibs["sib_«s.id.escape»"](context,sibs)'''
	private def dispatch callSIB(EndSIB s){
		s.executeSIB
	}

	private def dispatch CharSequence executeSIB(ProcessSIB s)
	'''
		# execute «s.proMod.name»
		«IF !s.branchSuccessors.empty»result_«s.id.escape» = «ENDIF»process«s.proMod.id.escape»(«s.inputPorts.map['''«name»=«dataSourcePredecessors.get(0).read»'''].join(",")»)
		«FOR b:s.branchSuccessors»
			if(result_«s.id.escape»["name"] == "«b.name»"):
				«FOR output:b.outputPorts»
					«FOR target:output.variableSuccessors»
						«target.write» = result_«s.id.escape»["ports"]["«output.name»"]
					«ENDFOR»
				«ENDFOR»
				«IF !b.controlFlowTargetSuccessors.empty»
					
					«FOR successors : b.controlFlowTargetSuccessors»
						«successors.callSIB»
					«ENDFOR»
				«ENDIF»
		«ENDFOR»
		
	'''

	private def dispatch CharSequence executeSIB(FunctionSIB s)
	'''
		# execute «s.functionName»
		«IF !s.branchSuccessors.empty»result_«s.id.escape» = «ENDIF»«s.functionName»(«s.inputPorts.map['''«dataSourcePredecessors.get(0).read»'''].join(",")»)
		«IF !s.branchSuccessors.empty»
			«FOR successor : s.branchSuccessors»
				«FOR output:successor.outputPorts»
					«FOR target:output.variableSuccessors»
						«target.write» = result_«s.id.escape»
					«ENDFOR»
				«ENDFOR»
				«IF !successor.controlFlowTargetSuccessors.empty»
					
					«FOR c : successor.controlFlowTargetSuccessors»
						«c.callSIB»
					«ENDFOR»
				«ENDIF»
			«ENDFOR»
		«ENDIF»
		
	'''

	private def dispatch CharSequence executeSIB(EndSIB s)
	'''
		context["endsib"]="«s.name»"
	'''


	private def escape(String s) {
		return s.replaceAll("-","_")
	}

}