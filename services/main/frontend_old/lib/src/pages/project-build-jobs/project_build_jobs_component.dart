import 'package:angular/angular.dart';
import 'dart:html';
import 'dart:math';
import 'dart:convert';
import 'package:angular_router/angular_router.dart';
import 'package:ng_bootstrap/ng_bootstrap.dart';
import 'package:CincoCloud/src/service/project_web_socket_factory_service.dart';
import 'package:CincoCloud/src/sync/messages.dart';
import '../../routes.dart' as top_routes;
import '../../model/core.dart';
import '../shared/navigation/navigation_component.dart';
import '../../service/notification_service.dart';
import '../../service/project_service.dart';
import '../../service/workspace_image_build_job_service.dart';
import '../../service/user_service.dart';
import './project-build-job-status-badge/project_build_job_status_badge_component.dart';

@Component(
    selector: 'project-build-jobs',
    templateUrl: 'project_build_jobs_component.html',
    directives: const [
      coreDirectives,
      routerDirectives,
      bsDirectives,
      NavigationComponent,
      ProjectBuildJobStatusBadgeComponent,
    ],
    styleUrls: const [
      'project_build_jobs_component.css'
    ],
    providers: const [
      ClassProvider(ProjectService),
      ClassProvider(WorkspaceImageBuildJobService)
    ],
    pipes: [commonPipes]
)
class ProjectBuildJobsComponent implements OnActivate, OnDeactivate {

  User user;
  Project project;
  Page<WorkspaceImageBuildJob> buildJobsPage;
  WebSocket projectWebSocket;

  int pageNumber = 0;
  int pageSize = 15;

  final ProjectService _projectService;
  final UserService _userService;
  final WorkspaceImageBuildJobService _buildJobService;
  final NotificationService _notificationService;
  final ProjectWebSocketFactoryService _projectWebSocketFactory;

  ProjectBuildJobsComponent(
      this._projectService,
      this._userService,
      this._buildJobService,
      this._notificationService,
      this._projectWebSocketFactory) {
  }

  @override
  void onActivate(_, RouterState current) async {
    var projectId = current.parameters['projectId'];
    _userService.fetchUser().then((u) {
      user = u;

      _projectService.getById(projectId).then((p) {
        project = p;

        activateWebSocket();

        document.title = "Cinco Cloud | " + project.name;

        loadPage();
      }).catchError((err) {
        window.console.log(err);
      });
    }).catchError((err){
      window.console.log(err);
    });
  }

  void loadPage() {
    _buildJobService.getAll(project.id, pageNumber, pageSize).then((p) {
      buildJobsPage = p;
    }).catchError((err) {
      window.console.log(err);
    });
  }

  void nextPage() {
    pageNumber++;
    loadPage();
  }

  void previousPage() {
    pageNumber = max(0, pageNumber - 1);
    loadPage();
  }

  @override
  void onDeactivate(RouterState current, _) {
    projectWebSocket?.close();
  }

  void activateWebSocket() {
    _projectWebSocketFactory.create(project.id).then((socket) {
      projectWebSocket = socket;

      projectWebSocket.onMessage.listen((MessageEvent e) {
        window.console.debug("[CINCO_CLOUD] onMessage Project Websocket");

        var message = WebSocketMessage.fromJSON(e.data);
        switch(message.event) {
          case WebSocketEvents.UPDATE_BUILD_JOB_STATUS:
            var job = WorkspaceImageBuildJob.fromJSOG(cache: Map(), jsog: message.content);
            var i = buildJobsPage.items.indexWhere((j) => j.id == job.id);
            if (i > -1) {
              buildJobsPage.items[i] = job;
            } else {
              // insert job at the top if we are on the first page
              if (buildJobsPage.number == 0) {
                buildJobsPage.items.insert(0, job);
                // remove the last job on the page if the page overflows
                if (buildJobsPage.items.length > pageSize) {
                  buildJobsPage.items.removeLast();
                  // increase the amount of pages if we are on the last page
                  if (buildJobsPage.number + 1 == buildJobsPage.amountOfPages) {
                    buildJobsPage.amountOfPages++;
                  }
                }
              }
            }
            break;
        }
      });
    });
  }

  void abortJob(WorkspaceImageBuildJob job) {
    _buildJobService.abort(project.id, job).then((abortedJob){
      _notificationService.displayMessage("The job has been aborted.", NotificationType.SUCCESS);
      job.status = abortedJob.status;
    }).catchError((err) {
      _notificationService.displayMessage("The job could not be aborted.", NotificationType.DANGER);
    });
  }

  void deleteJob(WorkspaceImageBuildJob job) {
    _buildJobService.remove(project.id, job).then((deletedJob){
      _notificationService.displayMessage("The job has been deleted.", NotificationType.SUCCESS);
      buildJobsPage.items.remove(job);
    }).catchError((err) {
      _notificationService.displayMessage("The job could not be deleted.", NotificationType.DANGER);
    });
  }

  String getDurationAsString(WorkspaceImageBuildJob job) {
    return job.finishedAt.difference(job.startedAt).inMinutes.toString() + "min";
  }

  Organization get organization => project == null ? null : project.organization;

  bool get hasNextPage => buildJobsPage != null && buildJobsPage.number + 1 < buildJobsPage.amountOfPages;

  bool get hasPreviousPage => buildJobsPage != null && buildJobsPage.number > 0;
}
