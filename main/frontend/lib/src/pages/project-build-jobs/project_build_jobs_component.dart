import 'package:angular/angular.dart';
import 'package:angular/security.dart';
import 'dart:html';
import 'dart:convert';
import 'package:angular_router/angular_router.dart';
import '../../routes.dart' as top_routes;
import '../../model/core.dart';
import '../shared/navigation/navigation_component.dart';
import '../../service/base_service.dart';
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
class ProjectBuildJobsComponent implements OnActivate {

  PyroUser user;
  PyroProject project;
  List<PyroWorkspaceImageBuildJob> buildJobs = List();

  int page = 0;
  int size = 15;

  final ProjectService _projectService;
  final UserService _userService;
  final WorkspaceImageBuildJobService _buildJobService;
  final NotificationService _notificationService;
  final Router _router;

  ProjectBuildJobsComponent(
      this._projectService,
      this._userService,
      this._buildJobService,
      this._notificationService,
      this._router) {
  }

  @override
  void onActivate(_, RouterState current) async {
    var projectId = current.parameters['projectId'];
    _userService.fetchUser().then((u) {
      user = u;

      _projectService.getById(projectId).then((p) {
        project = p;

        document.title = "Cinco Cloud | " + project.name;

        _buildJobService.getAll(project.id, page, size).then((jobs){
          buildJobs = jobs;
        }).catchError((err) {
          window.console.log(err);
        });
      }).catchError((err) {
        window.console.log(err);
      });
    }).catchError((err){
      window.console.log(err);
    });
  }

  PyroOrganization get organization => project == null ? null : project.organization;
}
