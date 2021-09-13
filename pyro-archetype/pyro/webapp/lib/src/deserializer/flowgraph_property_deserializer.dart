import 'package:FlowGraphTool/src/model/core.dart' as core;
import 'package:FlowGraphTool/src/model/flowgraph.dart';

class FlowGraphPropertyDeserializer
{
  static core.IdentifiableElement deserialize(dynamic jsog,Map cache)
  {
  	if(jsog.containsKey('@ref')){
  		return cache[jsog['@ref']];
  	}
    //for each graphmodel element, no types
    if(jsog['runtimeType'] == 'info.scce.pyro.flowgraph.rest.End'){
      return End.fromJSOG(jsog,cache);
    } else if(jsog['runtimeType'] == 'info.scce.pyro.flowgraph.rest.Swimlane'){
      return Swimlane.fromJSOG(jsog,cache);
    } else if(jsog['runtimeType'] == 'info.scce.pyro.flowgraph.rest.SubFlowGraph'){
      return SubFlowGraph.fromJSOG(jsog,cache);
    } else if(jsog['runtimeType'] == 'info.scce.pyro.flowgraph.rest.Transition'){
      return Transition.fromJSOG(jsog,cache);
    } else if(jsog['runtimeType'] == 'info.scce.pyro.flowgraph.rest.Start'){
      return Start.fromJSOG(jsog,cache);
    } else if(jsog['runtimeType'] == 'info.scce.pyro.flowgraph.rest.Activity'){
      return Activity.fromJSOG(jsog,cache);
    } else if(jsog['runtimeType'] == 'info.scce.pyro.flowgraph.rest.ExternalActivity'){
      return ExternalActivity.fromJSOG(jsog,cache);
    } else if(jsog['runtimeType'] == 'info.scce.pyro.flowgraph.rest.LabeledTransition'){
      return LabeledTransition.fromJSOG(jsog,cache);
    } else if(jsog['runtimeType'] == 'info.scce.pyro.flowgraph.rest.FlowGraphDiagram'){
      return FlowGraphDiagram.fromJSOG(jsog,cache);
    }
    throw new Exception("Unknown element type: ${jsog['runtimeType']}");
  }
}
