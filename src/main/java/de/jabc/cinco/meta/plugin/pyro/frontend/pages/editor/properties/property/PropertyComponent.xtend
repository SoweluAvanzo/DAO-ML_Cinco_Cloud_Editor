package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.property

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class PropertyComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNamePropertyComponent()'''property_component.dart'''
	
	def contentPropertyComponent() {
		'''
		import 'package:angular/angular.dart';
		import 'dart:async';
		
		import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
		«FOR m:gc.mglModels»
			import 'package:«gc.projectName.escapeDart»/«m.modelFilePath»' as «m.name.lowEscapeDart»;
		«ENDFOR»
		«FOR g:gc.discreteGraphModels»
			import 'package:«gc.projectName.escapeDart»/«g.propertyFilePath»' as «g.name.lowEscapeDart»Property;
		«ENDFOR»
		
		@Component(
		    selector: 'property',
		    templateUrl: 'property_component.html',
		    directives: const [
			    coreDirectives«
			    FOR g:gc.discreteGraphModels BEFORE ",\n" SEPARATOR ","»
			    	«g.name.lowEscapeDart»Property.PropertyComponent
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
			 
			«FOR g:gc.discreteGraphModels»
			  /// checks if the given element belongs to
			  /// the «g.dartFQN»
			  bool check«g.name.fuEscapeDart»(GraphModel element)
			  {
			    return element.$type()=='«g.typeName»';
			  }
			«ENDFOR»
		
		}
		'''
	}
	
	def fileNamePropertyComponentTemplate()'''property_component.html'''
	
	def contentPropertyComponentTemplate() // TODO:SAMI: this is error-prone, if multiple packages have same named graphModel
	'''
		«FOR g:gc.discreteGraphModels»
			<«g.name.lowEscapeDart»
			    *ngIf="check«g.name.fuEscapeDart»(currentGraphModel)"
			    [currentElement]="currentElement"
			    [currentGraphModel]="currentGraphModel"
			    (hasChanged)="hasChangedSC.add($event)"
			></«g.name.lowEscapeDart»>
		«ENDFOR»
	'''
}
