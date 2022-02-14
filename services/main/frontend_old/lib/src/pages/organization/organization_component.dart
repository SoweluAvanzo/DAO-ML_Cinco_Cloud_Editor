import 'dart:convert';
import 'dart:html';
import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';

import '../../model/core.dart';
import '../../service/organization_service.dart';
import '../../service/user_service.dart';
import '../../service/notification_service.dart';
import '../shared/navigation/navigation_component.dart';
import '../projects/projects_component.dart';
import './users/users_component.dart';
import './appearance/appearance_component.dart';
import './access_rights/access_rights_component.dart';

@Component(
  selector: 'organization',
  templateUrl: 'organization_component.html',
  styleUrls: const ['organization_component.css'],
  directives: const [coreDirectives, routerDirectives, NavigationComponent, ProjectsComponent, UsersComponent, AppearanceComponent, AccessRightsComponent],
  providers: const [ClassProvider(UserService), ClassProvider(OrganizationService)],
) 
class OrganizationComponent implements OnActivate {

  User currentUser;
  Organization organization;
  
  final Router _router;
  final OrganizationService _organizationService;
  final UserService _userService;
  final NotificationService _notificationService;

  String menuState = "projects";
  
  OrganizationComponent(this._organizationService, this._userService, this._notificationService, this._router) {
  }

  @override
  void onActivate(_, RouterState current) async {
    var orgId = current.parameters['orgId'];
    _userService.fetchUser().then((u){
      currentUser = u;
      
      _organizationService.getById(orgId)
      	.then((org){
      	  organization = org;
      	  document.title = "Cinco Cloud | " + org.name;
      	})
      	.catchError((err) {
      	  window.console.log(err);
      	});
    }).catchError((err){
      window.console.log(err);
    });
  }

  void setMenuState(dynamic e, String state) {
  	e.preventDefault();
  	menuState = state;
  }
  
  bool isMenuState(String state) {
  	return menuState == state;
  }
  
  void orgChanged(Organization organization) {
    this.organization = organization;
  }

  bool get isOrgOwner => organization.owners.indexWhere((u) => u.id == currentUser.id) > -1;
}

