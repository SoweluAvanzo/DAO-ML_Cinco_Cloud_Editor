import 'package:angular_router/angular_router.dart';

import 'route_paths.dart';
import '../login/login_component.template.dart' as ng;
import '../registration/registration_component.template.dart' as ng;
import '../welcome/welcome_component.template.dart' as ng;

export 'route_paths.dart';

class Routes {
  static final login = RouteDefinition(
    routePath: RoutePaths.login,
    component: ng.LoginComponentNgFactory,
  );
  
  static final registration = RouteDefinition(
    routePath: RoutePaths.registration,
    component: ng.RegistrationComponentNgFactory,
  );
  
  static final welcome = RouteDefinition(
    routePath: RoutePaths.welcome,
    component: ng.WelcomeComponentNgFactory,
    useAsDefault: true
  );
  
  static final all = <RouteDefinition>[
	  login,
	  registration,
	  welcome,
  ];
}
