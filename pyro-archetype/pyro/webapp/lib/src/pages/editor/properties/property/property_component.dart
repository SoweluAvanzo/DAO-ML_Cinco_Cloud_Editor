import 'package:angular/angular.dart';
import 'dart:async';

import 'package:FlowGraphTool/src/model/core.dart';
import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/flowgraphdiagram/property_component.dart' as flowgraphdiagramProperty;

@Component(
    selector: 'property',
    templateUrl: 'property_component.html',
    directives: const [
	    coreDirectives
,	    flowgraphdiagramProperty.PropertyComponent
    ]
)
class PropertyComponent {
	@Input()
	PyroElement currentElement;
	
	@Input()
	GraphModel currentGraphModel;
	
	final hasChangedSC = new StreamController();
	@Output() Stream get hasChanged => hasChangedSC.stream;
	 
	/// checks if the given element belongs to
	/// the flowgraph.FlowGraphDiagram
	bool checkFlowGraphDiagram(GraphModel element)
	{
	  return element.$type()=='flowgraph.FlowGraphDiagram';
	}

}
