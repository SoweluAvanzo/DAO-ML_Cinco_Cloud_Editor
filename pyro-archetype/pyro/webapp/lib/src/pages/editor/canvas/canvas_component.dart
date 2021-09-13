import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'dart:async';
import 'dart:html';
import 'dart:convert';
import 'package:angular/src/security/dom_sanitization_service.dart';
import 'package:ng_bootstrap/ng_bootstrap.dart';

import 'package:FlowGraphTool/src/model/core.dart';
import 'package:FlowGraphTool/src/model/message.dart';
import 'package:FlowGraphTool/src/service/base_service.dart';
import 'package:FlowGraphTool/src/service/notification_service.dart';
import 'package:FlowGraphTool/src/utils/graph_model_permission_utils.dart';
import 'package:FlowGraphTool/src/service/graph_service.dart';
import 'dart:js' as js;

import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;
import 'package:FlowGraphTool/src/pages/editor/canvas/graphs/flowgraph/flowgraphdiagram_component.dart' as flowgraph;
import 'package:FlowGraphTool/src/model/externallibrary.dart' as externallibrary;

@Component(
    selector: 'pyro-canvas',
    templateUrl: 'canvas_component.html',
    styleUrls: const ['package:FlowGraphTool/src/pages/editor/editor_component.css'],
    directives: const [
        bsDropdownDirectives,
    	coreDirectives,formDirectives
,    	flowgraph.FlowGraphDiagramCanvasComponent
    ]
)
class CanvasComponent implements OnInit, OnChanges {
	
  final DomSanitizationService _domSanitizationService;
  final NotificationService _notificationService;
  final GraphService _graphService;

  @ViewChild('flowgraphdiagram_canvas_component')
  flowgraph.FlowGraphDiagramCanvasComponent flowgraphdiagramCanvasComponent;

  final selectionChangedSC = new StreamController();
  @Output() Stream get selectionChanged => selectionChangedSC.stream;
  final selectionChangedModalSC = new StreamController();
  @Output() Stream get selectionChangedModal => selectionChangedModalSC.stream;

  final hasChangedSC = new StreamController();
  @Output() Stream get hasChanged => hasChangedSC.stream;
  
  final tabChangedSC = new StreamController();
  @Output() Stream get tabChanged => tabChangedSC.stream;
  
  final jumpToSC = new StreamController();
  @Output() Stream get jumpTo => jumpToSC.stream;
  
  @Input()
  PyroUser user;
  @Input()
  GraphModel currentFile;
  @Input()
  List<PyroGraphModelPermissionVector> permissionVectors = new List();
  @Input()
  LocalGraphModelSettings currentLocalSettings;
  
  bool isFullScreen = false;
  bool canEdit = false;
  bool isGenerating = false;
  bool isInterpreting = false;
  
  /// validation level
  bool isError = true;
  bool isWarning = true;
  bool isInfo = true;
  
  bool isGluelines = false;
  
  CanvasComponent(this._domSanitizationService, this._notificationService, this._graphService){}

  @override
  ngOnInit() {
  	if(window.localStorage.containsKey('PYRO_CANVAS_CHECK_IS_ERROR')) {
		this.isError = window.localStorage['PYRO_CANVAS_CHECK_IS_ERROR']=='true';
	} else {
		this.isError = true;
	}
	if(window.localStorage.containsKey('PYRO_CANVAS_CHECK_IS_WARNING')) {
		this.isWarning = window.localStorage['PYRO_CANVAS_CHECK_IS_WARNING']=='true';
	} else {
		this.isWarning = true;
	}
	if(window.localStorage.containsKey('PYRO_CANVAS_CHECK_IS_INFO')) {
		this.isInfo = window.localStorage['PYRO_CANVAS_CHECK_IS_INFO']=='true';
	} else {
		this.isInfo = true;
	}
  }
  
  @override
  ngOnChanges(Map<String, SimpleChange> changes) {
  	  this._graphService.canvasComponent = this;
      
      if(isFlowGraphDiagram()){
        canEdit = GraphModelPermissionUtils.canUpdate("FLOW_GRAPH_DIAGRAM", permissionVectors);
      }
  }
  
  void executeCommands(CompoundCommandMessage m,bool forceExecute) {
  	if(flowgraphdiagramCanvasComponent!=null) {
  		flowgraphdiagramCanvasComponent.executeCommands(m,forceExecute);
  	}
  }
  
  void updateProperties(IdentifiableElement element) {
  if(flowgraphdiagramCanvasComponent!=null) {
  	flowgraphdiagramCanvasComponent.updateProperties(element);
  }
  }
  
  
  void export(dynamic e, String type){
  	  e.preventDefault();
      if(flowgraphdiagramCanvasComponent!=null) {
      	flowgraphdiagramCanvasComponent.export(type);
      }
  }
  
  Map<String,String> getEditorButtons(){
	if(currentFile!=null && currentFile is flowgraph.FlowGraphDiagram){
		return new Map<String,String>();
	}
  }
  
  void updateScaleFactorStr(String s,bool persist) {
    	updateScaleFactor(int.parse(s)*0.01,persist);
  }
  
  void updateScale({double factor:0.0,bool persist:true}) {
  	if(flowgraphdiagramCanvasComponent!=null) {
  		flowgraphdiagramCanvasComponent.updateScale(factor,persist:persist);
  	}
  }
  
  void updateScaleFactor(double factor,[bool persist=true]) => updateScale(factor:factor,persist:persist);
  
  void updateRouting() {
	if(flowgraphdiagramCanvasComponent!=null) {
		flowgraphdiagramCanvasComponent.updateRouting();
	}
  }
  
  void undo() {
  if(flowgraphdiagramCanvasComponent!=null) {
  	flowgraphdiagramCanvasComponent.undo();
  }
  }
  	  
	void redo() {
		if(flowgraphdiagramCanvasComponent!=null) {
			flowgraphdiagramCanvasComponent.redo();
		}
	}
	  
  
  // for each graph model
  
  bool isFlowGraphDiagram(){
  	if(currentFile!=null){
  		return currentFile is flowgraph.FlowGraphDiagram;
  	}
  	return false;
  }
  
  // for each ecore model
  
  bool isExternalLibrary(){
  	if(currentFile!=null){
  		return currentFile is externallibrary.ExternalLibrary;
  	}
  	return false;
  }
  
  bool isModelFile() {
  	if(currentFile!=null){
      return currentFile is GraphModel;
    }
    return false;
  }
  
  bool hasChecks() {
  	if(currentFile!=null){
  	}
  	return false;
  }
  
  bool hasGenerator() {
  	if(currentFile!=null){
		if(currentFile is flowgraph.FlowGraphDiagram) {
			return true;
		}
    }
    return false;
  }
  
  Map<String,String> getGenerators() {
	Map<String,String> map = new Map<String,String>();
	if(currentFile is flowgraph.FlowGraphDiagram) {
		return map;
	}
    return map;
  }
  
  GraphModel getModelFile() {
      return this.currentFile;
  }
    
  String getScaleValue() {
    var m = getModelFile();
    if(m==null || m.scale == null){
    	return "100";
    }
    return (getModelFile().scale*100).toInt().toString();
  }
  

	bool hasIcon() {
		if(currentFile is flowgraph.FlowGraphDiagram){
		  return true;
		}
	    return false;
	}
	  
	String getIcon() {
	  	  if(currentFile is flowgraph.FlowGraphDiagram){
	  	    return "img/flowgraph/FlowGraph.png";
	  	  }
	  	  return "";
	}	
	
	void toggleIsError() {
		isError = !isError;
		window.localStorage['PYRO_CANVAS_CHECK_IS_ERROR'] = isError?'true':'false';
		_updateChecks();
	}
	void toggleIsWarning() {
		isWarning = !isWarning;
		window.localStorage['PYRO_CANVAS_CHECK_IS_WARNING'] = isWarning?'true':'false';
		_updateChecks();
	}
	void toggleIsInfo() {
		isInfo = !isInfo;
		window.localStorage['PYRO_CANVAS_CHECK_IS_INFO'] = isInfo?'true':'false';
		_updateChecks();
	}
	
	void toggleGluelines() {
		isGluelines = !isGluelines;
		_updateGluelines();
	}
	
	void _updateChecks() {
		if(flowgraphdiagramCanvasComponent!=null) {
		   	flowgraphdiagramCanvasComponent.updateCheckLevel(isError,isWarning,isInfo);
		}
	}
	
	void _updateGluelines() {
		if(flowgraphdiagramCanvasComponent!=null) {
		   	flowgraphdiagramCanvasComponent.updateGlueline(isGluelines);
		}
	}
	
	bool isActiveRouter(String s){
	      return s==(currentFile as GraphModel).router;
	  }
	
	  bool isActiveConnector(String s){
	      return s==(currentFile as GraphModel).connector;
	  }
	
	  void changeRouteLayout(String type,dynamic e)
	  {
	    e.preventDefault();
	    currentFile.router = type;
	    updateRouting();
	  }
	  
	  void executeGraphmodelButton(String key) {
		if(flowgraphdiagramCanvasComponent!=null) {
		   	flowgraphdiagramCanvasComponent.executeGraphmodelButton(key);
		}
	  }
	
	  void changeConnectorLayout(String type,dynamic e)
	  {
	    e.preventDefault();
	    currentFile.connector = type;
	    updateRouting();
	  }
	  
	  void triggerInterpreter(String name,dynamic e) {
	  	 if(e != null) {
	  	 	e.preventDefault();
	  	 }
	  	 if(isInterpreting) {
	  	 	_notificationService.displayMessage("A interpreter is still running",NotificationType.WARNING);
	  	 	return;
	  	 }
	  	 if(isModelFile()) {
	  	 }
	  }
	  
	  void triggerGenerator(String name,dynamic e) {
	  	 if(e != null) {
	  	 	e.preventDefault();
	  	 }
	  	 if(isGenerating) {
	  	 	_notificationService.displayMessage("A generator is still running",NotificationType.WARNING);
	  	 	return;
	  	 }
	  	 if(isModelFile()) {
			if(currentFile.$type() == 'flowgraph.FlowGraphDiagram') {
				if(name == null) {
					isGenerating = true;
					_graphService.generateGraph(currentFile, name).then((response){
						var s = response.responseText;
						_notificationService.displayMessage("FlowGraphDiagram ${currentFile.filename} generation completed successfully!",NotificationType.SUCCESS);
					})
					.catchError((e){
						_notificationService.displayLongMessage("FlowGraphDiagram ${currentFile.filename} generation failed!",NotificationType.DANGER);
					})
					.whenComplete(()=>isGenerating=false);
				}
			   	return;
			}
		 	_notificationService.displayMessage("No generator annotated for current graphmodel",NotificationType.WARNING);
		 } else {
		    _notificationService.displayMessage("No graphmodel present to generate",NotificationType.WARNING);
		 }
	  }
}
