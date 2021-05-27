import 'package:angular/angular.dart';
import 'package:angular/security.dart';
import 'dart:html';
import 'package:angular_router/angular_router.dart';

import '../../model/core.dart';
import '../../components/workspace_image_badge/workspace_image_badge_component.dart';
import '../shared/navigation/navigation_component.dart';
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
class ProjectComponent implements OnActivate {

  PyroUser user;
  PyroProject project;
  bool deploying = true;
  bool deploySuccess = false;
  PyroProjectDeployment deployment;

  final ProjectService _projectService;
  final UserService _userService;
  final DomSanitizationService _domSanitizationService;

  ProjectComponent(
      this._projectService,
      this._userService,
      this._domSanitizationService) {
  }

  @override
  void onActivate(_, RouterState current) async {
    var projectId = current.parameters['projectId'];
    _userService.fetchUser().then((u) {
      user = u;

      _projectService.getById(projectId).then((p) {
        project = p;

        _projectService.deploy(project).then((d) {
          deployment = d;
          window.console.log("deploy project");
          window.console.log(deployment);
          deploying = false;
          deploySuccess = true;
        }).catchError((err) {
          deploying = false;
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

  SafeResourceUrl get editorUrl => _domSanitizationService.bypassSecurityTrustResourceUrl(deployment.url);
}
