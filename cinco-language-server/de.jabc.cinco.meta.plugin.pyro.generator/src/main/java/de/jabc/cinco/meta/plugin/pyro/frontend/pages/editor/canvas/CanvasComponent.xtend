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
	import 'dart:js' as js;
	
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
	import 'package:«gc.projectName.escapeDart»/src/model/message.dart';
	import 'package:«gc.projectName.escapeDart»/src/utils/redirect_stack.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/notification_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/utils/graph_model_permission_utils.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/graph_service.dart';
	
	«FOR g:gc.mglModels»
		import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
	«ENDFOR»
	«FOR g:gc.concreteGraphModels»
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
			coreDirectives,
			formDirectives
			«FOR g:gc.concreteGraphModels BEFORE "," SEPARATOR ","»
				«g.modelPackage.name.lowEscapeDart».«g.name.fuEscapeDart»CanvasComponent
			«ENDFOR»
		]
	)
	class CanvasComponent implements OnInit, OnChanges {
	
		final DomSanitizationService _domSanitizationService;
		final NotificationService _notificationService;
		final GraphService _graphService;
	
		«FOR g:gc.concreteGraphModels»
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
		
		final changeLayoutSC = new StreamController();
		@Output() Stream get changeLayout => changeLayoutSC.stream;
		
		@Input()
		PyroUser user;
		@Input()
		GraphModel currentFile;
		@Input()
		RedirectionStack redirectionStack;
		@Input()
		List<PyroGraphModelPermissionVector> permissionVectors = new List();
		@Input()
		LocalGraphModelSettings currentLocalSettings;
		@Input()
		String layoutType = null;
		
		bool isFullScreen = false;
		bool canEdit = false;
		bool isGenerating = false;
		bool isInterpreting = false;
		
		/// validation level
		bool isError = true;
		bool isWarning = true;
		bool isInfo = true;
		
		bool isGluelines = false;
		
		«IF !gc.projectServices.empty»
			static Map<dynamic, dynamic> SERVICE_MAP = new Map<dynamic, dynamic>();
		«ENDIF»
		static List<String> RUNNING_ACTIONS = new List<String>();
		
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
			«FOR g:gc.concreteGraphModels SEPARATOR " else "
			»if(is«g.name.fuEscapeDart»()){
				canEdit = GraphModelPermissionUtils.canUpdate("«g.name.toUnderScoreCase»", permissionVectors);
			}«
			ENDFOR»
			«IF !gc.projectServices.empty»
				this.retreiveServices(currentFile);
			«ENDIF»
		}
		
		dynamic getCanvasComponent() {
			«FOR g:gc.concreteGraphModels SEPARATOR " else "
			»if(is«g.name.fuEscapeDart»()){
				return this.«g.name.lowEscapeDart»CanvasComponent;
			}«ENDFOR»
			return null;
		}
		
		void executeCommands(CompoundCommandMessage m,bool forceExecute) {
			«FOR g:gc.concreteGraphModels»
				if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.executeCommands(m,forceExecute);
				}
		 	«ENDFOR»
		}
		
		void updateProperties(IdentifiableElement element) {
			«FOR g:gc.concreteGraphModels»
				if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.updateProperties(element);
				}
			«ENDFOR»
		}
		
		void export(dynamic e, String type){
			e.preventDefault();
			«FOR g:gc.concreteGraphModels»
				if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.export(type);
				}
			«ENDFOR»
		}
		
		Map<String,String> getEditorButtons(){
			«{
				val editorButtonModels = gc.concreteGraphModels.filter[!editorButtons.empty]
				
				'''
					«IF !editorButtonModels.empty»
						if(currentFile != null) {
							«FOR g:editorButtonModels SEPARATOR " else "
							»if(currentFile.$type() == '«g.typeName»'){
								Map<String,String> m = new Map<String,String>();
								«FOR a:g.editorButtons»
									m['«a.value.get(1).escapeJavaDart»'] = '«a.value.get(1)»';
								«ENDFOR»
								return m;
							}«ENDFOR»
						}
					«ENDIF»
				'''
			}»
			return new Map<String,String>();
		}
		
		void updateScaleFactorStr(String s,bool persist) {
			updateScaleFactor(int.parse(s)*0.01,persist);
		}
		
		void updateScale({double factor:0.0,bool persist:true}) {
			«FOR g:gc.concreteGraphModels»
				if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.updateScale(factor,persist:persist);
				}
			«ENDFOR»
		}
		
		void updateScaleFactor(double factor,[bool persist=true]) => updateScale(factor:factor,persist:persist);
		
		void updateRouting() {
			«FOR g:gc.concreteGraphModels»
				if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.updateRouting();
				}
			«ENDFOR»
		}
		
		void undo() {
			«FOR g:gc.concreteGraphModels»
				if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.undo();
				}
			«ENDFOR»
		}
		
		void redo() {
			«FOR g:gc.concreteGraphModels»
				if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.redo();
				}
			«ENDFOR»
		}
		
		// for each graph model
		«FOR g:gc.concreteGraphModels»
		
			bool is«g.name.fuEscapeDart»(){
				if(currentFile!=null){
					return currentFile.$type() == '«g.typeName»';
				}
				return false;
			}
		«ENDFOR»
	
		// for each ecore model
		«FOR g:gc.ecores»
			
			bool is«g.name.fuEscapeDart»(){
				return currentFile!=null && currentFile.$type() == '«g.typeName»';
			}
		«ENDFOR»
		
		bool isModelFile() {
			if(currentFile!=null){
				return currentFile is GraphModel;
			}
			return false;
		}
		
		bool hasChecks() {
			return currentFile!=null«
				IF !gc.concreteGraphModels.filter[it.hasChecks].empty
				» && (
					«FOR g:gc.concreteGraphModels.filter[it.hasChecks] SEPARATOR " ||"»
						currentFile.$type() == '«g.typeName»'
					«ENDFOR»
				)«ENDIF»;
		}
		
		bool hasGenerator() {
			return currentFile!=null && (
				«FOR g:gc.concreteGraphModels.filter[generating] SEPARATOR " ||"»
					currentFile.$type() == '«g.typeName»'
				«ENDFOR»
			);
		}
		
		Map<String,String> getGenerators() {
			Map<String,String> map = new Map<String,String>();
			if(currentFile!=null){
				«FOR g:gc.concreteGraphModels.filter[generating] SEPARATOR " else "
				»if(currentFile.$type() == '«g.typeName»') {
					«FOR a:g.generators.filter[value.length>=3]»
						map['«a.value.get(0)»'] = '«a.value.get(2)»';
					«ENDFOR»
					return map;
				}«
				ENDFOR»
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
			return currentFile!=null && (
				«FOR g:gc.concreteGraphModels.filter[!iconPath.nullOrEmpty] SEPARATOR " ||"»
					currentFile.$type() == '«g.typeName»'
				«ENDFOR»
			);
		}
		
		String getIcon() {
			if(currentFile!=null){
				«FOR g:gc.concreteGraphModels.filter[!iconPath.nullOrEmpty] SEPARATOR " else "
				»if(currentFile.$type() == '«g.typeName»'){
					return "«g.iconPath(true)»";
				}«
				ENDFOR»
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
			«FOR g:gc.concreteGraphModels»
				if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.updateCheckLevel(isError,isWarning,isInfo);
				}
			«ENDFOR»
		}
		
		void _updateGluelines() {
			«FOR g:gc.concreteGraphModels»
				if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
					«g.name.lowEscapeDart»CanvasComponent.updateGlueline(isGluelines);
				}
			«ENDFOR»
		}
		
		bool isActiveRouter(String s) {
			return s == currentFile.router;
		}
		
		bool isActiveConnector(String s) {
			return s == currentFile.connector;
		}
		
		void changeRouteLayout(String type,dynamic e) {
			e.preventDefault();
			currentFile.router = type;
			updateRouting();
		}
		
		bool isRunning(String key) {
			return RUNNING_ACTIONS.contains(key);
		}
		
		void executeGraphmodelButton(String key, String value) {
			«{
				val editorButtonModels = gc.concreteGraphModels.filter[!editorButtons.empty]
				'''
					«FOR g:editorButtonModels SEPARATOR " else "
					»if(currentFile.$type() == "«g.typeName»" && «g.name.lowEscapeDart»CanvasComponent!=null) {
						RUNNING_ACTIONS.add(key);
						«g.name.lowEscapeDart»CanvasComponent.executeGraphmodelButton(key).then((onValue) {
							if(RUNNING_ACTIONS.contains(key)) {
								RUNNING_ACTIONS.remove(key);
							}
							if(onValue == null) {
								_notificationService.displayMessage("An error occured on executing '" + value + "'", NotificationType.DANGER);
							} else {
								_notificationService.displayMessage("'" + value + "' succeded!", NotificationType.SUCCESS);
							}
						}).catchError((e) {
							if(RUNNING_ACTIONS.contains(key)) {
								RUNNING_ACTIONS.remove(key);
							}
						});
					}«
					ENDFOR»
				'''
			}»
		}
		
		void changeConnectorLayout(String type,dynamic e) {
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
			if(isModelFile() && currentFile != null) {
				«FOR g:gc.concreteGraphModels.filter[interpreting] SEPARATOR " else "
				»if(currentFile.$type() == '«g.typeName»') {
					isInterpreting = true;
				 	HttpRequest.getString("${_graphService.getBaseUrl()}/«g.name.lowEscapeDart»/interpreter/${currentFile.id}/private",withCredentials: true).then((s){
						_notificationService.displayMessage("«g.name» ${currentFile.filename} interpreter finished successfully!",NotificationType.SUCCESS);
					})
					.catchError((e){
						_notificationService.displayLongMessage("«g.name» ${currentFile.filename} interpreter failed!",NotificationType.DANGER);
					})
					.whenComplete(()=>isInterpreting=false);
				}«
				ENDFOR»
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
			if(isModelFile() && currentFile != null) {
				«FOR g:gc.concreteGraphModels.filter[generating] SEPARATOR " else "
				»if(currentFile.$type() == '«g.typeName»') {
					«FOR a:g.generators»	
						«{
							val generatorId = '''«IF a.value.length >= 3»'«a.value.get(0)»'«ELSE»null«ENDIF»'''
							'''
								if(name == «generatorId») {
									isGenerating = true;
									_graphService.generateGraph(currentFile, name).then((response){
										var s = response.responseText;
										_notificationService.displayMessage("«g.name» ${currentFile.filename} generation completed successfully!", NotificationType.SUCCESS);
									})
									.catchError((e){
										_notificationService.displayLongMessage("«g.name» ${currentFile.filename} generation failed!", NotificationType.DANGER);
									})
									.whenComplete(()=>isGenerating=false);
								}
							'''
						}»
					«ENDFOR»
					return;
				}«
				ENDFOR»
				_notificationService.displayMessage("No generator annotated for current graphmodel",NotificationType.WARNING);
			} else {
				_notificationService.displayMessage("No graphmodel present to generate",NotificationType.WARNING);
			}
		}

		bool canJumpForward() {
			return redirectionStack.canForwardOnRedirectStack() != null;
		}
		
		bool canJumpBackwards() {
			return redirectionStack.canBackwardsOnRedirectStack() != null;
		}
		
		void jumpBackwards() {
			if(!canJumpBackwards()) {
				return;
			}
			var location = redirectionStack.backwardsOnRedirectStack();
			navigateOnStack(location);
		}
		
		void jumpForward() {
			if(!canJumpForward()) {
				return;
			}
			var location = redirectionStack.forwardOnRedirectStack();
			navigateOnStack(location);
		}
		
		dynamic navigateOnStack(dynamic location) {
			jumpToSC.add({
				"type": "navigation",
				"location": location,
			});
		}
		«IF !gc.projectServices.empty»
			
			/**
			 * SERVICE-HANDLING
			 */
			
			var showServiceConfigModal = false;
			var serviceConfigModalStatus = 0;
			var currentService = null;
			var serviceMap = new Map<String, Map<String, String>>();
			var serviceAttributeList = new List<ServiceAttribute>();
			var ppp = false;
			
			void retreiveServices(GraphModel g)   {
				if(g == null) {
					return;
				}
				_graphService.listServices(g).then((s) {
					SERVICE_MAP = s;
				});
			}
			
			Map<dynamic, dynamic> getServices()   {
				return SERVICE_MAP;
			}
			
			void showServiceConfig(String serviceName) {
			    fetchServiceValues();
			    getServiceValues(serviceName);
			    checkServiceValues(currentService);
			    showServiceConfigModal = true;
			}
			
			void closeServiceConfig() {
			    currentService = null;
			    showServiceConfigModal = false;
			}
			
			dynamic getServiceValues(String serviceName) {
			    if(currentService != serviceName) {
			        serviceAttributeList.clear();
			        serviceMap[serviceName].forEach((k, v) {
			            serviceAttributeList.add(new ServiceAttribute(k, v));
			        });
			        currentService = serviceName;
			    }
			    return serviceAttributeList;
			}
			
			void setServiceValueChanged(String serviceName, dynamic serviceAttribute, dynamic e) {
			    serviceAttribute.value = e;
			    serviceMap[serviceName][serviceAttribute.key] = e;
			}
			
			String getServiceValueChanged(String serviceName, String key) {
			    return serviceMap[serviceName][key];
			}
			
			void blurServiceValueChanged(String serviceName) async {
			    if(serviceName == null) {
			        return;
			    }
			    var data = new Map<String, String>();
			    data.addAll(serviceMap[serviceName]);
			    serviceConfigModalStatus = 0;
			    _graphService.updateServiceValues(serviceName, data);
			    checkServiceValues(serviceName);
			    saveServiceValues(serviceName);
			}
			
			void checkServiceValues(String serviceName) {
			    var data = new Map<String, String>();
			    data.addAll(serviceMap[serviceName]);
			    serviceConfigModalStatus = 0;
			    _graphService.checkServiceValues(serviceName, data).then((status) {
			        if(status) {
			            serviceConfigModalStatus = 1;
			        } else {
			            serviceConfigModalStatus = -1;
			        }
			    });
			}
			
			void triggerService(String serviceName) {
				var data = this.serviceMap[serviceName];
				if(data == null) {
					_notificationService.displayMessage("No values were set! Please ensure the values are correct under 'Edit'!", NotificationType.WARNING);
					return;
				} else {
					_notificationService.displayMessage("Connecting to service '"+serviceName+"'!", NotificationType.INFO);
				}
				_graphService.triggerService(serviceName, data).then((onValue) {
					if(onValue) {
						_notificationService.displayMessage("Connected to service '"+serviceName+"'!", NotificationType.SUCCESS);
					} else {
						_notificationService.displayMessage("Could not trigger service! Please ensure the values are correct!", NotificationType.DANGER);
					}
					retreiveServices(this.currentFile);
				});
			}
			
			void fetchServiceValues() {
			    resetServiceValues();
			    serviceConfigModalStatus = 0;
			}
			  
			bool connectedToTheia() {
					return js.context.callMethod('connectedToTheia');
			}

			void resetServiceValues() {
			    var localStorage = window.localStorage;
			    «FOR s : gc.projectServices»
			    «{
			    	val serviceName = s.projectServiceName;
			    	'''
						{
						    if(!localStorage.containsKey("«serviceName»")) {
						        serviceMap["«serviceName»"] = new Map<String, String>();
						        «FOR attr : s.projectServiceAttributes»
						        	serviceMap["«serviceName»"]["«attr.escapeJava»"] = "";
						        «ENDFOR»
						        localStorage["«serviceName»"] = json.encode(serviceMap["«serviceName»"]);
						    } else {
						        var jsonMap = json.decode(localStorage["«serviceName»"]);
						        var map = new Map<String, String>();
						        for(var e in jsonMap.entries) {
						            map[e.key] = e.value;
						        }
						        serviceMap["«serviceName»"] = map;
						    }
						}
			    	'''
			    }»
			    
			    «ENDFOR»
			}
			
			void saveServiceValues(String serviceName) {
			    var localStorage = window.localStorage;
			    localStorage[serviceName] = json.encode(serviceMap[serviceName]);
			}
		«ENDIF»
	}
	«IF !gc.projectServices.empty»
		
		class ServiceAttribute {
			String key;
		    String value;
		
		    ServiceAttribute(String key, String value) {
		        this.key = key;
		        this.value = value;
		  	}
		}
	«ENDIF»
	'''
	
	def hasGenerator() {
		gc.concreteGraphModels.exists[generating]
	}
	
	def hasInterpreter() {
		gc.concreteGraphModels.exists[interpreting]
	}
	
	def hasChecks() {
		gc.concreteGraphModels.exists[it.hasChecks]
	}
	
	def fileNameCanvasComponentTemplate()'''canvas_component.html'''
	
	def contentCanvasComponentTemplate()
	'''
		<div *ngIf="currentFile!=null" class="h-100 d-flex flex-column">
			«IF !gc.projectServices.empty»
				<div *ngIf="showServiceConfigModal" class="modal d-block show fade in" tabindex="-1" role="dialog" aria-labelledby="createEntryLabel">
				    <div class="modal-dialog" role="document">
				        <div class="modal-content">
				            <div class="modal-header">
				            	<h4 class="modal-title">{{currentService}}</h4>
				                <button type="button" class="close" (click)="closeServiceConfig()" aria-label="Close">
				                	<span aria-hidden="true">&times;</span>
				            	</button>
				            </div>
				            <div class="modal-body">
				                <form class="form-horizontal" style="padding-right: 5px;" (ngSubmit)="blurServiceValueChanged(null)">
									<div *ngFor="let serviceValue of getServiceValues(currentService)">
										<div class="form-group col-12">
											<label>{{serviceValue.key}}</label>
											<input 
												[ngModel]="serviceValue.value"
												(ngModelChange)="setServiceValueChanged(currentService, serviceValue, $event)"
												(blur)="blurServiceValueChanged(currentService)"
												type="text"
												class="form-control"
											>
										</div>
									</div>
								</form>
				            </div>
				            <div class="modal-footer">
								<div [ngSwitch]="serviceConfigModalStatus">
									<i *ngSwitchCase="1" class="fa fa-check-circle" aria-hidden="true"> valid</i>
									<i *ngSwitchCase="-1" class="fa fa-times-circle" aria-hidden="true"> not valid</i>
									<i *ngSwitchDefault class="fa fa-spinner" aria-hidden="true"> checking...</i>
								</div>
				                <button type="button" class="btn" (click)="closeServiceConfig()">Close</button>
				            </div>
				        </div>
				    </div>
				</div>
			«ENDIF»
			<div class="card-header d-flex flex-row align-items-center pyro-panel-heading">
				<template [ngIf]="isModelFile()">
					<bs-dropdown class="mr-2 dropdown">
						<button type="button" class="btn btn-sm dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" data-placement="bottom" title="Choose to change view mode">View</button>
						<bs-dropdown-menu class="dropdown-menu">
							<h6 class="dropdown-header">Switch layout to...</h6>
							<li>
								<div *ngIf="layoutType != 'micro'">
									<a class="dropdown-item"  (click)="changeLayoutSC.add('micro')">micro</a>
								</div>
								<div *ngIf="layoutType != 'classic'">
									<a class="dropdown-item"  (click)="changeLayoutSC.add('classic')">classic</a>
								</div>
							</li>
							<h6 class="dropdown-header">Layout</h6>
							<li><a class="dropdown-item"  (click)="changeLayoutSC.add('DEFAULT')">Default</a></li>
							<li><a class="dropdown-item"  (click)="changeLayoutSC.add('MINIMAL')">Minimal</a></li>
							<li><a class="dropdown-item"  (click)="changeLayoutSC.add('MAXIMUM_CANVAS')">Maximum Canvas</a></li>
							<li><a class="dropdown-item"  (click)="changeLayoutSC.add('COMPLETE')">Complete</a></li>
						</bs-dropdown-menu>
					</bs-dropdown>
					<bs-dropdown class="mr-2 dropdown">
						<button type="button" class="btn btn-sm dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						  Export
						</button>
						<bs-dropdown-menu class="dropdown-menu">
						  <li><a class="dropdown-item" href="#" (click)="export($event, 'svg')">Export as SVG</a></li>
						  <li><a class="dropdown-item" href="#" (click)="export($event, 'png')">Export as PNG</a></li>
						  <!--<li><a class="dropdown-item" href="#" [attr.download]="currentFile.filename+'.'+currentFile.extension">Export for CINCO</a></li>-->
						</bs-dropdown-menu>
					</bs-dropdown>
					<bs-dropdown class="mr-2 dropdown">
						<button type="button" class="btn btn-sm dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" data-placement="bottom" title="Choose from different edge routing algorithms">
						  Routing
						</button>
						<bs-dropdown-menu class="dropdown-menu">
						  <h6 class="dropdown-header">Routing modes</h6>
						  <a class="dropdown-item" [class.active]="isActiveRouter('normal')" href (click)="changeRouteLayout('normal',$event)">Normal</a>
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
						<div *ngIf="isModelFile() && hasChecks()" class="btn-group btn-group-sm mr-2">
							<button type="button" (click)="toggleIsError()" [class.active]="isError" class="btn btn-sm btn-outline-danger" data-toggle="tooltip" data-placement="bottom" title="Toggle error markers on canvas">E</button>
							<button type="button" (click)="toggleIsWarning()" [class.active]="isWarning" class="btn btn-sm btn-outline-warning" data-toggle="tooltip" data-placement="bottom" title="Toggle warning markers on canvas">W</button>
							<button type="button" (click)="toggleIsInfo()" [class.active]="isInfo" class="btn btn-sm btn-outline-info" data-toggle="tooltip" data-placement="bottom" title="Toggle information markers on canvas">I</button>
						</div>
					«ENDIF»
				    «IF hasInterpreter»
				    	<div *ngIf="isModelFile()" class="btn-group btn-group-sm mr-2">
				    		<button class="btn btn-sm" (click)="triggerInterpreter(null,null)" data-toggle="tooltip" data-placement="bottom" title="Trigger the generation process"
				    			*ngIf="«FOR g:gc.concreteGraphModels.filter[interpreting] SEPARATOR "||"»currentFile.$type()=='«g.typeName»'«ENDFOR»">
				    			<i class="fas fa-fw fa-play"></i> <strong>Run</strong>
				    		</button>
				    	</div>
				    «ENDIF»
					«IF hasGenerator»
						<template [ngIf]="isModelFile()&&hasGenerator()">
							<template [ngIf]="getGenerators().length <= 1">
								<button class="btn btn-sm" (click)="triggerGenerator(null,null)"	
									data-toggle="tooltip" data-placement="bottom" title="Trigger the generation process"
									*ngIf="«FOR g:gc.concreteGraphModels.filter[generating] SEPARATOR "||"»currentFile.$type()=='«g.typeName»'«ENDFOR»">
									<i class="fas fa-fw fa-cog"></i> <strong>G</strong>
								</button>
							</template>
							<template [ngIf]="getGenerators().length > 1">
								<bs-dropdown class="mr-2 dropdown">
									<button type="button" class="btn btn-sm dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" data-placement="bottom" title="Choose from different generators">
										<i class="fas fa-fw fa-cog"></i> <strong>G</strong>
									</button>
									<bs-dropdown-menu class="dropdown-menu">
										<a *ngFor="let g of getGenerators().entries" class="dropdown-item" href (click)="triggerGenerator(g.key,$event)">{{g.value}}</a>
									</bs-dropdown-menu>
								</bs-dropdown>
							</template>
						</template>
					«ENDIF»
					<div *ngIf="isModelFile() && !connectedToTheia()" id="glue-toggle" class="btn-group btn-group-sm mr-2">
			    		<button type="button" (click)="toggleGluelines()" [class.active]="isGluelines" class="btn btn-sm btn-outline-secondary" data-toggle="tooltip" data-placement="bottom" title="Toggle the glue lines on canvas">
			    			<i class="fas fa-ruler-combined"></i>
			    		</button>
			    		<button type="button" (click)="jumpBackwards()" [class.disabled]="!canJumpBackwards()" [class.active]="!canJumpBackwards()" class="btn btn-sm btn-outline-primary" data-toggle="tooltip" data-placement="bottom" title="jump to previous model">
							<i class="fas fa-angle-left"></i>
						</button>
						<button type="button" (click)="jumpForward()" [class.disabled]="!canJumpForward()" [class.active]="!canJumpForward()" class="btn btn-sm btn-outline-primary" data-toggle="tooltip" data-placement="bottom" title="jump to next model">
							<i class="fas fa-angle-right"></i>
						</button>
					</div>
					«IF !gc.projectServices.empty»
						<bs-dropdown class="mr-2 ml-2 dropdown">
							<button type="button" class="btn btn-sm dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" data-placement="bottom" title="Choose service to start">
								Services
							</button>
							<bs-dropdown-menu class="dropdown-menu" *ngIf="getServices().isNotEmpty">
								<h6 class="dropdown-header">Active</h6>
								<a *ngFor="let service of getServices()['active']" class="dropdown-item">
									<button type="button" class="btn" (click)="triggerService(service)">
										{{service}}
									</button>
									<button type="button" class="btn" (click)="showServiceConfig(service)">
										Edit
									</button>
								</a>
								<h6 class="dropdown-header">Disabled</h6>
								<a *ngFor="let service of getServices()['disabled']" class="dropdown-item">
									<button type="button" class="btn">
										{{service}}
									</button>
								</a>
								<h6 class="dropdown-header">Running</h6>
								<a *ngFor="let service of getServices()['other']" class="dropdown-item">
									<button type="button" class="btn" [class.disabled]="true">
										{{service}}
									</button>
									<button type="button" class="btn" (click)="showServiceConfig(service)">
										Edit
									</button>
								</a>
							</bs-dropdown-menu>
						</bs-dropdown>
					«ENDIF»
					<div *ngIf="getEditorButtons().isNotEmpty" class="btn-group btn-group-sm mr-2">
						<button *ngFor="let b of getEditorButtons().entries" type="button" class="btn" [class.disabled]="isRunning(b.key)" (click)="executeGraphmodelButton(b.key, b.value)">
						    {{b.value}}
						</button>
					</div>
			    </template>
				<div class="flex-shrink-0 ml-auto btn-group btn-group-sm" role="group" *ngIf="currentFile!=null">
					<template [ngIf]="isModelFile()">
						<input
						[ngModel]="getScaleValue()"
						(ngModelChange)="updateScaleFactorStr($event.toString(),true)"
						type="range" min="25" max="150">
					</template>
				</div>
			</div>
		
			<div class="card pyro-panel h-100 d-flex flex-column">
				<div class="card-body pyro-panel-body p-0 overflow-hidden d-flex flex-column canvas-wrapper">
					«FOR g:gc.ecores»
						<template [ngIf]="is«g.name.fuEscapeDart»()">
							<p>No Editor available for «g.name» Ecore</p>
						</template>
					«ENDFOR»
					<!-- for each graph type -->
					«FOR g:gc.concreteGraphModels»
						<«g.name.lowEscapeDart»-canvas
						#«g.name.lowEscapeDart»_canvas_component
						class="d-flex flex-column h-100"
						*ngIf="is«g.name.fuEscapeDart»()"
							[user]="user"
							[isError]="isError"
							[isWarning]="isWarning"
							[isInfo]="isInfo"
							[isFullScreen]="isFullScreen"
							[currentGraphModel]="currentFile"
							[currentLocalSettings]="currentLocalSettings"
							(hasChanged)="hasChangedSC.add($event)"
							(selectionChanged)="selectionChangedSC.add($event)"
							(selectionChangedModal)="selectionChangedModalSC.add($event)"
							(jumpTo)="jumpToSC.add($event)"
						></«g.name.lowEscapeDart»-canvas>
					«ENDFOR»
				</div>
			</div>
		</div>
	'''
}
