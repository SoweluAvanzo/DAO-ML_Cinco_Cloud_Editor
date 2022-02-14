import 'package:angular/angular.dart';
import 'dart:html';
import 'dart:convert';
import 'package:angular_router/angular_router.dart';

import '../../model/core.dart';
import 'new_project/new_project_component.dart';
import 'edit_project/edit_project_component.dart';
import 'delete_project/delete_project_component.dart';
import 'project_list/project_list_component.dart';

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
      ProjectListComponent,
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
class ProjectsComponent implements OnInit {
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
      _orgArvService.getMy("${organization.id}").then((arv) {
        orgArv = arv;
      });
    }).catchError((_) => _router.navigate(RoutePaths.login.toUrl()));
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

  bool get canCreate => orgArv != null && orgArv.accessRights.contains(OrganizationAccessRight.CREATE_PROJECTS);

  bool canEdit(User user, Project project) {
    return orgArv != null && orgArv.accessRights.contains(OrganizationAccessRight.EDIT_PROJECTS);
  }

  bool canDelete(User user, Project project) {
    return orgArv != null && orgArv.accessRights.contains(OrganizationAccessRight.DELETE_PROJECTS);
  }

  bool canManageMembers(User user, Project project) {
    return false;
  }

  String getProjectBuildJobsUrl(Project project) {
    return top_routes.RoutePaths.projectBuildJobs.toUrl(parameters: {"projectId": project.id.toString()});
  }
}
