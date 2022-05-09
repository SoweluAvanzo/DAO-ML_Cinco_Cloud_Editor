package de.jabc.cinco.meta.plugin.pyro.frontend

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class Main extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameMain()'''main.dart'''
	
	def contentMain()
	'''
	import 'package:angular/angular.dart';
	import 'package:angular_router/angular_router.dart';
	import 'package:«gc.projectName.escapeDart»/app_component.template.dart' as ng;
	
	import 'main.template.dart' as self;
	
	@GenerateInjector(
	  routerProviders
	)
	final InjectorFactory injector = self.injector$Injector;
	
	@GenerateInjector(
	    routerProvidersHash
	)
	final InjectorFactory injectorLocal = self.injectorLocal$Injector;
	
	
	void main() {
	  final local = const bool.fromEnvironment('local',defaultValue: true);
	  if(local) {
	    runApp(ng.AppComponentNgFactory, createInjector: injectorLocal);
	  } else {
	    runApp(ng.AppComponentNgFactory, createInjector: injector);
	  }
	}

	
	'''
}
