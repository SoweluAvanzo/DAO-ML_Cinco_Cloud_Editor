package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.canvas

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class CanvasComponent extends Generatable {
	
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameCanvasComponent()'''canvas_component.dart'''
	
	def contentCanvasComponent()
	'''
	import 'package:angular/angular.dart';
	import 'package:angular_forms/angular_forms.dart';
	import 'dart:async';
	import 'dart:html';
	import 'dart:convert';
	import 'package:angular/src/security/dom_sanitization_service.dart';
	import 'package:ng_bootstrap/ng_bootstrap.dart';
	
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
	import 'package:«gc.projectName.escapeDart»/src/model/message.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/base_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/notification_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/utils/graph_model_permission_utils.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/graph_service.dart';
	import 'dart:js' as js;
	
	«FOR g:gc.mglModels»
		import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
	«ENDFOR»
	«FOR g:gc.graphMopdels»
		import 'package:«gc.projectName.escapeDart»/«g.componentFilePath»' as «g.modelPackage.name.lowEscapeDart»;
	«ENDFOR»
	«FOR g:gc.ecores»
		import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
	«ENDFOR»
	
	@Component(
	    selector: 'pyro-canvas',
	    templateUrl: 'canvas_component.html',
	    styleUrls: const ['package:«gc.projectName.escapeDart»/src/pages/editor/editor_component.css'],
	    directives: const [
	        bsDropdownDirectives,
	    	coreDirectives,formDirectives
	    	«FOR g:gc.graphMopdels BEFORE "," SEPARATOR ","»
	    		«g.modelPackage.name.lowEscapeDart».«g.name.fuEscapeDart»CanvasComponent
    		«ENDFOR»
	    ]
	)
	class CanvasComponent implements OnInit, OnChanges {
		
	  final DomSanitizationService _domSanitizationService;
	  final NotificationService _notificationService;
	  final GraphService _graphService;
	
	  «FOR g:gc.graphMopdels»
	  	@ViewChild('«g.name.lowEscapeDart»_canvas_component')
	  	«g.modelPackage.name.lowEscapeDart».«g.name.fuEscapeDart»CanvasComponent «g.name.lowEscapeDart»CanvasComponent;
	  «ENDFOR»
	
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
	  PyroProject project;
	  @Input()
	  PyroFile currentFile;
	  @Input()
	  List<PyroGraphModelPermissionVector> permissionVectors = new List();
	  @Input()
	  LocalGraphModelSettings currentLocalSettings;
	  
	  dynamic cached_url = null;
	  String tmp_url = null;
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
	      if (isBinaryFile() || isURLFile() || isTextFile()) {
	     	var url = "${BaseService.getUrl()}/pyrofile/read/projectresource/${project.id.toString()}/${project.fullPath(currentFile)}${currentFile.getFullName()}";
	     	if (tmp_url != url) {
	      		tmp_url = url;
	      		cached_url = _domSanitizationService.bypassSecurityTrustResourceUrl(url);
	      	}
	      }
	      
	      «FOR g:gc.graphMopdels»
	      if(is«g.name.fuEscapeDart»()){
	        canEdit = GraphModelPermissionUtils.canUpdate("«g.name.toUnderScoreCase»", permissionVectors);
	      }
      	  «ENDFOR»
	  }
	  
	  void executeCommands(CompoundCommandMessage m,bool forceExecute) {
	  	«FOR g:gc.graphMopdels»
	  		if(«g.name.lowEscapeDart»CanvasComponent!=null) {
	  			«g.name.lowEscapeDart»CanvasComponent.executeCommands(m,forceExecute);
	  		}
	   	«ENDFOR»
	  }
	  
	  void updateProperties(IdentifiableElement element) {
	  «FOR g:gc.graphMopdels»
	    if(«g.name.lowEscapeDart»CanvasComponent!=null) {
	    	«g.name.lowEscapeDart»CanvasComponent.updateProperties(element);
	    }
	  «ENDFOR»
	  }
	  
	  void createNewFile(String type,dynamic e)
	  {
	  	  	  if(e!=null)e.preventDefault();
	  	  	  var c = project.files.where((f)=>f.filename!=null&&f.filename.startsWith("untitled")).length;
			  var name = "untitled${c.toString()}";
	  	      switch(type)
	  	      {
	  	        «FOR g:gc.creatableGraphmodels»
	  	          case '«g.name.fuEscapeDart»':{
	  	            var g = new «g.dartFQN»();
	  	            g.filename = name;
	  	            _graphService.create«g.name.escapeDart»(g,project).then((f){
	  	            	var found = project.files.where((f)=>f.filename==name);
	  	            	if(found.isNotEmpty) {
	  	            		Map m = new Map();
	  	            		m['graphmodel_id'] = found.first.id;
	  	            	  	jumpToSC.add(m);showFile(found.first,null);
	  	            	}
	  	            });
	  	            break;
	  	          }
	  	        «ENDFOR»

	  	      }
	  	
	  }
	  
	  void export(dynamic e, String type){
	  	  e.preventDefault();
	      «FOR g:gc.graphMopdels»
	      	if(«g.name.lowEscapeDart»CanvasComponent!=null) {
	      		«g.name.lowEscapeDart»CanvasComponent.export(type);
	      	}
	      «ENDFOR»
	  }
	  
	  Map<String,String> getEditorButtons(){
		«FOR g:gc.graphMopdels»
			if(currentFile!=null && currentFile is «g.dartFQN»){
				«IF g.editorButtons.empty»
					return new Map<String,String>();
				«ELSE»
					Map<String,String> m = new Map<String,String>();
					«FOR a:g.editorButtons»
						m['«a.value.get(1)»'] = '«a.value.get(1).escapeJava»';
					«ENDFOR»
					return m;
				«ENDIF»
			}
		«ENDFOR»
	  }
	  
	  void updateScaleFactorStr(String s,bool persist) {
	    	updateScaleFactor(int.parse(s)*0.01,persist);
	  }
	  
	  void updateScale({double factor:0.0,bool persist:true}) {
	  	«FOR g:gc.graphMopdels»
	  		if(«g.name.lowEscapeDart»CanvasComponent!=null) {
	  			«g.name.lowEscapeDart»CanvasComponent.updateScale(factor,persist:persist);
	  		}
	  	«ENDFOR»
	  }
	  
	  void updateScaleFactor(double factor,[bool persist=true]) => updateScale(factor:factor,persist:persist);
	  
	  void updateRouting() {
		«FOR g:gc.graphMopdels»
		  if(«g.name.lowEscapeDart»CanvasComponent!=null) {
		  	«g.name.lowEscapeDart»CanvasComponent.updateRouting();
		  }
		«ENDFOR»
	  }
	  
	  void undo() {
	  «FOR g:gc.graphMopdels»
	  	if(«g.name.lowEscapeDart»CanvasComponent!=null) {
	  		«g.name.lowEscapeDart»CanvasComponent.undo();
	  	}
	  «ENDFOR»
	  }
	  	  
		void redo() {
			«FOR g:gc.graphMopdels»
				if(«g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.redo();
				}
			«ENDFOR»
		}
		  
	  bool isOpen(){
	    return currentLocalSettings.openedFiles.contains(currentFile);
	  }
	  
	  // for each graph model
	  «FOR g:gc.graphMopdels»
	  	
	  	bool is«g.name.fuEscapeDart»(){
	  		if(currentFile!=null){
	  			return currentFile is «g.dartFQN»;
	  		}
	  		return false;
	  	}
	  «ENDFOR»
	  
	  // for each ecore model
	  «FOR g:gc.ecores»
	  	
	  	bool is«g.name.fuEscapeDart»(){
	  		if(currentFile!=null){
	  			return currentFile is «g.dartFQN»;
	  		}
	  		return false;
	  	}
	  «ENDFOR»
	  
	  bool isModelFile() {
	  	if(currentFile!=null){
	      return currentFile is GraphModel;
	    }
	    return false;
	  }
	  
	  bool hasChecks() {
	  	if(currentFile!=null){
			«FOR g:gc.graphMopdels.filter[hasChecks(it)]»
				if(currentFile is «g.dartFQN») {
					return true;
				}
			«ENDFOR»
	  	}
	  	return false;
	  }
	  
	  bool hasGenerator() {
	  	if(currentFile!=null){
			«FOR g:gc.graphMopdels.filter[generating]»
				if(currentFile is «g.dartFQN») {
					return true;
				}
			«ENDFOR»
	    }
	    return false;
	  }
	  
	  Map<String,String> getGenerators() {
		Map<String,String> map = new Map<String,String>();
		if(currentFile!=null){
		«FOR g:gc.graphMopdels.filter[generating]»
			if(currentFile is «g.dartFQN») {
				«FOR a:g.generators.filter[value.length>=3]»
					map['«a.value.get(0)»'] = '«a.value.get(2)»';
				«ENDFOR»
				return map;
			}
		«ENDFOR»
	    }
	    return map;
	  }
	  
	  GraphModel getModelFile() {
	    	if(currentFile!=null && currentFile is GraphModel){
	        return currentFile as GraphModel;
	      }
	      return null;
	  }
	    
	  String getScaleValue() {
	    var m = getModelFile();
	    if(m==null || m.scale == null){
	    	return "100";
	    }
	    return (getModelFile().scale*100).toInt().toString();
	  }
	  
	  bool isTextFile() {
	  	if(currentFile!=null){
	      return currentFile is PyroTextualFile;
	    }
	    return false;
	  }
	  
	  bool isURLFile() {
	  	if(currentFile!=null){
	      return currentFile is PyroURLFile;
	    }
	    return false;
	  }
	  
	  bool isBinaryFile() {
	  	if(currentFile!=null){
	      return currentFile is PyroBinaryFile;
	    }
	    return false;
	  }
	  get url => cached_url;
	
	  String getActiveFile(PyroFile openedFile)
	  {
	    if(currentFile != openedFile)
	    {
	      return "active";
	    }
	    return "";
	  }
	
	  void showFile(PyroFile openedFile, dynamic e) {
	      if (e != null) {
	        e.preventDefault();
	      }
	      if ((currentFile == null || currentFile.id != openedFile?.id) &&
	      currentLocalSettings.openedFiles.contains(openedFile)) {
	      	currentFile = openedFile;
	      	tabChangedSC.add(currentFile);
	      }
	  }
	
	  void removeFileFromActiveList(PyroFile openedFile,dynamic e)
	  {
	  	if(e!=null){
	    	e.preventDefault();
	    }
	    currentLocalSettings.openedFiles.remove(openedFile);
	    if(currentFile?.id == openedFile?.id)
	    {
	    	«FOR g:gc.graphMopdels»
	    		if(openedFile is «g.dartFQN»){
	    			js.context.callMethod('destroy_«g.jsCall»',[]);
	    		}
	    	«ENDFOR»
	     	tabChangedSC.add(null);
	    }
	  }
	  
	  String getURL() => "${BaseService.getUrl()}/pyrofile/read/projectresource/${project.id.toString()}/${project.fullPath(currentFile)}${currentFile.getFullName()}";
	  String getCINCOExportURL() => "${BaseService.getUrl()}/pyrofile/export/${currentFile.extension}/${currentFile.id.toString()}/${currentFile.filename}.${currentFile.extension}";

	
		bool hasIcon(PyroFile openedFile) {
		  	if(openedFile==null){
		      return false;
		    }
			«FOR g:gc.graphMopdels.filter[!iconPath.nullOrEmpty]»
			    if(openedFile is «g.dartFQN»){
			      return true;
			    }
			«ENDFOR»
		    return false;
		}
		  
		String getIcon(PyroFile openedFile) {
		  	 if(openedFile==null){
		  	      return "";
		  	  }
		  	  «FOR g:gc.graphMopdels.filter[!iconPath.nullOrEmpty]»
		  	    if(openedFile is «g.dartFQN»){
		  	      return "«g.iconPath(true)»";
		  	    }
		  	  «ENDFOR»
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
			«FOR g:gc.graphMopdels»
			if(«g.name.lowEscapeDart»CanvasComponent!=null) {
			   	«g.name.lowEscapeDart»CanvasComponent.updateCheckLevel(isError,isWarning,isInfo);
			}
			«ENDFOR»
		}
		
		void _updateGluelines() {
			«FOR g:gc.graphMopdels»
			if(«g.name.lowEscapeDart»CanvasComponent!=null) {
			   	«g.name.lowEscapeDart»CanvasComponent.updateGlueline(isGluelines);
			}
			«ENDFOR»
		}
		
		bool isActiveRouter(String s){
		    if(currentFile!=null && currentFile is GraphModel){
		      return s==(currentFile as GraphModel).router;
		    }
		    return false;
		  }
		
		  bool isActiveConnector(String s){
		    if(currentFile!=null && currentFile is GraphModel){
		      return s==(currentFile as GraphModel).connector;
		    }
		    return false;
		  }
		
		  void changeRouteLayout(String type,dynamic e)
		  {
		    e.preventDefault();
		    if(currentFile is GraphModel) {
		      (currentFile as GraphModel).router = type;
		    }
		    updateRouting();
		  }
		  
		  void executeGraphmodelButton(String key) {
			«FOR g:gc.graphMopdels»
				if(«g.name.lowEscapeDart»CanvasComponent!=null) {
				   	«g.name.lowEscapeDart»CanvasComponent.executeGraphmodelButton(key);
				}
			«ENDFOR»
		  }
		
		  void changeConnectorLayout(String type,dynamic e)
		  {
		    e.preventDefault();
		    if(currentFile is GraphModel) {
		      (currentFile as GraphModel).connector = type;
		    }
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
				«FOR g:gc.graphMopdels.filter[interpreting]»
					if((currentFile as GraphModel).$type() == '«g.typeName»') {
						isInterpreting = true;
					 	HttpRequest.getString("${_graphService.getBaseUrl()}/«g.name.lowEscapeDart»/interpreter/${currentFile.id}/private",withCredentials: true).then((s){
						   _notificationService.displayMessage("«g.name» ${currentFile.filename} interpreter finished successfully!",NotificationType.SUCCESS);
						})
						.catchError((e){
							_notificationService.displayLongMessage("«g.name» ${currentFile.filename} interpreter failed!",NotificationType.DANGER);
						})
						.whenComplete(()=>isInterpreting=false);
					}
				«ENDFOR»
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
				«FOR g:gc.graphMopdels.filter[generating]»
					if((currentFile as GraphModel).$type() == '«g.typeName»') {
						«FOR a:g.generators»
						«{
	  	 	  	 	   	val generatorId = '''«IF a.value.length >= 3»'«a.value.get(0)»'«ELSE»null«ENDIF»'''
	  	 	  	 	   	'''
							if(name == «generatorId») {
								isGenerating = true;
								_graphService.generateGraph(currentFile, name).then((response){
									var s = response.responseText;
									this.project.merge(PyroProject.fromJSOG(cache: new Map(),jsog: jsonDecode(s)));
									_notificationService.displayMessage("«g.name» ${currentFile.filename} generation completed successfully!",NotificationType.SUCCESS);
								})
								.catchError((e){
									_notificationService.displayLongMessage("«g.name» ${currentFile.filename} generation failed!",NotificationType.DANGER);
								})
								.whenComplete(()=>isGenerating=false);
							}
					    '''}»
					    «ENDFOR»
					   	return;
					}
				«ENDFOR»
			 	_notificationService.displayMessage("No generator annotated for current graphmodel",NotificationType.WARNING);
			 } else {
			    _notificationService.displayMessage("No graphmodel present to generate",NotificationType.WARNING);
			 }
		  }
	}
	'''
	
	def hasGenerator() {
		gc.graphMopdels.exists[generating]
	}
	
	def hasInterpreter() {
		gc.graphMopdels.exists[interpreting]
	}
	
	def hasChecks() {
		gc.graphMopdels.exists[it.hasChecks]
	}
	
	def fileNameCanvasComponentTemplate()'''canvas_component.html'''
	
	def contentCanvasComponentTemplate()
	'''
	<div class="d-flex flex-row">
		<div class="w-100">
			<ul class="nav nav-tabs nav-tabs-justified" *ngIf="currentFile!=null">
			    <li
			        *ngFor="let openedFile of currentLocalSettings.openedFiles"
			        role="presentation"
			        [ngClass]="getActiveFile(openedFile)"
			        class="nav-item"
			    >
			        <a class="nav-link" data-toggle="tooltip" data-placement="bottom" title="close tab" style="cursor: pointer;padding-bottom: 5px;padding-top: 5px;" href (click)="showFile(openedFile,$event)"><img *ngIf="hasIcon(openedFile)" [src]="getIcon(openedFile)" style="width: 16px;height: 16px;margin-right: 5px;"> {{ openedFile.getFullName() }} <i class="fas fa-times" (click)="removeFileFromActiveList(openedFile,$event)"></i></a>
			
			    </li>
			</ul>
		</div>
	</div>
	<div *ngIf="currentFile==null" class="h-100 d-flex flex-column">
		<div class="row">
	   		<div class="col-12"><h5 style="text-align:center;margin-top:15px">Create a new Model?</h5></div>
	   		<div class="col-12">
	       		<div class="row row-cols-sm-2 row-cols-md-4">
	       			<div class="col">
						«FOR g:gc.graphMopdels»
							<div class="card pyro-panel" style="background-color: rgb(255 255 255 / 0.13);margin: 15px;">
								<div class="card-body pyro-panel-body">
									<h6>«g.name»</h6>
									<p>Create a new «g.name» file</p>
									<a class="btn btn-primary" href (click)="createNewFile('«g.name.fuEscapeDart»',$event)">Create</a>
								</div>
							</div>
						«ENDFOR»
	       			</div>
	       		</div>
	   		</div>
	   	</div>
	</div>
<div *ngIf="currentFile!=null" class="h-100 d-flex flex-column">

    <div class="card-header d-flex flex-row align-items-center pyro-panel-heading">

		<template [ngIf]="isModelFile()">
			<bs-dropdown class="mr-2">
			    <button type="button" class="btn btn-sm dropdown-toggle" >
			      Export
			    </button>
			    <bs-dropdown-menu>
			      <li><a class="dropdown-item" href="#" (click)="export($event, 'svg')">Export as SVG</a></li>
			      <li><a class="dropdown-item" href="#" (click)="export($event, 'png')">Export as PNG</a></li>
			      <li><a class="dropdown-item" [attr.href]="getCINCOExportURL()" [attr.download]="currentFile.filename+'.'+currentFile.extension">Export for CINCO</a></li>
			    </bs-dropdown-menu>
			  </bs-dropdown>
		 
		 	<bs-dropdown class="mr-2">
			    <button type="button" class="btn btn-sm dropdown-toggle" data-toggle="tooltip" data-placement="bottom" title="Choose from different edge routing algorithms">
			      Routing
			    </button>
			    <bs-dropdown-menu>
	              <h6 class="dropdown-header">Routing modes</h6>
	              <a class="dropdown-item" [class.active]="isActiveRouter(null)" href (click)="changeRouteLayout(null,$event)">Default</a>
	              <a class="dropdown-item" [class.active]="isActiveRouter('orthogonal')" href (click)="changeRouteLayout('orthogonal',$event)">Orthogonal</a>
	              <a class="dropdown-item" [class.active]="isActiveRouter('metro')" href (click)="changeRouteLayout('metro',$event)">Metro</a>
	              <a class="dropdown-item" [class.active]="isActiveRouter('manhattan')" href (click)="changeRouteLayout('manhattan',$event)">Manhatten</a>
	              <h6 class="dropdown-header">Connector layout</h6>
	              <a class="dropdown-item" [class.active]="isActiveConnector('normal')" href (click)="changeConnectorLayout('normal',$event)">Normal</a>
	              <a class="dropdown-item" [class.active]="isActiveConnector('smooth')" href (click)="changeConnectorLayout('smooth',$event)">Smooth</a>
	              <a class="dropdown-item" [class.active]="isActiveConnector('rounded')" href (click)="changeConnectorLayout('rounded',$event)">Rounded</a>
	              <a class="dropdown-item" [class.active]="isActiveConnector('jumpover')" href (click)="changeConnectorLayout('jumpover',$event)">Jumpover</a>
			    </bs-dropdown-menu>
			  </bs-dropdown>
			  
			  <div *ngIf="isModelFile()" class="btn-group btn-group-sm mr-2">
			  	<button class="btn" (click)="undo()">
			  	  <i class="fas fa-undo-alt"></i> Undo
			  	</button>
			  	<button class="btn" (click)="redo()">
			  	  <i class="fas fa-redo-alt"></i> Redo
			  	</button>
			  </div>
			  «IF hasChecks»
			   <div *ngIf="isModelFile()&&hasChecks()" class="btn-group btn-group-sm mr-2">
			   		<button type="button" (click)="toggleIsError()" [class.active]="isError" class="btn btn-sm btn-outline-danger" data-toggle="tooltip" data-placement="bottom" title="Toggle error markers on canvas">E</button>
			   	    <button type="button" (click)="toggleIsWarning()" [class.active]="isWarning" class="btn btn-sm btn-outline-warning" data-toggle="tooltip" data-placement="bottom" title="Toggle warning markers on canvas">W</button>
			   	    <button type="button" (click)="toggleIsInfo()" [class.active]="isInfo" class="btn btn-sm btn-outline-info" data-toggle="tooltip" data-placement="bottom" title="Toggle information markers on canvas">I</button>
			   </div>
			  «ENDIF»
			  «IF hasInterpreter»
			  	<div *ngIf="isModelFile()" class="btn-group btn-group-sm mr-2">
			  		<button class="btn btn-sm" (click)="triggerInterpreter(null,null)" data-toggle="tooltip" data-placement="bottom" title="Trigger the generation process"
			  			*ngIf="«FOR g:gc.graphMopdels.filter[interpreting] SEPARATOR "||"»currentFile.$type()=='«g.typeName»'«ENDFOR»">
			  			<i class="fas fa-fw fa-play"></i> <strong>Run</strong>
			  		</button>
			  	</div>
			  «ENDIF»
			  «IF hasGenerator»
			  	<template [ngIf]="isModelFile()&&hasGenerator()">
			  		<template [ngIf]="getGenerators().length <= 1">
			  			<button class="btn btn-sm" (click)="triggerGenerator(null,null)"	
			  				data-toggle="tooltip" data-placement="bottom" title="Trigger the generation process"
			  				*ngIf="«FOR g:gc.graphMopdels.filter[generating] SEPARATOR "||"»currentFile.$type()=='«g.typeName»'«ENDFOR»">
			  				<i class="fas fa-fw fa-cog"></i> <strong>G</strong>
			  			</button>
			  		</template>
			  		<template [ngIf]="getGenerators().length > 1">
			  			<bs-dropdown class="mr-2">
			  				<button type="button" class="btn btn-sm dropdown-toggle" data-toggle="tooltip" data-placement="bottom" title="Choose from different generators">
			  					<i class="fas fa-fw fa-cog"></i> <strong>G</strong>
			  				</button>
			  				<bs-dropdown-menu>
			  					<a *ngFor="let g of getGenerators().entries" class="dropdown-item" href (click)="triggerGenerator(g.key,$event)">{{g.value}}</a>
			  				</bs-dropdown-menu>
			  			</bs-dropdown>
			  		</template>
			  	</template>
			  «ENDIF»
  	          <div *ngIf="isModelFile()" id="glue-toggle" class="btn-group btn-group-sm mr-2">
  			   		<button type="button" (click)="toggleGluelines()" [class.active]="isGluelines" class="btn btn-sm btn-outline-secondary" data-toggle="tooltip" data-placement="bottom" title="Toggle the glue lines on canvas">
  			   			<i class="fas fa-ruler-combined"></i>
  			   		</button>
  			   </div>
  			  <div *ngIf="getEditorButtons().isNotEmpty" class="btn-group btn-group-sm mr-2">
   			  	<button *ngFor="let b of getEditorButtons().entries" type="button" class="btn" (click)="executeGraphmodelButton(b.value)">
   			  	  {{b.value}}
   			  	</button>
   			  </div>
		  </template>
             	
        <div class="flex-shrink-0 ml-auto btn-group btn-group-sm" role="group" *ngIf="currentFile!=null">
    		<a *ngIf="(isBinaryFile()||isTextFile()||isURLFile())" [attr.href]="getURL()" class="btn btn-link" target="_blank" data-toggle="tooltip" data-placement="bottom" title="Open in a new Tab">
    		     <i class="fas fa-external-link-alt"></i>
            </a>
    		<template [ngIf]="isModelFile()">
    			<input
    			[ngModel]="getScaleValue()"
  				(ngModelChange)="updateScaleFactorStr($event.toString(),false)"
    			type="range" min="25" max="150">
    		</template>
        </div>
        
     </div>

    <div class="card pyro-panel h-100 d-flex flex-column">
        <div class="card-body pyro-panel-body p-0 overflow: hidden">
        	«FOR g:gc.ecores»
        		<template [ngIf]="is«g.name.fuEscapeDart»()&&isOpen()">
        			<p>No Editor available for «g.name» Ecore</p>
        		</template>
        	«ENDFOR»
            <!-- for each graph type -->
			«FOR g:gc.graphMopdels»
				<«g.name.lowEscapeDart»-canvas
				#«g.name.lowEscapeDart»_canvas_component
				*ngIf="is«g.name.fuEscapeDart»()&&isOpen()"
				    [user]="user"
				    [isError]="isError"
				    [isWarning]="isWarning"
				    [isInfo]="isInfo"
				    [isFullScreen]="isFullScreen"
				    [currentGraphModel]="currentFile"
				    [currentLocalSettings]="currentLocalSettings"
				    [canEdit]="canEdit"
				    [project]="project"
				    (hasChanged)="hasChangedSC.add($event)"
				    (selectionChanged)="selectionChangedSC.add($event)"
				    (selectionChangedModal)="selectionChangedModalSC.add($event)"
				    (close)="tabChangedSC.add($event)"
				    (jumpTo)="jumpToSC.add($event)"
				></«g.name.lowEscapeDart»-canvas>
			«ENDFOR»
           <template [ngIf]="(isBinaryFile()||isTextFile())&&isOpen()">
          	  <iframe width="100%" height="500px" style="background: #fff;" [src]="url" frameborder="0" allowfullscreen></iframe>
           </template>
           <template [ngIf]="isURLFile()&&isOpen()">
          	  <iframe width="100%" height="500px" style="background: #fff;" [src]="url" frameborder="0" allowfullscreen sandbox="allow-forms allow-scripts"></iframe>
           </template>
	        </div>
	    </div>
	</div>
	'''
}
