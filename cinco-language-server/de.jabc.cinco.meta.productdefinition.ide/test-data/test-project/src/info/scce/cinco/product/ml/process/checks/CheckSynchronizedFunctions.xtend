package info.scce.cinco.product.ml.process.checks

import info.scce.cinco.product.ml.process.mcam.modules.checks.MLProcessCheck
import info.scce.cinco.product.ml.process.mlprocess.MLProcess
import java.util.List
import info.scce.cinco.product.ml.process.mlprocess.InternalService
import info.scce.cinco.product.ml.process.mlprocess.ExternalService
import jupyter.Function
import jupyter.Parameter

class CheckSynchronizedFunctions extends MLProcessCheck{
	
	override check(MLProcess model) {
		checkSynchronized_InternalService(model.internalServices.toList)
		checkSynchronized_ExternalService(model.externalServices.toList)
	}
	
	def checkSynchronized_ExternalService(List<ExternalService> services) {
		for(service : services){
			if(service.fun===null) {
				addError(service, '''Function of ExternalService is undefined''')
			}
//			//check number of ports
//			var fun = service.fun as Function
//			var inputPortnumber = fun.inputs.size
//			var outputPortName = fun.output.name
//			if(!service.inputPorts.size.equals(inputPortnumber)){
//				addError(service, '''ExternalService "«fun.name»" is not synchronized: Model has «inputPortnumber» input(s), but "«fun.name»" has «service.inputPorts.size» input(s).''')	
//			}
//			//check port name 
//			for(inputPort : service.inputPorts){
//				if(!fun.inputs.contains((inputPort.parameter as Parameter).name)){
//					addError(service, '''ExternalService "«fun.name»" is not synchronized: InputPort "«(inputPort.parameter as Parameter).name»" is not a valid inputPort.''')
//				}
//			}
//			var portExists = false
//			for(outputPorts : service.outputPorts){
//				if((outputPorts.parameter as Parameter).name.equals(outputPortName)){
//					portExists = false
//				}
//			}
//			if(!portExists){
//				addError(service, '''Outputport with name "«outputPortName»" is missing.''')
//			}
			
			//check port type passt
		}
	}
	
	def checkSynchronized_InternalService(List<InternalService> services) {
		for(service : services){
			if(service.proMod===null) {
				addError(service, '''Sub model of InternalService is undefined''')
			}
		}
	}
}