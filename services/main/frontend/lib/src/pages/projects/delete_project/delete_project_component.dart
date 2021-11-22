import 'package:angular/angular.dart';
import 'dart:async';

import '../../../model/core.dart';
import '../../../service/project_service.dart';

@Component(
    selector: 'delete-project',
    directives: const [coreDirectives],
    templateUrl: 'delete_project_component.html'
)
class DeleteProjectComponent {

  final _close = new StreamController();
  @Output() Stream get close => _close.stream;

  final _delete = new StreamController();
  @Output() Stream get delete => _delete.stream;

  @Input()
  User user;

  @Input()
  Project project;

  ProjectService projectService;

  DeleteProjectComponent(ProjectService this.projectService) {
  }
  
  void closeModal(dynamic e) {
    e.preventDefault();
    _close.add(e);
  }

  void deleteProject(dynamic e) {
    e.preventDefault();
    projectService.remove(project, user).then((u){
      user = u;
      _close.add(e);
      _delete.add(e);
    });
  }
}
