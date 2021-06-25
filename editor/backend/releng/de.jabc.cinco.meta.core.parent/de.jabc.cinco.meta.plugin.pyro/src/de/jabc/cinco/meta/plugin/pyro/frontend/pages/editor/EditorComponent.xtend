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
	import 'dart:convert';
	import 'dart:js';
	
	import 'package:«gc.projectName.escapeDart»/src/pages/main/routes.dart';
	import 'package:«gc.projectName.escapeDart»/src/routes.dart' as top_routes;
	
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/user_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/menu/menu_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/explorer/explorer_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/canvas/canvas_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/properties/properties_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/palette/list/list_view.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/palette/palette_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/map/map_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/command_history/command_history_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/utils/editor_tabs_draggable/editor_tabs_draggable.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/utils/editor_tabs_dropzone/editor_tabs_dropzone.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/notification_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/organization_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/check_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/graph_model_permission_vector_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/style_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/editor_grid_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/editor_data_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/check/check_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/graph_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/project_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/view/tree_view.dart';
	
	«FOR g:gc.graphMopdels»
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
		    	ClassProvider(ProjectService),
		    	ClassProvider(OrganizationService),
		    	ClassProvider(CheckService)
	    	],
	    directives: const [
	      TreeViewComponent,
	      MenuComponent,
	      routerDirectives,
	      coreDirectives,
	      ExplorerComponent,
	      CanvasComponent,
	      PropertiesComponent,
	      PaletteComponent,
	      MapComponent,
	      CheckComponent,
	      CommandHistoryComponent,
	      bsDirectives,
	      EditorTabsDropzoneComponent,
	      EditorTabsDraggableComponent
	    ]
	)
	class EditorComponent implements OnDestroy, OnActivate, OnDeactivate, AfterViewInit, AfterViewChecked {
	
	  PyroUser user;
	  List<PyroUser> activeUsers = new List();
	  
	  PyroProject inputProject;
	
	  PyroProject project;
	  
	  PyroOrganization organization;
	  
	  PyroEditorGrid grid;
	  Map<int, PyroEditorGridItem> gridItemMap = new Map();
	  
	  @ViewChildren(BsTabsComponent)
	  List<BsTabsComponent> widgetTabs = new List();
	  @ViewChildren(PropertiesComponent)
	  List<PropertiesComponent> properties = new List();
	  
	  List<PyroGraphModelPermissionVector> permissionVectors;
	
	  PyroFile currentFile = null;
	
	  IdentifiableElement selectedElement = null;
	  IdentifiableElement selectedElementModal = null;
	
	  LocalGraphModelSettings currentLocalSettings;
	  	  
	  int fullscreenWidgetId;
	  
	  final GraphService graphService;
	  final UserService _userService;
	  final NotificationService _notificationService;
	  final OrganizationService _organizationService;
	  final GraphModelPermissionVectorService _permissionService;
	  final StyleService _styleService;
	  final Router _router;
	  final EditorGridService _editorGridService;
	  final EditorDataService _editorDataService;
	  
	  String selected = null;
	  bool showNav = false;
	  String mainLayout = "classic";
	  
	  
	  WebSocket webSocketProject;
	  	
	  EditorComponent(this._editorGridService, this.graphService, this._router, this._userService, this._notificationService, 
	  				  this._organizationService, this._styleService, this._permissionService, this._editorDataService) {
	    currentLocalSettings = new LocalGraphModelSettings();
	    permissionVectors = new List();
	  }
		  
		@override
		void ngAfterViewInit() {	
			var timer = new Timer.periodic(const Duration(milliseconds: 100), (Timer t){
				if (grid != null&&mainLayout=='classic') {
					t.cancel();
		  			
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
			});   
		}
	    
		@override
		void ngAfterViewChecked() {
			if (grid != null) {
				document.dispatchEvent(new CustomEvent("editor:grid-reinit", detail: grid.toJSOG(new Map())));
			}
		}
	  
	  @override
	  void onActivate(_, RouterState current) async {
	  	if(window.localStorage.containsKey('PYRO_EDITOR_SELECTED')){
	  		this.selected = window.localStorage['PYRO_EDITOR_SELECTED']=='null'?null:window.localStorage['PYRO_EDITOR_SELECTED'];
		} else {
			this.selected = null;
		}
		if(window.localStorage.containsKey('PYRO_EDITOR_MAIN_LAYOUT')) {
			this.mainLayout = window.localStorage['PYRO_EDITOR_MAIN_LAYOUT'];
		} else {
			this.mainLayout = 'classic';
		}
		if(window.localStorage.containsKey('PYRO_EDITOR_SHOW_NAV')) {
			this.showNav = window.localStorage['PYRO_EDITOR_SHOW_NAV'] == 'true';
		} else {
			this.showNav = false;
		}
	  	    	
	    var orgId = current.parameters['orgId'];
	    var projectId = current.parameters['projectId'];
	    _userService.loadUser().then((u){
	      user = u;
	      _editorDataService.user = u;
	      graphService.loadProjectStructureById(projectId).then((p){
	        project=p;
	        _editorDataService.project = p;
	        document.title = "${p.name} editor";
	        activateWebSocket();
	        loadOpenedFiles();
	      }).catchError((e){
	      	_router.navigate(top_routes.Routes.organizations.toUrl());
	      });
	      _organizationService.getById(orgId).then((org){
	      	organization = org;
	      	_editorDataService.organization = org;
	      	_styleService.update(organization.style);
	      }).catchError((err){
	      	_router.navigate(top_routes.Routes.organizations.toUrl());
	      });
	      _permissionService.getMy("${projectId}").then((pvs) {
	      	permissionVectors = pvs;
	      }).catchError((err){
	      	_router.navigate(top_routes.Routes.organizations.toUrl());
	      });	
	      _editorGridService.get(int.tryParse(projectId)).then((g) {
	        updateGrid(g);
	      }).catchError((err){
	        _router.navigate(top_routes.Routes.organizations.toUrl());
	  	  }); 	     	     
	    }).catchError((_){_router.navigate(Routes.login.toUrl());});
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
	  }
	  void toggleNav() {
	  	showNav = !showNav;
	  	window.localStorage['PYRO_EDITOR_SHOW_NAV'] = showNav?'true':'false';
	  }
	  void changedMainLayout(layout) {
		mainLayout = layout;
		window.localStorage['PYRO_EDITOR_MAIN_LAYOUT'] = layout;
		ngAfterViewInit();
	  }
	  
	  @override
	  void ngOnDestroy() {
	      closeWebSocket();
	  }
	  
	  @override
	  void onDeactivate(_, RouterState next) async {
	  	_styleService.handleOnDeactivate(next);
	  }
	  
	  bool get isGraphModel => currentFile is GraphModel;
	  	  
	  void closeWebSocket() {
	      if(this.webSocketProject != null && this.webSocketProject.readyState == WebSocket.OPEN) {
	        window.console.debug("Closing Websocket webSocketCurrentUser");
	        this.webSocketProject.close();
	        this.webSocketProject = null;
	      }
	  }
	  
	  void toggleFullscreen(int id) {
	  	fullscreenWidgetId = fullscreenWidgetId != null ? null : id;
	  }
	  
	  bool isFullscreen(int id) {
	  	return fullscreenWidgetId == id;
	  }
	  
	  bool showWidget(int id) {
	  	return fullscreenWidgetId == null || fullscreenWidgetId == id;
	  }
	  
	  void loadOpenedFiles() {
	      Map<String, String> queryParams = getQueryParams();
	      if (queryParams.containsKey('others') && queryParams.containsKey('currentFile')) {
	        List<PyroFile> allFiles = project.allFiles();
	      
	        int currentFileId = int.tryParse(queryParams['currentFile']);
	        List<PyroFile> currentFileList = allFiles.where((f) => f.id == currentFileId).toList();
	        if (currentFileList.length > 0) {
	          _addAndOpenFile(currentFileList[0]);
	        }
	        
	        queryParams['others'].split(',').forEach((id) {
	          int fileId = int.tryParse(id);
	          List<PyroFile> currentFileList = allFiles.where((f) => f.id == fileId).toList();
	  	    if (currentFileList.length > 0) {
	  	      _addAndOpenFile(currentFileList[0]);
	  	    }
	        });
	      }
	    }
	       
	    void updateQueryParams(dynamic obj) {
	      List<String> paramsList = new List();
	      obj.forEach((String key, dynamic value) {
	        String v = Uri.encodeComponent('$value');
	        paramsList.add('$key=$v');
	      });
	      String params = paramsList.join('&');
	        
	    	final local = const bool.fromEnvironment('local', defaultValue: true);  	
	    	String url = '';
	    	if (local) {
	    		String hash = window.location.hash.split('?')[0];
	    		url = window.location.pathname + hash + '?' + params;
	    	} else {
	    		url = window.location.pathname + '?' + params;
	    	}
	    	window.history.replaceState({}, document.title, url);
	    }
	    
	    Map<String, String> getQueryParams() {
	    	final local = const bool.fromEnvironment('local', defaultValue: true);  
	    	if (local) {
	    	  List<String> values = window.location.hash.split('?');
	    	  Map<String, String> queryParams = new Map();
	    	  if (values.length > 1) {
	    	    values.removeAt(0);
	    	    String query = values.join('?');
	    	    query.split('&').forEach((param){
	    	    	List<String> kv = param.split('=');
	    	    	queryParams[kv[0]] = Uri.decodeComponent(kv[1]);
	    	    });
	    	  } 
	    	  return queryParams;
	    	} else {
	    	  return this._router.current.queryParameters;
	    	}
	    }
	  
	  void activateWebSocket() {
	      BaseService.getTicket().then((ticket) {
	        if (this.user != null && this.webSocketProject == null) {
	          this.webSocketProject = new WebSocket(
	              '${graphService.getBaseUrl(protocol: 'ws:')}/ws/project/${project.id}/${ticket}/private');
	  
	          // Callbacks for currentUser
	          this.webSocketProject.onOpen.listen((e) {
	            window.console.debug("[PYRO] onOpen Project Websocket");
	          });
	          this.webSocketProject.onMessage.listen((MessageEvent e) {
	            window.console.debug("[PYRO] onMessage Project Websocket");
	  
	            if (e.data != null) {
	              var jsog = jsonDecode(e.data);
	  
	              switch (jsog['event']) {
	                case 'project:removeUser':
	                  activeUsers.removeWhere((u) => u.id == jsog['content']);
	                  break;
	                case 'project:updateUserList':
	                  activeUsers = List.from(jsog['content']
	                      .map((u) => PyroUser.fromJSOG(new Map(), u)));
	                  break;
	                default:
						var newProject = PyroProject.fromJSOG(
							cache: new Map(), jsog: jsog['content']);
						List<PyroFile> deleted = getDeletedFiles(project, newProject);
						project.merge(newProject);
						for (var d in deleted) {
							hasDeleted(d);
						}
						break;
	              }

	            }
	          });
	          this.webSocketProject.onClose.listen((CloseEvent e) {
	            //_notificationService.displayMessage(
	            //    "Project Synchronization Terminated", NotificationType.WARNING);
	            if (e.code == 4001) {
	              //project has been deleted or access denied
	              _router.navigate(top_routes.Routes.organizations.toUrl());
	            }
	            window.console.debug("[PYRO] onClose Project Websocket");
	          });
	          this.webSocketProject.onError.listen((e) {
	            _notificationService.displayMessage(
	                "Project-Synchronization Error", NotificationType.DANGER);
	            window.console
	                .debug("[PYRO] Error on Project Websocket: ${e.toString()}");
	          });
	        }
	      });
	    }
	  
	  void changedTabbing(PyroFile e) {
	    if (e != null) {
	      currentFile = e;
	      if (e is GraphModel) {
	        selectedElement = e as GraphModel;
	      }
	    }
	    // if there is no currentFile
	    else if (currentFile == null) {
	      // if there is an openedFile that can be opened as alternative
	      if (currentLocalSettings.openedFiles.isNotEmpty) {
	        currentFile = currentLocalSettings.openedFiles.last;
	        if (currentLocalSettings.openedFiles.last is GraphModel) {
	          selectedElement = currentLocalSettings.openedFiles.last as GraphModel;
	        }
	      } else {
	        // If there is absolutly no file to open
	        // show the "wizard page" => hard routing not needed!
	        //
	        // var orgId = organization.id;
	        // var projectId = project.id;
	        // var routePath = top_routes.RoutePaths.editor
	        //    .toUrl(parameters: {'orgId': '$orgId', 'projectId': '$projectId'});
	        //_router.navigate(routePath);
	        
	        return;
	      }
	    } else if (currentFile != null) {
	      // if the currentFile is not an openedFile anymore
	      if (currentLocalSettings.openedFiles
	          .where((g) => g.id == currentFile.id)
	          .isEmpty) {
	        // if there is an openedFile that can be opened as alternative
	        if (currentLocalSettings.openedFiles.isNotEmpty) {
	          currentFile = currentLocalSettings.openedFiles.last;
	          if (currentLocalSettings.openedFiles.last is GraphModel) {
	            selectedElement =
	                currentLocalSettings.openedFiles.last as GraphModel;
	          }
	        } else {
	          // If there is absolutly no file to open
	          // show the "wizard page"
	          // var orgId = organization.id;
	          // var projectId = project.id;
	          
	          currentFile = null;
	          
	          //_router.navigate(top_routes.RoutePaths.editor.toUrl(
	          //    parameters: {'orgId': '$orgId', 'projectId': '$projectId'}));
	          return;
	        }
	      }
	    }
	    var others = "_";
	    if (currentLocalSettings.openedFiles.isNotEmpty) {
	      others = currentLocalSettings.openedFiles.map((pf) => pf.id).join(",");
	    }
	    updateQueryParams({'currentFile': currentFile.id, 'others': others});
	  }
	
	  /*
	   * identifies deleted files and handles closing of deleted but still
	   * opened
	   */
	  List<PyroFile> getDeletedFiles(PyroProject old, PyroProject update) {
	      var oldFiles = old.allFiles();
	      var newFiles = update.allFiles();
	      var deletedFiles = new List<PyroFile>();
	      for (var f in oldFiles) {
	        bool deleted = true;
	        for (var g in newFiles) {
	          if (g.id == f.id) {
	            deleted = false;
	          }
	        }
	        if (deleted) {
	          deletedFiles.add(f);
	        }
	      }
	      return deletedFiles;
	  }
	  
	  void hasDeletedGraph(PyroFile g)
	  {
	      if(currentFile==g){
	        currentFile=null;
	      }
	      if(currentLocalSettings.openedFiles.contains(g)){
	      	this.graphService.canvasComponent.removeFileFromActiveList(g, null);
	      }
	  }
	  
	  void hasDeletedFolder(PyroFolder f)
	  {
	      f.innerFolders.forEach((n) => hasDeletedFolder(n));
	      f.files.forEach((n) => hasDeletedGraph(n));
	  }
	  
	  void hasDeleted(dynamic e)
	  {
	      if(e is PyroFolder){
	        hasDeletedFolder(e);
	      }
	      if(e is PyroFile){
	        hasDeletedGraph(e);
	      }
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
	  
	  createWidgetArea(PyroEditorWidget widget) {
	    _editorGridService.createArea(project.id, grid.id).then((area) {
	      moveWidget({
	        'widgetId': widget.id,
	        'toAreaId': area.id,
	        'fromAreaId': null
	      });
	    });
	  }
	
	  removeWidget(dynamic e, PyroEditorGridItem area, PyroEditorWidget widget) {
	    if (e != null) e.preventDefault();   
	    _editorGridService.removeWidget(project.id, grid.id, widget.id).then((g){
	      updateGrid(g);
	    });
	  }
	  
	  removeWidgetArea(dynamic e, PyroEditorGridItem area) {
	      e.preventDefault();    
	      _editorGridService.removeArea(project.id, grid.id, area.id).then((g){
	        updateGrid(g);
	      });
	    }
	    
	  moveWidget(dynamic data) {
	  	if (data['toAreaId'] == data['fromAreaId']) {
	  	  return;
	  	}
	  
	    _editorGridService.moveWidget(project.id, grid.id, data['widgetId'], data['toAreaId']).then((g) {
	      updateGrid(g);
	    });
	  }
	  
	  Object trackByWidgetId(_, dynamic o) => o is PyroEditorWidget ? o.id : o;
	  
	  Object trackByWidgetAreaId(_, dynamic o) => o is PyroEditorGridItem ? o.id : o;
	
	  void changeStructure(dynamic e)
	  {
	  }
	
	  void changedGraph(CompoundCommandMessage ccm)
	  {
	    sendMessage(ccm);
	  }
	
	  void changedProperties(PropertyMessage pm)
	  {
	  	this.graphService.canvasComponent.updateProperties(pm.delegate);
	    sendMessage(pm).then((m){
	    if (m is CompoundCommandMessage) {
	         this.graphService.canvasComponent.executeCommands(m,true);
	         graphService.update(currentFile.id);
	       }
	    });
	  }
	  
	  void _addAndOpenFile(PyroFile file) {
	  	if(!currentLocalSettings.openedFiles.contains(file)) {
	  		currentLocalSettings.openedFiles.add(file);
	  	}
	  	currentFile = file;
	  	if(currentFile is GraphModel) {
	  		graphService.loadCommandGraph(currentFile, []).then((_) {
	  			selectedElement = currentFile as GraphModel;
	  		});
	  	}
	  }
	
	  void addAndOpenFile(PyroFile file)
	  {
	    _addAndOpenFile(file);
	    var others = "_";
	    if(currentLocalSettings.openedFiles.isNotEmpty){
	      others = currentLocalSettings.openedFiles.map((pf)=>pf.id).join(",");
	    }
	    updateQueryParams({'currentFile':currentFile.id,'others':others});
	  }
	
	  void selectionChanged(IdentifiableElement element)
	  {
	    selectedElement = element;
	  }
	  
	  void selectionChangedModal(IdentifiableElement element)
	  {
	     selectedElementModal = element;
	     if(element != null) {
	       	properties.forEach((n)=>n.showModal());     
	     }
	  }
	  
	  void currentDragging(MapListValue value)
	  {
	    print(value.name);
	  }
	  
	  void jumpToPrime(Map map) {
	  	 var gs = project.allGraphModels().where((n)=>n.id==int.parse(map['graphmodel_id'].toString()));
	  	 if(gs.isNotEmpty) {	  	 	
		  	 addAndOpenFile(gs.first);
	  	 }
	  }
	
	  void receiveMessage(String json)
	  {
	    Message message = Message.fromJSON(json);
	    _notificationService.displayMessage("Update",NotificationType.INFO);
	    if(message is CompoundCommandMessage) {
	      receiveGraphModelUpdate(message);
	    }
	    if(message is ProjectMessage) {
	      receiveProjectStructureUpdate(message);
	    }
	    if(message is PropertyMessage) {
	      receivePropertyUpdate(message);
	    }
	  }
	  
	  changeGridLayout(String layout) {
		_editorGridService.setLayout(project.id, grid.id, layout).then((grid) {
		  updateGrid(grid);
		});
	  }
	
	  Future<Message> sendMessage(Message message) async
	  {
	      return graphService.sendMessage(message,(currentFile as GraphModel).$lower_type(),currentFile.id);
	  }
	
	  void receiveProjectStructureUpdate(ProjectMessage message)
	  {
	    project.merge(message.project);
	  }
	
	  void receiveGraphModelUpdate(CompoundCommandMessage message)
	  {
	    GraphModel gm = project.allGraphModels().where((g) => g.id==message.graphModelId).first;
	    if(gm != null) {
	      // for each graph model
		  «FOR g:gc.graphMopdels»
		      if(gm.$lower_type() == '«g.lowerType»') {
		        «g.name.fuEscapeDart»CommandGraph cg = new «g.name.fuEscapeDart»CommandGraph(gm,new List());
		        cg.receiveCommand(message);
		      }
		  «ENDFOR»
	    }
	  }
	
	  void receivePropertyUpdate(PropertyMessage message)
	  {
	    GraphModel gm = project.allGraphModels().where((g) => g.id==message.graphModelId).first;
	    if(gm != null) {
	        IdentifiableElement ie = gm.allElements().where((n)=>n.id==message.delegate.id).first;
	        if(ie != null) {
	          ie.merge(message.delegate,structureOnly:true);
	        }
	    }
	  }
	}
	
	'''
	def contentEditorTemplate()
	'''
	<div *ngIf="user!=null&&project!=null">
	    <pyro-menu
	        *ngIf="mainLayout=='classic'"
	        style="padding-left: 0;"
	        (clickWidget)="createWidgetArea($event)"
	        (changeLayout)="changeGridLayout($event)"
	        (changedMainLayout)="changedMainLayout($event)"
	        [user]="user"
	        [currentLayout]="'classic'"
	        [activeUsers]="activeUsers"
	        [organization]="organization"
	        [project]="project"
	        [currentFile]="currentFile"
	    >
	    </pyro-menu>
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
		<div *ngIf="showNav && mainLayout=='micro'" class="pyro-micro-navigation">
			<pyro-menu
		        style="padding-left: 0;"
		        (changedMainLayout)="changedMainLayout($event)"
		        (clickWidget)="createWidgetArea($event)"
		        (changeLayout)="changeGridLayout($event)"
		        [currentLayout]="'micro'"
		        [user]="user"
		        [activeUsers]="activeUsers"
		        [organization]="organization"
		        [project]="project"
		        [currentFile]="currentFile"
		    >
		    </pyro-menu>
		</div>
		<div class="row" *ngIf="grid != null&&mainLayout=='micro'" style="margin-right:0">
			<div [style.width.px]="selected==null?'39':'265'" style="padding-right: 0;">
				<div class="row">
					<button id="menu-button" (click)="toggleNav()" class="btn btn-primary" style="width: 36px;height: 32px;margin-left: 34px;margin-top: 0px;"><i class="fas fa-list-ul _ngcontent-hvg-13"></i></button>
			        <div id="scroll-menu" [style.top.px]="showNav?100:31" >
			          <ul class="nav nav-tabs left-tabs sideways-tabs" style="margin-top:50px;">
			        	<ng-container *ngFor="let widgetArea of grid.items; trackBy: trackByWidgetAreaId">
			        		<ng-container *ngFor="let widget of widgetArea.widgets; trackBy: trackByWidgetId">
					            <li *ngIf="widget.key=='explorer'" class="nav-item">
					              <a class="nav-link" [class.active]="selected=='explorer'" title="Show Explorer" href (click)="selectView($event,'explorer')">Explorer</a>
					            </li>
					            <li *ngIf="widget.key=='palette'" class="nav-item">
					              <a class="nav-link" [class.active]="selected=='palette'" title="Show Palette" href (click)="selectView($event,'palette')">Palette</a>
					            </li>
					            <li *ngIf="widget.key=='map'" class="nav-item">
					              <a class="nav-link" [class.active]="selected=='map'" title="Show Map" href (click)="selectView($event,'map')">Map</a>
					            </li>
					            <li *ngIf="widget.key=='checks'" class="nav-item">
					              <a class="nav-link" [class.active]="selected=='check'" title="Show Checks" href (click)="selectView($event,'check')">Check</a>
					            </li>
					            <li *ngIf="widget.key=='command_history'" class="nav-item">
					              <a class="nav-link" [class.active]="selected=='comman-history'" title="Show Command History" href (click)="selectView($event,'comman-history')">History</a>
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
		
			        <div class="pyro-micor-menu" style="height: 100%;padding-left:0;width:210px;border: #57747b 2px solid;" *ngIf="selected!=null">
			        	<h5 style="margin-top: 7px;margin-bottom:5px;text-align: center;">{{selected}}</h5>
					      <explorer
							    *ngIf="selected=='explorer'"
				            	class="d-flex flex-column h-100"
				                [user]="user"
				                [project]="project"
				                [currentFile]="currentFile"
				                [permissionVectors]="permissionVectors"
				                (hasDeleted)="hasDeleted($event)"
				                (hasChanged)="changeStructure($event)"
				                (openFile)="addAndOpenFile($event)"
				          ></explorer>
				          <map
							  class="d-flex flex-column h-100"
							  *ngIf="selected=='map'&&isGraphModel"
							  [currentGraphModel]="currentFile"
						  ></map>
				          <palette class="d-flex flex-column h-100"
								*ngIf="selected=='palette'&&isGraphModel"
						        [currentGraphModel]="currentFile"
						        [permissionVectors]="permissionVectors"
						        (dragged)="currentDragging($event)"
					        ></palette>
					      <check
		                	*ngIf="selected=='check'&&isGraphModel"
		            	    class="d-flex flex-column h-100"
		                    [currentGraphModel]="currentFile"
		                  ></check>
		                   <command-history
			            	    class="d-flex flex-column h-100"
			            	    (reverted)="graphService.canvasComponent.undo()"
			                	*ngIf="isGraphModel&&selected=='comman-history'"
			                ></command-history>
							«FOR pc:eps.filter[pluginComponent.fetchURL!==null].map[pluginComponent]»
									<tree-view
									    *ngIf="selected=='«pc.tab»'&&isGraphModel"
										[user]="user"
										[project]="project"
										[currentGraphModel]="currentFile"
									    [name]="'«pc.tab»'"
										[fetchUrl]="'«pc.fetchURL»'"
										[clickUrl]="'«pc.clickURL»'"
										[dbClickUrl]="'«pc.dbClickURL»'"
										>
									</tree-view>
							«ENDFOR»
			        </div>
				</div>
			</div>
			<div [class.micro-column-right-xl]="selected==null" [class.micro-column-right-sm]="selected!=null" style="height: calc(100vh + 28px);overflow: hidden;padding-left: 0;">
				<pyro-canvas
		    	    class="d-flex flex-column h-100" 
		    	    style="overflow: hidden"
		        	#canvas
		        	[user]="user"
		            [project]="project"
		            [currentFile]="currentFile"
		            [currentLocalSettings]="currentLocalSettings"
		            [permissionVectors]="permissionVectors"
		            (selectionChanged)="selectionChanged($event)"
		            (selectionChangedModal)="selectionChangedModal($event)"
		            (hasChanged)="changedGraph($event)"
		            (tabChanged)="changedTabbing($event)"
		                (jumpTo)="jumpToPrime($event)"
		          ></pyro-canvas>
			</div>
		</div>
		
	    <div class="grid-stack" *ngIf="mainLayout=='classic'&&grid != null">
	    
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
						
					  <bs-tab-content [for]="tabs">
					    <ng-container *ngFor="let widget of widgetArea.visibleWidgets; trackBy: trackByWidgetId">
					      <template bs-tab-panel [name]="widget.key">
					       
					       <div [ngSwitch]="widget.key">
							 <explorer
							    *ngSwitchCase="'explorer'"
				            	class="d-flex flex-column h-100"
				                [user]="user"
				                [project]="project"
				                [currentFile]="currentFile"
				                [permissionVectors]="permissionVectors"
				                (hasDeleted)="hasDeleted($event)"
				                (hasChanged)="changeStructure($event)"
				                (openFile)="addAndOpenFile($event)"
				            >
				            </explorer>
				            
				            <ng-container *ngSwitchCase="'map'">
				               <map
	                  			  class="d-flex flex-column h-100"
	                  			  *ngIf="isGraphModel"
	                  			  [currentGraphModel]="currentFile"
	                		  ></map>
				            </ng-container>
				            
							<ng-container *ngSwitchCase="'canvas'">
							  <pyro-canvas
							    class="d-flex flex-column h-100" 
							    style="overflow: hidden"
								#canvas
								[user]="user"
							    [project]="project"
							    [currentFile]="currentFile"
							    [currentLocalSettings]="currentLocalSettings"
							    [permissionVectors]="permissionVectors"
							    (selectionChanged)="selectionChanged($event)"
							    (selectionChangedModal)="selectionChangedModal($event)"
							    (hasChanged)="changedGraph($event)"
							    (tabChanged)="changedTabbing($event)"
							    (jumpTo)="jumpToPrime($event)"
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
					              <command-history
				            	    class="d-flex flex-column h-100"
				            	    (reverted)="graphService.canvasComponent.undo()"
				                	*ngIf="isGraphModel"
				                ></command-history>
				            </ng-container>
				            
							«FOR pc:eps.filter[pluginComponent.fetchURL!==null].map[pluginComponent]»
								<ng-container *ngSwitchCase="'«pc.key»'">
								    <tree-view
								        *ngIf="isGraphModel"
										[user]="user"
										[project]="project"
										[currentGraphModel]="currentFile"
								        [name]="'«pc.tab»'"
										[fetchUrl]="'«pc.fetchURL»'"
										[clickUrl]="'«pc.clickURL»'"
										[dbClickUrl]="'«pc.dbClickURL»'"
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
