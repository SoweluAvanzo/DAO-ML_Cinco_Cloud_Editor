package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.graphs.graphmodel

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel

class PropertyComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNamePropertyComponent()'''«propertyComponentFileDart»'''
	
	def contentPropertyComponent(GraphModel g)
	'''
	import 'package:angular/angular.dart';
	import 'dart:async';
	
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
	
	import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
	
	// the «g.dartFQN» itself
	import 'package:«gc.projectName.escapeDart»/«g.propertyElementFilePath»';
	
	// all elements of the «g.name.lowEscapeDart»
	«FOR elem:g.elementsAndTypes»
		import 'package:«gc.projectName.escapeDart»/«elem.propertyElementFilePath»';
	«ENDFOR»
	
	@Component(
	    selector: '«g.name.lowEscapeDart»',
	    templateUrl: 'property_component.html',
	    directives: const [
	      «g.name.fuEscapeDart»PropertyComponent,
	      coreDirectives
	      «FOR elem:g.elementsAndTypes BEFORE "," SEPARATOR ","»
	      	«elem.name.fuEscapeDart»PropertyComponent
	      «ENDFOR»
	    ]
	)
	class PropertyComponent {
	
	  @Input()
	  PyroElement currentElement;
	  
	  @Input()
	  GraphModel currentGraphModel;
	  
	  final hasChangedSC = new StreamController();
	  @Output() Stream get hasChanged => hasChangedSC.stream;
	  
	  «FOR elem:g.elementsAndTypes.filter[!isIsAbstract] + #[g]»
	  	/// checks if the given element is a «elem.dartFQN»
	  	/// instance.
	  	bool check«elem.name.fuEscapeDart»(PyroElement element) {
	  		return element.$type()=='«elem.typeName»';
	  	}
	  «ENDFOR»
	}
	
	'''
	
	def fileNamePropertyComponentTemplate()'''«propertyComponentFileHTML»'''
	
	def contentPropertyComponentTemplate(GraphModel g)
	'''
	<«g.name.lowEscapeDart»-property
	    *ngIf="check«g.name.fuEscapeDart»(currentElement)"
	    [currentElement]="currentElement"
	    [currentGraphModel]="currentGraphModel"
	    (hasChanged)="hasChangedSC.add($event)"
	>
	</«g.name.lowEscapeDart»-property>
	«FOR elem:g.elementsAndTypes.filter[!isIsAbstract]»
	<«elem.name.lowEscapeDart»-property
	    *ngIf="check«elem.name.fuEscapeDart»(currentElement)"
	    [currentElement]="currentElement"
	    [currentGraphModel]="currentGraphModel"
	    (hasChanged)="hasChangedSC.add($event)"
	></«elem.name.lowEscapeDart»-property>
	«ENDFOR»
	'''
}
