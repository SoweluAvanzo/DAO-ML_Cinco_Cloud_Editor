import 'package:angular_router/angular_router.dart';
import '../../route_paths.dart' as _parent;

class RoutePaths {
  static final login = RoutePath(path: 'login',parent: _parent.RoutePaths.home);
  static final registration = RoutePath(path: 'registration',parent: _parent.RoutePaths.home);
  static final welcome = RoutePath(path: 'welcome',parent: _parent.RoutePaths.home);
}
