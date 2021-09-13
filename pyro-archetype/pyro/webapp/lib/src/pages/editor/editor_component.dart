import 'package:FlowGraphTool/src/model/message.dart';
import 'package:FlowGraphTool/src/service/base_service.dart';

import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';
import 'package:ng_bootstrap/ng_bootstrap.dart';
import 'dart:html';
import 'dart:async';
import 'dart:convert';
import 'dart:js';

import 'package:FlowGraphTool/src/routes.dart' as top_routes;

import 'package:FlowGraphTool/src/model/core.dart';
import 'package:FlowGraphTool/src/service/user_service.dart';
import 'package:FlowGraphTool/src/pages/editor/canvas/canvas_component.dart';
import 'package:FlowGraphTool/src/pages/editor/properties/properties_component.dart';
import 'package:FlowGraphTool/src/pages/editor/palette/list/list_view.dart';
import 'package:FlowGraphTool/src/pages/editor/palette/palette_component.dart';
import 'package:FlowGraphTool/src/pages/editor/command_history/command_history_component.dart';
import 'package:FlowGraphTool/src/pages/editor/utils/editor_tabs_draggable/editor_tabs_draggable.dart';
import 'package:FlowGraphTool/src/pages/editor/utils/editor_tabs_dropzone/editor_tabs_dropzone.dart';
import 'package:FlowGraphTool/src/service/notification_service.dart';
import 'package:FlowGraphTool/src/service/check_service.dart';
import 'package:FlowGraphTool/src/service/graph_model_permission_vector_service.dart';
import 'package:FlowGraphTool/src/service/style_service.dart';
import 'package:FlowGraphTool/src/service/editor_grid_service.dart';
import 'package:FlowGraphTool/src/service/editor_data_service.dart';
import 'package:FlowGraphTool/src/pages/editor/check/check_component.dart';
import 'package:FlowGraphTool/src/service/graph_service.dart';
import 'package:FlowGraphTool/src/view/tree_view.dart';

import 'package:FlowGraphTool/src/pages/editor/canvas/graphs/flowgraph/flowgraphdiagram_command_graph.dart';

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
      bsDirectives,
      EditorTabsDropzoneComponent,
      EditorTabsDraggableComponent
    ]
)
class EditorComponent implements OnActivate, OnDeactivate, AfterViewInit, AfterViewChecked {

  PyroUser user;
  List<PyroUser> activeUsers = new List();
  
  PyroEditorGrid grid;
  Map<int, PyroEditorGridItem> gridItemMap = new Map();
  
  @ViewChildren(BsTabsComponent)
  List<BsTabsComponent> widgetTabs = new List();
  @ViewChildren(PropertiesComponent)
  List<PropertiesComponent> properties = new List();
  
  List<PyroGraphModelPermissionVector> permissionVectors;

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
  String mainLayout = "classic";
  
  
  	
  EditorComponent(this._editorGridService, this.graphService, this._router, this._userService, this._notificationService, 
  				  this._styleService, this._permissionService, this._editorDataService) {
    currentLocalSettings = new LocalGraphModelSettings();
    permissionVectors = new List();

	fetchGrid(); // TODO: SAMI: THEIA: needed?
  }
	  
	@override
	void ngAfterViewInit() {	
		var timer = new Timer.periodic(const Duration(milliseconds: 100), (Timer t){
			if (grid != null && mainLayout=='classic') {
				initializeGrid(t);
			}
		});   
	}

	void fetchGrid() {
		this._editorGridService.get().then((g) {
			this.updateGrid(g);
		});
	}

	void initializeGrid(Timer t) {
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
    
	@override
	void ngAfterViewChecked() {
		if (grid != null) {
			document.dispatchEvent(new CustomEvent("editor:grid-reinit", detail: grid.toJSOG(new Map())));
		}
	}
  
	@override
  	void onActivate(_, RouterState current) async {
		if(current.queryParameters.containsKey("token")) {
  	 			// TODO: SAMI: THEIA
			// window.localStorage[BaseService.tokenKey] = current.queryParameters["token"];
		} else {
			print("ERR: no token in URL");
			return;
		}
		int modelId = 0;
		if(current.parameters.containsKey("modelId")) {
			modelId = int.tryParse(current.parameters["modelId"]);
		} else {
			print("ERR: no modelId in URL");
			return;
		}
		String ext = null;
		if(current.queryParameters.containsKey("ext")) {
			ext = current.queryParameters["ext"];
		} else {
			print("ERR: no extension in URL");
			return;
		}
		_userService.loadUser().then((u){
			user = u;
			_editorDataService.user = u;
			document.title = "editor";
			if(ext == "flowgraph") {
				graphService.loadGraphFlowGraphDiagram(modelId).then((g) {
					this.currentFile = g;
					this.selectedElement = g;
					this.selectedElementModal = g;
				}).catchError((_){});
			}
		}).catchError((_){});
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
  void onDeactivate(_, RouterState next) async {
  	_styleService.handleOnDeactivate(next);
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
  
    _editorGridService.moveWidget(grid.id, data['widgetId'], data['toAreaId']).then((g) {
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
  	 //TODO Open File by ID
  }

  void receiveMessage(String json)
  {
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
	_editorGridService.setLayout(grid.id, layout).then((grid) {
	  updateGrid(grid);
	});
  }

  Future<Message> sendMessage(Message message) async
  {
      return graphService.sendMessage(message,currentFile.$lower_type(),currentFile.id);
  }

  void receiveGraphModelUpdate(CompoundCommandMessage message)
  {
	  if(this.currentFile.$lower_type() == 'flowgraphdiagram') {
	    FlowGraphDiagramCommandGraph cg = new FlowGraphDiagramCommandGraph(this.currentFile,new List());
	    cg.receiveCommand(message);
	  }
  }

  void receivePropertyUpdate(PropertyMessage message)
  {
        IdentifiableElement ie = this.currentFile.allElements().where((n)=>n.id==message.delegate.id).first;
        if(ie != null) {
          ie.merge(message.delegate,structureOnly:true);
        }
  }
}

