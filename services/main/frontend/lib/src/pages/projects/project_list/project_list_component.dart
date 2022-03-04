import 'dart:async';
import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';
import 'package:ng_bootstrap/ng_bootstrap.dart';

import '../../../model/core.dart';
import '../../../components/workspace_image_badge/workspace_image_badge_component.dart';

import '../../../routes.dart' as top_routes;

import '../../main/routes.dart';
import '../../main/route_paths.dart';

typedef bool ProjectToBoolFn(User user, Project project);

@Component(
    selector: 'project-list',
    templateUrl: 'project_list_component.html',
    directives: const [
      coreDirectives,
      routerDirectives,
      WorkspaceImageBadgeComponent,
      bsDirectives
    ],
    styleUrls: const [
      'project_list_component.css'
    ],
    providers: const [],
    exports: const [
      RoutePaths,
      Routes
    ])
class ProjectListComponent  {

  @Input()
  User user;

  @Input()
  ProjectToBoolFn canEdit;

  @Input()
  ProjectToBoolFn canDelete;

  @Input()
  ProjectToBoolFn canManageMembers;

  @Input()
  List<Project> projects;

  final editSC = new StreamController();
  @Output() Stream get edit => editSC.stream;

  final deleteSC = new StreamController();
  @Output() Stream get delete => deleteSC.stream;

  final Router _router;

  ProjectListComponent(this._router) {
  }

  void deleteProject(Project project) {
    this.deleteSC.add(project);
  }

  void editProject(Project project) {
    this.editSC.add(project);
  }

  void openCurrentProject(Project project) {
    _router.navigate(top_routes.RoutePaths.project.toUrl(parameters: {"projectId": project.id.toString()}));
  }

  String getProjectBuildJobsUrl(Project project) {
    return top_routes.RoutePaths.projectBuildJobs.toUrl(parameters: {"projectId": project.id.toString()});
  }

  String getProjectMembersUrl(Project project) {
    return top_routes.RoutePaths.projectMembers.toUrl(parameters: {"projectId": project.id.toString()});
  }
}
