import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'dart:async';

import '../../../model/core.dart';
import '../../../service/project_service.dart';


@Component(
    selector: 'new-project',
    directives: const [coreDirectives, formDirectives],
    templateUrl: 'new_project_component.html'
)
class NewProjectComponent {

  @Output()
  final newProjectSC = new StreamController();
  @Output() Stream get newProject => newProjectSC.stream;

  @Output()
  final dismissSC = new StreamController();
  @Output() Stream get dismiss => dismissSC.stream;

  bool show = false;
  PyroUser user;
  PyroOrganization organization;
  ProjectService _projectService;
  
  NewProjectComponent(ProjectService this._projectService) {
  }

  void createNewProject(String name, String description) {
    _projectService.create(name, description, organization, user).then((p){
    	this.newProjectSC.add(p);
	});
  }
  
  void open(PyroUser u, PyroOrganization org) {
    show = true;
  	user = u;
  	organization = org;
  }
  
  void close() {
  	show = false;
  }

}

