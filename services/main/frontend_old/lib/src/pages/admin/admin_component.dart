import 'package:angular/angular.dart';
import 'dart:html';
import 'package:angular_router/angular_router.dart';

import '../main/routes.dart';
import '../main/route_paths.dart';
import '../../routes.dart' as top_routes;
import './routes.dart' as admin_routes;
import './route_paths.dart' as admin_routes;

import '../../model/core.dart';
import '../shared/navigation/navigation_component.dart';
import '../../service/user_service.dart';

@Component(
  selector: 'admin',
  templateUrl: 'admin_component.html',
  directives: const [coreDirectives, NavigationComponent, routerDirectives],
  providers: const [ClassProvider(UserService)],
  styleUrls: const ['admin_component.css'],
  exports: [admin_routes.RoutePaths, admin_routes.Routes],
)
class AdminComponent implements OnInit {

  final UserService _userService;
  final Router _router;
 
  User currentUser;
  
  AdminComponent(this._userService, this._router) {
  }

  @override
  void ngOnInit() {  	
    _userService.fetchUser().then((user) {
  	  if (!user.systemRoles.contains(UserSystemRole.ADMIN)) {
  	    _router.navigate(top_routes.Routes.organizations.toUrl());
  	  } else {
  	    currentUser = user;
  	  }  		
  	});	
  } 
}
