import 'package:FlowGraphTool/src/model/core.dart' as core;
import 'package:FlowGraphTool/src/model/command_graph.dart';
import 'package:FlowGraphTool/src/model/command.dart';

import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;
//prime referenced ecore externalLibrary
import 'package:FlowGraphTool/src/model/externallibrary.dart' as externallibrary;

import 'dart:js' as js;

class FlowGraphDiagramCommandGraph extends CommandGraph{

  FlowGraphDiagramCommandGraph(core.GraphModel currentGraphModel,List<HighlightCommand> highlightings,{Map jsog}) : super(currentGraphModel,highlightings,jsog:jsog);


  @override
  core.Node execCreateNodeType(String type,core.PyroElement primeElement)
  {
    // for each node type
	if(type == 'flowgraph.End'){
		var newNode = new flowgraph.End();
		return newNode;
	} else if(type == 'flowgraph.Swimlane'){
		var newNode = new flowgraph.Swimlane();
		return newNode;
	} else if(type == 'flowgraph.SubFlowGraph'){
		var newNode = new flowgraph.SubFlowGraph();
		newNode.subFlowGraph = primeElement as flowgraph.FlowGraphDiagram;
		return newNode;
	} else if(type == 'flowgraph.Start'){
		var newNode = new flowgraph.Start();
		return newNode;
	} else if(type == 'flowgraph.Activity'){
		var newNode = new flowgraph.Activity();
		return newNode;
	}
    throw new Exception("Unkown node type ${type}");
  }

  @override
  core.Edge execCreateEdgeType(String type)
  {
    core.Edge edge;
     if(type == 'flowgraph.Transition') {
     	edge = new flowgraph.Transition();
     } else if(type == 'flowgraph.LabeledTransition') {
     	edge = new flowgraph.LabeledTransition();
     }
     return edge;
  }
  
  @override
  void execCreateEdgeCommandCanvas(CreateEdgeCommand cmd) {
      core.ModelElement e = findElement(cmd.delegateId);
      
      if(cmd.type=='flowgraph.Transition'){
      	js.context.callMethod('create_edge_transition_flowgraphdiagram',[
      		cmd.sourceId,cmd.targetId,cmd.delegateId,js.JsObject.jsify(cmd.positions),js.JsObject.jsify(e.styleArgs()),e.$information(),e.$label()
      	]);
      	return;
      }
      if(cmd.type=='flowgraph.LabeledTransition'){
      	js.context.callMethod('create_edge_labeledtransition_flowgraphdiagram',[
      		cmd.sourceId,cmd.targetId,cmd.delegateId,js.JsObject.jsify(cmd.positions),js.JsObject.jsify(e.styleArgs()),e.$information(),e.$label()
      	]);
      	return;
      }
  }
  
  @override
  void execRemoveEdgeCanvas(int id,String type) {
  	if(type=='flowgraph.Transition'){
  		js.context.callMethod('remove_edge_transition_flowgraphdiagram',[
  			id
  		]);
  	} else if(type=='flowgraph.LabeledTransition'){
  		js.context.callMethod('remove_edge_labeledtransition_flowgraphdiagram',[
  			id
  		]);
  	}
  }
  
  @override
  void execReconnectEdgeCommandCanvas(ReconnectEdgeCommand cmd) {
  	if(cmd.type=='flowgraph.Transition'){
  		js.context.callMethod('reconnect_edge_transition_flowgraphdiagram',[
  			cmd.sourceId,cmd.targetId,cmd.delegateId
  		]);
  	} else if(cmd.type=='flowgraph.LabeledTransition'){
  		js.context.callMethod('reconnect_edge_labeledtransition_flowgraphdiagram',[
  			cmd.sourceId,cmd.targetId,cmd.delegateId
  		]);
  	}
  }
  
	@override
	void execCreateNodeCommandCanvas(CreateNodeCommand cmd) {
		core.ModelElement e = findElement(cmd.delegateId);
		if(e == null) {
			return;
		}
		var x = cmd.x;
		var y = cmd.y;
		if(e.container is core.Container) {
			var parent = e.container;
			while(parent!=null&&parent is core.Container) {
				x += (parent as core.Container).x;
				y += (parent as core.Container).y;
				parent = (parent as core.Container).container;
			}
		}
		if(cmd.type=='flowgraph.End'){
		js.context.callMethod('create_node_end_flowgraphdiagram',[
			x+(cmd.width~/2),
			y+(cmd.height~/2)
			,cmd.width,cmd.height,cmd.delegateId,cmd.containerId,js.JsObject.jsify(e.styleArgs()),e.$information(),e.$label()
			]);
			return;
		} else if(cmd.type=='flowgraph.Swimlane'){
		js.context.callMethod('create_node_swimlane_flowgraphdiagram',[
			x,
			y
			,cmd.width,cmd.height,cmd.delegateId,cmd.containerId,js.JsObject.jsify(e.styleArgs()),e.$information(),e.$label()
			]);
			return;
		} else if(cmd.type=='flowgraph.SubFlowGraph'){
		js.context.callMethod('create_node_subflowgraph_flowgraphdiagram',[
			x,
			y
			,cmd.width,cmd.height,cmd.delegateId,cmd.containerId,js.JsObject.jsify(e.styleArgs()),e.$information(),e.$label(),cmd.primeId
			]);
			return;
		} else if(cmd.type=='flowgraph.Start'){
		js.context.callMethod('create_node_start_flowgraphdiagram',[
			x+(cmd.width~/2),
			y+(cmd.height~/2)
			,cmd.width,cmd.height,cmd.delegateId,cmd.containerId,js.JsObject.jsify(e.styleArgs()),e.$information(),e.$label()
			]);
			return;
		} else if(cmd.type=='flowgraph.Activity'){
		js.context.callMethod('create_node_activity_flowgraphdiagram',[
			x,
			y
			,cmd.width,cmd.height,cmd.delegateId,cmd.containerId,js.JsObject.jsify(e.styleArgs()),e.$information(),e.$label()
			]);
			return;
		}
	}
	
	@override
	void execMoveNodeCanvas(MoveNodeCommand cmd) {
		_moveNodeCanvas(cmd.type,cmd.delegateId,cmd.containerId,cmd.x,cmd.y);
		_moveEmbeddedNodes(cmd.delegateId);
	}
	
	void _moveNodeCanvas(String type,int delegateId, int containerId, int x, int y) {
	var elem = findElement(delegateId) as core.Node;
	 	if(elem == null) {
	 		return;
	 	}
	 	if(elem.container is core.Container) {
	 		var parent = elem.container;
	 		while(parent!=null&&parent is core.Container) {
	 			x += (parent as core.Container).x;
	 			y += (parent as core.Container).y;
	 			parent = (parent as core.Container).container;
	 		}
		}
		if(type=='flowgraph.End'){
				js.context.callMethod('move_node_end_flowgraphdiagram',[
				x+(elem.width~/2),
				y+(elem.height~/2),delegateId,containerId
				]);
				return;
		} else if(type=='flowgraph.Swimlane'){
				js.context.callMethod('move_node_swimlane_flowgraphdiagram',[
				x,
				y,delegateId,containerId
				]);
				return;
		} else if(type=='flowgraph.SubFlowGraph'){
				js.context.callMethod('move_node_subflowgraph_flowgraphdiagram',[
				x,
				y,delegateId,containerId
				]);
				return;
		} else if(type=='flowgraph.Start'){
				js.context.callMethod('move_node_start_flowgraphdiagram',[
				x+(elem.width~/2),
				y+(elem.height~/2),delegateId,containerId
				]);
				return;
		} else if(type=='flowgraph.Activity'){
				js.context.callMethod('move_node_activity_flowgraphdiagram',[
				x,
				y,delegateId,containerId
				]);
				return;
		}
	}
	 
	
	void _moveEmbeddedNodes(int id) {
		var elem = findElement(id) as core.Node;
		if(elem == null) {
			return;
		}
		elem.allElements().where((n)=>n.id!=id).where((n)=>n is core.Node).map((n)=>n as core.Node).forEach((n)=>_moveNodeCanvas(n.$type(),n.id,n.container.id,n.x,n.y));
	}
	
	@override
	void execRemoveNodeCanvas(int id, String type) {
		if(type=='flowgraph.End'){
			js.context.callMethod('remove_node_end_flowgraphdiagram',[
				id
			]);
			return;
		}
		if(type=='flowgraph.Swimlane'){
			js.context.callMethod('remove_node_swimlane_flowgraphdiagram',[
				id
			]);
			return;
		}
		if(type=='flowgraph.SubFlowGraph'){
			js.context.callMethod('remove_node_subflowgraph_flowgraphdiagram',[
				id
			]);
			return;
		}
		if(type=='flowgraph.Start'){
			js.context.callMethod('remove_node_start_flowgraphdiagram',[
				id
			]);
			return;
		}
		if(type=='flowgraph.Activity'){
			js.context.callMethod('remove_node_activity_flowgraphdiagram',[
				id
			]);
			return;
		}
	}
	
	@override
	void execResizeNodeCommandCanvas(ResizeNodeCommand cmd) {
	    if(cmd.type=='flowgraph.End'){
	      js.context.callMethod('resize_node_end_flowgraphdiagram',[
	        cmd.width,cmd.height,cmd.direction,cmd.delegateId
	      ]);
	      return;
	    }
	    if(cmd.type=='flowgraph.Swimlane'){
	      js.context.callMethod('resize_node_swimlane_flowgraphdiagram',[
	        cmd.width,cmd.height,cmd.direction,cmd.delegateId
	      ]);
	      return;
	    }
	    if(cmd.type=='flowgraph.SubFlowGraph'){
	      js.context.callMethod('resize_node_subflowgraph_flowgraphdiagram',[
	        cmd.width,cmd.height,cmd.direction,cmd.delegateId
	      ]);
	      return;
	    }
	    if(cmd.type=='flowgraph.Start'){
	      js.context.callMethod('resize_node_start_flowgraphdiagram',[
	        cmd.width,cmd.height,cmd.direction,cmd.delegateId
	      ]);
	      return;
	    }
	    if(cmd.type=='flowgraph.Activity'){
	      js.context.callMethod('resize_node_activity_flowgraphdiagram',[
	        cmd.width,cmd.height,cmd.direction,cmd.delegateId
	      ]);
	      return;
	    }
	}
	
	@override
	void execRotateNodeCommandCanvas(RotateNodeCommand cmd) {
	    if(cmd.type=='flowgraph.End'){
	      js.context.callMethod('rotate_node_end_flowgraphdiagram',[
	        cmd.angle,cmd.delegateId
	      ]);
	    }
	    if(cmd.type=='flowgraph.Swimlane'){
	      js.context.callMethod('rotate_node_swimlane_flowgraphdiagram',[
	        cmd.angle,cmd.delegateId
	      ]);
	    }
	    if(cmd.type=='flowgraph.SubFlowGraph'){
	      js.context.callMethod('rotate_node_subflowgraph_flowgraphdiagram',[
	        cmd.angle,cmd.delegateId
	      ]);
	    }
	    if(cmd.type=='flowgraph.Start'){
	      js.context.callMethod('rotate_node_start_flowgraphdiagram',[
	        cmd.angle,cmd.delegateId
	      ]);
	    }
	    if(cmd.type=='flowgraph.Activity'){
	      js.context.callMethod('rotate_node_activity_flowgraphdiagram',[
	        cmd.angle,cmd.delegateId
	      ]);
	    }
	}
	
	@override
	void execUpdateBendPointCanvas(UpdateBendPointCommand cmd) {
		var positions = new js.JsArray();
		cmd.positions.forEach((p){
		 		var pos = new js.JsArray();
		 		pos['x'] = p.x;
		 		pos['y'] = p.y;
		 		positions.add(pos);
		});
	   js.context.callMethod('update_bendpoint_flowgraphdiagram',[
	     positions,cmd.delegateId
	   ]);
	}
	
	@override
	void execUpdateElementCanvas(UpdateCommand cmd) {
	    core.IdentifiableElement identifiableElement = cmd.element;
		if(identifiableElement is core.ModelElement){
			js.context.callMethod('update_element_flowgraphdiagram',[
			  null,
			  identifiableElement.id,
			  js.JsObject.jsify(identifiableElement.styleArgs()),
			  identifiableElement.$information(),
			  identifiableElement.$label()
			]);
		}
	}
	
	@override
	void execAppearanceCanvas(AppearanceCommand cmd) {
		js.context.callMethod('update_element_appearance_flowgraphdiagram',[
			cmd.delegateId,
			cmd.shapeId,
			cmd.background_r,cmd.background_g,cmd.background_b,
			cmd.foreground_r,cmd.foreground_g,cmd.foreground_b,
			cmd.lineInVisible,
			cmd.lineStyle,
			cmd.transparency,
			cmd.lineWidth,
			cmd.filled,
			cmd.angle,
			cmd.fontName,
			cmd.fontSize,
			cmd.fontBold,
			cmd.fontItalic,
			cmd.imagePath
		]);
	}
	
	@override
	void execHighlightCanvas(HighlightCommand cmd) {
		var preColor = js.context.callMethod('update_element_highlight_flowgraphdiagram',[
			cmd.id,
			cmd.background_r,cmd.background_g,cmd.background_b,
			cmd.foreground_r,cmd.foreground_g,cmd.foreground_b
		]);
		Map<String,dynamic> pc = new Map();
		pc['background'] = preColor['background'];
		pc['foreground'] = preColor['foreground'];
		cmd.setPre(pc);
		super.highlightings.add(cmd);
	}
	
	@override
	void revertHighlightCanvas(HighlightCommand cmd) {
	
		if(findElement(cmd.id)==null) {
			return;
		}
		js.context.callMethod('update_element_highlight_flowgraphdiagram',[
			cmd.id,
			cmd.pre_background_r,cmd.pre_background_g,cmd.pre_background_b,
			cmd.pre_foreground_r,cmd.pre_foreground_g,cmd.pre_foreground_b
		]);
	}
}
