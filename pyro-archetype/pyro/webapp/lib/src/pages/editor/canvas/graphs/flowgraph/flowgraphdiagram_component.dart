import 'dart:async';
import 'dart:html' as html;
import 'dart:convert' as convert;
import 'package:angular/angular.dart';

import 'package:FlowGraphTool/src/model/core.dart' as core;
import 'package:FlowGraphTool/src/model/message.dart';
import 'package:FlowGraphTool/src/model/command.dart';
import 'package:FlowGraphTool/src/model/check.dart';
import 'package:FlowGraphTool/src/service/base_service.dart';
import 'package:FlowGraphTool/src/service/check_service.dart';
import 'package:FlowGraphTool/src/service/editor_data_service.dart';
import 'package:FlowGraphTool/src/pages/editor/palette/list/list_view.dart';
import 'package:FlowGraphTool/src/pages/shared/context_menu/context_menu.dart';
import 'package:FlowGraphTool/src/service/context_menu_service.dart';
import 'package:FlowGraphTool/src/service/graph_service.dart';
import '../../dialog/message_dialog.dart';
import '../../dialog/display_dialog.dart';
import 'dart:js' as js;

import 'flowgraphdiagram_command_graph.dart';
import 'package:FlowGraphTool/src/pages/editor/palette/graphs/flowgraph/palette_builder.dart';

import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;
//prime referenced ecore externalLibrary
import 'package:FlowGraphTool/src/model/externallibrary.dart' as externallibrary;

@Component(
    selector: 'flowgraphdiagram-canvas',
    templateUrl: 'flowgraphdiagram_component.html',
    styleUrls: const [],
    directives: const [coreDirectives,MessageDialog,DisplayDialog]
)
class FlowGraphDiagramCanvasComponent implements OnInit, OnDestroy {


  final selectionChangedSC = new StreamController();
  @Output() Stream get selectionChanged => selectionChangedSC.stream;
  
  final selectionChangedModalSC = new StreamController();
  @Output() Stream get selectionChangedModal => selectionChangedModalSC.stream;
  
  final hasChangedSC = new StreamController();
  @Output() Stream get hasChanged => hasChangedSC.stream;
  
  
  final jumpToSC = new StreamController();
  @Output() Stream get jumpTo => jumpToSC.stream;

  @Input()
  flowgraph.FlowGraphDiagram currentGraphModel;
  
  @Input()
  core.PyroUser user;
  
  
  @Input()
  bool isFullScreen;
  
  @Input()
  core.LocalGraphModelSettings currentLocalSettings;
  

  FlowGraphDiagramCommandGraph commandGraph;
  
  html.WebSocket webSocketGraphModel;

  final GraphService graphService;
  
  final CheckService checkService;
  final EditorDataService _editorDataService;
  final ContextMenuService _contextMenuService;
  
  bool showMessageDialog = false;
  dynamic dialogMessage = null;
  String messageDialogType = null;
  
  bool showDisplayDialog = false;
  DisplayMessages displayMessages = null;
  
  List<HighlightCommand> highlightings = new List();
  
  core.PyroElement currentSelection = null;
  
  bool loading = true;
  
  ///check leve
  @Input()
  bool isError = true;
  @Input()
  bool isWarning = true;
  @Input()
  bool isInfo = true;

  FlowGraphDiagramCanvasComponent(
	GraphService this.graphService,
	this.checkService,
	this._editorDataService,
	this._contextMenuService) {
  }

  @override
  void ngOnInit() {
  	loading = true;
  	initCanvas();
  	activateWebSocket();
  }
  
  @override
  ngOnDestroy() {
      this.closeWebSocket();
  }
  
  void cb_cursor_moved(int x,int y) {
      if (webSocketGraphModel != null) {
    	  var wsMessage = {
    	    'senderId': user.id, 
    	    'event': 'updateCursorPosition',
    	    'content': {
    	      'runtimeType': 'info.scce.pyro.sync.UpdateCursorPosition',
    	      'graphModelId': currentGraphModel.id,
    	      'x': x,
    	      'y': y
    	    }
    	  };
    	  if (webSocketGraphModel.readyState == html.WebSocket.OPEN)
    	    webSocketGraphModel.send(convert.jsonEncode(wsMessage));
      }
  }
  
  void closeMessgeDialog(dynamic e) {
  	showMessageDialog = false;
  	dialogMessage = null;
	if(messageDialogType=='one_answer'){
		e['senderId']=user.id;
		e['runtimeType']='info.scce.pyro.message.DialogAnswer';
		this.webSocketGraphModel.send(convert.jsonEncode(e));
	}
	messageDialogType=null;
  }
  
	void closeDisplayDialog(dynamic e) {
	  	showDisplayDialog = false;
	  	displayMessages = null;
	}
	void downloadDisplayDialog(dynamic e) {
	  	showDisplayDialog = false;
	  	displayMessages = null;
	}
	
	void closeWebSocket() {
		if(this.webSocketGraphModel != null && this.webSocketGraphModel.readyState == html.WebSocket.OPEN) {
			html.window.console.debug("Closing Websocket webSocketCurrentUser");
			this.webSocketGraphModel.close();
			this.webSocketGraphModel = null;
			_editorDataService.graphModelWebSocketSC.add(null);
		}
	}
  	
  void executeCommands(CompoundCommandMessage m,bool forceExecute) => commandGraph.receiveCommand(m,forceExecute: forceExecute);
  
  void refreshChecks(CheckResults crs) {
	if (crs != null) {
	  js.context.callMethod("refresh_checks_flowgraphdiagram",[
		crs.toJS()
	  ]);	
	}
  }
			  
  void executeGraphmodelButton(String key) {
	graphService.executeGraphmodelButtonFlowGraphDiagram(currentGraphModel.id, currentGraphModel,key,highlightings).then((m){
		startPropagation().then((_){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					commandGraph.receiveCommand(m,forceExecute: true);
				}
			}
		}).then((_)=>endPropagation());
	});
  }
  
  void activateWebSocket() {
      BaseService.getTicket().then((ticket) {
        if (this.currentGraphModel != null &&
            user != null &&
            this.webSocketGraphModel == null) {
          this.webSocketGraphModel = new html.WebSocket(
              '${graphService.getBaseUrl(protocol: 'ws:')}/ws/graphmodel/${currentGraphModel.id}/${ticket}/private');
          _editorDataService.graphModelWebSocketSC.add(webSocketGraphModel);
  
          // Callbacks for currentUser
          this.webSocketGraphModel.onOpen.listen((e) {
            html.window.console.debug("[PYRO] onOpen GraphModel Websocket");
          });
  
          this.webSocketGraphModel.onMessage.listen((html.MessageEvent e) {
            html.window.console.debug("[PYRO] onMessage GraphModel Websocket");
            if (e.data != null) {
              var jsog = convert.jsonDecode(e.data);
  
              String event = jsog['event'];
              if (event == 'dialog') {
                //message for current user
                if (jsog['content']['messageType'] ==
                    'message_dialog_no_answer') {
                  print(jsog['content']['message']);
                  dialogMessage = jsog['content'];
                  messageDialogType = 'no_answer';
                  showMessageDialog = true;
                }
                if (jsog['content']['messageType'] ==
                    'message_dialog_one_answer') {
                  print(jsog['content']['message']);
                  dialogMessage = jsog['content'];
                  messageDialogType = 'one_answer';
                  showMessageDialog = true;
                }
              } else if (event == 'updateCursorPosition') {
                // update cursor
                var senderId = jsog['senderId'];
                if (senderId.toString() != user.id.toString()) {
                }
              } else {
                // update graph model changed by another user
                if (jsog['senderId'].toString() != user.id.toString()) {
                  if (jsog['content']['messageType'] == 'graphmodel') {
                    startPropagation().then((_) {
                      var _g = flowgraph.FlowGraphDiagram.fromJSOG(
                          jsog['content'], new Map());
                      currentGraphModel.merge(_g, structureOnly: true);
                      currentGraphModel.connector = _g.connector;
                      currentGraphModel.router = _g.router;
                      currentGraphModel.filename = _g.filename;
                      currentGraphModel.height = _g.height;
                      currentGraphModel.width = _g.width;
                      js.context.callMethod('update_routing_flowgraphdiagram', [
                        currentGraphModel.router,
                        currentGraphModel.connector
                      ]);
                    }).then((_) => endPropagation());
                  } else {
                    var m = Message.fromJSOG(jsog['content']);
                    startPropagation().then((_) {
                      if (m is CompoundCommandMessage) {
                        executeCommands(m, true);
                      }
                    }).then((_) => endPropagation());
                  }
                }
              }
            }
          });
  
          this.webSocketGraphModel.onClose.listen((html.CloseEvent e) {
            if (e.code == 4001) {
              //graphmodel has been deleted or access denied
              //TODO remove file in VS Code
            }
            html.window.console.debug("[PYRO] onClose GraphModel Websocket");
          });
          this.webSocketGraphModel.onError.listen((e) {
            html.window.console
                .debug("[PYRO] Error on GraphModel Websocket: ${e.toString()}");
          });
        }
      });
    }
	
  
  

  
  void initCanvas() {
	graphService.loadCommandGraphFlowGraphDiagram(currentGraphModel,highlightings).then((cg){
		commandGraph = cg;
		commandGraph.editorCommand.listen((c){
			if(c is OpenFileCommand) {
				Map m = new Map();
				m['graphmodel_id'] = c.id;
					jumpToSC.add(m);
			}
		});
  	  	currentGraphModel.merge(cg.currentGraphModel);
  	  	graphService.update(currentGraphModel.id);
  	  	js.context.callMethod("load_flowgraphdiagram",[
  	  	     currentGraphModel.width,
  	  	     currentGraphModel.height,
  	  	     currentGraphModel.scale,
  	  	     currentGraphModel.id,
  	  	     currentGraphModel.router,
  	  	     currentGraphModel.connector,
  	  	     //callback afert initialization
  	  	     initialized,
  	  	     //message callbacks
  	  	     cb_element_selected,
  	  	     cb_graphmodel_selected,
  	  	     cb_update_bendpoint,
  	  	     cb_can_move_node,
  	  	     cb_can_connect_edge,
  	  	     cb_get_valid_targets,
  	  	     cb_is_valid_connection,
  	  	     cb_get_valid_containers,
  	  	     cb_is_valid_container,
  	  	     cb_get_custom_actions,
  	  	     cb_fire_dbc_actions,
  	  	     cb_delete_selected,
  	  	     cb_cursor_moved,
  	  	     cb_property_persist,
  	  	     cb_create_node_end_flowgraphdiagram,
  	  	     cb_remove_node_end_flowgraphdiagram,
  	  	     cb_move_node_end_flowgraphdiagram,
  	  	     cb_resize_node_end_flowgraphdiagram,
  	  	     cb_rotate_node_end_flowgraphdiagram,
  	  	     cb_create_node_swimlane_flowgraphdiagram,
  	  	     cb_remove_node_swimlane_flowgraphdiagram,
  	  	     cb_move_node_swimlane_flowgraphdiagram,
  	  	     cb_resize_node_swimlane_flowgraphdiagram,
  	  	     cb_rotate_node_swimlane_flowgraphdiagram,
  	  	     cb_create_node_subflowgraph_flowgraphdiagram,
  	  	     cb_remove_node_subflowgraph_flowgraphdiagram,
  	  	     cb_move_node_subflowgraph_flowgraphdiagram,
  	  	     cb_resize_node_subflowgraph_flowgraphdiagram,
  	  	     cb_rotate_node_subflowgraph_flowgraphdiagram,
  	  	     cb_create_edge_transition_flowgraphdiagram,
  	  	     cb_remove_edge_transition_flowgraphdiagram,
  	  	     cb_reconnect_edge_transition_flowgraphdiagram,
  	  	     cb_create_node_start_flowgraphdiagram,
  	  	     cb_remove_node_start_flowgraphdiagram,
  	  	     cb_move_node_start_flowgraphdiagram,
  	  	     cb_resize_node_start_flowgraphdiagram,
  	  	     cb_rotate_node_start_flowgraphdiagram,
  	  	     cb_create_node_activity_flowgraphdiagram,
  	  	     cb_remove_node_activity_flowgraphdiagram,
  	  	     cb_move_node_activity_flowgraphdiagram,
  	  	     cb_resize_node_activity_flowgraphdiagram,
  	  	     cb_rotate_node_activity_flowgraphdiagram,
  	  	     cb_create_edge_labeledtransition_flowgraphdiagram,
  	  	     cb_remove_edge_labeledtransition_flowgraphdiagram,
  	  	     cb_reconnect_edge_labeledtransition_flowgraphdiagram
  		]);
  
		}).whenComplete(()=>loading = false);
	 }
	 
	 
	  
	  
	  Future<Null> startPropagation(){
	   js.context.callMethod('start_propagation_flowgraphdiagram',[]);
	   return new Future.value(null);
	  }
	  
	  void endPropagation(){
	    js.context.callMethod('end_propagation_flowgraphdiagram',[]);
	  }
	  
	  void updateCheckLevel(bool isError,bool isWarning,bool isInfo) {
	  	this.isError = isError;
	  	this.isWarning = isWarning;
	  	this.isInfo = isInfo;
	  }
	  
	 void updateGlueline(bool isGlueline) {
	 	js.context.callMethod("refresh_gluelines_flowgraphdiagram",[isGlueline]);
	 }
	 
	 void export(String type) {
	 		if(type=='svg'){
	 		   	js.context.callMethod('download_svg',[currentGraphModel.filename]);
	 		}
	 		if(type=='png'){
	 		   	js.context.callMethod('download_png',[currentGraphModel.filename]);
	 		}
	 }
	 
	 void updateScale(double factor,{bool persist:true}) {
	 	if(factor!=0.0) {
	 		currentGraphModel.scale = factor;
	 	}
	 	if(persist) {
	 		graphService.updateGraphModel(currentGraphModel).then((_){
	 			js.context.callMethod('update_scale_flowgraphdiagram',[currentGraphModel.scale]);
	 		});
	 	} else {
	 		js.context.callMethod('update_scale_flowgraphdiagram',[currentGraphModel.scale]);
	 	}
	 	
	 }
	 
	 void updateRouting() {
	 	graphService.updateGraphModel(currentGraphModel).then((_){
	 		js.context.callMethod('update_routing_flowgraphdiagram',[currentGraphModel.router,currentGraphModel.connector]);
	 	});
	 }
	 
	 void updateProperties(core.IdentifiableElement ie) {
	   	if(ie is! core.GraphModel){
			currentGraphModel.allElements().where((n)=>n is core.ModelElement).forEach((n)=>updateElement(n));
		} else {
			if(ie.$type() == "flowgraph.FlowGraphDiagram"){
				currentGraphModel.merge(ie,structureOnly:false);
			}
		}
		
		//check for prime referencable element in same graph and update
	}
	
	void cb_property_persist(int id,String value) {
	 	
	 	var elem = currentGraphModel.allElements().where((n)=>n.id == id).first;
		PropertyMessage pm = new PropertyMessage(
		    elem.id,
		    elem.runtimeType.toString(),
		    elem,
		    user.id
		);
		graphService.sendMessage(pm,"flowgraphdiagram",currentGraphModel.id).then((m){
			startPropagation().then((_){
				if (m is CompoundCommandMessage) {
					executeCommands(m,true);
				}
			}).then((_)=>endPropagation());
		});
	}
	
	void undo() {
		var ccm = commandGraph.undo(user);
		if(ccm != null && !ccm.cmd.queue.isEmpty) {
		  graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m) {
		startPropagation().then((_){
			if(m is CompoundCommandMessage){
				if(m.type == 'undo_valid_answer'){
					commandGraph.receiveCommand(m);
				}
			}
		}).then((_)=>endPropagation());
		  });
		}
	}
	
	void redo() {
		var ccm = commandGraph.redo(user);
	  	if(ccm!=null && !ccm.cmd.queue.isEmpty) {
		    graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m) {
				startPropagation().then((_){
					if(m is CompoundCommandMessage){
						if(m.type == 'redo_valid_answer'){
							commandGraph.receiveCommand(m);
						}
					}
				}).then((_)=>endPropagation());
		    });
		}
	}
	
	/// create the current graphmodel initailly
	void initialized() {
		//add nodes and container bottom up
		startPropagation().then((_){
			initContainerDeeply(currentGraphModel);
			//connect by edges
			currentGraphModel.modelElements.where((n)=>n is core.Edge).forEach((e){
				if(e.$type() == "flowgraph.Transition"){
					create_edge_transition_flowgraphdiagram(e);
				} else if(e.$type() == "flowgraph.LabeledTransition"){
					create_edge_labeledtransition_flowgraphdiagram(e);
				}
			});
			updateCheckLevel(isError,isWarning,isInfo);
		}).then((_)=>endPropagation());
	}
	
	void initContainerDeeply(core.ModelElementContainer container) {
	    for(var node in container.modelElements) {
	    	if(node.$type() == "flowgraph.End"){
	    	  create_node_end(node);
	    	  continue;
	    	}
	    	if(node.$type() == "flowgraph.Swimlane"){
	    	  create_node_swimlane(node);
	    	  continue;
	    	}
	    	if(node.$type() == "flowgraph.SubFlowGraph"){
	    	  create_node_subflowgraph(node);
	    	  continue;
	    	}
	    	if(node.$type() == "flowgraph.Start"){
	    	  create_node_start(node);
	    	  continue;
	    	}
	    	if(node.$type() == "flowgraph.Activity"){
	    	  create_node_activity(node);
	    	  continue;
	    	}
	    }
	}
	
	 core.IdentifiableElement findElement(int id) {
	  if(id==currentGraphModel.id){
	   return currentGraphModel;
	  }
	  return currentGraphModel.allElements().firstWhere((n)=>n.id==id, orElse: () => null);
	 }
	 
	 void cb_get_custom_actions(int id,int x,int y,int canvasX, int canvasY) {
	 	startPropagation().then((_){
	 		graphService.fetchCustomActionsForFlowGraphDiagram(id,currentGraphModel).then((map){
	 			
	 			List<MapList> mapList = FlowGraphDiagramPaletteBuilder.build(currentGraphModel);
	 			List<ContextMenuEntry> mainMenuItems = new List();
	 			List<ContextMenuEntry> menuItems = new List();
	 			
	 			mapList.forEach((ml) {
	 				ml.values.forEach((val){
	 				  if(cb_can_move_node(-1,id,tmpNode:val.instance) == true) {
	 					  menuItems.add(ContextMenuItem(null, val.name, true, () {
	 					    js.context.callMethod("create_node_flowgraphdiagram_after_drop", [canvasX,canvasY,val.identifier]);	
	 					  }));
	 				  }
	 			  });
	 			});
	 			if(menuItems.isNotEmpty) {
	 				mainMenuItems.add(
	 					ContextMenuItem(null, 'Create here...', true, null, subItems: menuItems)
	 				);
	 			}
	 			mainMenuItems.add(
	 				ContextMenuItem(null, 'Edit...', true, (){
	 					cb_element_selected(id,openModal:true);
	 				})
	 			);
	 			if(map.isNotEmpty){
	 				map.forEach((k,v){
	 					mainMenuItems.add(
	 						ContextMenuItem(null, v, true, (){
	 							  	cb_fire_cm_action(k,id);
	 						})
	 					);
	 				});
	 			}
	 			_contextMenuService.show(
	 				ContextMenu(
	 					x, y, mainMenuItems
	 				)
	 			);
	 		});
	 	}).then((_)=>endPropagation());
	 }
	 
	 void cb_delete_selected() {
	 	if(currentSelection == null || currentSelection is core.GraphModel) {
	 		return;
	 	}
	 	//prevent for disbale remove
	 	if(currentSelection is core.Node) {
	 		var node = currentSelection as core.Node;
	 		startPropagation().then((_){
	 			var container = findElement(node.container.id) as core.ModelElementContainer;
	 			var ccm = commandGraph.sendRemoveNodeCommand(node.id,user);
	 			graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
	 				if(m is CompoundCommandMessage){
	 					if(m.type=='basic_valid_answer'){
	 					 	selectionChangedSC.add(currentGraphModel);
	 					 	node.container = null;
	 					 	startPropagation().then((_){
	 					 		commandGraph.receiveCommand(m,forceExecute: true);
	 					 		commandGraph.storeCommand(m.cmd);
	 					 		if(container is! core.GraphModel){
	 					 		   updateElement(container as core.ModelElement);
	 					 		}
	 					 	}).then((_)=>endPropagation());
	 					 }
	 				}
	 			});
	 		}).then((_)=>endPropagation());
	 	}
	 	if(currentSelection is core.Edge) {
	 		var edge = currentSelection as core.Edge;
	 		startPropagation().then((_){
	 			var ccm = commandGraph.sendRemoveEdgeCommand(
	 				edge.id,
	 				edge.source.id,
	 				edge.target.id,
	 				edge.source.$type(),
	 				edge.target.$type(),
	 				edge.$type(),
	 				user
	 			);
	 			graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
	 				if(m is CompoundCommandMessage){
	 				  if(m.type=='basic_valid_answer'){
	 				  	startPropagation().then((_){
	 				  		selectionChangedSC.add(currentGraphModel);
	 				  		commandGraph.receiveCommand(m,forceExecute: true);
	 				  		commandGraph.storeCommand(m.cmd);
	 				  	}).then((_)=>endPropagation());
	 				  }
	 				}
	 			});
	 		}).then((_)=>endPropagation());
	 	}
	 }
	 
	 void cb_fire_cm_action(String fqn,int id) {
	 	graphService.triggerCustomActionsForFlowGraphDiagram(id, currentGraphModel,fqn,highlightings).then((m){
	 		startPropagation().then((_){
	 			if(m is CompoundCommandMessage){
	 				if(m.type == 'basic_valid_answer'){
	 					commandGraph.receiveCommand(m,forceExecute: true);
	 				}
	 			}
	 		}).then((_)=>endPropagation());
	 	});
	 }
	 
	 void cb_fire_dbc_actions(int id) {
	 	// refresh currentgraph, to prevent an update-anomaly in terms of prime-references
	 	// to keep the model eventually consistent
	 	graphService.loadCommandGraph(currentGraphModel, highlightings).then((_){
	 		graphService.triggerDoubleClickActionsForFlowGraphDiagram(id, currentGraphModel,highlightings).then((m){
	 			startPropagation().then((_){
	 				if(m is CompoundCommandMessage){
	 					if(m.type == 'basic_valid_answer'){
	 						commandGraph.receiveCommand(m,forceExecute: true);
	 					}
	 				}
	 			}).then((_)=>endPropagation());
	 		}).then((_){
	 			//Execute jumpToDefinition
	 			var elem = findElement(id);
	 		});
	 	});
	 }
	 
	void cb_element_selected(int id,{bool openModal:false}) {
		if (id != null) { // cursors have no id and should be ignored
			if(id<0){
				if(openModal){
					selectionChangedModalSC.add(currentGraphModel);
				} else {
					selectionChangedSC.add(currentGraphModel);
				}
			} else {
				//find element
				var newSelection = findElement(id);
				currentSelection = newSelection;
				if(openModal){
					selectionChangedModalSC.add(newSelection);
				} else {
					selectionChangedSC.add(newSelection);
				}
			}
		}
	}
	
	 bool check_bendpoint_update(core.Edge edge,List positions) {
	 	if(positions==null) {
	 		return edge.bendingPoints.isNotEmpty;
	 	}
	 	 if(positions.length==edge.bendingPoints.length) {
	 	  for(var pos in positions){
	 	   var x = pos['x'];
	 	   var y = pos['y'];
	 	   var found = false;
	 	   for(var b in edge.bendingPoints) {
	 	    if(b.x==x&&b.y==y){
	 	     found = true;
	 	     break;
	 	    }
	 	   }
	 	   if(found==false){
	 	    return true;
	 	   }
	 	  }
	 	 } else {
	 	 return true;
	 	}
	 	return false;
	 }
	 
	 void cb_graphmodel_selected() {
	 currentSelection = null;
	 selectionChangedSC.add(currentGraphModel);
	 }
	 
	 void updateElement(core.ModelElement elem,{String cellId}) {
	 	if(elem==null) return;
	 			js.context.callMethod('update_element_flowgraphdiagram',[
	 	cellId,
	 		elem.id,
	 		elem.styleArgs(),
	 		elem.$information(),
	 		elem.$label()
	 			]);
	 	
	 }
	 
	 bool cb_is_valid_container(int nodeId,int containerId) {
	 	var resp = cb_can_move_node(nodeId,containerId);
	 	return resp is bool && resp == true;
	 }
	 
	 js.JsArray cb_get_valid_containers(int id,String type) {
	 	var possibleNodes = [];
	 	if(id==-1) {
	 		switch(type) {
	 			case 'flowgraph.End':
	 				possibleNodes.add(new flowgraph.End());
	 				break;
	 			case 'flowgraph.Swimlane':
	 				possibleNodes.add(new flowgraph.Swimlane());
	 				break;
	 			case 'flowgraph.Start':
	 				possibleNodes.add(new flowgraph.Start());
	 				break;
	 			case 'flowgraph.Activity':
	 				possibleNodes.add(new flowgraph.Activity());
	 				break;
	 			case 'flowgraph.FlowGraphDiagram':
	 				possibleNodes.add(new flowgraph.SubFlowGraph());
	 				break;
	 		}
	 	}
	 	var valids = new js.JsArray();
	 	
	 	// identify all valid containers
	 	for (var node in possibleNodes) {
	 		currentGraphModel.allElements().where((n)=>n is core.Container).forEach((n) {
	 			var re = null;
	 			if(id==-1) {
	 				re = cb_can_move_node(-1,n.id,tmpNode:node);
	 			} else {
	 				re = cb_can_move_node(id,n.id);
	 			}
	 			if(re is bool && re == true) {
	 				valids.add(n.id);
	 			}
	 		});
	 	}
	 	return valids;
	 }
	
	dynamic cb_can_move_node(int id,int containerId,{core.Node tmpNode:null}) {
		var node = null;
		if(tmpNode == null) {
			node = findElement(id) as core.Node;
		} else {
			node = tmpNode;
		}
		var c = findElement(containerId);
		
		// can not find any node for positional information
		   if (node == null) return false;
		   
		var x = node.x;
		var y = node.y;
		
		// container with given id does not exist
		// just move the node
		if(c is! core.ModelElementContainer){
			var arr = js.JsArray();
			arr['x'] = x;
			arr['y'] = y;
			if(node.container != null) {
				arr['containerId'] = node.container.id;
			}
			return arr;
		}
		
		var container = c as core.ModelElementContainer;
		if(node.container == null || node.container.id!=container.id) {
			// switch container to..
			// ...graphModel
			if(container.$type() == "flowgraph.FlowGraphDiagram") {
				//check if type can be contained in group
				if(
					node.$type() == "flowgraph.End"
				) {
					// node is of type, that fits the constraint
					return true;
				}
				//check if type can be contained in group
				if(
					node.$type() == "flowgraph.Start"
				) {
					// node is of type, that fits the constraint
					return true;
				}
				//check if type can be contained in group
				if(
					node.$type() == "flowgraph.SubFlowGraph"
				) {
					// node is of type, that fits the constraint
					return true;
				}
				//check if type can be contained in group
				if(
					node.$type() == "flowgraph.Activity"
				) {
					// node is of type, that fits the constraint
					return true;
				}
				//check if type can be contained in group
				if(
					node.$type() == "flowgraph.Swimlane"
				) {
					// node is of type, that fits the constraint
					return true;
				}
			}
			
			// ...other containers
			else if(container.$type() == "flowgraph.Swimlane") {
				//check if type can be contained in group
				if(
					node.$type() == "flowgraph.End"
				) {
					// node is of type, that fits the constraint
					return true;
				}
				//check if type can be contained in group
				if(
					node.$type() == "flowgraph.Activity"
				) {
					// node is of type, that fits the constraint
					return true;
				}
				//check if type can be contained in group
				if(
					node.$type() == "flowgraph.Start"
				) {
					int group2Size = 0;
					group2Size += container.modelElements.where((n)=>n.$type() == "flowgraph.Start").length;
					if(group2Size<1){
						// node is of type and inside the bounding constraint
						return true;
					}
				}
				//check if type can be contained in group
				if(
					node.$type() == "flowgraph.SubFlowGraph"
				) {
					// node is of type, that fits the constraint
					return true;
				}
			}
			
			// cannot switch to other container
			var arr = js.JsArray();
			arr['x'] = x;
			arr['y'] = y;
			if(node.container != null) {
				arr['containerId'] = node.container.id;
			}
			return arr;
		}
		
		// same container or no previous container
		return true;
	}
		
		js.JsArray cb_get_valid_targets(int id) {
		var valids = new js.JsArray();
		currentGraphModel.allElements().where((n)=>n is core.Node).forEach((n){
			var re = cb_can_connect_edge(-1,id,n.id);
		
			if(re is bool && re == true) {
				valids.add(n.id);
			}
		});
		return valids;
	}
	
	bool cb_is_valid_connection(int edgeId, int sourceId, int targetId) {
		var re = cb_can_connect_edge(edgeId,sourceId,targetId);
		return re is bool && re == true;
	}

	bool can_connect_source(core.Edge edge,core.Node source,core.Node target) {
		if(edge.source == null || edge.source.id!=source.id) {
			//source changed
			if(source.$type() == "flowgraph.End"){
				if(edge == null || edge.$type() == "flowgraph.Transition")
				{
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition")
				{
				}
			}else if(source.$type() == "flowgraph.Swimlane"){
				if(edge == null || edge.$type() == "flowgraph.Transition")
				{
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition")
				{
				}
			}else if(source.$type() == "flowgraph.SubFlowGraph"){
				if(edge == null || edge.$type() == "flowgraph.Transition")
				{
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition")
				{
					var outgoing = source.outgoing;
					
					int groupSize0 = 0;
					groupSize0 += outgoing.where((n)=>n.$type() == "flowgraph.LabeledTransition").length;
						if(true)
						{
							return true;
						}
				}
			}else if(source.$type() == "flowgraph.Start"){
				if(edge == null || edge.$type() == "flowgraph.Transition")
				{
					var outgoing = source.outgoing;
					
					int groupSize0 = 0;
					groupSize0 += outgoing.where((n)=>n.$type() == "flowgraph.Transition").length;
						if(groupSize0<1)
						{
							return true;
						}
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition")
				{
				}
			}else if(source.$type() == "flowgraph.Activity"){
				if(edge == null || edge.$type() == "flowgraph.Transition")
				{
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition")
				{
					var outgoing = source.outgoing;
					
					int groupSize0 = 0;
					groupSize0 += outgoing.where((n)=>n.$type() == "flowgraph.LabeledTransition").length;
						if(true)
						{
							return true;
						}
				}
			}
		}
		return false;
	}
	
	bool can_connect_target(core.Edge edge,core.Node source,core.Node target) {
		if(edge.target == null || edge.target.id!=target.id){
			//target changed
			if(target.$type() == "flowgraph.End"){
				if(edge == null || edge.$type() == "flowgraph.Transition") {
					var incoming = target.incoming;
					
					int groupSize0 = 0;
					groupSize0 += incoming.where((n)=>n.$type() == "flowgraph.Transition").length;
					groupSize0 += incoming.where((n)=>n.$type() == "flowgraph.LabeledTransition").length;
					if(true)
					{
						return true;
					}
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition") {
					var incoming = target.incoming;
					
					int groupSize0 = 0;
					groupSize0 += incoming.where((n)=>n.$type() == "flowgraph.Transition").length;
					groupSize0 += incoming.where((n)=>n.$type() == "flowgraph.LabeledTransition").length;
					if(true)
					{
						return true;
					}
				}
			}else if(target.$type() == "flowgraph.Swimlane"){
				if(edge == null || edge.$type() == "flowgraph.Transition") {
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition") {
				}
			}else if(target.$type() == "flowgraph.SubFlowGraph"){
				if(edge == null || edge.$type() == "flowgraph.Transition") {
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition") {
				}
			}else if(target.$type() == "flowgraph.Start"){
				if(edge == null || edge.$type() == "flowgraph.Transition") {
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition") {
				}
			}else if(target.$type() == "flowgraph.Activity"){
				if(edge == null || edge.$type() == "flowgraph.Transition") {
				} else if(edge == null || edge.$type() == "flowgraph.LabeledTransition") {
				}
			}
		}
		return false;
	}
	
	dynamic cb_can_connect_edge(int id,int sourceId,int targetId) {
		var edge = id==-1?null:findElement(id) as core.Edge;
		List<core.Edge> edgeTypes = id==-1?[
			new flowgraph.Transition(),
			new flowgraph.LabeledTransition()
		]:[findElement(id)];
		
		var sourceElem = findElement(sourceId);
		var targetElem = findElement(targetId);
		if(sourceElem is! core.Node || targetElem is! core.Node) {
			return false;
		}
		
		var source = sourceElem as core.Node;
		var target = targetElem as core.Node;
		
		for(var e in edgeTypes)
		{
			var targetResult = can_connect_source(e,source,target);
			var sourceResult = can_connect_target(e,source,target);
			if(edge==null) {
				if(targetResult && sourceResult) {
					return true;
				}
			}
			else {
				if(targetResult || sourceResult) {
					return true;
				}
			}
		}
		if(edge == null) {
			return false;
		}
		var arr = js.JsArray();
		arr['target'] = edge.target.id;
		arr['source'] = edge.source.id;
		return arr;
	}
	
	void remove_node_cascade(core.Node node) {
		if(node == null){ return; }
		else if(node.$type() == "flowgraph.End") { remove_node_cascade_end_flowgraphdiagram(node); }
		else if(node.$type() == "flowgraph.Swimlane") { remove_node_cascade_swimlane_flowgraphdiagram(node); }
		else if(node.$type() == "flowgraph.SubFlowGraph") { remove_node_cascade_subflowgraph_flowgraphdiagram(node); }
		else if(node.$type() == "flowgraph.Start") { remove_node_cascade_start_flowgraphdiagram(node); }
		else if(node.$type() == "flowgraph.Activity") { remove_node_cascade_activity_flowgraphdiagram(node); }
	}
	
	//for each node
	
	void create_node_end(flowgraph.End node) {
		var x = node.x;
		var y = node.y;
		
		if(node.container is core.Container) {
			core.ModelElementContainer parent = node.container;
			while(parent!=null&&parent is core.Container) {
				x += (parent as core.Container).x;
				y += (parent as core.Container).y;
				parent = (parent as core.Container).container;
			}
		}
	   js.context.callMethod('create_node_end_flowgraphdiagram',
	   [   
	   		x+(node.width~/2),
	   		y+(node.height~/2),
		   	node.width,
		   	node.height,
		   	node.id,
		   	node.container.id,
		   	node.styleArgs(),
		   	node.$information(),
		   	node.$label()
	   ]);
	}
	
	void cb_create_node_end_flowgraphdiagram(int x,int y,int width,int height,String cellId,int containerId) {
		
		var container = findElement(containerId) as core.ModelElementContainer;
		var containerType =
		        container != null ? container.$type() : null;
	
	    var ccm = commandGraph.sendCreateNodeCommand("flowgraph.End",x-(width~/2),
	    y-(height~/2),containerId,containerType,width,height,user);
	    startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					var cmd = m.cmd.queue.first;
					if(cmd is CreateNodeCommand){
						var node = new flowgraph.End();
						node.x = x;
						node.y = y;
						node.id = cmd.delegateId;
						node.container = container;
						container.modelElements.add(node);
						updateElement(node,cellId: cellId);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
						if(container is! core.GraphModel){
							updateElement(container as core.ModelElement);
						}
						selectionChangedSC.add(node);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	void remove_node_cascade_end_flowgraphdiagram(flowgraph.End node) {
		//remove connected edges
		node.outgoing.forEach((e) {
			e.target.incoming.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.outgoing.clear();
		node.incoming.forEach((e) {
			e.source.outgoing.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.incoming.clear();
		js.context.callMethod('remove_node_end_flowgraphdiagram', [node.id]);
	}
	
	void cb_remove_node_end_flowgraphdiagram(int id) {
		var node = findElement(id) as flowgraph.End;
		var container = findElement(node.container.id) as core.ModelElementContainer;
		var ccm = commandGraph.sendRemoveNodeCommand(id,user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			selectionChangedSC.add(currentGraphModel);
			node.container = null;
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					commandGraph.receiveCommand(m,forceExecute: true);
					commandGraph.storeCommand(m.cmd);
					if(container is! core.GraphModel){
					updateElement(container as core.ModelElement);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	
	void cb_move_node_end_flowgraphdiagram(int x,int y,int id,containerId) {
		var container = findElement(containerId) as core.ModelElementContainer;
		var node = findElement(id) as core.Node;
		if(node.x==x && node.y==y){
			return;
		}
		var ccm = commandGraph.sendMoveNodeCommand(id, x, y, containerId, user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
		if(m is CompoundCommandMessage){
			if(m.type == 'basic_valid_answer'){
				if(!container.modelElements.contains(node)){
					node.container.modelElements.remove(node);
					node.container = container;
					container.modelElements.add(node);
					if(container is! core.GraphModel){
						updateElement(container as core.ModelElement);
					}
				}
				node.x = x;
				node.y = y;
				updateElement(node);
				commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
				commandGraph.storeCommand(m.cmd);
			}
			else {
				//revert
				commandGraph.revert(m);
			}
		}
		}).whenComplete(()=>endPropagation());
	}
	
	void cb_resize_node_end_flowgraphdiagram(int width,int height,String direction,int id) {
		var node = findElement(id) as core.Node;
		if(node.width!=width||node.height!=height) {
			startPropagation();
			var ccm = commandGraph.sendResizeNodeCommand(id,width,height,direction,user);
			graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
				if(m is CompoundCommandMessage){
					if(m.type == 'basic_valid_answer'){
						node.width = width;
						node.height = height;
						updateElement(node);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
					}
					else {
						//revert
						commandGraph.revert(m);
					}
				}
			}).whenComplete(()=>endPropagation());
		}
	}
		
	void cb_rotate_node_end_flowgraphdiagram(int angle,int id) {
		var node = findElement(id) as core.Node;
	   	var ccm =commandGraph.sendRotateNodeCommand(id,angle,user);
		startPropagation();
	   	graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					node.angle = angle;
					updateElement(node);
					commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
					commandGraph.storeCommand(m.cmd);
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	void create_node_swimlane(flowgraph.Swimlane node) {
		var x = node.x;
		var y = node.y;
		
		if(node.container is core.Container) {
			core.ModelElementContainer parent = node.container;
			while(parent!=null&&parent is core.Container) {
				x += (parent as core.Container).x;
				y += (parent as core.Container).y;
				parent = (parent as core.Container).container;
			}
		}
	   js.context.callMethod('create_node_swimlane_flowgraphdiagram',
	   [   
	   		x,
	   		y,
		   	node.width,
		   	node.height,
		   	node.id,
		   	node.container.id,
		   	node.styleArgs(),
		   	node.$information(),
		   	node.$label()
	   ]);
	}
	
	void cb_create_node_swimlane_flowgraphdiagram(int x,int y,int width,int height,String cellId,int containerId) {
		
		var container = findElement(containerId) as core.ModelElementContainer;
		var containerType =
		        container != null ? container.$type() : null;
	
	    var ccm = commandGraph.sendCreateNodeCommand("flowgraph.Swimlane",x,
	    y,containerId,containerType,width,height,user);
	    startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					var cmd = m.cmd.queue.first;
					if(cmd is CreateNodeCommand){
						var node = new flowgraph.Swimlane();
						node.x = x;
						node.y = y;
						node.id = cmd.delegateId;
						node.container = container;
						container.modelElements.add(node);
						updateElement(node,cellId: cellId);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
						if(container is! core.GraphModel){
							updateElement(container as core.ModelElement);
						}
						selectionChangedSC.add(node);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	void remove_node_cascade_swimlane_flowgraphdiagram(flowgraph.Swimlane node) {
		node.modelElements.where((n)=>n is core.Node).forEach((n)=>remove_node_cascade(n));
		//remove connected edges
		node.outgoing.forEach((e) {
			e.target.incoming.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.outgoing.clear();
		node.incoming.forEach((e) {
			e.source.outgoing.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.incoming.clear();
		js.context.callMethod('remove_node_swimlane_flowgraphdiagram', [node.id]);
	}
	
	void cb_remove_node_swimlane_flowgraphdiagram(int id) {
		var node = findElement(id) as flowgraph.Swimlane;
		var container = findElement(node.container.id) as core.ModelElementContainer;
		var ccm = commandGraph.sendRemoveNodeCommand(id,user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			selectionChangedSC.add(currentGraphModel);
			node.container = null;
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					commandGraph.receiveCommand(m,forceExecute: true);
					commandGraph.storeCommand(m.cmd);
					if(container is! core.GraphModel){
					updateElement(container as core.ModelElement);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	
	void cb_move_node_swimlane_flowgraphdiagram(int x,int y,int id,containerId) {
		var container = findElement(containerId) as core.ModelElementContainer;
		var node = findElement(id) as core.Node;
		if(node.x==x && node.y==y){
			return;
		}
		var ccm = commandGraph.sendMoveNodeCommand(id, x, y, containerId, user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
		if(m is CompoundCommandMessage){
			if(m.type == 'basic_valid_answer'){
				if(!container.modelElements.contains(node)){
					node.container.modelElements.remove(node);
					node.container = container;
					container.modelElements.add(node);
					if(container is! core.GraphModel){
						updateElement(container as core.ModelElement);
					}
				}
				node.x = x;
				node.y = y;
				updateElement(node);
				commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
				commandGraph.storeCommand(m.cmd);
			}
			else {
				//revert
				commandGraph.revert(m);
			}
		}
		}).whenComplete(()=>endPropagation());
	}
	
	void cb_resize_node_swimlane_flowgraphdiagram(int width,int height,String direction,int id) {
		var node = findElement(id) as core.Node;
		if(node.width!=width||node.height!=height) {
			startPropagation();
			var ccm = commandGraph.sendResizeNodeCommand(id,width,height,direction,user);
			graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
				if(m is CompoundCommandMessage){
					if(m.type == 'basic_valid_answer'){
						node.width = width;
						node.height = height;
						updateElement(node);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
					}
					else {
						//revert
						commandGraph.revert(m);
					}
				}
			}).whenComplete(()=>endPropagation());
		}
	}
		
	void cb_rotate_node_swimlane_flowgraphdiagram(int angle,int id) {
		var node = findElement(id) as core.Node;
	   	var ccm =commandGraph.sendRotateNodeCommand(id,angle,user);
		startPropagation();
	   	graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					node.angle = angle;
					updateElement(node);
					commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
					commandGraph.storeCommand(m.cmd);
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	void create_node_subflowgraph(flowgraph.SubFlowGraph node) {
		var x = node.x;
		var y = node.y;
		
		if(node.container is core.Container) {
			core.ModelElementContainer parent = node.container;
			while(parent!=null&&parent is core.Container) {
				x += (parent as core.Container).x;
				y += (parent as core.Container).y;
				parent = (parent as core.Container).container;
			}
		}
	   js.context.callMethod('create_node_subflowgraph_flowgraphdiagram',
	   [   
	   		x,
	   		y,
		   	node.width,
		   	node.height,
		   	node.id,
		   	node.container.id,
		   	node.styleArgs(),
		   	node.$information(),
		   	node.$label()
	   ]);
	}
	
	void cb_create_node_subflowgraph_flowgraphdiagram(int x,int y,int width,int height,String cellId,int containerId,int primeId) {
		
		var container = findElement(containerId) as core.ModelElementContainer;
		var containerType =
		        container != null ? container.$type() : null;
	
	    var ccm = commandGraph.sendCreateNodeCommand("flowgraph.SubFlowGraph",x,
	    y,containerId,containerType,width,height,user,primeId:primeId);
	    startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					var cmd = m.cmd.queue.first;
					if(cmd is CreateNodeCommand){
						var node = new flowgraph.SubFlowGraph();
						node.x = x;
						node.y = y;
						node.id = cmd.delegateId;
						node.container = container;
						container.modelElements.add(node);
						//prime node -> update element properties
						var primeElem = cmd.primeElement;
						var elements = currentGraphModel.allElements().where((n)=>n.id==primeElem.id);
						if(elements.isNotEmpty){
							node.subFlowGraph = elements.first as flowgraph.FlowGraphDiagram;
						} else {
							node.subFlowGraph = primeElem as flowgraph.FlowGraphDiagram;
						}
						updateElement(node,cellId: cellId);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
						if(container is! core.GraphModel){
							updateElement(container as core.ModelElement);
						}
						selectionChangedSC.add(node);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	void remove_node_cascade_subflowgraph_flowgraphdiagram(flowgraph.SubFlowGraph node) {
		//remove connected edges
		node.outgoing.forEach((e) {
			e.target.incoming.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.outgoing.clear();
		node.incoming.forEach((e) {
			e.source.outgoing.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.incoming.clear();
		js.context.callMethod('remove_node_subflowgraph_flowgraphdiagram', [node.id]);
	}
	
	void cb_remove_node_subflowgraph_flowgraphdiagram(int id) {
		var node = findElement(id) as flowgraph.SubFlowGraph;
		var container = findElement(node.container.id) as core.ModelElementContainer;
		var ccm = commandGraph.sendRemoveNodeCommand(id,user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			selectionChangedSC.add(currentGraphModel);
			node.container = null;
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					commandGraph.receiveCommand(m,forceExecute: true);
					commandGraph.storeCommand(m.cmd);
					if(container is! core.GraphModel){
					updateElement(container as core.ModelElement);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	
	void cb_move_node_subflowgraph_flowgraphdiagram(int x,int y,int id,containerId) {
		var container = findElement(containerId) as core.ModelElementContainer;
		var node = findElement(id) as core.Node;
		if(node.x==x && node.y==y){
			return;
		}
		var ccm = commandGraph.sendMoveNodeCommand(id, x, y, containerId, user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
		if(m is CompoundCommandMessage){
			if(m.type == 'basic_valid_answer'){
				if(!container.modelElements.contains(node)){
					node.container.modelElements.remove(node);
					node.container = container;
					container.modelElements.add(node);
					if(container is! core.GraphModel){
						updateElement(container as core.ModelElement);
					}
				}
				node.x = x;
				node.y = y;
				updateElement(node);
				commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
				commandGraph.storeCommand(m.cmd);
			}
			else {
				//revert
				commandGraph.revert(m);
			}
		}
		}).whenComplete(()=>endPropagation());
	}
	
	void cb_resize_node_subflowgraph_flowgraphdiagram(int width,int height,String direction,int id) {
		var node = findElement(id) as core.Node;
		if(node.width!=width||node.height!=height) {
			startPropagation();
			var ccm = commandGraph.sendResizeNodeCommand(id,width,height,direction,user);
			graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
				if(m is CompoundCommandMessage){
					if(m.type == 'basic_valid_answer'){
						node.width = width;
						node.height = height;
						updateElement(node);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
					}
					else {
						//revert
						commandGraph.revert(m);
					}
				}
			}).whenComplete(()=>endPropagation());
		}
	}
		
	void cb_rotate_node_subflowgraph_flowgraphdiagram(int angle,int id) {
		var node = findElement(id) as core.Node;
	   	var ccm =commandGraph.sendRotateNodeCommand(id,angle,user);
		startPropagation();
	   	graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					node.angle = angle;
					updateElement(node);
					commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
					commandGraph.storeCommand(m.cmd);
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	void create_node_start(flowgraph.Start node) {
		var x = node.x;
		var y = node.y;
		
		if(node.container is core.Container) {
			core.ModelElementContainer parent = node.container;
			while(parent!=null&&parent is core.Container) {
				x += (parent as core.Container).x;
				y += (parent as core.Container).y;
				parent = (parent as core.Container).container;
			}
		}
	   js.context.callMethod('create_node_start_flowgraphdiagram',
	   [   
	   		x+(node.width~/2),
	   		y+(node.height~/2),
		   	node.width,
		   	node.height,
		   	node.id,
		   	node.container.id,
		   	node.styleArgs(),
		   	node.$information(),
		   	node.$label()
	   ]);
	}
	
	void cb_create_node_start_flowgraphdiagram(int x,int y,int width,int height,String cellId,int containerId) {
		
		var container = findElement(containerId) as core.ModelElementContainer;
		var containerType =
		        container != null ? container.$type() : null;
	
	    var ccm = commandGraph.sendCreateNodeCommand("flowgraph.Start",x-(width~/2),
	    y-(height~/2),containerId,containerType,width,height,user);
	    startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					var cmd = m.cmd.queue.first;
					if(cmd is CreateNodeCommand){
						var node = new flowgraph.Start();
						node.x = x;
						node.y = y;
						node.id = cmd.delegateId;
						node.container = container;
						container.modelElements.add(node);
						updateElement(node,cellId: cellId);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
						if(container is! core.GraphModel){
							updateElement(container as core.ModelElement);
						}
						selectionChangedSC.add(node);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	void remove_node_cascade_start_flowgraphdiagram(flowgraph.Start node) {
		//remove connected edges
		node.outgoing.forEach((e) {
			e.target.incoming.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.outgoing.clear();
		node.incoming.forEach((e) {
			e.source.outgoing.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.incoming.clear();
		js.context.callMethod('remove_node_start_flowgraphdiagram', [node.id]);
	}
	
	void cb_remove_node_start_flowgraphdiagram(int id) {
		var node = findElement(id) as flowgraph.Start;
		var container = findElement(node.container.id) as core.ModelElementContainer;
		var ccm = commandGraph.sendRemoveNodeCommand(id,user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			selectionChangedSC.add(currentGraphModel);
			node.container = null;
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					commandGraph.receiveCommand(m,forceExecute: true);
					commandGraph.storeCommand(m.cmd);
					if(container is! core.GraphModel){
					updateElement(container as core.ModelElement);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	
	void cb_move_node_start_flowgraphdiagram(int x,int y,int id,containerId) {
		var container = findElement(containerId) as core.ModelElementContainer;
		var node = findElement(id) as core.Node;
		if(node.x==x && node.y==y){
			return;
		}
		var ccm = commandGraph.sendMoveNodeCommand(id, x, y, containerId, user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
		if(m is CompoundCommandMessage){
			if(m.type == 'basic_valid_answer'){
				if(!container.modelElements.contains(node)){
					node.container.modelElements.remove(node);
					node.container = container;
					container.modelElements.add(node);
					if(container is! core.GraphModel){
						updateElement(container as core.ModelElement);
					}
				}
				node.x = x;
				node.y = y;
				updateElement(node);
				commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
				commandGraph.storeCommand(m.cmd);
			}
			else {
				//revert
				commandGraph.revert(m);
			}
		}
		}).whenComplete(()=>endPropagation());
	}
	
	void cb_resize_node_start_flowgraphdiagram(int width,int height,String direction,int id) {
		var node = findElement(id) as core.Node;
		if(node.width!=width||node.height!=height) {
			startPropagation();
			var ccm = commandGraph.sendResizeNodeCommand(id,width,height,direction,user);
			graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
				if(m is CompoundCommandMessage){
					if(m.type == 'basic_valid_answer'){
						node.width = width;
						node.height = height;
						updateElement(node);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
					}
					else {
						//revert
						commandGraph.revert(m);
					}
				}
			}).whenComplete(()=>endPropagation());
		}
	}
		
	void cb_rotate_node_start_flowgraphdiagram(int angle,int id) {
		var node = findElement(id) as core.Node;
	   	var ccm =commandGraph.sendRotateNodeCommand(id,angle,user);
		startPropagation();
	   	graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					node.angle = angle;
					updateElement(node);
					commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
					commandGraph.storeCommand(m.cmd);
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	void create_node_activity(flowgraph.Activity node) {
		var x = node.x;
		var y = node.y;
		
		if(node.container is core.Container) {
			core.ModelElementContainer parent = node.container;
			while(parent!=null&&parent is core.Container) {
				x += (parent as core.Container).x;
				y += (parent as core.Container).y;
				parent = (parent as core.Container).container;
			}
		}
	   js.context.callMethod('create_node_activity_flowgraphdiagram',
	   [   
	   		x,
	   		y,
		   	node.width,
		   	node.height,
		   	node.id,
		   	node.container.id,
		   	node.styleArgs(),
		   	node.$information(),
		   	node.$label()
	   ]);
	}
	
	void cb_create_node_activity_flowgraphdiagram(int x,int y,int width,int height,String cellId,int containerId) {
		
		var container = findElement(containerId) as core.ModelElementContainer;
		var containerType =
		        container != null ? container.$type() : null;
	
	    var ccm = commandGraph.sendCreateNodeCommand("flowgraph.Activity",x,
	    y,containerId,containerType,width,height,user);
	    startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					var cmd = m.cmd.queue.first;
					if(cmd is CreateNodeCommand){
						var node = new flowgraph.Activity();
						node.x = x;
						node.y = y;
						node.id = cmd.delegateId;
						node.container = container;
						container.modelElements.add(node);
						updateElement(node,cellId: cellId);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
						if(container is! core.GraphModel){
							updateElement(container as core.ModelElement);
						}
						selectionChangedSC.add(node);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	void remove_node_cascade_activity_flowgraphdiagram(flowgraph.Activity node) {
		//remove connected edges
		node.outgoing.forEach((e) {
			e.target.incoming.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.outgoing.clear();
		node.incoming.forEach((e) {
			e.source.outgoing.remove(e);
			e.container.modelElements.remove(e);
			e.container = null;
			js.context.callMethod('remove_edge__flowgraphdiagram', [e.id]);
		});
		node.incoming.clear();
		js.context.callMethod('remove_node_activity_flowgraphdiagram', [node.id]);
	}
	
	void cb_remove_node_activity_flowgraphdiagram(int id) {
		var node = findElement(id) as flowgraph.Activity;
		var container = findElement(node.container.id) as core.ModelElementContainer;
		var ccm = commandGraph.sendRemoveNodeCommand(id,user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			selectionChangedSC.add(currentGraphModel);
			node.container = null;
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					commandGraph.receiveCommand(m,forceExecute: true);
					commandGraph.storeCommand(m.cmd);
					if(container is! core.GraphModel){
					updateElement(container as core.ModelElement);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	
	void cb_move_node_activity_flowgraphdiagram(int x,int y,int id,containerId) {
		var container = findElement(containerId) as core.ModelElementContainer;
		var node = findElement(id) as core.Node;
		if(node.x==x && node.y==y){
			return;
		}
		var ccm = commandGraph.sendMoveNodeCommand(id, x, y, containerId, user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
		if(m is CompoundCommandMessage){
			if(m.type == 'basic_valid_answer'){
				if(!container.modelElements.contains(node)){
					node.container.modelElements.remove(node);
					node.container = container;
					container.modelElements.add(node);
					if(container is! core.GraphModel){
						updateElement(container as core.ModelElement);
					}
				}
				node.x = x;
				node.y = y;
				updateElement(node);
				commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
				commandGraph.storeCommand(m.cmd);
			}
			else {
				//revert
				commandGraph.revert(m);
			}
		}
		}).whenComplete(()=>endPropagation());
	}
	
	void cb_resize_node_activity_flowgraphdiagram(int width,int height,String direction,int id) {
		var node = findElement(id) as core.Node;
		if(node.width!=width||node.height!=height) {
			startPropagation();
			var ccm = commandGraph.sendResizeNodeCommand(id,width,height,direction,user);
			graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
				if(m is CompoundCommandMessage){
					if(m.type == 'basic_valid_answer'){
						node.width = width;
						node.height = height;
						updateElement(node);
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
					}
					else {
						//revert
						commandGraph.revert(m);
					}
				}
			}).whenComplete(()=>endPropagation());
		}
	}
		
	void cb_rotate_node_activity_flowgraphdiagram(int angle,int id) {
		var node = findElement(id) as core.Node;
	   	var ccm =commandGraph.sendRotateNodeCommand(id,angle,user);
		startPropagation();
	   	graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					node.angle = angle;
					updateElement(node);
					commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
					commandGraph.storeCommand(m.cmd);
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	// for each edge
	
	void create_edge_transition_flowgraphdiagram(flowgraph.Transition edge) {
	   js.context.callMethod('create_edge_transition_flowgraphdiagram',
	   [
		   edge.source.id,
		   edge.target.id,
		   edge.id,
		   new js.JsArray.from(edge.bendingPoints.map((n){
		   	     	var arr = new js.JsArray();
		   			arr['x'] = n.x;
		   			arr['y'] = n.y;
		   			return arr;
		   }).toList()),
		   edge.styleArgs(),edge.$information(),edge.$label()
	   ]);
	}
	
	void cb_create_edge_transition_flowgraphdiagram(int sourceId,int targetId,String cellId, List positions) {
		var source = findElement(sourceId) as core.Node;
		var target = findElement(targetId) as core.Node;
		var currentBendpoints = new List<core.BendingPoint>();
		if(positions!=null){
		  positions.forEach((p){
			var b = new core.BendingPoint();
			b.x = p['x'];
			b.y = p['y'];
			currentBendpoints.add(b);
			 });
		}
	   	var ccm = commandGraph.sendCreateEdgeCommand("flowgraph.Transition",targetId,sourceId,target.$type(),source.$type(),currentBendpoints,user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					var cmd = m.cmd.queue.first;
					if(cmd is CreateEdgeCommand){
						var edge = new flowgraph.Transition();
						edge.id = cmd.delegateId;
						edge.container = currentGraphModel;
						currentGraphModel.modelElements.add(edge);
						edge.source = source;
						source.outgoing.add(edge);
						edge.target = target;
						target.incoming.add(edge);
						edge.bendingPoints = new List.from(currentBendpoints);
						updateElement(edge,cellId: cellId);
						updateElement(source);
						updateElement(target);
						ccm.cmd.queue.first.delegateId=edge.id;
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
						selectionChangedSC.add(edge);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	
	void cb_remove_edge_transition_flowgraphdiagram(int id) {
		var edge = findElement(id) as core.Edge;
		var ccm = commandGraph.sendRemoveEdgeCommand(id,edge.source.id,edge.target.id,edge.source.$type(),edge.target.$type(),"flowgraph.Transition",user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
	   		if(m is CompoundCommandMessage){
	   			if(m.type == 'basic_valid_answer'){
	   				selectionChangedSC.add(currentGraphModel);
	   				var source = edge.source;
	   				source.outgoing.remove(edge);
	   				updateElement(source);
	   				edge.source = null;
	   				var target = edge.target;
	   				target.incoming.remove(edge);
	   				updateElement(target);
	   				edge.target = null;
	   				edge.container.modelElements.remove(edge);
	   				edge.container = null;
	   				commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
	   				commandGraph.storeCommand(m.cmd);
	   			}
	   			else {
	   				//revert
	   				commandGraph.revert(m);
	   			}
	   		}
		}).whenComplete(()=>endPropagation());
	}
	
	void cb_reconnect_edge_transition_flowgraphdiagram(int sourceId,int targetId,int id) {
		var edge = findElement(id) as core.Edge;
		var source = findElement(sourceId) as core.Node;
		var target = findElement(targetId) as core.Node;
		if(edge.source.id!=sourceId||edge.target.id!=targetId) {
		   	var ccm = commandGraph.sendReconnectEdgeCommand(id,sourceId,targetId,user);
			startPropagation();
		 	graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
		 	 		if(m is CompoundCommandMessage){
		 	 			if(m.type == 'basic_valid_answer'){
		 	 				if(edge.target != target){
		 	 					edge.target.incoming.remove(edge);
		 	 					edge.target = target;
		 	 					target.incoming.add(edge);
		 	 					updateElement(target);
		 	 				}
		 	 				if(edge.source != source){
		 	 					edge.source.outgoing.remove(edge);
		 	 					edge.source = source;
		 	 					source.outgoing.add(edge);
		 	 					updateElement(source);
		 	 				}
		 	 				updateElement(edge);
		 	 				commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
		 	 				commandGraph.storeCommand(m.cmd);
		 	 			}
		 	 			else {
		 	 				//revert
		 	 				commandGraph.revert(m);
		 	 			}
		 	 		}
	 		}).whenComplete(()=>endPropagation());
		}
	}
	
	void create_edge_labeledtransition_flowgraphdiagram(flowgraph.LabeledTransition edge) {
	   js.context.callMethod('create_edge_labeledtransition_flowgraphdiagram',
	   [
		   edge.source.id,
		   edge.target.id,
		   edge.id,
		   new js.JsArray.from(edge.bendingPoints.map((n){
		   	     	var arr = new js.JsArray();
		   			arr['x'] = n.x;
		   			arr['y'] = n.y;
		   			return arr;
		   }).toList()),
		   edge.styleArgs(),edge.$information(),edge.$label()
	   ]);
	}
	
	void cb_create_edge_labeledtransition_flowgraphdiagram(int sourceId,int targetId,String cellId, List positions) {
		var source = findElement(sourceId) as core.Node;
		var target = findElement(targetId) as core.Node;
		var currentBendpoints = new List<core.BendingPoint>();
		if(positions!=null){
		  positions.forEach((p){
			var b = new core.BendingPoint();
			b.x = p['x'];
			b.y = p['y'];
			currentBendpoints.add(b);
			 });
		}
	   	var ccm = commandGraph.sendCreateEdgeCommand("flowgraph.LabeledTransition",targetId,sourceId,target.$type(),source.$type(),currentBendpoints,user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			if(m is CompoundCommandMessage){
				if(m.type == 'basic_valid_answer'){
					var cmd = m.cmd.queue.first;
					if(cmd is CreateEdgeCommand){
						var edge = new flowgraph.LabeledTransition();
						edge.id = cmd.delegateId;
						edge.container = currentGraphModel;
						currentGraphModel.modelElements.add(edge);
						edge.source = source;
						source.outgoing.add(edge);
						edge.target = target;
						target.incoming.add(edge);
						edge.bendingPoints = new List.from(currentBendpoints);
						updateElement(edge,cellId: cellId);
						updateElement(source);
						updateElement(target);
						ccm.cmd.queue.first.delegateId=edge.id;
						commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
						commandGraph.storeCommand(m.cmd);
						selectionChangedSC.add(edge);
					}
				}
				else {
					//revert
					commandGraph.revert(m);
				}
			}
		}).whenComplete(()=>endPropagation());
	}
	
	
	void cb_remove_edge_labeledtransition_flowgraphdiagram(int id) {
		var edge = findElement(id) as core.Edge;
		var ccm = commandGraph.sendRemoveEdgeCommand(id,edge.source.id,edge.target.id,edge.source.$type(),edge.target.$type(),"flowgraph.LabeledTransition",user);
		startPropagation();
		graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
	   		if(m is CompoundCommandMessage){
	   			if(m.type == 'basic_valid_answer'){
	   				selectionChangedSC.add(currentGraphModel);
	   				var source = edge.source;
	   				source.outgoing.remove(edge);
	   				updateElement(source);
	   				edge.source = null;
	   				var target = edge.target;
	   				target.incoming.remove(edge);
	   				updateElement(target);
	   				edge.target = null;
	   				edge.container.modelElements.remove(edge);
	   				edge.container = null;
	   				commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
	   				commandGraph.storeCommand(m.cmd);
	   			}
	   			else {
	   				//revert
	   				commandGraph.revert(m);
	   			}
	   		}
		}).whenComplete(()=>endPropagation());
	}
	
	void cb_reconnect_edge_labeledtransition_flowgraphdiagram(int sourceId,int targetId,int id) {
		var edge = findElement(id) as core.Edge;
		var source = findElement(sourceId) as core.Node;
		var target = findElement(targetId) as core.Node;
		if(edge.source.id!=sourceId||edge.target.id!=targetId) {
		   	var ccm = commandGraph.sendReconnectEdgeCommand(id,sourceId,targetId,user);
			startPropagation();
		 	graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
		 	 		if(m is CompoundCommandMessage){
		 	 			if(m.type == 'basic_valid_answer'){
		 	 				if(edge.target != target){
		 	 					edge.target.incoming.remove(edge);
		 	 					edge.target = target;
		 	 					target.incoming.add(edge);
		 	 					updateElement(target);
		 	 				}
		 	 				if(edge.source != source){
		 	 					edge.source.outgoing.remove(edge);
		 	 					edge.source = source;
		 	 					source.outgoing.add(edge);
		 	 					updateElement(source);
		 	 				}
		 	 				updateElement(edge);
		 	 				commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
		 	 				commandGraph.storeCommand(m.cmd);
		 	 			}
		 	 			else {
		 	 				//revert
		 	 				commandGraph.revert(m);
		 	 			}
		 	 		}
	 		}).whenComplete(()=>endPropagation());
		}
	}

	void cb_update_bendpoint(List positions,int id) {
		var edge = findElement(id) as core.Edge;
		
		//check if update is present
		if(check_bendpoint_update(edge,positions)) {
			var currentBendpoints = new List<core.BendingPoint>();
			if(positions != null) {
				positions.forEach((p) {
					var b = new core.BendingPoint();
					b.x = p['x'];
					b.y = p['y'];
					currentBendpoints.add(b);
				});
			}
			var ccm = commandGraph.sendUpdateBendPointCommand(id,currentBendpoints,new List.from(edge.bendingPoints),user);
			startPropagation();
			graphService.sendMessage(ccm,"flowgraphdiagram",currentGraphModel.id).then((m){
			   	  	if(m is CompoundCommandMessage){
			   	  		if(m.type == 'basic_valid_answer'){
			   	  			edge.bendingPoints = new List();
			   	  			positions.forEach((p){
			   	  				var b = new core.BendingPoint();
			   	  				b.x = p['x'];
			   	  				b.y = p['y'];
			   	  				edge.bendingPoints.add(b);
			   	  			});
			   	  			commandGraph.receiveCommand(m.customCommands(),forceExecute: true);
			   	  			commandGraph.storeCommand(m.cmd);
			   	  		}
			   	  		else {
			   	  			//revert
			   	  			commandGraph.revert(m);
			   	  		}
			   	  	}
		   	}).whenComplete(()=>endPropagation());
		}
	}
}

