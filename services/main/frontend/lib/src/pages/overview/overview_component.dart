import 'dart:convert';
import 'dart:html';
import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';

import '../../routes.dart' as top_routes;
import '../../model/core.dart';
import '../../service/organization_service.dart';
import '../../service/project_service.dart';
import '../../service/user_service.dart';
import '../../service/notification_service.dart';
import '../shared/navigation/navigation_component.dart';
import '../projects/project_list/project_list_component.dart';
import '../projects/new_project/new_project_component.dart';
import '../projects/edit_project/edit_project_component.dart';
import '../projects/delete_project/delete_project_component.dart';

@Component(
  selector: 'overview',
  templateUrl: 'overview_component.html',
  styleUrls: const ['overview_component.css'],
  directives: const [
    coreDirectives,
    routerDirectives,
    NewProjectComponent,
    EditProjectComponent,
    DeleteProjectComponent,
    NavigationComponent,
    ProjectListComponent
  ],
  providers: const [
    ClassProvider(UserService),
    ClassProvider(ProjectService),
    ClassProvider(OrganizationService)
  ],
) 
class OverviewComponent implements OnInit {

  @ViewChild(NewProjectComponent)
  NewProjectComponent newProjectModal;

  User currentUser;
  List<Organization> organizations = List();
  List<Project> projects = List();

  bool showEditProjectModal = false;
  Project editProject = null;
  bool showDeleteProjectModal = false;
  Project deleteProject = null;
  
  final UserService _userService;
  final ProjectService _projectService;
  final OrganizationService _organizationService;
  final NotificationService _notificationService;
  final Router _router;

  OverviewComponent(
      this._organizationService,
      this._userService,
      this._projectService,
      this._notificationService,
      this._router) {}

  @override
  void ngOnInit() {
    _userService.fetchUser().then((user) {
      this.currentUser = user;
      _projectService.getAll().then((projects) {
        this.projects = projects;
      }).catchError((err) {
        window.console.log(err);
      });
    }).catchError((err) {
      window.console.log(err);
    });
  }

  void handleEditProject(Project project) {
    editProject = project;
    showEditProjectModal = true;
  }

  void handleDeleteProject(Project project) {
    deleteProject = project;
    showDeleteProjectModal = true;
  }

  void handleProjectDeleted() {
    projects.removeWhere((p) => p.id == deleteProject.id);
    showDeleteProjectModal = false;
    deleteProject = null;
    _notificationService.displayMessage("The project has been deleted.", NotificationType.SUCCESS);
  }

  void handleProjectEdited(Project project) {
    _notificationService.displayMessage("Project has been edited.", NotificationType.SUCCESS);
  }

  void handleNewProject(Project project) {
    projects.add(project);
    newProjectModal.close();
    _notificationService.displayMessage("Project ${project.name} has been created.", NotificationType.SUCCESS);
  }

  void openCurrentProject(Project project) {
    var projectId = project.id;
    _router.navigate(top_routes.RoutePaths.project.toUrl(parameters: {"projectId": projectId.toString()}));
  }

  bool canEditOrDeleteOrManageMembers(User user, Project project) {
    return project.owner.id == user.id;
  }
}

