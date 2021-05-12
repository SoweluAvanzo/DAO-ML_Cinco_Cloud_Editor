import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'dart:async';

import '../../../model/core.dart';
import '../../../service/project_service.dart';

enum ProjectType {
  EMPTY,
  TEMPLATE
}

@Component(
    selector: 'new-project',
    styleUrls: const ['new_project_component.css'],
    directives: const [coreDirectives, formDirectives],
    templateUrl: 'new_project_component.html'
)
class NewProjectComponent {

  final newProjectSC = new StreamController();
  @Output() Stream get newProject => newProjectSC.stream;

  final dismissSC = new StreamController();
  @Output() Stream get dismiss => dismissSC.stream;

  bool show = false;
  ProjectType projectType = ProjectType.EMPTY;

  PyroUser user;
  PyroOrganization organization;
  ProjectService _projectService;

  NewProjectComponent(ProjectService this._projectService) {
  }

  void createNewProject(String name, String description) {
    _projectService.create(name, description, organization, user).then((p) {
      this.newProjectSC.add(p);
    });
  }

  void open(PyroUser u, PyroOrganization org) {
    show = true;
    projectType = ProjectType.EMPTY;
    user = u;
    organization = org;
  }

  void setEmptyProjectType() {
    projectType = ProjectType.EMPTY;
  }

  void setTemplateProjectType() {
    projectType = ProjectType.TEMPLATE;
  }

  void close() {
    show = false;
  }

  bool get isEmptyProjectType => projectType == ProjectType.EMPTY;
  bool get isTemplateProjectType => projectType == ProjectType.TEMPLATE;
}
