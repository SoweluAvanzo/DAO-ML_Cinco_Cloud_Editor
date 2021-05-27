import 'package:angular/angular.dart';
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

  final ProjectService _projectService;
  final UserService _userService;

  ProjectComponent(
      this._projectService,
      this._userService) {
  }

  @override
  void onActivate(_, RouterState current) async {
    var projectId = current.parameters['projectId'];
    _userService.fetchUser().then((u) {
      user = u;

      _projectService.getById(projectId).then((p) {
        project = p;

        _projectService.deploy(project).then((_) {
          window.console.log("deploy project");
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
}
