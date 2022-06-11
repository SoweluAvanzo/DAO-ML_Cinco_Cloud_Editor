package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import java.util.List
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPlugin
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPluginRegistry

class EditorComponent extends Generatable {
	List<EditorViewPlugin> eps
	
	new(GeneratorCompound gc) {
		super(gc)
		
		eps = new EditorViewPluginRegistry().getPlugins(gc)
	}
	
	def fileNameEditorComponent()'''editor_component.dart'''
	
	def fileNameEditorTemplate()'''editor_component.html'''
	
	def contentEditorComponent()
	'''
	import 'package:«gc.projectName.escapeDart»/src/model/message.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/base_service.dart';
	
	import 'package:angular/angular.dart';
	import 'package:angular_router/angular_router.dart';
	import 'package:ng_bootstrap/ng_bootstrap.dart';
	import 'dart:html';
	import 'dart:async';
	import 'dart:js' as js;
	
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/user_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/canvas/canvas_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/properties/properties_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/map/map_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/palette/list/list_view.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/palette/palette_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/command_history/command_history_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/utils/editor_tabs_draggable/editor_tabs_draggable.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/utils/editor_tabs_dropzone/editor_tabs_dropzone.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/notification_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/check_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/graph_model_permission_vector_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/style_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/editor_grid_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/editor_data_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/check/check_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/graph_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/view/tree_view.dart';
	import 'package:«gc.projectName.escapeDart»/src/utils/redirect_stack.dart';
	
	«FOR g:gc.concreteGraphModels»
		import 'package:«gc.projectName.escapeDart»/«g.commandGraphPath»';
	«ENDFOR»
	
	@Component(
	    selector: 'editor',
	    templateUrl: 'editor_component.html',
	    styleUrls: const ['editor_component.css'],
	    providers: const [
	    		ClassProvider(EditorGridService),
	    		ClassProvider(EditorDataService),
		    	ClassProvider(GraphService),
		    	ClassProvider(CheckService)
	    	],
	    directives: const [
	      TreeViewComponent,
	      routerDirectives,
	      coreDirectives,
	      CanvasComponent,
	      PropertiesComponent,
	      PaletteComponent,
	      CheckComponent,
	      CommandHistoryComponent,
	      MapComponent,
	      bsDirectives,
	      EditorTabsDropzoneComponent,
	      EditorTabsDraggableComponent
	    ]
	)
	class EditorComponent implements OnActivate, OnDeactivate {
	    PyroUser user;
	    List<PyroUser> activeUsers = new List();
	    String token = null;
	    
	    PyroEditorGrid grid;
	    Map<int, PyroEditorGridItem> gridItemMap = new Map();
	    
	    @ViewChildren(BsTabsComponent)
	    List<BsTabsComponent> widgetTabs = new List();
	    
	    static List<PropertiesComponent> properties = new List<PropertiesComponent>();
	    @ViewChildren(PropertiesComponent) set propertiesComp(content) {
	    	if(content is List<PropertiesComponent>) {
	    		properties.addAll(content);
	    		properties = properties.toSet().toList();
	    	}
	    }
	    
	    static List<TreeViewComponent> treeComponents = new List<TreeViewComponent>();
	    void registerTreeViewComponent(TreeViewComponent t) {
	    	if(!treeComponents.contains(t)) {
	    		treeComponents.add(t);
	    	}
	    }
	    
	    List<PyroGraphModelPermissionVector> permissionVectors;
	    RedirectionStack redirectionStack = new RedirectionStack();
	    GraphModel currentFile = null;
	    IdentifiableElement selectedElement = null;
	    IdentifiableElement selectedElementModal = null;
	    LocalGraphModelSettings currentLocalSettings;
	    int fullscreenWidgetId;
	    final GraphService graphService;
	    final UserService _userService;
	    final NotificationService _notificationService;
	    final GraphModelPermissionVectorService _permissionService;
	    final StyleService _styleService;
	    final Router _router;
	    final EditorGridService _editorGridService;
	    final EditorDataService _editorDataService;
	    String selected = null;
	    bool showNav = false;
	    String mainLayout = "micro";
	    
	    EditorComponent(this._editorGridService, this.graphService, this._router, this._userService, this._notificationService, 
	    				    this._styleService, this._permissionService, this._editorDataService) {
	    	currentLocalSettings = new LocalGraphModelSettings();
	    	permissionVectors = new List();
	    }
	    
	    EditorComponent get instance => this;
	    
		@override
		void onActivate(_, RouterState current) async {
			token = null;
			int modelId = 0;
			String typeOrExtension = null;
			if(current.queryParameters.containsKey("token")) {
			    // window.localStorage[BaseService.tokenKey] = current.queryParameters["token"];
			    token = current.queryParameters["token"];
			} else {
			    print("ERR: no token in URL");
			    return;
			}
			if(current.parameters.containsKey("modelId")) {
			    modelId = int.tryParse(current.parameters["modelId"]);
			} else {
			    print("ERR: no modelId in URL");
			    return;
			}
			if(current.queryParameters.containsKey("ext")) {
			    typeOrExtension = current.queryParameters["ext"];
			} else {
			    print("ERR: no extension/type in URL");
			    return;
			}
			_userService.loadUser().then((u){
			    user = u;
			    _editorDataService.user = u;
			    document.title = "editor";
			            redirectionStack.initReditionStack(typeOrExtension, modelId);
			    this.loadGraphModel(typeOrExtension, modelId);         	         
			}).catchError((_){});
		}
	    
	    @override
	    void onDeactivate(_, RouterState next) async {
	    	_styleService.handleOnDeactivate(next);
	    }
		
		Future<dynamic> loadGraphModel(String typeOrExtension, int modelId) {
			return graphService.loadGraphModel(typeOrExtension, modelId).then((g) {
				this.currentFile = g;
				this.selectedElement = g;
				this.selectedElementModal = g;
				document.title = "editor - " + g.$displayName();
			}).catchError((_){}).then((_) => postGraphModelSwitched());
		}
		
		void initializeEditor() {
			if(mainLayout == 'classic') {
				var t = new Timer.periodic(const Duration(milliseconds: 20), (Timer t){
					if (this.grid != null) {
						t.cancel();
						initEditorGrid();
					}
				});
			} else if(mainLayout == 'micro') {
				js.context.callMethod("initializeResizeParameter", [this.currentFile.$lower_type()]);
			}
			loadAppearance();
		}
		
		initEditorGrid() {
			initializeGrid();
			reinitGrid();
			if(this.currentFile != null) {
				var functionCall = 'reaAdjustDimensions_'+ this.currentFile.$lower_type();
				js.context.callMethod(functionCall);
			}
		}
	
		Future<dynamic> fetchGrid() {
			grid = null;
			return this._editorGridService.get().then((g) {
				this.updateGrid(g);
			});
		}

		void initializeGrid() {
			document.dispatchEvent(new CustomEvent("editor:grid-init"));
			document.on["editor:grid-change"].listen((Event event) {
				var items = (event as CustomEvent).detail['items'];
				var map = new Map<String, PyroEditorGridItem>();
				_editorDataService.grid.items.forEach((i) {
					map.putIfAbsent(i.id.toString(), () => i);
				});
				items.forEach((i){
					if(i['x'] is int && i['y'] is int) {
						var id = i['id'];
						if(map.containsKey(id)) {
							map[id].x = i['x'];
							map[id].y = i['y'];
							map[id].width = i['width'];
							map[id].height = i['height'];
						}
					}
				});
				_editorGridService.update(grid).then((g) {
					updateGrid(g);
				});
			});
		}
	    
	    void selectView(dynamic e,String view) {
			e.preventDefault();
			if(selected==view) {
				selected = null;
				window.localStorage['PYRO_EDITOR_SELECTED'] = null;
			} else {
				window.localStorage['PYRO_EDITOR_SELECTED'] = view;
				selected = view;
			}
			js.context.callMethod("initializeResizeParameter", [this.currentFile.$lower_type()]);
	    }

	    void changedMainLayout(layout) {
			mainLayout = layout;
			window.localStorage['PYRO_EDITOR_MAIN_LAYOUT'] = layout;
			initializeEditor();
	    }
	    
	    bool get isGraphModel => currentFile is GraphModel;
	    
	    void toggleFullscreen(int id) {
	    	fullscreenWidgetId = fullscreenWidgetId != null ? null : id;
	    }
	    
	    bool isFullscreen(int id) {
	    	return fullscreenWidgetId == id;
	    }
	    
	    bool showWidget(int id) {
	    	return fullscreenWidgetId == null || fullscreenWidgetId == id;
	    }
	    
		updateGrid(PyroEditorGrid g) {
			_editorDataService.grid = g;
			gridItemMap.clear();
			_editorDataService.grid.items.forEach((item){
				gridItemMap[item.id] = item;
			});
			grid = _editorDataService.grid;
			
			// set new active tab after moving tabs
			widgetTabs.forEach((widgetTab) {
				bool hasActiveTab = widgetTab.tabs.fold(true, (acc, val) => acc && val.active);
				if (!hasActiveTab) {
					widgetTab.setSelected(widgetTab.tabs[0]);
				}
			});
		}
	    
		void reinitGrid() {
		  document.dispatchEvent(new CustomEvent("editor:grid-reinit", detail: grid.toJSOG(new Map())));
		}
	    
	    createWidgetArea(PyroEditorWidget widget) {
	        _editorGridService.createArea(grid.id).then((area) {
	            moveWidget({
	                'widgetId': widget.id,
	                'toAreaId': area.id,
	                'fromAreaId': null
	            });
	        });
	    }
	
	    removeWidget(dynamic e, PyroEditorGridItem area, PyroEditorWidget widget) {
	        if (e != null) e.preventDefault();     
	        _editorGridService.removeWidget(grid.id, widget.id).then((g){
	            updateGrid(g);
	        });
	    }
	    
	    removeWidgetArea(dynamic e, PyroEditorGridItem area) {
	    	e.preventDefault();        
	    	_editorGridService.removeArea(grid.id, area.id).then((g){
	    		updateGrid(g);
	    	});
	    }
	    
	    moveWidget(dynamic data) {
	    	if (data['toAreaId'] == data['fromAreaId']) {
	    	    return;
	    	}
	        _editorGridService.moveWidget(grid.id, data['widgetId'], data['toAreaId']).then((g) => updateGrid(g));
	    }
	    
	    Object trackByWidgetId(_, dynamic o) => o is PyroEditorWidget ? o.id : o;
	    
	    Object trackByWidgetAreaId(_, dynamic o) => o is PyroEditorGridItem ? o.id : o;
	
	    void changeStructure(dynamic e) {}
	
		void changedData(message) {
			String type = message['type'];
			var m = message['message'];
			if(type == 'update') {
			  changedGraph(m);
			} else if(type == '«websocketEventPrime»') {
			  changedReference(m);
			}
		}
		
		void changedGraph(m) {
			var allElements = currentFile.allElements();
			var exists = allElements.contains(selectedElement);
			if(!exists) {
				selectedElement = currentFile;
			}
			for(var p in properties) {
				p.rebuildTrees();
			}
		}
		
		void changedReference(m) {
			for(var t in treeComponents) {
			  t.load(null);
			}
		}
		
		void changedProperties(PropertyMessage pm) {
			this.graphService.canvasComponent.updateProperties(pm.delegate);
			sendMessage(pm).then((m){
				if (m is CompoundCommandMessage) {
					this.graphService.canvasComponent.executeCommands(m,false);
					graphService.update(currentFile.id);
				}
			});
		}
		
		void selectionChanged(IdentifiableElement element) {
			selectedElement = element;
		}
		
		void selectionChangedModal(IdentifiableElement element) {
			selectedElementModal = element;
			if(element != null) {
				properties.forEach((n)=>n.showModal());         
			}
		}
		
		void currentDragging(MapListValue value) {
			print(value.name);
		}

		void jumpToPrime(Map m) {
			if(m['type'] == "navigation") {
				var location = m['location'];
				var modelType = location['type'];
				if(graphService.isGraphModel(modelType)) {
					var modelId = location['id'];
		            preGraphModelSwitched();
					this.loadGraphModel(modelType, modelId);
				}
			} else if(m['type'] == "jumpToPrime"){
			    IdentifiableElement primeNode = m['primeNode'];
			    GraphModel parentGraphModel = m['graphModel'];
			    if(primeNode != null && parentGraphModel != null) {
			        graphService.jumpToPrime(
			            parentGraphModel.$type(),
			            primeNode.$type(),
			            parentGraphModel.id,
			            primeNode.id
			        ).then((m) {
				    	String modelType = m['graphmodel_type'];
			        	if(graphService.isGraphModel(modelType)) {
				        	int modelId = int.parse(m['graphmodel_id']);
	                int elementId = int.parse(m['element_id']);
	                String elementType = m['element_type'];
	                print("jumping to prime:\n${m}");
	                        preGraphModelSwitched();
	                redirectionStack.pushToRedirectStack(modelType, modelId);
	                this.loadGraphModel(modelType, modelId).then((_) {
	                  // TODO: focus and highlight element here with elementId and elementType
	                  // Also put those information on the RedirectStack (pushToRedirectStack),
	                  // so that it could be triggered on forward and backward navigation, too
	                });
	              }
			        });
			    }
			}
		}

		void preGraphModelSwitched() {
			blockInteraction();
		}
		
		void postGraphModelSwitched() {
		    fetchGrid();
		    initializeEditor();
			unblockInteraction();
		}
		
		void loadAppearance() {
			new Timer.periodic(const Duration(milliseconds: 20), (Timer t){
				if (
					this.graphService.canvasComponent != null
					&& this.graphService.canvasComponent.getCanvasComponent()?.commandGraph != null
				) {
					t.cancel();
					this.graphService.loadAppearance(this.currentFile).then((m) {
						if (m is CompoundCommandMessage) {
						    this.graphService.canvasComponent.executeCommands(m,true);
						    this.graphService.update(currentFile.id);
						}
					});
				}
			});
		}
		
		void blockInteraction() {
			var functionCall = 'start_propagation_'+ this.currentFile.$lower_type();
			js.context.callMethod(functionCall);
		}
		
		void unblockInteraction() {
			var functionCall = 'end_propagation_'+ this.currentFile.$lower_type();
			js.context.callMethod(functionCall);
		}
		
	    void receiveMessage(String json) {
	        Message message = Message.fromJSON(json);
	        _notificationService.displayMessage("Update",NotificationType.INFO);
	        if(message is CompoundCommandMessage) {
	            receiveGraphModelUpdate(message);
	        }
	        if(message is PropertyMessage) {
	            receivePropertyUpdate(message);
	        }
	    }
		
		changeGridLayout(String layout) {
			if(layout == 'micro' || layout == 'classic') {
				changedMainLayout(layout);
			} else {
				_editorGridService.setLayout(grid.id, layout).then((g) {
					updateGrid(g);
					initializeEditor();
				});
			}
		}
	
	    Future<Message> sendMessage(Message message) async {
	    	return graphService.sendMessage(message,currentFile.$type(),currentFile.id);
	    }
	
	    void receiveGraphModelUpdate(CompoundCommandMessage message) {
		    «FOR g:gc.concreteGraphModels SEPARATOR " else "
		    »if(this.currentFile.$type() == '«g.typeName»') {
		    	«g.name.fuEscapeDart»CommandGraph cg = new «g.name.fuEscapeDart»CommandGraph(this.currentFile,new List());
		    	cg.receiveCommand(message);
		    }«
		    ENDFOR»
	    }
	
	    void receivePropertyUpdate(PropertyMessage message) {
			IdentifiableElement ie = this.currentFile.allElements().where((n)=>n.id==message.delegate.id).first;
			if(ie != null) {
			    ie.merge(message.delegate,structureOnly:true);
			}
	    }
	}
	
	'''
	def contentEditorTemplate()
	'''
	<div *ngIf="user!=null" [style.overflow]="mainLayout=='micro'? 'hidden' : 'auto'">
		<properties
			*ngIf="isGraphModel"
		    [user]="user"
		    [isModal]="true"
		    [currentGraphModel]="currentFile"
		    [currentGraphElement]="selectedElementModal"
		    (hasChanged)="changedProperties($event)"
		    (hasClosed)="selectionChangedModal(null)"
		>
		</properties>
		<div id="micro-editor" class="row" *ngIf="grid != null && mainLayout == 'micro'">
			<div id="scroll-menu">
				<ul class="nav nav-tabs left-tabs sideways-tabs" style="margin-top: 67px; height: max-content;">
				  <ng-container *ngFor="let widgetArea of grid.items; trackBy: trackByWidgetAreaId">
					  <ng-container *ngFor="let widget of widgetArea.widgets; trackBy: trackByWidgetId">
						  <li *ngIf="widget.key=='palette'" class="nav-item">
							<a class="nav-link" [class.active]="selected=='palette'" title="Show Palette" href (click)="selectView($event,'palette')">Palette</a>
						  </li>
						  <li *ngIf="widget.key=='checks'" class="nav-item">
							<a class="nav-link" [class.active]="selected=='check'" title="Show Checks" href (click)="selectView($event,'check')">Check</a>
						  </li>
						  <li *ngIf="widget.key=='command_history'" class="nav-item">
							<a class="nav-link" [class.active]="selected=='command-history'" title="Show Command History" href (click)="selectView($event,'command-history')">History</a>
						  </li>
						  <li *ngIf="widget.key=='map'" class="nav-item">
							<a class="nav-link" [class.active]="selected=='map'" title="Show Map" href (click)="selectView($event,'map')">Map</a>
						  </li>
						  «FOR pc:eps.filter[pluginComponent.fetchURL!==null].map[pluginComponent]»
						  	<li *ngIf="widget.key=='«pc.key»'" class="nav-item">
						  		<a class="nav-link" [class.active]="selected=='«pc.tab»'" title="Show «pc.tab»" href (click)="selectView($event,'«pc.tab»')">«pc.tab»</a>
						  	</li>
						  «ENDFOR»
					  </ng-container>
				  </ng-container>
				</ul>
			</div>
			<div id="pyro-micro-menu" *ngIf="selected != null">
				<h5 style="margin-top: 15px;margin-bottom:12px;text-align: center;">
					{{selected[0].toUpperCase() + selected.substring(1)}}
				</h5>
				<palette class="d-flex flex-column h-100"
					*ngIf="selected=='palette' && isGraphModel"
					[currentGraphModel]="currentFile"
					[permissionVectors]="permissionVectors"
					(dragged)="currentDragging($event)"
				></palette>
				<check class="d-flex flex-column h-100"
					*ngIf="selected=='check' && isGraphModel"
					[currentGraphModel]="currentFile"
				></check>
				<command-history class="d-flex flex-column h-100"
					*ngIf="selected=='command-history' && isGraphModel"
					(reverted)="graphService.canvasComponent.undo()"
				></command-history>
				<map class="d-flex flex-column h-100"
					*ngIf="selected=='map' && isGraphModel"
					[currentGraphModel]="currentFile"
				></map>
				«FOR pc:eps.filter[pluginComponent.fetchURL!==null].map[pluginComponent]»
						<tree-view
							*ngIf="selected=='«pc.tab»'&&isGraphModel"
							[user]="user"
							[currentGraphModel]="currentFile"
							[name]="'«pc.tab»'"
							[fetchUrl]="'«pc.fetchURL»'"
							[clickUrl]="'«pc.clickURL»'"
							[dbClickUrl]="'«pc.dbClickURL»'"
							[parent]="instance"
							style="height: 100%;"
							>
						</tree-view>
				«ENDFOR»
			</div>
			<div class="y-resizer" id="separator"></div>
			<div id="canvas-area"
				[style.width]="selected==null? 'calc(100vw - 46px)' : 'calc(100vw - 256px)'"
			>
				<pyro-canvas
					class="d-flex flex-column h-100" 
					style="overflow: hidden"
					#canvas
					[user]="user"
					[redirectionStack]="redirectionStack"
					[currentFile]="currentFile"
					[currentLocalSettings]="currentLocalSettings"
					[permissionVectors]="permissionVectors"
					[layoutType]="mainLayout"
					(selectionChanged)="selectionChanged($event)"
					(selectionChangedModal)="selectionChangedModal($event)"
					(hasChanged)="changedData($event)"
					(jumpTo)="jumpToPrime($event)"
					(changeLayout)="changeGridLayout($event)"
				></pyro-canvas>
			</div>
		</div>
		
	    <div class="grid-stack mt-2" *ngIf="mainLayout=='classic'&&grid != null">
	 		<ng-container *ngFor="let widgetArea of grid.items; trackBy: trackByWidgetAreaId">
		    	<div 
					class="grid-stack-item" 
					*ngIf="gridItemMap[widgetArea.id] != null"
					[class.fullscreen]="isFullscreen(widgetArea.id)"
					[class.hidden]="!showWidget(widgetArea.id)"
					[attr.data-gs-id]="widgetArea.id"
				>
					<div class="grid-stack-item-content">
						<div class="grid-stack-item-header d-flex align-items-center justify-content-end" style="min-height: 25px"> 
						  <i class="fas fa-trash" 
						  	 (click)="removeWidgetArea($event, widgetArea)"
						  	 *ngIf="!isFullscreen(widgetArea.id)"
						  ></i>
		 				  <i class="ml-3 fas" 
		 				  	 [class.fa-compress]="isFullscreen(widgetArea.id)"
		 				  	 [class.fa-expand]="!isFullscreen(widgetArea.id)"
		 				  	 (click)="toggleFullscreen(widgetArea.id)"
		 				  ></i>
						</div>
					    <div class="grid-stack-item-body" *ngIf="widgetArea.widgets.length > 0">
							<editor-tabs-dropzone [area]="widgetArea" (drop)="moveWidget($event)">
							    <bs-tabs #tabs>
							  		<ng-container *ngFor="let widget of widgetArea.widgets; trackBy: trackByWidgetId">
							  		  <template bsTab [select]="widget.key">
							  		  	<editor-tabs-draggable 
							  		  		[widget]="widget" 
							  		  		(close)="removeWidget(null, widgetArea, $event)"
							  		  		(detach)="createWidgetArea($event)"
							  		  	>
									      {{widget.tab}} <small (click)="removeWidget($event, widgetArea, widget)"><i class="remove-widget fas fa-times ml-1"></i></small>
									    </editor-tabs-draggable>
									  </template>
								    </ng-container>
							    </bs-tabs>
							</editor-tabs-dropzone>
							<bs-tab-content [for]="tabs" style="height: 100%;">
								<ng-container *ngFor="let widget of widgetArea.visibleWidgets; trackBy: trackByWidgetId">
									<template bs-tab-panel [name]="widget.key">
										<div [ngSwitch]="widget.key" style="height: 100%;">
											<ng-container *ngSwitchCase="'canvas'">
												<pyro-canvas
													class="d-flex flex-column h-100" 
												    style="overflow: hidden"
													#canvas
													[user]="user"
												    [currentFile]="currentFile"
													[redirectionStack]="redirectionStack"
												    [currentLocalSettings]="currentLocalSettings"
												    [permissionVectors]="permissionVectors"
												    [layoutType]="mainLayout"
												    (selectionChanged)="selectionChanged($event)"
												    (selectionChangedModal)="selectionChangedModal($event)"
												    (hasChanged)="changedData($event)"
												    (jumpTo)="jumpToPrime($event)"
												    (changeLayout)="changeGridLayout($event)"
												></pyro-canvas>
											</ng-container>
											<ng-container *ngSwitchCase="'properties'">
												<properties
													class="d-flex flex-column h-100"
									                *ngIf="currentFile != null && selectedElement != null && isGraphModel"
									                [user]="user"
									                [currentGraphModel]="currentFile"
									                [currentGraphElement]="selectedElement"
									                (hasChanged)="changedProperties($event)"
									            >
									            </properties>
									        </ng-container>
						            		<ng-container *ngSwitchCase="'palette'">
									            <palette class="d-flex flex-column h-100"
													*ngIf="isGraphModel"
											        [currentGraphModel]="currentFile"
											        [permissionVectors]="permissionVectors"
											        (dragged)="currentDragging($event)"
										        ></palette>
						            		</ng-container>
								            <ng-container *ngSwitchCase="'checks'">
									            <check
								            	    class="d-flex flex-column h-100"
								                	*ngIf="isGraphModel"
								                    [currentGraphModel]="currentFile"
								                ></check>
								            </ng-container>
								            <ng-container *ngSwitchCase="'command_history'">
									              <command-history class="d-flex flex-column h-100"
								            	    (reverted)="graphService.canvasComponent.undo()"
								                	*ngIf="isGraphModel"
								                ></command-history>
								            </ng-container>
								            <ng-container *ngSwitchCase="'map'">
								            	<map class="d-flex flex-column h-100"
								            		*ngIf="isGraphModel"
								            		[currentGraphModel]="currentFile"
								            	></map>
								            </ng-container>
											«FOR pc:eps.filter[pluginComponent.fetchURL!==null].map[pluginComponent]»
												
												<ng-container *ngSwitchCase="'«pc.key»'">
												    <tree-view
												        *ngIf="isGraphModel"
														[user]="user"
														[currentGraphModel]="currentFile"
												        [name]="'«pc.tab»'"
														[fetchUrl]="'«pc.fetchURL»'"
														[clickUrl]="'«pc.clickURL»'"
														[dbClickUrl]="'«pc.dbClickURL»'"
														[parent]="instance"
														style="height: 100%;"
													>
													</tree-view>
												</ng-container>
											«ENDFOR»
											<div *ngSwitchDefault>
											  No widget available
											</div>
								  		</div>
							       	</template>
							    </ng-container>
							</bs-tab-content>
						</div>
					</div>
				</div>
			</ng-container>
	    </div>
	</div>
	'''
}
