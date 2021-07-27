import 'package:angular_router/angular_router.dart';

class RoutePaths {
  static final home = RoutePath(path: 'home');
  static final organizations = RoutePath(path: 'organizations');
  static final organization = RoutePath(path: 'organizations/:orgId');
  static final editor = RoutePath(path: 'organizations/:orgId/projects/:projectId/editor');
  static final projectPermissions = RoutePath(path: 'organizations/:orgId/projects/:projectId/permissions');
  static final admin = RoutePath(path: 'admin');
  static final logout = RoutePath(path: 'logout');
  static final profile = RoutePath(path: 'profile');
}
