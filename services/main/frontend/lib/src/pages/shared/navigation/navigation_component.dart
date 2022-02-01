import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';

import '../../../routes.dart' as top_routes;
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

  @Input("user")
  User currentUser;

  @Input("organization")
  Organization currentOrganization;

  bool get isAdmin => currentUser != null && currentUser.systemRoles.contains(UserSystemRole.ADMIN);

  String get organizationUrl => top_routes.RoutePaths.organization.toUrl(parameters: {'orgId': currentOrganization.id.toString()});
}
