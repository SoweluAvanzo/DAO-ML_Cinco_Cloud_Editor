import 'dart:html';
import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';

import '../../model/core.dart';
import '../../service/user_service.dart';
import '../../service/organization_service.dart';
import '../../service/notification_service.dart';
import '../../service/project_service.dart';
import '../../service/graph_model_permission_vector_service.dart';
import '../../service/style_service.dart';
import '../../pages/shared/navigation/navigation_component.dart';
import '../../utils/graph_model_permission_utils.dart';
import '../../route_paths.dart';
import './permissions/permissions_component.dart';

@Component(
  selector: 'project-permissions',
  directives: const [coreDirectives, NavigationComponent, PermissionsComponent],
  providers: const [ClassProvider(ProjectService)],
  templateUrl: 'project_permissions_component.html'
)
class ProjectPermissionsComponent implements OnActivate, OnDeactivate {

  final UserService _userService;
  final OrganizationService _organizationService;
  final ProjectService _projectService;
  final NotificationService _notificationService;
  final GraphModelPermissionVectorService _permissionService;
  final Router _router;
  final StyleService _styleService;
 
  PyroUser user; 
  PyroOrganization organization;
  Map<int, List<PyroGraphModelPermissionVector>> permissionVectorMap;
 
  ProjectPermissionsComponent(
  	this._userService, 
  	this._permissionService, 
  	this._notificationService,
  	this._organizationService,
  	this._router,
  	this._projectService,
  	this._styleService
  ) {
  	permissionVectorMap = new Map();
  }
  
  @override
  void onActivate(_, RouterState current) async {
  	var projectId = current.parameters['projectId'];
  	var orgId = current.parameters['orgId'];
  	  	
  	_userService.loadUser().then((u){
      user = u;
      return _organizationService.getById(orgId).then((o) {
		organization = o;
		_styleService.update(organization.style);
		return _projectService.getById(projectId).then((p) {
		  if (!GraphModelPermissionUtils.canChangePermissions(user,p)) {
      	    _router.navigate(RoutePaths.organization.toUrl(parameters: {'orgId': '${o.id}'}));
      	    return null;
      	  }
		
		  return _permissionService.getAll(projectId).then((pvs) {
            pvs.forEach((pv){
              if (permissionVectorMap[pv.user.id] == null) {
                permissionVectorMap[pv.user.id] = new List();
           	  }
          	  permissionVectorMap[pv.user.id].add(pv);
            });
          });
		});
      });
    }).catchError((err) {
    });
  }
  
  @override
  void onDeactivate(_, RouterState next) async {
  	_styleService.handleOnDeactivate(next);
  }
  
  handleChanged(PyroUser user, List<PyroGraphModelPermissionVector> vectors) {
  	permissionVectorMap[user.id] = vectors;
  }
  
  List<PyroUser> get users {
  	if (organization == null) {
  	  return [];
  	} else {
  	  return new List.from(organization.owners)..addAll(organization.members);
  	}
  }
}
