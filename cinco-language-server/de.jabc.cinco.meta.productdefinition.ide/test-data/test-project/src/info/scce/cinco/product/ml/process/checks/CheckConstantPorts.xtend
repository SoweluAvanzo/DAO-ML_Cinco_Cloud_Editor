package info.scce.cinco.product.ml.process.checks

import info.scce.cinco.product.ml.process.mcam.modules.checks.MLProcessCheck
import info.scce.cinco.product.ml.process.mlprocess.MLProcess
import info.scce.cinco.product.ml.process.mlprocess.TextConstant
import info.scce.cinco.product.ml.process.mlprocess.NumberConstant
import info.scce.cinco.product.ml.process.mlprocess.FilePathConstant
import info.scce.cinco.product.ml.process.mlprocess.BoolConstant
import info.scce.cinco.product.ml.process.mlprocess.ListConstant

class CheckConstantPorts extends MLProcessCheck{
	
	/**
	 * Checks if the value of a ConstantPort is set and non-empty.
	 */
	override check(MLProcess model) {
		model.constantPorts.forEach[c]
	}
	
	def dispatch c(TextConstant t){
		if(t.value.isNullOrEmpty){
			addError(t, '''Value should not be empty.''')
		}
	}
	
	def dispatch c(FilePathConstant t){
		if(t.path.isNullOrEmpty){
			addError(t, '''Value should not be empty.''')
		}
	}

	def dispatch c(NumberConstant t){
		if(t.value.isNullOrEmpty){
			addError(t, '''Value should not be empty.''')
		}
	}

	def dispatch c(BoolConstant t){
		
	}

	def dispatch c(ListConstant t){

	}
}