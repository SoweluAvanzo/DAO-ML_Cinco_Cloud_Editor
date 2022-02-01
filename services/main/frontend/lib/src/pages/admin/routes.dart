import 'package:angular_router/angular_router.dart';

import './route_paths.dart';
import './user-management/admins/admins_component.template.dart' as ng;
import './user-management/organization_managers/organization_managers_component.template.dart' as ng;
import './user-management/users/users_component.template.dart' as ng;
import './system/settings/settings_component.template.dart' as ng;

export 'route_paths.dart';

class Routes {
  static final umAdmins = RouteDefinition(
    routePath: RoutePaths.umAdmins,
    component: ng.AdminsComponentNgFactory,
  );
  
  static final umOrganizationManagers = RouteDefinition(
    routePath: RoutePaths.umOrganizationManagers,
    component: ng.OrganizationManagersComponentNgFactory,
  );
  
  static final umUsers = RouteDefinition(
    routePath: RoutePaths.umUsers,
    component: ng.UsersComponentNgFactory,
  );

  static final sysSettings = RouteDefinition(
  	routePath: RoutePaths.sysSettings,
    component: ng.SettingsComponentNgFactory,
  );
      
  static final all = <RouteDefinition>[
  	  RouteDefinition.redirect(
	    path: '/',
	    redirectTo: RoutePaths.umAdmins.toUrl(),
	  ),
	  umAdmins,
	  umOrganizationManagers,
	  umUsers,
	  sysSettings
  ];
}
