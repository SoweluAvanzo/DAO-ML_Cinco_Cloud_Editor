import '../model/core.dart';

class GraphModelPermissionUtils {
	
	static bool canChangePermissions(PyroUser user) {
		return true;
	}
	
	static bool canCreate(String type, List<PyroGraphModelPermissionVector> vectors) {
	  return vectors.indexWhere((v) => v.graphModelType == type && v.permissions.contains(PyroCrudOperation.CREATE)) > -1;
	}
	
	static bool canRead(String type, List<PyroGraphModelPermissionVector> vectors) {
	  return vectors.indexWhere((v) => v.graphModelType == type && v.permissions.contains(PyroCrudOperation.READ)) > -1;
	}
	
	static bool canUpdate(String type, List<PyroGraphModelPermissionVector> vectors) {
	  return vectors.indexWhere((v) => v.graphModelType == type && v.permissions.contains(PyroCrudOperation.UPDATE)) > -1;
	}
	
	static bool canDelete(String type, List<PyroGraphModelPermissionVector> vectors) {
	  return vectors.indexWhere((v) => v.graphModelType == type && v.permissions.contains(PyroCrudOperation.DELETE)) > -1;
	}
}
