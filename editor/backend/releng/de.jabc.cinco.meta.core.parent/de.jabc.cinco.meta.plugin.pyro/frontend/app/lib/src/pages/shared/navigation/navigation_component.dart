import 'package:angular/angular.dart';
import 'dart:html';
import 'package:angular_router/angular_router.dart';

import '../../main/routes.dart';
import '../../main/route_paths.dart';
import '../../../routes.dart' as top_routes;

import '../../../service/style_service.dart';
import '../../../pages/profile/profile_image/profile_image_component.dart';
import '../../../model/core.dart';

@Component(
  selector: 'navigation',
  templateUrl: 'navigation_component.html',
  directives: const [coreDirectives, routerDirectives, ProfileImageComponent],
  styleUrls: const ['navigation_component.css'],
  providers: const [],
  exports: const [top_routes.RoutePaths]
)
class NavigationComponent {

  final Router _router;
  final StyleService _styleService;
  
  @Input("user")
  PyroUser currentUser;
  
  NavigationComponent(this._router, this._styleService) {
  }
  
  bool get isAdmin => currentUser != null && currentUser.systemRoles.contains(PyroSystemRole.ADMIN);
  
  PyroStyle get style => _styleService.style;
}
