import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'dart:async';

import '../../../model/core.dart';
import '../../../service/project_service.dart';


@Component(
    selector: 'edit-project',
    directives: const [coreDirectives, formDirectives],
    templateUrl: 'edit_project_component.html'
)
class EditProjectComponent implements OnInit {

  final closeSC = new StreamController();
  @Output() Stream get close => closeSC.stream;

  final editedProjectSC = new StreamController();
  @Output() Stream get editedProject => editedProjectSC.stream;

  @Input()
  PyroUser user;
  
  @Input()
  PyroOrganization organization;

  @Input()
  PyroProject project;

  ProjectService _projectService;

  String projectName;
  String projectDescription;
  String selectedOwnerId;

  bool hasBeenSaved = false;

  EditProjectComponent(this._projectService) {
  }

  @override
  void ngOnInit() {
    projectName = project.name;
    projectDescription = project.description;
    selectedOwnerId = "${user.id}";
  }

  void editProject() {
    project.name = projectName;
    project.description = projectDescription;
        
    int id = int.tryParse(selectedOwnerId);
    print(id);
    List<PyroUser> allUsers = new List.from(organization.owners)..addAll(organization.members);
    int i = allUsers.indexWhere((u) => u.id == id);
    project.owner = allUsers[i];
    
    _projectService.update(project).then((_){
    	hasBeenSaved=true;
    	editedProjectSC.add(project);
    });
  }
  
  bool get canChangeOwner {
    return organization.owners.indexWhere((u) => u.id == user.id) > -1 || user.systemRoles.length > 0;
  }
}

