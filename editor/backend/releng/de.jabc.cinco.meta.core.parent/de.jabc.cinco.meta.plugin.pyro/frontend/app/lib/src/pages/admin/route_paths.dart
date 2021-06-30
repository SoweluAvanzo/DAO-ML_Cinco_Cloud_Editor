import 'package:angular_router/angular_router.dart';
import '../../route_paths.dart' as parent;

class RoutePaths {
  static final umAdmins = RoutePath(path: 'user-management/admins', parent: parent.RoutePaths.admin);
  static final umOrganizationManagers = RoutePath(path: 'user-management/organization-managers', parent: parent.RoutePaths.admin);
  static final umUsers = RoutePath(path: 'user-management/users', parent: parent.RoutePaths.admin);
  static final sysAppearance = RoutePath(path: 'system/appearance', parent: parent.RoutePaths.admin);
  static final sysSettings = RoutePath(path: 'system/settings', parent: parent.RoutePaths.admin);
}
