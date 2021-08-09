import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'dart:async';

import '../../../model/core.dart';
import '../../../service/project_service.dart';
import '../../../service/workspace_image_service.dart';
import '../../../pages/shared/toggle_button/toggle_button_component.dart';
import '../../../components/workspace_image_badge/workspace_image_badge_component.dart';


@Component(
    selector: 'edit-project',
    directives: const [
      ToggleButtonComponent,
      WorkspaceImageBadgeComponent,
      coreDirectives,
      formDirectives
    ],
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
  WorkspaceImageService _workspaceImageService;

  String projectName;
  String projectDescription;
  String selectedOwnerId;
  bool imagePublished;

  bool hasBeenSaved = false;
  String activeTab = 'project';

  EditProjectComponent(this._projectService, this._workspaceImageService) {
  }

  @override
  void ngOnInit() {
    projectName = project.name;
    projectDescription = project.description;
    selectedOwnerId = "${user.id}";
    imagePublished = project.image.published;
  }

  void editProject() {
    project.name = projectName;
    project.description = projectDescription;
        
    int id = int.tryParse(selectedOwnerId);
    List<PyroUser> allUsers = new List.from(organization.owners)..addAll(organization.members);
    int i = allUsers.indexWhere((u) => u.id == id);
    project.owner = allUsers[i];
    
    _projectService.update(project).then((_){
    	hasBeenSaved=true;
    	editedProjectSC.add(project);
    });
  }

  void editImage() {
    project.image.published = imagePublished;
    _workspaceImageService.update(project.image).then((_) {
      hasBeenSaved = true;
      editedProjectSC.add(project);
    });
  }

  void handleImagePublishedChanged(bool value) {
    imagePublished = value;
  }
  
  bool get canChangeOwner {
    return organization.owners.indexWhere((u) => u.id == user.id) > -1 || user.systemRoles.length > 0;
  }

  void setProjectTab(dynamic e) {
    e.preventDefault();
    activeTab = 'project';
    hasBeenSaved = false;
  }

  void setImageTab(dynamic e) {
    e.preventDefault();
    activeTab = 'image';
    hasBeenSaved = false;
  }

  bool get isProjectTab => activeTab == 'project';
  bool get isImageTab => activeTab == 'image';
}

