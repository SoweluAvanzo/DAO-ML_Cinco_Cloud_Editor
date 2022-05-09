package de.jabc.cinco.meta.plugin.pyro.frontend

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class Pubspec extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNamePubspec()'''pubspec.yaml'''
	
	def contentPubspec()
	'''
	name: «gc.projectName.escapeDart»
	description: A Pyro WebIME
	version: 0.0.1
	environment:
	 sdk: ^2.4.1
	dependencies:
	 angular: ^5.1.0
	 angular_forms: ^2.1.0
	 angular_router: ^2.0.0-alpha+20
	 ng_bootstrap: ^1.1.1
	 dnd: ^1.4.2
	dev_dependencies:
	 protobuf: 0.13.15
	 build_runner: 1.6.9
	 build_web_compilers: 2.3.0
	'''
	
}
