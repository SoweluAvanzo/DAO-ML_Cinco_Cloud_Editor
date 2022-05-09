package info.scce.cinco.product.ml.process.checks

import info.scce.cinco.product.ml.process.mcam.modules.checks.MLProcessCheck
import info.scce.cinco.product.ml.process.mlprocess.DataService
import info.scce.cinco.product.ml.process.mlprocess.ExternalService
import info.scce.cinco.product.ml.process.mlprocess.InputPort
import info.scce.cinco.product.ml.process.mlprocess.InternalService
import info.scce.cinco.product.ml.process.mlprocess.MLProcess
import java.util.List

class CheckDataFlowTypes extends MLProcessCheck {

	override check(MLProcess model) {
		checkDataFlowTypes_ExternalService(model.externalServices.toList)
		checkDataFlowTypes_InternalService(model.internalServices.toList)
	}


	/**
	 * Checks for external services if the type of an output port matches the type of the connected input port.
	 */
	def checkDataFlowTypes_ExternalService(List<ExternalService> services) {

		for(service : services.filter[fun!==null]){
			for(outputPort : service.outputPorts.filter[parameter!==null]){
				var outputPortType = outputPort.parameter.typeName
				for(outgoingDataFlow : outputPort.outgoingDataFlows){
					var inputPort = outgoingDataFlow.targetElement as InputPort
					if(inputPort.parameter !== null) {
						var inputPortType = inputPort.parameter.typeName
						if(!outputPortType.equals(inputPortType)){
							addError(outputPort,'''Type of port "«outputPort.parameter.name»" in function "«service.fun.name»" does not match type of port "«inputPort.parameter.name»" in function "«inputPort.container.name»".''')
						}
					}
				}
			}
		}
	}

	/**
	 * Checks for internal services if the type of an output port matches the type of the connected input port.
	 */
	def checkDataFlowTypes_InternalService(List<InternalService> services) {
		for(service : services.filter[proMod!==null]){
			for(outputPort : service.outputPorts){
				var outputPortType = outputPort.parameter.typeName
				for(outgoingDataFlow : outputPort.outgoingDataFlows){
					var inputPort = outgoingDataFlow.targetElement as InputPort
					var inputPortType = inputPort.parameter.typeName
					if(!outputPortType.equals(inputPortType)){
						addError(outputPort,'''Type of port "«outputPort.parameter.name»" in function "«service.proMod.name»" does not match type of port "«inputPort.parameter.name»" in function "«inputPort.container.name»".''')
					}
				}
			}
		}
	}

	/**
	 * Help method to determine the name of a port's container
	 */
	private def String name(DataService service){
		if(service instanceof ExternalService && (service as ExternalService).fun !== null){
			return (service as ExternalService).fun.name
		}else if(service instanceof InternalService && (service as InternalService).proMod !== null){
			return (service as InternalService).proMod.name
		}
	}
}