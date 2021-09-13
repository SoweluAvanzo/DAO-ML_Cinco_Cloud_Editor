import 'package:angular/angular.dart';
import 'dart:async';

import 'package:FlowGraphTool/src/model/core.dart';

import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraphdiagram;

// the flowgraph.FlowGraphDiagram itself
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/flowgraphdiagram_property_component.dart';

// all elements of the flowgraphdiagram
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/end_property_component.dart';
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/swimlane_property_component.dart';
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/subflowgraph_property_component.dart';
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/transition_property_component.dart';
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/start_property_component.dart';
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/activity_property_component.dart';
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/labeledtransition_property_component.dart';

@Component(
    selector: 'flowgraphdiagram',
    templateUrl: 'property_component.html',
    directives: const [
      FlowGraphDiagramPropertyComponent,
      coreDirectives
,      EndPropertyComponent,
      SwimlanePropertyComponent,
      SubFlowGraphPropertyComponent,
      TransitionPropertyComponent,
      StartPropertyComponent,
      ActivityPropertyComponent,
      LabeledTransitionPropertyComponent
    ]
)
class PropertyComponent {

  @Input()
  PyroElement currentElement;
  
  @Input()
  GraphModel currentGraphModel;
  
  final hasChangedSC = new StreamController();
  @Output() Stream get hasChanged => hasChangedSC.stream;
  
  /// checks if the given element is a flowgraph.End
  /// instance.
  bool checkEnd(PyroElement element) {
  	return element.$type()=='flowgraph.End';
  }
  /// checks if the given element is a flowgraph.Swimlane
  /// instance.
  bool checkSwimlane(PyroElement element) {
  	return element.$type()=='flowgraph.Swimlane';
  }
  /// checks if the given element is a flowgraph.SubFlowGraph
  /// instance.
  bool checkSubFlowGraph(PyroElement element) {
  	return element.$type()=='flowgraph.SubFlowGraph';
  }
  /// checks if the given element is a flowgraph.Transition
  /// instance.
  bool checkTransition(PyroElement element) {
  	return element.$type()=='flowgraph.Transition';
  }
  /// checks if the given element is a flowgraph.Start
  /// instance.
  bool checkStart(PyroElement element) {
  	return element.$type()=='flowgraph.Start';
  }
  /// checks if the given element is a flowgraph.Activity
  /// instance.
  bool checkActivity(PyroElement element) {
  	return element.$type()=='flowgraph.Activity';
  }
  /// checks if the given element is a flowgraph.LabeledTransition
  /// instance.
  bool checkLabeledTransition(PyroElement element) {
  	return element.$type()=='flowgraph.LabeledTransition';
  }
  /// checks if the given element is a flowgraph.FlowGraphDiagram
  /// instance.
  bool checkFlowGraphDiagram(PyroElement element) {
  	return element.$type()=='flowgraph.FlowGraphDiagram';
  }
}

