import 'dart:html';
import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';
import 'package:ng_bootstrap/ng_bootstrap.dart';
import '../../routes.dart' as top_routes;
import '../../model/core.dart';
import '../shared/navigation/navigation_component.dart';
import '../../service/notification_service.dart';
import '../../service/project_service.dart';
import '../../service/user_service.dart';
import '../admin/user-management/search_user/search_user_component.dart';

@Component(
    selector: 'project-members',
    templateUrl: 'project_members_component.html',
    directives: const [
      coreDirectives,
      routerDirectives,
      bsDirectives,
      NavigationComponent,
      SearchUserComponent,
    ],
    styleUrls: const [
      'project_members_component.css'
    ],
    providers: const [
      ClassProvider(ProjectService)
    ],
    pipes: [commonPipes]
)
class ProjectMembersComponent implements OnActivate {

  final ProjectService _projectService;
  final UserService _userService;
  final NotificationService _notificationService;

  User user;
  Project project;
  bool showFindUsersModal = false;

  ProjectMembersComponent(
      this._projectService,
      this._userService,
      this._notificationService) {
  }

  @override
  void onActivate(_, RouterState current) async {
    _userService.fetchUser().then((u) {
      user = u;
    }).catchError((err){
      window.console.log(err);
    });

    var projectId = current.parameters['projectId'];
    _projectService.getById(projectId).then((p) {
      project = p;
      document.title = "Cinco Cloud | " + project.name + " Members";
    }).catchError((err) {
      window.console.log(err);
    });
  }

  void addProjectMember(User user) {
    _projectService.addMember(project.id.toString(), user)
        .then((p) {
          _notificationService.displayMessage("${user.name} has been added to the project.", NotificationType.SUCCESS);
          project = p;
          showFindUsersModal = false;
        })
        .catchError((err) {
          _notificationService.displayMessage("${user.name} could not be added to the project.", NotificationType.DANGER);
          window.console.log(err);
        });
  }

  void removeProjectMember(User user) {
    _projectService.removeMember(project.id.toString(), user)
        .then((p) {
          _notificationService.displayMessage("${user.name} has been removed from the project.", NotificationType.SUCCESS);
          project = p;
        }).catchError((err) {
          _notificationService.displayMessage("${user.name} could not be removed from the project.", NotificationType.DANGER);
          window.console.log(err);
        });
  }

  void openFindUsersModal() {
    showFindUsersModal = true;
  }

  Organization get organization => project == null ? null : project.organization;

  bool get isProjectOwner => project != null && user != null && project.owner.id == user.id;
}
