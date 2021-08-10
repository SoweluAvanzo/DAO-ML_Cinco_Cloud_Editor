import 'package:angular/angular.dart';
import 'package:angular/security.dart';
import 'dart:html';
import 'dart:convert';
import 'package:angular_router/angular_router.dart';

import '../../routes.dart' as top_routes;
import '../../model/core.dart';
import '../../components/workspace_image_badge/workspace_image_badge_component.dart';
import '../shared/navigation/navigation_component.dart';
import '../../service/base_service.dart';
import '../../service/notification_service.dart';
import '../../service/project_service.dart';
import '../../service/user_service.dart';

@Component(
    selector: 'project',
    templateUrl: 'project_component.html',
    directives: const [
      coreDirectives,
      routerDirectives,
      WorkspaceImageBadgeComponent,
      NavigationComponent
    ],
    styleUrls: const [
      'project_component.css'
    ],
    providers: const [
      ClassProvider(ProjectService)
    ]
)
class ProjectComponent implements OnActivate, OnDeactivate {

  PyroUser user;
  WebSocket projectWebSocket;
  PyroProject project;
  PyroProjectDeployment deployment;

  final ProjectService _projectService;
  final UserService _userService;
  final DomSanitizationService _domSanitizationService;
  final NotificationService _notificationService;
  final Router _router;

  ProjectComponent(
      this._projectService,
      this._userService,
      this._notificationService,
      this._domSanitizationService,
      this._router) {
  }

  @override
  void onActivate(_, RouterState current) async {
    var projectId = current.parameters['projectId'];
    _userService.fetchUser().then((u) {
      user = u;

      _projectService.getById(projectId).then((p) {
        project = p;

        activateWebSocket().then((_) {
          _projectService.deploy(project).then((d) {
            deployment = d;
            window.console.log("deploy project");
          }).catchError((err) {
            window.console.log(err);
          });
        }).catchError((err) {
          window.console.log(err);
        });

        document.title = "Cinco Cloud | " + project.name;
      }).catchError((err) {
        window.console.log(err);
      });
    }).catchError((err){
      window.console.log(err);
    });
  }

  @override
  void onDeactivate(RouterState current, _) {
    projectWebSocket.close();
  }

  Future<dynamic> activateWebSocket() async {
    return BaseService.getTicket().then((ticket) {
      if (user != null && projectWebSocket == null) {
        projectWebSocket = new WebSocket(
            '${_userService.getBaseUrl(protocol: 'ws:')}/ws/project/${project.id}/${ticket}/private'
        );

        projectWebSocket.onOpen.listen((e) {
          window.console.debug("[PYRO] onOpen Project Websocket");
        });

        projectWebSocket.onMessage.listen((MessageEvent e) {
          window.console.debug("[PYRO] onMessage Project Websocket");

          var message = jsonDecode(e.data);
          switch(message['event']) {
            case 'project:podDeploymentStatus':
              deployment = PyroProjectDeployment.fromJSOG(new Map(), message['content']);
              break;
            case 'workspaces:jobs:results':
              var result = WorkspaceImageBuildResult.fromJSOG(new Map(), message['content']);
              if (result.success) {
                _notificationService.displayMessage("Image ${result.image} has been build successfully.", NotificationType.SUCCESS);
              } else {
                _notificationService.displayMessage("Image ${result.image} could not be build: ${result.message}", NotificationType.DANGER);
              }
              break;
          }
        });

        projectWebSocket.onClose.listen((CloseEvent e) {
          window.console.debug("[PYRO] onClose Project Websocket");
          _router.navigate(top_routes.RoutePaths.organization.toUrl(parameters: {'orgId': project.organization.id.toString()}));
        });

        projectWebSocket.onError.listen((e) {
          _notificationService.displayMessage("Failed to connect with websocket.", NotificationType.DANGER);
          window.console.debug("[PYRO] Error on Websocket projectWebSocket: ${e.toString()}");
        });
      }
    });
  }

  PyroOrganization get organization => project == null ? null : project.organization;

  SafeResourceUrl get editorUrl => _domSanitizationService.bypassSecurityTrustResourceUrl(deployment.url + '?jwt=' + window.localStorage['pyro_token'] + "&projectId=" + project.id.toString());
}
