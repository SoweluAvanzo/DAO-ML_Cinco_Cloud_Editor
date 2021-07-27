import 'dart:html';
import 'dart:async';
import 'package:angular/angular.dart';

import '../../../model/core.dart';
import './permission_component.dart';

@Component(
  selector: 'permissions',
  directives: const [coreDirectives, PermissionComponent],
  styleUrls: const ['permissions_component.css'],
  templateUrl: 'permissions_component.html'
)
class PermissionsComponent {
 
  final changedSc = new StreamController();
  @Output() Stream get changed => changedSc.stream;
 
  @Input()
  List<PyroGraphModelPermissionVector> permissionVectors;
 
  PermissionsComponent() {
  	permissionVectors = new List();
  }
  
  void handleChanged(PyroGraphModelPermissionVector vector) {
    var i = permissionVectors.indexWhere((v) => v.graphModelType == vector.graphModelType);
    permissionVectors[i] = vector;
    changedSc.add(permissionVectors);
  }
}
