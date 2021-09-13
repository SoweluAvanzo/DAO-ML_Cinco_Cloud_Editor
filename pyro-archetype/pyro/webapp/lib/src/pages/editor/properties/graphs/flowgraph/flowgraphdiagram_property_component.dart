import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'dart:async';

import 'package:FlowGraphTool/src/model/core.dart' as core;
import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;

@Component(
	selector: 'flowgraphdiagram-property',
	templateUrl: 'flowgraphdiagram_property_component.html',
	directives: const [coreDirectives,formDirectives]
)
class FlowGraphDiagramPropertyComponent {

  @Input()
  flowgraph.FlowGraphDiagram currentElement;
  
  @Input()
  core.GraphModel currentGraphModel;

  final hasChangedSC = new StreamController();
  @Output() Stream get hasChanged => hasChangedSC.stream;
  
  
  FlowGraphDiagramPropertyComponent() {
  }
 
  
  void valueChanged(dynamic e) {
  	hasChangedSC.add(currentElement);
  }
  
  //get for enumeration literals
  
  // for each primitive list attribute
  
	

	
	
	int trackPrimitiveValue(int index, dynamic e)
	{
		return index;
	}
	
	void updatemodelName(v) {
		currentElement.modelName = v;
	}
}
