package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.map

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class MapComponent extends Generatable {

	new(GeneratorCompound gc) {
		super(gc)
	}

	def fileNameMapComponent() '''map_component.dart'''

	def contentMapComponent() '''
		import 'package:angular/angular.dart';
		
		import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
		
		«FOR m:gc.mglModels»
			import 'package:«gc.projectName.escapeDart»/«m.modelFilePath»' as «m.name.lowEscapeDart»;
		«ENDFOR»
		
		import 'dart:js' as js;
		
		@Component(
		    selector: 'map',
		    templateUrl: 'map_component.html',
		    directives: const [coreDirectives],
		    styleUrls: const ['package:«gc.projectName.escapeDart»/src/pages/editor/editor_component.css']
		)
		class MapComponent implements OnChanges, AfterViewInit {
		
		  @Input()
		  GraphModel currentGraphModel;
		 
		  @override
		  ngOnChanges(Map<String, SimpleChange> changes) {
		    if(changes.containsKey("currentGraphModel")) {
		      var value = changes["currentGraphModel"].currentValue;
		      if(value!=null) {
		        triggerMap(value);
		      }
		    }
		  }
		  
		  @override
		  ngAfterViewInit() {
		  	triggerMap(currentGraphModel);
		  }
		   
		  void triggerMap(GraphModel g) {
		  	«FOR g : gc.graphMopdels SEPARATOR " else "
		  	»if(g is «g.dartFQN»){
		  		js.context.callMethod('create_«g.jsCall»_map',[]);
		  	}«
		  	ENDFOR»
		  }
		   
		  «FOR g : gc.graphMopdels»
		  	bool check«g.name.escapeDart»() {
		  		if(currentGraphModel==null){
		  		   return false;
		  		}
		  	  	return currentGraphModel.$type == '«g.typeName»';
		  	}
		 «ENDFOR»
		}
		
	'''

	def fileNameMapComponentTemplate() '''map_component.html'''

	def contentMapComponentTemplate() '''
		<div class="card pyro-panel d-flex flex-column h-100" *ngIf="currentGraphModel!=null">
		    <div class="card-body p-0 pyro-panel-body">
				«FOR g : gc.graphMopdels»
					<div *ngIf="check«g.name.escapeDart»()" class="mx-auto" id="paper_map_«g.name.lowEscapeDart»"></div>
				«ENDFOR»
			</div>
		</div>
	'''

}
