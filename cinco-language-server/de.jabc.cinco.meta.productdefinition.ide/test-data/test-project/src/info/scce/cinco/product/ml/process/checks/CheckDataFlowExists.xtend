package info.scce.cinco.product.ml.process.checks

import info.scce.cinco.product.ml.process.mcam.modules.checks.MLProcessCheck
import info.scce.cinco.product.ml.process.mlprocess.ConstantPort
import info.scce.cinco.product.ml.process.mlprocess.ExternalService
import info.scce.cinco.product.ml.process.mlprocess.Inputs
import info.scce.cinco.product.ml.process.mlprocess.InternalService
import info.scce.cinco.product.ml.process.mlprocess.MLProcess
import info.scce.cinco.product.ml.process.mlprocess.Outputs
import java.util.List
import info.scce.cinco.product.ml.process.mlprocess.TextConstant
import info.scce.cinco.product.ml.process.mlprocess.NumberConstant
import info.scce.cinco.product.ml.process.mlprocess.BoolConstant
import info.scce.cinco.product.ml.process.mlprocess.FilePathConstant
import info.scce.cinco.product.ml.process.mlprocess.ListConstant

class CheckDataFlowExists extends MLProcessCheck {

	/**
	 * Checks for each node if data flow exists.
	 */
	override check(MLProcess model) {
		checkInternalServices(model.internalServices.toList)
		checkExternalServices(model.externalServices.toList)
		checkConstants(model.constantPorts.toList)
		checkOutputs(model.outputss.toList)
		checkInputs(model.inputss.toList)
	}

	/**
	 * Checks if all ports of "Outputs" have data flow/ incoming edge.
	 */
	def checkOutputs(List<Outputs> outputs) {
		outputs.map[endInputPorts].flatten.filter[incoming.isNullOrEmpty].forEach[
			addError('''EndInputPort "«name»" of "Outputs" needs an incoming edge.''')
		]
	}

	/**
	 * checks if all ports of "Inputs" have data flow/outgoing edge.
	 */
	def checkInputs(List<Inputs> inputs) {
		inputs.map[startOutputPorts].flatten.filter[outgoing.isNullOrEmpty].forEach[
			addError('''StartOutputPort "«name»" of "Inputs" needs an outgoing edge.''')
		]

	}

	/**
	 * Checks if all ports of an internal service has data flow.
	 */
	def checkInternalServices(List<InternalService> services) {
		for (service : services.filter[proMod !== null]) {
			for (outputPort : service.outputPorts.filter[outgoing.isEmpty && parameter!==null]) {
					addError(outputPort, '''Outputport "«outputPort.parameter.name»" of "«service.proMod.name»" needs an outgoing edge.''')
			}
			for (inputPort : service.inputPorts.filter[incoming.isEmpty && parameter!==null]) {
					addError(inputPort, '''InputPort "«inputPort.parameter.name»" of "«service.proMod.name»" needs an incoming edge.''')
			}
		}
	}


	/**
	 * Checks if all ports of an external service has data flow.
	 */
	def checkExternalServices(List<ExternalService> services) {
		for (service : services.filter[fun !== null]) {
			for (outputPort : service.outputPorts.filter[outgoing.isEmpty && parameter!==null]) {
					addError(outputPort, '''Outputport "«outputPort.parameter.name»" of "«service.fun.name»" needs an outgoing edge.''')
			}
			for (inputPort : service.inputPorts.filter[incoming.isEmpty && parameter!==null]) {
					addError(inputPort, '''InputPort "«inputPort.parameter.name»" of "«service.fun.name»" needs an incoming edge.''')
			}

		}
	}

	/**
	 * Checks if ConstantPort has dataflow.
	 */
	def checkConstants(List<ConstantPort> ports) {
		for(port : ports){
			if(port.outgoingInferredStartDataFlows.isNullOrEmpty){
				addWarning(port, '''ConstantPort "«port.value»" without any outgoing edges is useless.''')
			}
		}
	}

	def dispatch String getValue(TextConstant c) {
		return c.value
	}
	def dispatch String getValue(NumberConstant c) {
		return c.value
	}
	def dispatch String getValue(BoolConstant c) {
		return c.value.toString
	}
	def dispatch String getValue(FilePathConstant c) {
		return c.path
	}

	def dispatch String getValue(ListConstant c) {
		return c.entries.length.toString
	}
}