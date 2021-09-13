import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'dart:async';

import 'package:FlowGraphTool/src/model/core.dart' as core;
import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;

@Component(
	selector: 'activity-property',
	templateUrl: 'activity_property_component.html',
	directives: const [coreDirectives,formDirectives]
)
class ActivityPropertyComponent {

  @Input()
  flowgraph.Activity currentElement;
  
  @Input()
  core.GraphModel currentGraphModel;

  final hasChangedSC = new StreamController();
  @Output() Stream get hasChanged => hasChangedSC.stream;
  
  
  ActivityPropertyComponent() {
  }
 
  
  void valueChanged(dynamic e) {
  	hasChangedSC.add(currentElement);
  	currentElement.$isDirty = false;
  }
  
  //get for enumeration literals
  
  // for each primitive list attribute
  
	

	
	
	int trackPrimitiveValue(int index, dynamic e)
	{
		return index;
	}
	
	void updatename(v) {
		currentElement.name = v;
		currentElement.$isDirty = true;
	}
	
	void updatedescription(v) {
		currentElement.description = v;
		currentElement.$isDirty = true;
	}
}
