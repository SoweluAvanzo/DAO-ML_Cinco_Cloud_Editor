package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.canvas.graphs.graphmodel

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import java.util.HashMap
import java.util.HashSet
import mgl.ContainingElement
import mgl.Edge
import mgl.EdgeElementConnection
import mgl.GraphModel
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import org.eclipse.emf.ecore.EObject
import style.Styles
import graphmodel.ModelElementContainer
import org.eclipse.emf.ecore.EPackage

class GraphmodelComponent extends Generatable {

	new(GeneratorCompound gc) {
		super(gc)
	}

	def fileNameGraphModelCommandGraph(GraphModel graphModel) '''«graphModel.commandGraphFile»'''

	def contentGraphModelCommandGraph(GraphModel g, Styles styles) {
		val inheritedConcreteType = g.extends !== null ? g.extends.firstConcreteType as GraphModel : null
		val hasConcreteCommandGraph = inheritedConcreteType !== null && inheritedConcreteType !== g
		val commandGraphName = hasConcreteCommandGraph ? '''«inheritedConcreteType.name.fuEscapeDart»CommandGraph''' : '''CommandGraph'''
		val primeReferencedPackages = g.primeReferencedElements.map[modelPackage].filter(MGLModel).toSet
		val ecoreReferencedPackages = g.ecorePrimeRefs.map[type].map[modelPackage].filter(EPackage).toSet
		'''
			import 'package:«gc.projectName.escapeDart»/src/model/core.dart' as core;
			import 'package:«gc.projectName.escapeDart»/src/model/command.dart';
			«IF hasConcreteCommandGraph»
				import 'package:«gc.projectName.escapeDart»/«inheritedConcreteType.commandGraphPath»';
			«ELSE»
				import 'package:«gc.projectName.escapeDart»/src/model/command_graph.dart';
			«ENDIF»
			import 'package:«gc.projectName.escapeDart»/src/filesupport/fileuploader.dart';
			
			import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.modelPackage.name.lowEscapeDart»;
			
			«IF !primeReferencedPackages.empty || !ecoreReferencedPackages.empty»
				// prime referenced models
				«FOR pm : primeReferencedPackages.filter[!it.equals(g)]»
					import 'package:«gc.projectName.escapeDart»/«pm.modelFilePath»' as «pm.name.lowEscapeDart»;
				«ENDFOR»
				«FOR pm : ecoreReferencedPackages»
					import 'package:«gc.projectName.escapeDart»/«pm.modelFilePath»' as «pm.name.lowEscapeDart»;
				«ENDFOR»
			«ENDIF»
			
			import 'dart:js' as js;
			
			class «g.name.fuEscapeDart»CommandGraph extends «commandGraphName»{
			
			«g.name.fuEscapeDart»CommandGraph(core.GraphModel currentGraphModel,List<HighlightCommand> highlightings,{Map jsog}) : super(currentGraphModel,highlightings,jsog:jsog);
			
			@override
			core.Node execCreateNodeType(String type,core.PyroElement primeElement)
			{
			  // for each node type
				«FOR elem : g.nodes.filter[!isIsAbstract] SEPARATOR " else "
				»if(type == '«elem.typeName»'){
					var newNode = new «elem.dartFQN»();
					«IF elem.prime»
						newNode.«elem.primeReference.name.escapeDart» = primeElement as «elem.primeReference.type.dartFQN»;
					«ENDIF»
					return newNode;
				}«
				ENDFOR»
			  throw new Exception("Unkown node type ${type}");
			}
			
			@override
			core.Edge execCreateEdgeType(String type)
			{
			  core.Edge edge;
			   «FOR elem : g.edges.filter[!isIsAbstract] SEPARATOR " else "
			   »if(type == '«elem.typeName»') {
			   	edge = new «elem.dartFQN»();
			   }«
			   ENDFOR»
			   return edge;
			}
			
			@override
			void execCreateEdgeCommandCanvas(CreateEdgeCommand cmd) {
			    core.ModelElement e = findElement(cmd.delegateId);
			    if(e == null) {
			    	return;
			    }
			    «FOR edge : g.edges.filter[!isIsAbstract]»
			    	if(cmd.type == '«edge.typeName»'){
						var positions = new js.JsArray();
						cmd.positions.forEach((p){
								var pos = new js.JsArray();
								pos['x'] = p.x;
								pos['y'] = p.y;
								positions.add(pos);
						});
			    		js.context.callMethod('create_edge_«edge.jsCall(g)»',[
			    			cmd.sourceId, cmd.targetId, cmd.delegateId, positions, js.JsObject.jsify(e.styleArgs()), e.$information(), e.$label()
			    		]);
			    		return;
			    	}
				 «ENDFOR»
			}
			
			@override
			void execRemoveEdgeCanvas(int id,String type) {
				«FOR edge : g.edges.filter[!isIsAbstract] SEPARATOR " else "
				»if(type=='«edge.typeName»'){
					js.context.callMethod('remove_edge_«edge.jsCall(g)»',[
						id
					]);
				}«
				ENDFOR»
			}
			
			@override
			void execReconnectEdgeCommandCanvas(ReconnectEdgeCommand cmd) {
				«FOR edge : g.edges.filter[!isIsAbstract] SEPARATOR " else "
				»if(cmd.type=='«edge.typeName»'){
					js.context.callMethod('reconnect_edge_«edge.jsCall(g)»',[
						cmd.sourceId,cmd.targetId,cmd.delegateId
					]);
				}«
				ENDFOR»
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
					«FOR node : g.nodes.filter[!isIsAbstract] SEPARATOR " else "
					»if(cmd.type=='«node.typeName»'){
					js.context.callMethod(
						'create_node_«node.jsCall(g)»',
						[
							x,y,cmd.width,cmd.height,cmd.delegateId,cmd.containerId,js.JsObject.jsify(e.styleArgs()),e.$information(),e.$label()«IF node.prime»,cmd.primeId«ENDIF»
						]);
						return;
					}«
					ENDFOR»
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
					«FOR node : g.nodes.filter[!isIsAbstract] SEPARATOR " else "
					»if(type=='«node.typeName»'){
							js.context.callMethod('move_node_«node.jsCall(g)»',[
								x,
								y,
								delegateId,
								containerId
							]);
							return;
					}«
					ENDFOR»
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
					«FOR node : g.nodes.filter[!isIsAbstract]»
						if(type=='«node.typeName»'){
							js.context.callMethod('remove_node_«node.jsCall(g)»',[
								id
							]);
							return;
						}
					«ENDFOR»
				}
				
				@override
				void execResizeNodeCommandCanvas(ResizeNodeCommand cmd) {
				  «FOR node : g.nodes.filter[!isIsAbstract]»
				  	if(cmd.type=='«node.typeName»'){
				  	 js.context.callMethod('resize_node_«node.jsCall(g)»',[
				  	   cmd.width,cmd.height,cmd.direction,cmd.delegateId
				  	 ]);
				  	 return;
				  	}
				  «ENDFOR»
				}
				
				@override
				void execRotateNodeCommandCanvas(RotateNodeCommand cmd) {
				  «FOR node : g.nodes.filter[!isIsAbstract]»
				  	if(cmd.type=='«node.typeName»'){
				  	 js.context.callMethod('rotate_node_«node.jsCall(g)»',[
				  	   cmd.angle,cmd.delegateId
				  	 ]);
				  	}
				  «ENDFOR»
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
				 js.context.callMethod('update_bendpoint_«g.jsCall»',[
				   positions,cmd.delegateId
				 ]);
				}
				
				@override
				void execUpdateElementCanvas(UpdateCommand cmd) {
				  core.IdentifiableElement identifiableElement = cmd.element;
					if(identifiableElement is core.ModelElement){
						js.context.callMethod('update_element_«g.jsCall»',[
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
					js.context.callMethod('update_element_appearance_«g.jsCall»',[
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
						FileReference.toDownloadPath(
							cmd.imagePath
						)
					]);
				}
				
				@override
				void execHighlightCanvas(HighlightCommand cmd) {
					var preColor = js.context.callMethod('update_element_highlight_«g.jsCall»',[
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
					js.context.callMethod('update_element_highlight_«g.jsCall»',[
						cmd.id,
						cmd.pre_background_r,cmd.pre_background_g,cmd.pre_background_b,
						cmd.pre_foreground_r,cmd.pre_foreground_g,cmd.pre_foreground_b
					]);
				}
			}
		'''
	}
	
	def propagation(CharSequence s) '''
		startPropagation().then((_){
			«s»
		}).then((_)=>endPropagation());
	'''

	def fileNameGraphModelComponent(GraphModel graphModel) '''«graphModel.componentFileDart»'''

	def contentGraphModelComponent(GraphModel g, Styles styles) {
		val primeReferencedPackages = g.primeReferencedElements.map[modelPackage].filter(MGLModel).toSet
		val ecoreReferencedPackages = g.ecorePrimeRefs.map[type].map[modelPackage].filter(EPackage).toSet
		'''
		import 'dart:async';
		import 'dart:html' as html;
		import 'dart:convert' as convert;
		import 'dart:js' as js;
		import 'package:angular/angular.dart';
		
		import '../../../../../model/core.dart' as core;
		import '../../../../../model/message.dart';
		import '../../../../../model/command.dart';
		import '../../../../../model/check.dart';
		import '../../../../../service/base_service.dart';
		import '../../../../../service/check_service.dart';
		import '../../../../../service/editor_data_service.dart';
		import '../../../../../pages/editor/palette/list/list_view.dart';
		import '../../../../../pages/shared/context_menu/context_menu.dart';
		import '../../../../../service/context_menu_service.dart';
		import '../../../../../service/graph_service.dart';
		import '../../dialog/message_dialog.dart';
		import '../../dialog/display_dialog.dart';
		
		import '«g.commandGraphFile»';
		import 'package:«gc.projectName.escapeDart»/«g.paletteBuilderPath»';
		import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.modelPackage.name.lowEscapeDart»;
		
		«IF !primeReferencedPackages.empty && !ecoreReferencedPackages.empty»
			// prime referenced models
			«FOR pm : primeReferencedPackages.filter[!it.equals(g)]»
				import 'package:«gc.projectName.escapeDart»/«pm.modelFilePath»' as «pm.name.lowEscapeDart»;
			«ENDFOR»
			«FOR pm : ecoreReferencedPackages»
				import 'package:«gc.projectName.escapeDart»/«pm.modelFilePath»' as «pm.name.lowEscapeDart»;
			«ENDFOR»
		«ENDIF»
		
		@Component(
			selector: '«g.name.lowEscapeDart»-canvas',
			templateUrl: '«g.componentFileHTML»',
			styleUrls: const [],
			directives: const [coreDirectives,MessageDialog,DisplayDialog]
		)
		class «g.name.fuEscapeDart»CanvasComponent implements OnInit, OnDestroy {
			
			final selectionChangedSC = new StreamController();
			@Output() Stream get selectionChanged => selectionChangedSC.stream;
			
			final selectionChangedModalSC = new StreamController();
			@Output() Stream get selectionChangedModal => selectionChangedModalSC.stream;
			
			final hasChangedSC = new StreamController();
			@Output() Stream get hasChanged => hasChangedSC.stream;
			
			final jumpToSC = new StreamController();
			@Output() Stream get jumpTo => jumpToSC.stream;
			
			@Input()
			«g.dartFQN» currentGraphModel;
			
			@Input()
			core.PyroUser user;
			
			@Input()
			bool isFullScreen;
			
			@Input()
			core.LocalGraphModelSettings currentLocalSettings;
			
			«g.name.fuEscapeDart»CommandGraph commandGraph;
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
			bool loading = true;
			
			List<HighlightCommand> highlightings = new List();
			core.PyroElement currentSelection = null;
			
			///check levels
			@Input()
			bool isError = true;
			@Input()
			bool isWarning = true;
			@Input()
			bool isInfo = true;
			
			«g.name.fuEscapeDart»CanvasComponent(
				GraphService this.graphService,
				this.checkService,
				this._editorDataService,
				this._contextMenuService
			) {}
			
			@override
			void ngOnInit() {
				loading = true;
				initCanvas().then((_) {
					BaseService.getTicket().then((ticket) => {
						activateWebSocket(ticket)
					});
				});
			}
			
			@override
			ngOnDestroy() {
				this.closeWebSocket();
			}
			
			Future<dynamic> initCanvas() {
				return graphService.loadCommandGraph«g.name.fuEscapeDart»(currentGraphModel,highlightings).then((cg){
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
					js.context.callMethod("load_«g.jsCall»",[
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
						«FOR elem:g.nodesAndEdges.filter[!isIsAbstract]  SEPARATOR ","»
							«IF elem instanceof Node»
								cb_create_node_«elem.jsCall(g)»,
								cb_remove_node_«elem.jsCall(g)»,
								cb_move_node_«elem.jsCall(g)»,
								cb_resize_node_«elem.jsCall(g)»,
								cb_rotate_node_«elem.jsCall(g)»
							«ENDIF»
							«IF elem instanceof Edge»
								cb_create_edge_«elem.jsCall(g)»,
								cb_remove_edge_«elem.jsCall(g)»,
								cb_reconnect_edge_«elem.jsCall(g)»
							«ENDIF»
						«ENDFOR»
					]);
					«IF g.hasAppearanceProvider(styles)»
						graphService.appearances«g.name.fuEscapeDart»(currentGraphModel).then((data){
							var jsog = convert.jsonDecode(data);
							var m = Message.fromJSOG(jsog);
							startPropagation().then((_) {
								executeUpdateCommand(m, true);
							}).then((_) => endPropagation());
						});
					«ENDIF»
				}).whenComplete(()=>loading = false);
			}
			
			void initialized() {
				//add nodes and container bottom up
				«'''
					initContainerDeeply(currentGraphModel);
					for(var node in currentGraphModel.modelElements){
						if(node is core.ModelElementContainer && (node as core.ModelElementContainer).modelElements.isNotEmpty){
							initContainerDeeply(node as core.ModelElementContainer);
						}
					};
					//connect by edges
					currentGraphModel.modelElements.where((n)=>n is core.Edge).forEach((e){
						«FOR edge : g.edges.filter[!isIsAbstract] SEPARATOR " else "
						»if(e.$type() == "«edge.typeName»"){
							create_edge_«edge.jsCall(g)»(e);
						}«
						ENDFOR»
					});
					updateCheckLevel(isError,isWarning,isInfo);
				'''.propagation»
			}
			
			/*
			 * websocket functions
			 */
			
			void closeWebSocket() {
				if(this.webSocketGraphModel != null && this.webSocketGraphModel.readyState == html.WebSocket.OPEN) {
					html.window.console.debug("Closing Websocket webSocketCurrentUser");
					this.webSocketGraphModel.close();
					this.webSocketGraphModel = null;
					_editorDataService.graphModelWebSocketSC.add(null);
				}
			}
			
			void activateWebSocket(String ticket) {
				if (this.currentGraphModel != null && user != null && this.webSocketGraphModel == null) {
					this.webSocketGraphModel = new html.WebSocket(
						'${graphService.getBaseUrl(protocol: 'ws:')}/ws/graphmodel/${currentGraphModel.id}/${ticket}/private'
					);
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
							} else if (event == 'display') {
								displayMessages = DisplayMessages.fromJSOG(jsog["content"]);
								showDisplayDialog = true;	
							}  else if (event == 'updateCursorPosition') {
								// update cursor
								var senderId = jsog['senderId'];
								if (senderId.toString() != user.id.toString()) {
									var content = jsog['content'];
									var x = content['x'];
									var y = content['y'];
									var userName = content['userName'];
									
									js.JsObject cursorManager = js.context['\$cursor_manager_«g.jsCall»'];
									cursorManager.callMethod('update_cursor', [senderId, userName, x, y]);  
								}
							} else if (event == '«websocketEventPrime»') {
								var m = Message.fromJSOG(jsog['content']);
								hasChangedSC.add({ "type": "«websocketEventPrime»", "message": m });
							} else {
								// update graph model changed by another user
								if (jsog['content']['messageType'] == 'graphmodel') {
									startPropagation().then((_) {
										var _g = «g.dartFQN».fromJSOG(jsog['content'], new Map());
										currentGraphModel.merge(_g, structureOnly: true);
										currentGraphModel.connector = _g.connector;
										currentGraphModel.router = _g.router;
										currentGraphModel.filename = _g.filename;
										currentGraphModel.height = _g.height;
										currentGraphModel.width = _g.width;
										js.context.callMethod('update_routing_«g.jsCall»', [
											currentGraphModel.router,
											currentGraphModel.connector
										]);
									}).then((_) => endPropagation());
								} else {
									var m = Message.fromJSOG(jsog['content']);
									startPropagation().then((_) {
										executeUpdateCommand(m, true);
									}).then((_) => endPropagation());
								}
							}
						}
					});
					this.webSocketGraphModel.onClose.listen((html.CloseEvent e) {
						if (e.code == 4001) {
							//graphmodel has been deleted or access denied
						}
						html.window.console.debug("[PYRO] onClose GraphModel Websocket");
					});
					this.webSocketGraphModel.onError.listen((e) {
						html.window.console.debug("[PYRO] Error on GraphModel Websocket: ${e.toString()}");
					});
				}
			}
			
			/*
			 * gui functions
			 */
			
			Future<Null> startPropagation(){
				js.context.callMethod('start_propagation_«g.jsCall»',[]);
				return new Future.value(null);
			}
			
			void endPropagation(){
				js.context.callMethod('end_propagation_«g.jsCall»',[]);
				«IF g.hasChecks»
					//reload checks
					fetchChecks();
				«ENDIF»
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
			
			void refreshChecks(CheckResults crs) {
				if (crs != null) {
				js.context.callMethod("refresh_checks_«g.jsCall»",[
					crs.toJS()
				]);	
				}
			}
			
			Future<Message> executeGraphmodelButton(String key) async {
				return graphService.executeGraphmodelButton(currentGraphModel, key, highlightings).then((m){
					«'''
						«'''
							commandGraph.receiveCommand(m, forceExecute: true);
						'''.checkCommand»
					'''.propagation»
					return m;
				});
			}
			
			void updateCheckLevel(bool isError,bool isWarning,bool isInfo) {
				this.isError = isError;
				this.isWarning = isWarning;
				this.isInfo = isInfo;
				«IF g.hasChecks»
					fetchChecks();
				«ENDIF»
			}
			
			void updateGlueline(bool isGlueline) {
				js.context.callMethod("refresh_gluelines_«g.jsCall»",[isGlueline]);
			}
			«IF g.hasChecks»
				
				void fetchChecks() {
					checkService.read(currentGraphModel).then((crs){
						CheckResults filteredCRS = CheckResults.filterChecks(CheckResults.copy(crs),isError,isWarning,isInfo);
						refreshChecks(filteredCRS);
					});
				}
			«ENDIF»
			
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
						js.context.callMethod('update_scale_«g.jsCall»',[currentGraphModel.scale]);
					});
				} else {
					js.context.callMethod('update_scale_«g.jsCall»',[currentGraphModel.scale]);
				}
			}
			
			void updateRouting() {
				graphService.updateGraphModel(currentGraphModel).then((_){
					js.context.callMethod('update_routing_«g.jsCall»',[currentGraphModel.router,currentGraphModel.connector]);
				});
			}
			
			void undo() {
				var ccm = commandGraph.undo(user);
				sendCommandMessage(ccm, null, null);
			}
			
			void redo() {
				var ccm = commandGraph.redo(user);
				sendCommandMessage(ccm, null, null);
			}
			
			/*
			 * property functions
			 */
			
			void updateProperties(core.IdentifiableElement ie) {
				if(ie is! core.GraphModel){
					currentGraphModel.allElements().where((n)=>n is core.ModelElement).forEach((n)=>updateElement(n));
				} else {
					if(ie.$type() == "«g.typeName»"){
						currentGraphModel.merge(ie,structureOnly:false);
					}
				}
				
				//check for prime referencable element in same graph and update
				«FOR pr : g.MGLModel.primeRefs.filter[referencedElement.graphModels.contains(g)] SEPARATOR " else "
				»if(ie.$type() == "«pr.referencedElement.typeName»") {
					//update all prime nodes for this element
					
					currentGraphModel.allElements()
						.where((n)=> n.$type() == "«(pr.eContainer as ModelElement).typeName»")
						.forEach((n)=>updateElement(n));
				}«
				ENDFOR»
			}
			
			void cb_property_persist(int id,String value) {
				var elem = currentGraphModel.allElements().where((n)=>n.id == id).first;
				PropertyMessage pm = new PropertyMessage(
				elem.id,
				elem.runtimeType.toString(),
				elem,
				user.id
				);
				«FOR e : g.nodes.filter[directlyEditable] SEPARATOR "else "
				»if(elem.$type() == "«e.typeName»") {
					(elem as «e.dartFQN»).«e.directlyEditableAttribute.name.escapeDart» = value;
				}«
				ENDFOR»
				sendCommandMessage(pm, null, null);
			}
			
			/*
			 * model-element functions
			 */
			
			void updateElement(core.ModelElement elem, {String cellId}) {
				if(elem == null) return;
				js.context.callMethod('update_element_«g.jsCall»',[
					cellId,
					elem.id,
					elem.styleArgs(),
					elem.$information(),
					elem.$label()
				]);
			}
			
			void initContainerDeeply(core.ModelElementContainer container) {
				«{
					val nodes = g.nodes.filter[!isIsAbstract]
					'''
						«IF !nodes.empty»
							for(var node in container.modelElements) {
								«FOR node : nodes»
									if(node.$type() == "«node.typeName»") {
										create_node_«node.jsCall(g)»(node);
										«IF node instanceof ModelElementContainer»
											initContainerDeeply(node);
										«ENDIF»
										continue;
									}
								«ENDFOR»
							}
						«ENDIF»
					'''
				}»
			}
			
			core.IdentifiableElement findElement(int id) {
				if(id==currentGraphModel.id){
					return currentGraphModel;
				}
				var elements = currentGraphModel.allElements();
				for(var e in elements) {
					if(e.id == id) {
						return e;
					}
				}
				return null; 
			}
			
			/*
			 * event action functions
			 */
			
			void cb_get_custom_actions(int id,int x,int y,int canvasX, int canvasY) {
				«'''
					graphService.fetchCustomActionsFor«g.name.escapeDart»(id,currentGraphModel).then((map){
						
						List<MapList> mapList = «g.name.fuEscapeDart»PaletteBuilder.build(currentGraphModel);
						List<ContextMenuEntry> mainMenuItems = new List();
						List<ContextMenuEntry> menuItems = new List();
						
						mapList.forEach((ml) {
							ml.values.forEach((val){
							if(cb_can_move_node(-1,id,tmpNode:val.instance) == true) {
								menuItems.add(ContextMenuItem(null, val.name, true, () {
									js.context.callMethod("create_node_«g.jsCall»_after_drop", [canvasX,canvasY,val.identifier]);	
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
				'''.propagation»
			}
			
			void cb_fire_cm_action(String fqn,int id) {
				graphService.triggerCustomActionsFor«g.name.escapeDart»(id, currentGraphModel,fqn,highlightings).then((m){
					«'''
						«'''
							commandGraph.receiveCommand(m,forceExecute: true);
						'''.checkCommand("basic_valid_answer")»
					'''.propagation»
				});
			}
			
			void cb_fire_dbc_actions(int id) {
				// refresh currentgraph, to prevent an update-anomaly in terms of prime-references
				// to keep the model eventually consistent
				graphService.loadCommandGraph(currentGraphModel, highlightings).then((_){
					graphService.triggerDoubleClickActionsFor«g.name.escapeDart»(id, currentGraphModel,highlightings).then((m){
						«'''
							«'''
									commandGraph.receiveCommand(m,forceExecute: true);
							'''.checkCommand("basic_valid_answer")»
						'''.propagation»
					}).then((_){
						«{
							val jumpablePrimeNodes = g.nodes.filter[prime].filter[hasJumpToAnnotation];
							'''
								«IF !jumpablePrimeNodes.empty»
									//Execute jumpToDefinition
									var elem = findElement(id);
									if(
										elem != null &&
										(«FOR p:jumpablePrimeNodes SEPARATOR "|| "
										»elem.$type() == "«p.typeName»"
										«ENDFOR»)
									) {
										Map m = new Map();
										m['primeNode'] = elem;
										m['graphModel'] = currentGraphModel;
										m['type'] = 'jumpToPrime';
										jumpToSC.add(m);
									}
								«ENDIF»
							'''
						}»
					});
				});
			}
			
			/*
			 * selected element functions
			 */
			
			void cb_graphmodel_selected() {
				currentSelection = null;
				selectionChangedSC.add(currentGraphModel);
				«IF g.containsPostSelect»
					graphService.triggerPostSelectFor«g.name.escapeDart»(id, currentGraphModel,"«g.typeName»",highlightings).then((m){
						«'''
							«'''
								commandGraph.receiveCommand(m,forceExecute: true);
							'''.checkCommand("basic_valid_answer")»
						'''.propagation»
					});
				«ENDIF»
			}
			
			void cb_element_selected(int id, {bool openModal: false}) {
				if (id != null) { // cursors have no id and should be ignored
					if(id < 0){
						if(openModal){
							selectionChangedModalSC.add(currentGraphModel);
						} else {
							selectionChangedSC.add(currentGraphModel);
						}
						«IF g.containsPostSelect»
							graphService.triggerPostSelectFor«g.name.escapeDart»(id, currentGraphModel,"«g.typeName»",highlightings).then((m){
								«'''
									«'''
										commandGraph.receiveCommand(m,forceExecute: true);
									'''.checkCommand("basic_valid_answer")»
								'''.propagation»
							});
						«ENDIF»
					} else {
						//find element
						var newSelection = findElement(id);
						currentSelection = newSelection;
						if(openModal){
							selectionChangedModalSC.add(newSelection);
						} else {
							selectionChangedSC.add(newSelection);
						}
						«FOR e : g.elements.filter[containsPostSelect]»
							if(newSelection.$type() == "«e.typeName»") {
								graphService.triggerPostSelectFor«g.name.escapeDart»(id, currentGraphModel,"«e.typeName»",highlightings).then((m){
										«'''
										«'''
											commandGraph.receiveCommand(m,forceExecute: true);
										'''.checkCommand("basic_valid_answer")»
									'''.propagation»
									});
							}
						«ENDFOR»
					}
				}
			}
			
			void cb_delete_selected() {
				if(currentSelection == null || currentSelection is core.GraphModel) {
					return;
				}
				//prevent for disbale remove
				«FOR e : g.elements.filter[!removable]»
					if(currentSelection.$type() == '«e.typeName»') {
						return;
					}
				«ENDFOR»
				if(currentSelection is core.Node) {
					var node = currentSelection as core.Node;
					var container = node.container;
					var containerId = container != null ? container.id : -1;
					container = findElement(containerId) as core.ModelElementContainer;
					if(container == null || containerId == -1) {
						return;
					}
					var ccm = commandGraph.sendRemoveNodeCommand(node.id,user);
					sendCommandMessage(
						ccm,
						(m) {
							selectionChangedSC.add(currentGraphModel);
							node.container = null;
						},
						(m) {
							if(container is! core.GraphModel){
								updateElement(container as core.ModelElement);
							}
						}
					);
				}
				if(currentSelection is core.Edge) {
					var edge = currentSelection as core.Edge;
					var ccm = commandGraph.sendRemoveEdgeCommand(
						edge.id,
						edge.source.id,
						edge.target.id,
						edge.source.$type(),
						edge.target.$type(),
						edge.$type(),
						user
					);
					sendCommandMessage(
						ccm,
						(m) {
							selectionChangedSC.add(currentGraphModel);
						},
						null
					);
				}
			}
			
			/*
			 * for each node
			 */
			«FOR node : g.nodes.filter[!isIsAbstract]»
				
				void create_node_«node.jsCall(g)»(«node.dartFQN» node) {
					«IF node.creatabel»
						create_node(node, "«node.jsCall(g)»");
					«ELSE»
						// node is not creatable
					«ENDIF»
				}
				
				void cb_create_node_«node.jsCall(g)»(
					int x, int y, int width, int height, String cellId, int containerId«IF node.isPrime», int primeId«ENDIF»
				) {
					«IF node.creatabel»
						cb_create_node("«node.typeName»", x, y, width, height, cellId, containerId, «IF node.isPrime»primeId«ELSE»-1«ENDIF»);
					«ELSE»
						// node is not creatable
					«ENDIF»
				}
				
				void remove_node_cascade_«node.jsCall(g)»(«node.dartFQN» node) {
					«IF node.removable»
						remove_node_cascade_internal(node, "«node.jsCall(g)»", «IF node instanceof NodeContainer»true«ELSE»false«ENDIF»);
					«ELSE»
						// node is not removable
					«ENDIF»
				}
				
				void cb_remove_node_«node.jsCall(g)»(int id) {
					«IF node.removable»
						cb_remove_node(id);
					«ELSE»
						// node is not removable
					«ENDIF»
				}
				
				
				void cb_move_node_«node.jsCall(g)»(int x, int y, int id, containerId) {
					«IF node.movable»
						cb_move_node(x, y, id, containerId);
					«ELSE»
						// node is not movable
					«ENDIF»
				}
				
				void cb_resize_node_«node.jsCall(g)»(int width,int height,String direction,int id) {
					«IF node.resizable»
						cb_resize_node(width, height, direction, id);
					«ELSE»
						// node is not resizable
					«ENDIF»
				}
				
				@deprecated
				void cb_rotate_node_«node.jsCall(g)»(int angle, int id) {
					cb_rotate_node(angle, id);
				}
			«ENDFOR»
			
			/* 
			 * for each edge
			 */
			«FOR edge : g.edges.filter[!isIsAbstract]»
				
				void create_edge_«edge.jsCall(g)»(core.Edge edge) {
					«IF edge.creatabel»
						create_edge(edge, "«edge.jsCall(g)»");
					«ELSE»
						// edge is not creatable
					«ENDIF»
				}
				
				void cb_create_edge_«edge.jsCall(g)»(int sourceId, int targetId, String cellId, List positions) {
					«IF edge.creatabel»
						cb_create_edge("«edge.typeName»", sourceId, targetId, cellId, positions);
					«ELSE»
						// edge is not creatable
					«ENDIF»
				}
				
				void cb_remove_edge_«edge.jsCall(g)»(int id) {
					«IF edge.removable»
						cb_remove_edge("«edge.typeName»", id);
					«ELSE»
						// edge is not removable
					«ENDIF»
				}
				
				void cb_reconnect_edge_«edge.jsCall(g)»(int sourceId, int targetId, int id) {
					cb_reconnect_edge(sourceId, targetId, id);
				}
			«ENDFOR»
			
			/*
			 * generated node functions
			 */
			
			js.JsArray cb_get_valid_containers(int id, String type, bool isReference) {
				var possibleNodes = [];
				if(id==-1) {
					if(isReference) {
						switch(type) {
							«{ 
								// resolve type mapping
								val nodes = g.nodes
								// entry is the type that is referenced, value are the possible resulting nodes
								val possibleMapping = new HashMap<EObject, java.util.Set<GraphicalModelElement>>
								val nodeTypes = nodes.map[resolveSubTypesAndType].flatten.toSet.filter[isPrime];
								for(nodeType : nodeTypes) {
									val primeReferenced = (nodeType as Node).primeReference.type
									val allReferenceableTypes = primeReferenced.resolveSubTypesAndType.toSet
									for(r : allReferenceableTypes) {
										if(!possibleMapping.containsKey(r)) {
											possibleMapping.put(r, new HashSet)
										}
										possibleMapping.get(r).add(nodeType as GraphicalModelElement);
									}
								}
								'''
									«FOR entry : possibleMapping.entrySet»
										case '«entry.key.typeName»':
											«FOR n : entry.value»
												possibleNodes.add(new «n.dartFQN»());
											«ENDFOR»
											break;
									«ENDFOR»
								'''
							}»
						}
					} else {
						switch(type) {
							«{ 
								// resolve type mapping
								val nodes = g.nodes
								// entry is the type that is referenced, value are the possible resulting nodes
								val possibleMapping = new HashMap<EObject, HashSet<GraphicalModelElement>>
								val nodeTypes = nodes.map[resolveSubTypesAndType].flatten.toSet.filter[!isPrime];
								for(nodeType : nodeTypes) {
									if(!possibleMapping.containsKey(nodeType)) {
										possibleMapping.put(nodeType, new HashSet)
									}
									possibleMapping.get(nodeType).add(nodeType as GraphicalModelElement)
								}
								'''
									«FOR entry : possibleMapping.entrySet»
										case '«entry.key.typeName»':
											«FOR n : entry.value»
												possibleNodes.add(new «n.dartFQN»());
											«ENDFOR»
											break;
									«ENDFOR»
								'''
							}»
						}
					}
				} else {
				possibleNodes.add(
				  findElement(id)
				);
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
			
			dynamic cb_can_move_node(int id, int containerId, {core.Node tmpNode:null}) {
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
				if(c is! core.ModelElementContainer) {
					var arr = js.JsArray();
					arr['x'] = x;
					arr['y'] = y;
					if(node.container != null) {
						arr['containerId'] = node.container.id;
					}
					return arr;
				}
				
				// containment-check
				var container = c as core.ModelElementContainer;
				if(node.container == null || node.container.id != container.id) {
					// switch container to..
					// ...graphModel
					«g.containmentCheck(g.MGLModel, false)»
					
					// ...other containers
					«{
						val containers = g.elements.filter[!isIsAbstract].filter(NodeContainer)
						'''
							«FOR container : containers»
								«container.containmentCheck(g.MGLModel, true)»
							«ENDFOR»
						'''
					}»
					
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
			
			void remove_node_cascade(core.Node node) {
				if(node == null){ return; }
				«FOR node : g.nodes.filter[!isIsAbstract]»
					else if(node.$type() == "«node.typeName»") { remove_node_cascade_«node.jsCall(g)»(node); }
				«ENDFOR»
			}
			
			/*
			 * static node functions
			 */
			
			bool cb_is_valid_container(int nodeId, int containerId) {
				var resp = cb_can_move_node(nodeId, containerId);
				return resp is bool && resp == true;
			}
			
			void create_node(core.Node node, String nodeJsCall) {
				var x = node.x;
				var y = node.y;
				
				if(node.container is core.Container) {
					core.ModelElementContainer parent = node.container;
					while(parent !=null && parent is core.Container) {
						x += (parent as core.Container).x;
						y += (parent as core.Container).y;
						parent = (parent as core.Container).container;
					}
				}
				var containerId = node.container != null ? node.container.id : -1;
				js.context.callMethod(
					'create_node_' + nodeJsCall,
					[
						x,
						y,
						node.width,
						node.height,
						node.id,
						containerId,
						node.styleArgs(),
						node.$information(),
						node.$label()
					]
				);
			}
			
			void cb_create_node(
				String nodeType, int x,int y,int width,int height,String cellId,int containerId, int primeId
			) {
				var container = findElement(containerId) as core.ModelElementContainer;
				var containerType = container != null ? container.$type() : null;
				var ccm = null;
				if(primeId >= 0) {
					ccm = commandGraph.sendCreateNodeCommand(
						nodeType, x, y, containerId, containerType, width, height, user, primeId: primeId
					);
				} else {
					ccm = commandGraph.sendCreateNodeCommand(
						nodeType, x, y, containerId, containerType, width, height, user
					);
				}
				sendCommandMessage(
					ccm,
					(m) {
						updateDelegateIdOfCell(m, cellId);
					},
					(m) {
						for(var cmd in m.cmd.queue) {
							if(cmd is CreateNodeCommand){
								// change selection
								var node = findElement(cmd.delegateId);
								// update container
								if(container is! core.GraphModel){
									updateElement(container as core.ModelElement);
								}
								selectionChangedSC.add(node);
							}
						}
					}
				);
			}
			
			void remove_node_cascade_internal(core.Node node, String nodeJsCall, bool isContainer) {
				if(node is core.ModelElementContainer) {
					(node as core.ModelElementContainer).modelElements.where(
						(n) => n is core.Node
					).forEach(
						(n) => remove_node_cascade(n)
					);
				}
				//remove connected edges
				node.outgoing.forEach((e) {
					e.target.incoming.remove(e);
					e.container.modelElements.remove(e);
					e.container = null;
					js.context.callMethod('remove_edge__«g.jsCall»', [e.id]);
				});
				node.outgoing.clear();
				node.incoming.forEach((e) {
					e.source.outgoing.remove(e);
					e.container.modelElements.remove(e);
					e.container = null;
					js.context.callMethod('remove_edge__«g.jsCall»', [e.id]);
				});
				node.incoming.clear();
				js.context.callMethod('remove_node_' + nodeJsCall, [node.id]);
			}
			
			void cb_remove_node(int id) {
				var node = findElement(id) as core.Node;
				if(node == null) {
					return;
				}
				var container = node.container;
			    var containerId = container != null ? container.id : -1;
				container = findElement(containerId) as core.ModelElementContainer;
				var ccm = commandGraph.sendRemoveNodeCommand(id, user);
				sendCommandMessage(
					ccm,
					(m) {
						selectionChangedSC.add(currentGraphModel);
						node.container = null;
					},
					(m) {
						if(container is! core.GraphModel) {
							updateElement(container as core.ModelElement);
						}
					}
				);
			}
			
			void cb_move_node(int x, int y, int id, containerId) {
				var node = findElement(id) as core.Node;
				if(node.x==x && node.y==y){
					return;
				}
				var ccm = commandGraph.sendMoveNodeCommand(id, x, y, containerId, user);
				sendCommandMessage(
					ccm,
					null,
					null
				);
			}
			
			void cb_resize_node(int width, int height, String direction, int id) {
				var node = findElement(id) as core.Node;
				if(node != null && (node.width != width || node.height != height) ) {
					startPropagation();
					var ccm = commandGraph.sendResizeNodeCommand(id, width, height, direction, user);
					sendCommandMessage(ccm, null, null);
				}
			}
			
			@deprecated
			void cb_rotate_node(int angle, int id) {
				var node = findElement(id) as core.Node;
				if(node != null) {
				 	var ccm =commandGraph.sendRotateNodeCommand(id, angle, user);
					sendCommandMessage(ccm, null, null);
				}
			}
			
			/*
			 * generated edge functions
			 */
			
			dynamic cb_can_connect_edge(int id, int sourceId, int targetId) {
				var edge = id == -1 ? null: findElement(id) as core.Edge;
				List<core.Edge> edgeTypes = id == -1 ? [
					«FOR e : g.edges.filter[!isAbstract] SEPARATOR ","»
						new «e.dartFQN»()
					«ENDFOR»
				] : [findElement(id)];
				
				// check types of source and target
				var sourceElem = findElement(sourceId);
				var targetElem = findElement(targetId);
				if(sourceElem is! core.Node || targetElem is! core.Node) {
					return false;
				}
				var source = sourceElem as core.Node;
				var target = targetElem as core.Node;
				
				for(var e in edgeTypes)
				{
					var canConnect = js.context.callMethod('canConnectEdge_«g.jsCall»',[
						source.id,
						target.id,
						e.$type()
					]);
					if(canConnect) {
						return true;
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
			
			/*
			 * static edge functions
			 */
			
			js.JsArray cb_get_valid_targets(int id) {
				var valids = new js.JsArray();
				currentGraphModel.allElements().where((n) => n is core.Node).forEach((n) {
					if(cb_is_valid_connection(-1, id, n.id)) {
						valids.add(n.id);
					}
				});
				return valids;
			}
			
			bool cb_is_valid_connection(int edgeId, int sourceId, int targetId) {
				var re = cb_can_connect_edge(edgeId, sourceId, targetId);
				return re is bool && re == true;
			}
			
			void create_edge(core.Edge edge, String edgeJsCall) {
				var positions = new js.JsArray.from(
					edge.bendingPoints.map( (n) {
						var arr = new js.JsArray();
						arr['x'] = n.x;
						arr['y'] = n.y;
						return arr;
					}
				).toList());
				js.context.callMethod(
					'create_edge_' + edgeJsCall,
					[
						edge.source.id,
						edge.target.id,
						edge.id,
						positions,
						edge.styleArgs(),
						edge.$information(),
						edge.$label()
					]
				);
			}
			
			void cb_create_edge(String edgeType, int sourceId, int targetId, String cellId, List positions) {
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
			 	var ccm = commandGraph.sendCreateEdgeCommand(
			 		edgeType, targetId, sourceId, target.$type(), source.$type(), currentBendpoints, user
			 	);
				sendCommandMessage(
					ccm,
					(m) {
						updateDelegateIdOfCell(m, cellId);
					},
					(m) {
						var cmd = m.cmd.queue.first;
						var element = findElement(cmd.delegateId);
						selectionChangedSC.add(element);
					}
				);
			}
			
			void cb_remove_edge(String edgeType, int id) {
				var edge = findElement(id) as core.Edge;
				if(edge == null) {
					return;
				}
				var ccm = commandGraph.sendRemoveEdgeCommand(
					id, edge.source.id, edge.target.id, edge.source.$type(), edge.target.$type(), edgeType, user
				);
				sendCommandMessage(ccm, null, null);
			}
			
			void cb_reconnect_edge(int sourceId, int targetId, int id) {
				var edge = findElement(id) as core.Edge;
				if(edge == null) {
					return;
				}
				if(edge.source.id != sourceId || edge.target.id != targetId) {
				 	var ccm = commandGraph.sendReconnectEdgeCommand(id, sourceId, targetId, user);
					sendCommandMessage(ccm, null, null);
				}
			}
			
			void cb_update_bendpoint(List positions,int id) {
				var edge = findElement(id) as core.Edge;
				if(edge == null) {
					return;
				}
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
					sendCommandMessage(ccm, null, null);
				}
			}
			
			bool check_bendpoint_update(core.Edge edge, List positions) {
				if(positions == null) {
					return edge.bendingPoints.isNotEmpty;
				}
				if(positions.length == edge.bendingPoints.length) {
					for(var pos in positions){
						var x = pos['x'];
						var y = pos['y'];
						var found = false;
						for(var b in edge.bendingPoints) {
							if(b.x == x && b.y == y){
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
			
			/*
			 * message functions
			 */
			
			void executeCommands(CompoundCommandMessage m,bool forceExecute) => commandGraph.receiveCommand(m,forceExecute: forceExecute);
			
			void executeUpdateCommand(Message m, bool forceExecute) {
				«'''
					executeCommands(m, forceExecute);
					hasChangedSC.add({ "type": "update", "message": m });
				'''.checkCommand»
			}
			
			void updateDelegateIdOfCell(CompoundCommandMessage m, cellId) {
				var cmd= m.cmd.queue.first;
				if(cmd is CreateNodeCommand || cmd is CreateEdgeCommand) {
					js.context.callMethod('update_elementId_«g.jsCall»',
					[
						cellId,
						cmd.delegateId
					]);
				}
			}
			
			void sendCommandMessage(cm, preExecute, postExecute) {
				if(cm != null) {
					startPropagation();
					graphService.sendMessage(cm,"«g.restEndpoint»",currentGraphModel.id).then((m) {
						if(preExecute != null) {
							preExecute(m);
						}
						this.executeUpdateCommand(m, false);
						if(postExecute != null){
							postExecute(m);
						}
					}).then((_)=>endPropagation());
				}
			}
		}
		'''
	}
	
	def boolean containesEdge(EdgeElementConnection c, Edge edge) {
		c.connectingEdges.contains(edge) || c.connectingEdges.nullOrEmpty
	}

	def checkCommand(CharSequence s) {
		return checkCommand(s, null)
	}

	def checkCommand(CharSequence s, String type) {
		return checkCommand(s, type, null)
	}

	def checkCommand(CharSequence s, String type, CharSequence revert) '''
		if(m is CompoundCommandMessage){
			«IF type !== null»
				if(m.type == '«type»'){
					«s»
				}
			«ELSE»
				«s»
			«ENDIF»
			«IF revert !== null»
				else {
					«revert»
				}
			«ENDIF»
		}
	'''

	def fileNameGraphModelComponentTemplate(GraphModel graphModel) '''«graphModel.componentFileHTML»'''

	def contentGraphModelComponentTemplate(GraphModel g) '''
		<message-dialog
			*ngIf="showMessageDialog==true"
			[content]="dialogMessage"
			[dialog]="messageDialogType"
			(close)="closeMessgeDialog($event)"
		>
		</message-dialog>
		<display-dialog
			*ngIf="showDisplayDialog==true"
			[messages]="displayMessages"
			(close)="closeDisplayDialog($event)"
			(download)="downloadDisplayDialog($event)"
		>
		</display-dialog>
		<div class="d-flex h-100 flex-column overflow-hidden">
			<template [ngIf]="loading">
				<h3 style="text-align: center;">Loading «g.name»..</h3>
				<div class="progress">
					<div class="progress-bar progress-bar-striped active" style="width: 100%;background-color: #be0101;"></div>
				</div>
			</template>
			<div class="h-100 w-100" ondragover="confirm_drop_«g.name.lowEscapeDart»(event)" ondrop="drop_on_canvas_«g.name.lowEscapeDart»(event)" id="paper_«g.name.lowEscapeDart»" style="margin:auto"></div>
		</div>
	'''

	def containmentCheck(ContainingElement container, MGLModel g, boolean isElse) {
		val containableElements = container.resolvePossibleContainingTypes.filter(mgl.BoundedConstraint).toSet
		'''
			«IF container instanceof GraphModel || !containableElements.empty»
				«IF isElse»else «ENDIF»if(container.$type() == "«container.typeName»") {
					«{
						containmentCheckTemplate(
							containableElements,
							[t| '''node.$type() == "«t.typeName»"'''],
							null,
							[currentType, concreteBlockingTypes, upperBound| 
								'''
									«IF upperBound>-1»
										{
											int groupSize = 0;
											«FOR t:concreteBlockingTypes»
												groupSize += container.modelElements.where((n)=>n.$type() == "«t.typeName»").length;
											«ENDFOR»
											// check bounding constraint
											if(groupSize>=«upperBound») {
												// node can not be placed
												return false;
											}
										}
									«ENDIF»
								'''
							],
							'''return true;''',
							'''return true;'''
						)
					}»
				}
			«ENDIF»
		'''
	}
}
