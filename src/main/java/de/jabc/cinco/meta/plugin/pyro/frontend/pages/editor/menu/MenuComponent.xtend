package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.menu

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class MenuComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameMenuComponent()'''menu_component.dart'''
	
	def contentMenuComponent()
	'''
	import 'package:angular/angular.dart';
	import 'package:angular_router/angular_router.dart';
	import 'dart:async';
	import 'dart:html';
	import 'package:dnd/dnd.dart';
	
	import '../../../model/core.dart';
	import '../explorer/graph_entry/create/create_file_component.dart';
	import '../active_users/active_users_component.dart';
	
	import '../../../service/project_service.dart';
	import '../../../service/notification_service.dart';
	import '../../../service/editor_data_service.dart';
	import '../../../service/editor_grid_service.dart';
	import '../../../service/graph_model_permission_vector_service.dart';
	import '../../../pages/shared/navigation/navigation_component.dart';
	
	import '../../../routes.dart' as top_routes;
	
	@Component(
	    selector: 'pyro-menu',
	    templateUrl: 'menu_component.html',
	    directives: const [coreDirectives, routerDirectives, NavigationComponent, ActiveUsersComponent, WidgetListItemComponent],
	    styleUrls: const ['menu_component.css'],
	    exports: const [top_routes.RoutePaths]
	)
	class MenuComponent implements OnInit {
	
	  final clickWidgetSC = new StreamController();
	  @Output() Stream get clickWidget => clickWidgetSC.stream;
	  
	  final changeLayoutSC = new StreamController();
	  @Output() Stream get changeLayout => changeLayoutSC.stream;
	  
	  final changeMainLayoutSC = new StreamController();
	  @Output() Stream get changedMainLayout => changeMainLayoutSC.stream;
	 
	  @Input()
	  PyroUser user;
	  @Input()
	  List<PyroUser> activeUsers = new List();
	  @Input()
	  PyroOrganization organization;
	  @Input()
	  PyroProject project;
	  @Input()
	  PyroFile currentFile;
	    
	  List<PyroGraphModelPermissionVector> permissionVectors;
	
	  final ProjectService projectService;
	  final NotificationService notificationService;
	  final GraphModelPermissionVectorService _permissionService;
	  final EditorDataService _editorDataService;
	  final Router _router;
	  final ElementRef _el;
	  
	  List<String> disabledProjectServices = new List();
	  List<String> activeProjectServices = new List();
	  @Input()
	  String currentLayout;
	
	  MenuComponent(this.projectService, 
	  				this._router, 
					this._permissionService, 
					this._editorDataService, 
					this._el, 
					this.notificationService) {
	    permissionVectors = new List();
	  }
	
	  @override
	  void ngOnInit() {
	  	«IF !gc.projectServices.empty || !gc.projectActions.empty»
	  		refreshServices();
	  	«ENDIF»
	    _permissionService.getMy("${project.id}").then((pvs) {
	      permissionVectors = pvs;
	    });
	  }
	  
	  void changeMainLayout(String layout) {
	  	currentLayout = layout;
	  	changeMainLayoutSC.add(currentLayout);
	  	
	  }
	  
	  void refreshServices() {
	  	projectService.checkService(project).then((map) {
	    	disabledProjectServices.clear();
	    	disabledProjectServices.addAll(map['disabled'].toList().cast<String>());
	    	activeProjectServices.clear();
	    	activeProjectServices.addAll(map['active'].toList().cast<String>());
	    });
	  }
	      
	  String getProjectsUrl() {
	  	return organization == null ? "" : top_routes.RoutePaths.organization.toUrl(parameters: {"orgId": organization.id.toString()});
	  }
		«IF !gc.projectServices.empty»
			
			String serviceName = "null";
			
			void closeServiceModal() {
				«FOR p:gc.projectServices»
					show«p.value.get(1).escapeDart» = false;
				«ENDFOR»
			}
		«ENDIF»
		«FOR p:gc.projectServices»
		
			bool show«p.value.get(1).escapeDart» = false;
			
			void trigger«p.value.get(1).escapeDart»() {
				if(disabledProjectServices.contains('«p.value.get(1).escapeJava»')) {
					return;
				}
				serviceName = "«p.value.get(1)»";
				show«p.value.get(1).escapeDart» = true;
			}
			
			void sendTrigger«p.value.get(1).escapeDart»(«FOR attr:p.value.subList(2,p.value.size) SEPARATOR ","»String «attr.escapeDart»«ENDFOR») {
				show«p.value.get(1).escapeDart» = false;
				Map<String,String> map = new Map();
				map["__type"] = "info.scce.pyro.service.rest.«p.value.get(1).fuEscapeJava»";
				map["runtimeType"] = "info.scce.pyro.service.rest.«p.value.get(1).fuEscapeJava»";
				«FOR attr:p.value.subList(2,p.value.size)»
					map["«attr.escapeJava»"] = «attr.escapeDart»;
				«ENDFOR»
				projectService.triggerService("«p.value.get(1).escapeJava»",project,map)
					.then((n){
						notificationService.displayMessage("«p.value.get(1)» Executed", NotificationType.SUCCESS);
						refreshServices();
					})
					.catchError((e){
						notificationService.displayMessage("«p.value.get(1)» could not be executed.", NotificationType.WARNING);
					  	refreshServices();
					});
			}
		«ENDFOR»
		
		«FOR p:gc.projectActions»
			void triggerAction«p.value.get(1).escapeDart»() {
				projectService.triggerAction("«p.value.get(1).escapeJava»",project)
				  	.then((n){
				  		notificationService.displayMessage("«p.value.get(1)» Executed", NotificationType.SUCCESS);
				  		refreshServices();
				  	})
				  	.catchError((e){
				  		notificationService.displayMessage("«p.value.get(1)» could not be executed", NotificationType.WARNING);
				  	  		refreshServices();
				  	  	});
			}
  	  	«ENDFOR»
	  
	  	PyroEditorGrid get grid => _editorDataService.grid;
	}
	
	@Component(
	  selector: 'widget-item',
	  template: """
	  	<div class="dropdown-item" #draggable draggable="true" (click)="clickWidgetSC.add(widget)" *ngIf="widget != null">
	  		<i class="fas fa-fw fa-grip-vertical"></i> {{widget.tab}}
		</div>
	  """,
	  directives: const [coreDirectives],
	)
	class WidgetListItemComponent implements AfterViewInit, OnDestroy {
	
	  final clickWidgetSC = new StreamController();
	  @Output() Stream get clickWidget => clickWidgetSC.stream;
	
	  @Input()
	  PyroEditorWidget widget;
	  
	  @ViewChild('draggable')
	  ElementRef draggableEl;
	  
	  final EditorGridService _editorGridService;
	  final EditorDataService _editorDataService;
	  
	  WidgetListItemComponent(this._editorGridService, this._editorDataService) {}
	  
	  void handleDragStart(e) {
	    e.dataTransfer.setData("widgetId", "${widget.id}");
	  }
	  
	  
	  
	  @override
	  void ngAfterViewInit() {
	    draggableEl.nativeElement.addEventListener('dragstart', handleDragStart);
	  }
	  
	  @override
	  void ngOnDestroy() {
	    draggableEl.nativeElement.removeEventListener('dragstart', handleDragStart);
	  }
	}
	
	'''
	
	def fileNameMenuTemplate()'''menu_component.html'''
	
	def contentMenuTemplate()
	'''
	<navigation [user]="user">
	  <div class="w-100 d-flex flex-row">
		  <ul class="navbar-nav mr-auto">
		  <li class="nav-item">
		    <a class="nav-link org-nav-text-color" [routerLink]="getProjectsUrl()">Projects</a>
		  </li> 
		  
		  <li class="widget-list nav-item dropdown" *ngIf="grid != null">
		      <a href="#" class="nav-link org-nav-text-color dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
		      	View <span class="caret"></span>
		      </a>
		      <div class="dropdown-menu">              	  
		      	  <h6 class="dropdown-header">Layout</h6>
		      	  <div class="dropdown-item" (click)="changeLayoutSC.add('DEFAULT')">Default</div>
		      	  <div class="dropdown-item" (click)="changeLayoutSC.add('MINIMAL')">Minimal</div>
		      	  <div class="dropdown-item" (click)="changeLayoutSC.add('MAXIMUM_CANVAS')">Maximum Canvas</div>
		      	  <div class="dropdown-item" (click)="changeLayoutSC.add('COMPLETE')">Complete</div>
		      </div>
		  </li>	
		  
		  <li class="widget-list nav-item dropdown" *ngIf="grid != null && grid.availableWidgets.length > 0">
		      <a href="#" class="nav-link org-nav-text-color dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
		      	Widgets <span class="caret"></span>
		      </a>
		      <div class="dropdown-menu">
		      	  <widget-item (clickWidget)="clickWidgetSC.add($event)" *ngFor="let w of grid.availableWidgets" [widget]="w"></widget-item>
		      </div>
		  </li>
		«IF !gc.projectServices.empty || !gc.projectActions.empty»
			<li class="widget-list nav-item dropdown">
				<a href="#" class="nav-link org-nav-text-color dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
					Services <span class="caret"></span>
				</a>
				<div class="dropdown-menu">
					«FOR p:gc.projectServices»
						<div class="dropdown-item" *ngIf="activeProjectServices.contains('«p.value.get(1).escapeJava»')" [class.disabled]="disabledProjectServices.contains('«p.value.get(1).escapeJava»')" (click)="trigger«p.value.get(1).escapeDart»()">«p.value.get(1)»</div>
					«ENDFOR»
					«FOR p:gc.projectActions»
						<div class="dropdown-item" *ngIf="activeProjectServices.contains('«p.value.get(1).escapeJava»')" (click)="triggerAction«p.value.get(1).escapeDart»()">«p.value.get(1)»</div>
					«ENDFOR»
				</div>
			</li>
		«ENDIF»
		  </ul>		
		  <button (click)="changeMainLayout('micro')" *ngIf="currentLayout=='classic'" class="btn btn-sm btn-success" type="button">New Layout</button>
		  <button (click)="changeMainLayout('classic')" *ngIf="currentLayout=='micro'" class="btn btn-sm btn-default" type="button">Classic Layout</button>
		  <active-users [users]="activeUsers" class="d-flex mr-4 d-none d-lg-block" *ngIf="activeUsers.length > 1"></active-users>
	  </div>
		«IF !gc.projectServices.empty»
			<div class="modal d-block show fade in" tabindex="-1" role="dialog"  *ngIf="«FOR p:gc.projectServices SEPARATOR "||"»show«p.value.get(1).escapeDart»«ENDFOR»">
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title">{{serviceName}}</h4>
							<button (click)="closeServiceModal()" type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						</div>
						<div class="modal-body">
							<h5>{{serviceName}}</h5>
							«FOR p:gc.projectServices»
								<form *ngIf="show«p.value.get(1).escapeDart»" (ngSubmit)="sendTrigger«p.value.get(1).escapeDart»(«FOR attr:p.value.subList(2,p.value.size) SEPARATOR ","»form«attr.escapeDart».value«ENDFOR»)" >
								«FOR attr:p.value.subList(2,p.value.size)»
									<div class="form-group">
										<label for="form«attr.escapeDart»">«attr»</label>
										<input #form«attr.escapeDart» type="text" class="form-control" id="form«attr.escapeDart»" required>
									</div>
								«ENDFOR»
								<div class="modal-footer">
									<button type="submit" *ngIf="show«p.value.get(1).escapeDart»" (click)="sendTrigger«p.value.get(1).escapeDart»(«FOR attr:p.value.subList(2,p.value.size) SEPARATOR ","»form«attr.escapeDart».value«ENDFOR»)" class="btn btn-success">Submit</button>
									<button type="button" class="btn" data-dismiss="modal" (click)="closeServiceModal()">Close</button>
								</div>
								</form>
							«ENDFOR»
						</div>
					</div>
				</div>
			</div>
		«ENDIF»
	</navigation>
	'''
	
}