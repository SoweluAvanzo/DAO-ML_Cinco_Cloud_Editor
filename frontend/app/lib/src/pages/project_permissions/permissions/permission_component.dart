import 'dart:html';
import 'dart:async';
import 'package:angular/angular.dart';
import 'package:ng_bootstrap/ng_bootstrap.dart';

import '../../../model/core.dart';
import '../../../service/graph_model_permission_vector_service.dart';
import '../../../service/notification_service.dart';
import '../../../pipes/normalize_enum_string_pipe.dart';

@Component(
  selector: 'graph-model-permission',
  directives: const [coreDirectives, bsDirectives],
  styleUrls: const ['permissions_component.css'],
  pipes: const [NormalizeEnumStringPipe],
  templateUrl: 'permission_component.html'
)
class PermissionComponent {

  final GraphModelPermissionVectorService _permissionService;
  final NotificationService _notificationService;
 
  final changedSc = new StreamController();
  @Output() Stream get changed => changedSc.stream;
 
  @Input("permissionVector")
  PyroGraphModelPermissionVector vector;
 
  PermissionComponent(this._permissionService, this._notificationService) {
  }
  
  List<String> getAvailablePermissions() {
    if (vector == null) return [];
  	List<String> list = [PyroCrudOperation.CREATE, PyroCrudOperation.READ, PyroCrudOperation.UPDATE, PyroCrudOperation.DELETE];
  	vector.permissions.forEach((p) {
  	  list.remove(p);
  	});
  	return list;
  }
  
  addPermission(dynamic $event, String permission) {
    $event.preventDefault();
    vector.permissions.add(permission);
    _permissionService.update(vector).then((updatedVector) {
      changedSc.add(updatedVector);
    }).catchError((err) {
      _notificationService.displayMessage('Permissions could not be updated.', NotificationType.DANGER);
      vector.permissions.remove(permission);
    });
  }
  
  addAllPermissions(dynamic e) {
    e.preventDefault();
    List<String> permissions = getAvailablePermissions();
    vector.permissions.addAll(permissions);
    _permissionService.update(vector).then((updatedVector) {
      changedSc.add(updatedVector);
    }).catchError((err) {
      _notificationService.displayMessage('Permissions could not be updated.', NotificationType.DANGER);
      permissions.forEach((p){vector.permissions.remove(p);});
    });
  }
  
  removePermission(String permission) {
    vector.permissions.remove(permission);
    _permissionService.update(vector).then((updatedVector) {
      changedSc.add(updatedVector);
    }).catchError((err) {
      _notificationService.displayMessage('Permissions could not be updated.', NotificationType.DANGER);
      vector.permissions.remove(permission);
    });
  }
}
