import 'package:angular/angular.dart';
import 'package:angular/security.dart';
import 'dart:html';
import 'dart:convert';
import 'package:angular_router/angular_router.dart';

import 'package:CincoCloud/src/sync/messages.dart';
import 'package:CincoCloud/src/service/project_web_socket_factory_service.dart';
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

  User user;
  WebSocket projectWebSocket;
  Project project;
  ProjectDeployment deployment;
  SafeResourceUrl editorUrl;

  final ProjectService _projectService;
  final UserService _userService;
  final DomSanitizationService _domSanitizationService;
  final NotificationService _notificationService;
  final Router _router;
  final ProjectWebSocketFactoryService _projectWebSocketFactory;

  ProjectComponent(
      this._projectService,
      this._userService,
      this._notificationService,
      this._domSanitizationService,
      this._router,
      this._projectWebSocketFactory) {
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
            var url = deployment.url + '?jwt=' + window.localStorage['pyro_token'] + "&projectId=" + project.id.toString();
            editorUrl = _domSanitizationService.bypassSecurityTrustResourceUrl(url);
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
    return _projectWebSocketFactory.create(project.id).then((socket) {
      projectWebSocket = socket;

      projectWebSocket.onMessage.listen((MessageEvent e) {
        window.console.debug("[CINCO_CLOUD] onMessage Project Websocket");

        var message = WebSocketMessage.fromJSON(e.data);
        switch(message.event) {
          case WebSocketEvents.UPDATE_POD_DEPLOYMENT_STATUS:
            deployment = ProjectDeployment.fromJSOG(new Map(), message.content);
            break;
          case WebSocketEvents.UPDATE_BUILD_JOB_STATUS:
            var job = WorkspaceImageBuildJob.fromJSOG(cache: Map(), jsog: message.content);
            if (job.status == 'FINISHED_WITH_SUCCESS') {
              _notificationService.displayMessage("Image has been build successfully.", NotificationType.SUCCESS);
            } else if (job.status == 'FINISHED_WITH_FAILURE') {
              _notificationService.displayMessage("Image could not be build.", NotificationType.DANGER);
            }
            break;
        }
      });

      projectWebSocket.onError.listen((e) {
        _notificationService.displayMessage("Failed to connect with websocket.", NotificationType.DANGER);
        window.console.debug("[CINCO_CLOUD] Error on Websocket projectWebSocket: ${e.toString()}");
      });
    });
  }

  Organization get organization => project == null ? null : project.organization;
}
