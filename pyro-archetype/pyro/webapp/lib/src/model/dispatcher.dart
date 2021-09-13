import 'core.dart' as core;

import 'flowgraph.dart' as impl_flowgraph;
import 'externallibrary.dart' as impl_externallibrary;

class GraphModelDispatcher {
	
	static core.PyroElement dispatchElement(Map<String, dynamic> jsog,Map cache) {
		if(jsog["runtimeType"]=='info.scce.pyro.flowgraph.rest.End'){
			return impl_flowgraph.End.fromJSOG(jsog,cache);
		} else if(jsog["runtimeType"]=='info.scce.pyro.flowgraph.rest.Swimlane'){
			return impl_flowgraph.Swimlane.fromJSOG(jsog,cache);
		} else if(jsog["runtimeType"]=='info.scce.pyro.flowgraph.rest.SubFlowGraph'){
			return impl_flowgraph.SubFlowGraph.fromJSOG(jsog,cache);
		} else if(jsog["runtimeType"]=='info.scce.pyro.flowgraph.rest.Transition'){
			return impl_flowgraph.Transition.fromJSOG(jsog,cache);
		} else if(jsog["runtimeType"]=='info.scce.pyro.flowgraph.rest.Start'){
			return impl_flowgraph.Start.fromJSOG(jsog,cache);
		} else if(jsog["runtimeType"]=='info.scce.pyro.flowgraph.rest.Activity'){
			return impl_flowgraph.Activity.fromJSOG(jsog,cache);
		} else if(jsog["runtimeType"]=='info.scce.pyro.flowgraph.rest.ExternalActivity'){
			return impl_flowgraph.ExternalActivity.fromJSOG(jsog,cache);
		} else if(jsog["runtimeType"]=='info.scce.pyro.flowgraph.rest.LabeledTransition'){
			return impl_flowgraph.LabeledTransition.fromJSOG(jsog,cache);
		} else if(jsog["runtimeType"]=='info.scce.pyro.flowgraph.rest.FlowGraphDiagram'){
			return impl_flowgraph.FlowGraphDiagram.fromJSOG(jsog,cache);
		}
		return dispatchEcoreElement(cache,jsog);
	}
	
	static core.PyroElement dispatchEcoreElement(Map cache,dynamic jsog) {
		if(jsog["__type"]=='externallibrary.ExternalActivityLibrary'){
			return impl_externallibrary.ExternalActivityLibrary.fromJSOG(jsog,cache);
		} else if(jsog["__type"]=='externallibrary.ExternalActivity'){
			return impl_externallibrary.ExternalActivity.fromJSOG(jsog,cache);
		}
		throw new Exception("Unkown element ${jsog['runtimeType']}");
	}
  	
	static core.PyroModelFile dispatchEcorePackage(Map cache,dynamic jsog) {
  		if(jsog["__type"]=='ExternalLibrary'
  			|| jsog["__type"]=='externallibrary.ExternalLibrary'
  		){
  			return impl_externallibrary.ExternalLibrary.fromJSOG(jsog,cache);
  		}
		throw new Exception("Unkown dispatching type ${jsog["__type"]}");
	}
	
	static core.PyroModelFile dispatch(Map cache,dynamic jsog){
		if(jsog["__type"]=='flowgraph.FlowGraphDiagram'){
		  return impl_flowgraph.FlowGraphDiagram.fromJSOG(jsog,cache);
		}
	    return dispatchEcorePackage(cache,jsog);
	}
	
	static core.ModelElementContainer dispatchFlowGraphModelElementContainer(
	  	Map cache,dynamic jsog
	  ){
	  	if(jsog["__type"]=='flowgraph.Swimlane'){
	  		return impl_flowgraph.Swimlane.fromJSOG(jsog,cache);
	  	}
		
	  	throw new Exception("Unkown modelelement type ${jsog["__type"]}");
	}
	
	static core.ModelElement dispatchFlowGraphModelElement(Map cache,dynamic jsog){
		if(jsog["__type"]=='flowgraph.End'){
			return impl_flowgraph.End.fromJSOG(jsog,cache);
		} else if(jsog["__type"]=='flowgraph.Swimlane'){
			return impl_flowgraph.Swimlane.fromJSOG(jsog,cache);
		} else if(jsog["__type"]=='flowgraph.SubFlowGraph'){
			return impl_flowgraph.SubFlowGraph.fromJSOG(jsog,cache);
		} else if(jsog["__type"]=='flowgraph.Transition'){
			return impl_flowgraph.Transition.fromJSOG(jsog,cache);
		} else if(jsog["__type"]=='flowgraph.Start'){
			return impl_flowgraph.Start.fromJSOG(jsog,cache);
		} else if(jsog["__type"]=='flowgraph.Activity'){
			return impl_flowgraph.Activity.fromJSOG(jsog,cache);
		} else if(jsog["__type"]=='flowgraph.ExternalActivity'){
			return impl_flowgraph.ExternalActivity.fromJSOG(jsog,cache);
		} else if(jsog["__type"]=='flowgraph.LabeledTransition'){
			return impl_flowgraph.LabeledTransition.fromJSOG(jsog,cache);
		}
		
		throw new Exception("Unkown modelelement type ${jsog["__type"]}");
	}
}
