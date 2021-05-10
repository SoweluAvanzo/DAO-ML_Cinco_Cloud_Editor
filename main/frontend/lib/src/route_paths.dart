import 'package:angular_router/angular_router.dart';

class RoutePaths {
  static final home = RoutePath(path: 'home');
  static final organizations = RoutePath(path: 'organizations');
  static final organization = RoutePath(path: 'organizations/:orgId');
  static final admin = RoutePath(path: 'admin');
  static final logout = RoutePath(path: 'logout');
  static final profile = RoutePath(path: 'profile');
}
