package info.scce.cinco.product.ml.process.transformer

import graphmodel.Container
import info.scce.cinco.product.base.process.baseprocess.BaseProcess
import info.scce.cinco.product.ml.process.mlprocess.Closure
import info.scce.cinco.product.ml.process.mlprocess.DataService
import info.scce.cinco.product.ml.process.mlprocess.ExternalService
import info.scce.cinco.product.ml.process.mlprocess.InferredPort
import info.scce.cinco.product.ml.process.mlprocess.Inputs
import info.scce.cinco.product.ml.process.mlprocess.InternalService
import info.scce.cinco.product.ml.process.mlprocess.MLProcess
import info.scce.cinco.product.ml.process.mlprocess.OutputPort
import info.scce.cinco.product.ml.process.mlprocess.Outputs
import info.scce.cinco.product.ml.process.mlprocess.TypedPort
import java.util.HashMap
import java.util.LinkedList
import java.util.List
import java.util.Map
import info.scce.cinco.product.base.process.baseprocess.ControlFlowElement
import info.scce.cinco.product.ml.process.mlprocess.InputPort
import info.scce.cinco.product.ml.process.mlprocess.Port
import info.scce.cinco.product.ml.process.mlprocess.ConstantPort

import jupyter.Function
import info.scce.cinco.product.base.process.baseprocess.SIB
import info.scce.cinco.product.base.process.baseprocess.StartSIB
import info.scce.cinco.product.base.process.baseprocess.ControlFlowTarget
import info.scce.cinco.product.base.process.baseprocess.BaseProcessFactory
import jupyter.Parameter
import info.scce.cinco.product.ml.process.mlprocess.NumberConstant
import info.scce.cinco.product.ml.process.mlprocess.TextConstant
import info.scce.cinco.product.ml.process.mlprocess.BoolConstant
import info.scce.cinco.product.ml.process.mlprocess.FilePathConstant
import info.scce.cinco.product.ml.process.mlprocess.ListConstant
import info.scce.cinco.product.ml.process.mlprocess.TextEntry
import info.scce.cinco.product.ml.process.mlprocess.FilePathEntry
import info.scce.cinco.product.ml.process.mlprocess.NumberEntry

class BaseProcessTransformer {
	
	String baseUrl;
	
	new(String baseUrl) {
		this.baseUrl = baseUrl
	}
	
	val Map<MLProcess,BaseProcess> relationTable = new HashMap
	
	public final int VARIABLE_Y_OFF = -100
	
	public final int CONSTANT_Y_OFF = -130
	
	def BaseProcess transform(MLProcess p) {
		//collect all reachable ml process
		val allMLProcesses = p.findDeeply(new LinkedList)
		//create base process for each mlprocess
		allMLProcesses.forEach[ml|
			val bp = BaseProcessFactory.eINSTANCE.createBaseProcess("/Test1/target",p.name+"Process")
			relationTable.put(ml,bp)
		]
		//fill base process with sibs
		relationTable.entrySet.forEach[e|ml2bp(e.key,e.value)]
		
		val initialProcess = relationTable.get(p);
		initialProcess.newStartSIB(0,0).newControlFlow(initialProcess.SIBs.findFirst[incoming.empty])
		//return baseProcess for initial ml process
		return initialProcess
		
	}
	
	private def dispatch int distanceToStart(Inputs i) {
		return 0
	}
	
	private def dispatch int distanceToStart(ConstantPort i) {
		return -1
	}
	
	private def dispatch int distanceToStart(InferredPort i) {
		return i.container.distanceToStart
	}
	
	private def dispatch int distanceToStart(TypedPort i) {
		return i.container.distanceToStart
	}
	
	private def dispatch int distanceToStart(DataService i) {
		val result = i.inputPorts.filter[!incoming.isEmpty].filter[predecessors.get(0) instanceof TypedPort]
		if(result.empty) {
			return 1
		}
		return 1+ result.map[predecessors.get(0).distanceToStart].max
	}
	
	private def dispatch int distanceToStart(graphmodel.GraphModel i) {
		return 0;
	}
	
	private def dispatch int distanceToStart(Outputs i) {
		return 1+i.endInputPorts.filter[!incoming.isEmpty].filter[predecessors.get(0) instanceof TypedPort].map[predecessors.get(0).distanceToStart].max
	}
	
	private def ml2bp(MLProcess ml,BaseProcess bp) {
		//find end
		var List<Container> container = new LinkedList
		val dataFlowEntries = ml.allContainers.filter(Closure) + ml.allContainers.filter(DataService).filter[isConnected]
		val distanceCounted = dataFlowEntries.groupBy[
			dfe|dfe.distanceToStart
		]
		val sorted = distanceCounted.entrySet.sortBy[key]
		sorted.forEach[n|
			println('''«n.key»:«n.value.map['''«it»'''].join(",")»''')
		]
		val Map<Container,ControlFlowElement> cache = new HashMap
		//create SIBs, variables and data flow
		sorted.forEach[value.forEach[transformPart(bp,cache)]]
		//create control flow
		val sortedArr = sorted.toList
		for(var i = 0;i<sortedArr.length;i++) {
			if(i < sortedArr.length -1) {
				//connect all in i from 0 to a
				var a = 0;
				for(;a<sortedArr.get(i).value.size-1;a++) {
					println('''connect: «sortedArr.get(i).value.get(a)» to «sortedArr.get(i).value.get(a+1)»''')
					sortedArr.get(i).value.get(a).connectTo(sortedArr.get(i).value.get(a+1),cache)

				}
				//connect last of i to first of i+1
				println('''connect: «sortedArr.get(i).value.get(a)» to «sortedArr.get(i+1).value.get(0)»''')
				sortedArr.get(i).value.get(a).connectTo(sortedArr.get(i+1).value.get(0),cache)
			}
		}
		
		
		if(ml.outputss.empty) {
			container.addAll(ml.outputss)
		} else {
			container.addAll(ml.dataServices.filter[outputPorts.empty])
		}


		/*println('''---> «ml.name»''')
		bp.startSIBs.forEach[n|
			println('''STARTSIB: -> «n.getSIBSuccessors.map['''«id»'''].join»''')
		]
		bp.functionSIBs.forEach[n|
			println('''(«»)FUNCTIONSIB: «n.functionName» «n.id»
			(«n.branchSuccessors.map['''«name»->«getSIBSuccessors.map['''«id»'''].join»'''].join»)''')
		]
		bp.processSIBs.forEach[n|
			println('''PROCESSSIB: «n.proMod.name» «n.id»
			(«n.branchSuccessors.map['''«name»->«getSIBSuccessors.map['''«id»'''].join»'''].join»)''')
		]
		bp.endSIBs.forEach[n|
			println('''ENDSIB: «n.name» «n.id»''')
		]
		println('''<--- «ml.name»''')*/
		
	}
	
	def connectTo(Container mlSource,Container mlTarget,Map<Container,ControlFlowElement> cache) {
		val source = cache.get(mlSource)
		val target = cache.get(mlTarget)
		if(source instanceof StartSIB) {
			source.newControlFlow(target as ControlFlowTarget)
		}
		else if(source instanceof SIB) {
			source.branchSuccessors.get(0).newControlFlow(target as ControlFlowTarget)
		} else {
			throw new IllegalStateException('''Unknown sourde «source»''')
		}
	}
	
	private def createVariable(String name,String type,BaseProcess bp) {
		val v = bp.variables.findFirst[it.name.equals(name)]
		if(v === null) {
			val vn =  bp.newVariable(bp.variables.size*(150),VARIABLE_Y_OFF)
			vn.name = name
			vn.typeName = type
			return vn
		}
		v
	}
	
	private def createConstant(ConstantPort port,BaseProcess bp) {
		val vn =  bp.newConstant(bp.constants.size*(150),CONSTANT_Y_OFF)
		if(port instanceof NumberConstant) {
			vn.value = port.value
		}
		if(port instanceof TextConstant) {
			vn.value = '''"«port.value»"'''
		}
		if(port instanceof BoolConstant) {
			vn.value = if(port.value)"True"else"False"
		}
		if(port instanceof FilePathConstant) {
			if(port.path.startsWith("http")) {
				vn.value = '''"«port.path»"'''
			} else {
				vn.value = '''"«baseUrl»«port.path»"'''				
			}
		}
		if(port instanceof ListConstant) {
			vn.value = '''[«port.entries.map[value].join(",")»]'''
		}
		
		return vn
	}

	private def dispatch getValue(TextEntry e) {
		return '''"«e.value»"'''
	}

	private def dispatch getValue(NumberEntry e) {
		return e.value.toString
	}

	private def dispatch getValue(FilePathEntry e) {
		return '''"«baseUrl»«e.value»"'''
	}

	private def dispatch getValue(BoolConstant e) {
		if(e.value) {
			return "True"
		}
		return "False"
	}
	
	
	private def dispatch transformPart(Outputs entity,BaseProcess bp,Map<Container,ControlFlowElement> cache) {
		val sib = bp.newEndSIB(entity.x,entity.y)
		sib.name = "End"
		entity.endInputPorts.filter[!incoming.empty].forEach[port|
			val newPort = sib.newInputPort(0,0)
			newPort.name = port.name
			val outputPort = port.predecessors.filter(OutputPort).get(0)
			val v = (outputPort.id).createVariable((outputPort.parameter as Parameter).typeName,bp)
			v.newDataFlow(newPort)
		]
		cache.put(entity,sib)
		
	}
	
	private def dispatch transformPart(Inputs entity,BaseProcess bp,Map<Container,ControlFlowElement> cache) {
		val sib = bp.newStartSIB(entity.x,entity.y)
		entity.startOutputPorts.filter[!outgoing.empty].forEach[port|
			val newPort = sib.newOutputPort(0,0)
			newPort.name = port.name
			val inputPort = port.successors.filter(InputPort).get(0)
			val v = (port.id).createVariable((inputPort.parameter as Parameter).typeName,bp)
			newPort.newDataFlow(v)
		]
		cache.put(entity,sib)
	}
	
	private def dispatch transformPart(ExternalService entity,BaseProcess bp,Map<Container,ControlFlowElement> cache) {
		val sib = bp.newFunctionSIB(entity.x,entity.y)
		sib.functionName = (entity.fun as Function).name
		sib.importStatement = (entity.fun as Function).import
		entity.transformSIB(sib,bp,cache)
	}
	
	private def dispatch transformPart(InternalService entity,BaseProcess bp,Map<Container,ControlFlowElement> cache) {
		val sib = bp.newProcessSIB(relationTable.get(entity.proMod),entity.x,entity.y)
		entity.transformSIB(sib,bp,cache)
	}
	
	private def transformSIB(DataService entity,SIB sib,BaseProcess bp,Map<Container,ControlFlowElement> cache) {
		//inputs
		entity.inputPorts.filter[!incoming.empty].forEach[port|
			val newPort = sib.newInputPort(0,0)
			newPort.name = (port.parameter as Parameter).name
			newPort.typeName = (port.parameter as Parameter).typeName
			val source = port.predecessors.get(0)
			if(source instanceof ConstantPort) {
				val v = source.createConstant(bp)
				v.newDataFlow(newPort)
			}
			if(source instanceof Port) {
				val v = (source.id).createVariable((port.parameter as Parameter).typeName,bp)
				v.newDataFlow(newPort)				
			}
		]
		
		//branch
		if(!entity.outputPorts.isEmpty) {

			val branch = bp.newBranch(entity.x,entity.y+entity.height + 10)
			branch.name = "End"
			sib.newBranchConnector(branch)
			entity.outputPorts.filter[!outgoing.empty].forEach[port|
				val newPort = branch.newOutputPort(0,0)
				newPort.name = (port.parameter as Parameter).name
				newPort.typeName = (port.parameter as Parameter).typeName
				val v = (port.id).createVariable((port.parameter as Parameter).typeName,bp)
				newPort.newDataFlow(v)
			]
		}
		//outputs
		cache.put(entity,sib)
	}

	private def isConnected(DataService service) {
		! (service.getInputPorts.exists[incoming.empty] || service.getOutputPorts.exists[outgoing.empty] )
	}
	
	
	private def Iterable<MLProcess> findDeeply(MLProcess bp,List<MLProcess> cache) {
		if(cache.contains(bp)) {
			return #[]
		}
		cache.add(bp)
		val found = new LinkedList(#[bp])
		found.addAll(bp.internalServices.filter[isConnected].map[proMod.findDeeply(cache)].flatten)
		found
	}
}