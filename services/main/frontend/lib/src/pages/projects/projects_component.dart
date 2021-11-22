import 'package:angular/angular.dart';
import 'dart:html';
import 'dart:convert';
import 'package:angular_router/angular_router.dart';
import 'package:ng_bootstrap/ng_bootstrap.dart';

import '../../model/core.dart';
import 'new_project/new_project_component.dart';
import 'edit_project/edit_project_component.dart';
import 'delete_project/delete_project_component.dart';
import '../../components/workspace_image_badge/workspace_image_badge_component.dart';

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
      DeleteProjectComponent,
      WorkspaceImageBadgeComponent,
      bsDirectives
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
  Project editProject = null;
  bool showDeleteProjectModal = false;
  Project deleteProject = null;

  @Input()
  Organization organization;

  @ViewChild(NewProjectComponent)
  NewProjectComponent newProjectModal;

  User user;
  OrganizationAccessRightVector orgArv;

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
    if (this.webSocketCurrentUser != null
        && this.webSocketCurrentUser.readyState == WebSocket.OPEN) {
      window.console.debug("Closing Websocket webSocketCurrentUser");
      this.webSocketCurrentUser.close();
    }
  }

  void activateWebSocket() {
    BaseService.getTicket().then((ticket) {
      if (this.user != null && this.webSocketCurrentUser == null) {
        this.webSocketCurrentUser = new WebSocket(
            '${userService.getBaseUrl(protocol: 'ws:')}/ws/user/${ticket}/private'
        );

        this.webSocketCurrentUser.onOpen.listen((e) {
          window.console.debug("[CINCO_CLOUD] onOpen User Websocket");
        });

        this.webSocketCurrentUser.onMessage.listen((MessageEvent e) {
          window.console.debug("[CINCO_CLOUD] onMessage User Websocket");
        });

        this.webSocketCurrentUser.onClose.listen((CloseEvent e) {
          window.console.debug("[CINCO_CLOUD] onClose User Websocket");
        });

        this.webSocketCurrentUser.onError.listen((e) {
          notificationService.displayMessage("Failed to connect with websocket.", NotificationType.DANGER);
          window.console.debug("[CINCO_CLOUD] Error on Websocket webSocketCurrentUser: ${e.toString()}");
        });
      }
    });
  }

  void showEditProject(Project project) {
    editProject = project;
    showEditProjectModal = true;
  }

  void removeProject(Project project) {
    deleteProject = project;
    showDeleteProjectModal = true;
  }

  void handleProjectDeleted() {
    showDeleteProjectModal = false;
    organization.projects.removeWhere((p) => p.id == deleteProject.id);
    deleteProject = null;
    notificationService.displayMessage("The project has been deleted.", NotificationType.SUCCESS);
  }

  void openCurrentProject(Project project) {
    var projectId = project.id;
    _router.navigate(top_routes.RoutePaths.project.toUrl(parameters: {"projectId": projectId.toString()}));
  }

  void handleNewProject(Project project) {
  	organization.projects.add(project);
  	newProjectModal.close();
  	notificationService.displayMessage("Project ${project.name} has been created.", NotificationType.SUCCESS);
  }
  
  void handleProjectEdited(Project project) {
  	notificationService.displayMessage("Project has been edited.", NotificationType.SUCCESS);
  }

  bool get canCreate =>
      orgArv != null &&
      orgArv.accessRights.contains(OrganizationAccessRight.CREATE_PROJECTS);

  bool get canEdit =>
      orgArv != null &&
      orgArv.accessRights.contains(OrganizationAccessRight.EDIT_PROJECTS);

  bool get canDelete =>
      orgArv != null &&
      orgArv.accessRights.contains(OrganizationAccessRight.DELETE_PROJECTS);

  String getProjectBuildJobsUrl(Project project) {
    return top_routes.RoutePaths.projectBuildJobs.toUrl(parameters: {"projectId": project.id.toString()});
  }
}
