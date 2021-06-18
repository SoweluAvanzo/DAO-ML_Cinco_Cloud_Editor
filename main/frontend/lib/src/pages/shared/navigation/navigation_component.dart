import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';

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

  final StyleService _styleService;
  
  @Input("user")
  PyroUser currentUser;

  @Input("organization")
  PyroOrganization currentOrganization;
  
  NavigationComponent(this._styleService) {
  }
  
  bool get isAdmin => currentUser != null && currentUser.systemRoles.contains(PyroSystemRole.ADMIN);

  String get organizationUrl => top_routes.RoutePaths.organization.toUrl(parameters: {'orgId': currentOrganization.id.toString()});
  
  PyroStyle get style => _styleService.style;
}
