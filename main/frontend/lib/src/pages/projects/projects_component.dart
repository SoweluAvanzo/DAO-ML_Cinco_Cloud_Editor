import 'package:angular/angular.dart';
import 'dart:async';
import 'package:angular_forms/angular_forms.dart';
import 'dart:html';
import 'dart:convert';
import 'package:angular_router/angular_router.dart';

import '../../model/core.dart';
import 'new_project/new_project_component.dart';
import 'edit_project/edit_project_component.dart';
import 'delete_project/delete_project_component.dart';

import '../../routes.dart' as top_routes;
import '../../service/base_service.dart';
import '../../service/project_service.dart';
import '../../service/user_service.dart';
import '../../service/notification_service.dart';
import '../../service/organization_service.dart';
import '../../service/organization_access_right_vector_service.dart';

import '../main/routes.dart';
import '../main/route_paths.dart';

@Component(
    selector: 'projects',
    templateUrl: 'projects_component.html',
    directives: const [
      coreDirectives,
      routerDirectives,
      NewProjectComponent,
      EditProjectComponent,
      DeleteProjectComponent
    ],
    styleUrls: const [
      'projects_component.css'
    ],
    providers: const [
      ClassProvider(ProjectService),
      ClassProvider(OrganizationService),
      ClassProvider(OrganizationAccessRightVectorService)
    ],
    exports: const [
      RoutePaths,
      Routes
    ])
class ProjectsComponent implements OnDestroy, OnInit {
  String editProjectHeader;

  bool showEditProjectModal = false;
  PyroProject editProject = null;
  bool showDeleteProjectModal = false;
  PyroProject deleteProject = null;

  @Input()
  PyroOrganization organization;

  @ViewChild(NewProjectComponent)
  NewProjectComponent newProjectModal;

  PyroUser user;
  PyroOrganizationAccessRightVector orgArv;

  final ProjectService projectService;
  final UserService userService;
  final OrganizationService organizationService;
  final NotificationService notificationService;
  final OrganizationAccessRightVectorService _orgArvService;
  final Router _router;

  WebSocket webSocketCurrentUser;

  ProjectsComponent(
      this.projectService,
      this.userService,
      this.organizationService,
      this.notificationService,
      this._orgArvService,
      this._router) {
    editProjectHeader = '';
  }

  @override
  void ngOnInit() {
    userService.loadUser().then((u) {
      user = u;
      activateWebSocket();
      _orgArvService.getMy("${organization.id}").then((arv) {
        orgArv = arv;
      });
    }).catchError((_) => _router.navigate(RoutePaths.login.toUrl()));
  }

  @override
  void ngOnDestroy() {
    if (this.webSocketCurrentUser != null &&
        this.webSocketCurrentUser.readyState == WebSocket.OPEN) {
      window.console.debug("Closing Websocket webSocketCurrentUser");
      this.webSocketCurrentUser.close();
      this.webSocketCurrentUser = null;
    }
  }

  void activateWebSocket() {
    BaseService.getTicket().then((ticket) {
      if (this.user != null && this.webSocketCurrentUser == null) {
        this.webSocketCurrentUser = new WebSocket(
            '${userService.getBaseUrl(protocol: 'ws:')}/ws/user/${ticket}/private');

        // Callbacks for currentUser
        this.webSocketCurrentUser.onOpen.listen((e) {
          window.console.debug("[PYRO] onOpen User Websocket");
        });
        this.webSocketCurrentUser.onMessage.listen((MessageEvent e) {
          window.console.debug("[PYRO] onMessage User Websocket");
          if (e.data != null) {
            var jsog = jsonDecode(e.data);
            if (jsog['senderId'] != user.id) {
              this.user = PyroUser.fromJSOG(new Map(), jsog['content']);
              notificationService.displayMessage(
                  "Update Received", NotificationType.INFO);
            }

          }
        });
        this.webSocketCurrentUser.onClose.listen((CloseEvent e) {
          // notificationService.displayMessage("Synchronisation Terminated", NotificationType.WARNING);
          window.console.debug("[PYRO] onClose User Websocket");
        });
        this.webSocketCurrentUser.onError.listen((e) {
          notificationService.displayMessage(
              "Synchronisation Error", NotificationType.DANGER);
          window.console.debug(
              "[PYRO] Error on Websocket webSocketCurrentUser: ${e.toString()}");
        });
      }
    });
  }

  void showEditProject(PyroProject project) {
    editProject = project;
    showEditProjectModal = true;
  }

  void removeProject(PyroProject project) {
    deleteProject = project;
    showDeleteProjectModal = true;
  }

  void handleProjectDeleted() {
    showDeleteProjectModal = false;
    organization.projects.removeWhere((p) => p.id == deleteProject.id);
    deleteProject = null;
    notificationService.displayMessage(
        "The project has been deleted.", NotificationType.SUCCESS);
  }

  void openCurrentProject(PyroProject project) {
    var orgId = organization.id;
    var projectId = project.id;
  }

  void handleNewProject(PyroProject project) {
  	organization.projects.add(project);
  	newProjectModal.close();
  	notificationService.displayMessage("Project ${project.name} has been created.", NotificationType.SUCCESS);
  }
  
  void handleProjectEdited(PyroProject project) {
  	notificationService.displayMessage("Project has been edited.", NotificationType.SUCCESS);
  }

  bool get canCreate =>
      orgArv != null &&
      orgArv.accessRights.contains(PyroOrganizationAccessRight.CREATE_PROJECTS);
  bool get canEdit =>
      orgArv != null &&
      orgArv.accessRights.contains(PyroOrganizationAccessRight.EDIT_PROJECTS);
  bool get canDelete =>
      orgArv != null &&
      orgArv.accessRights.contains(PyroOrganizationAccessRight.DELETE_PROJECTS);
}
