import 'dart:convert';
import 'dart:html';
import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';

import '../../routes.dart' as top_routes;
import '../../model/core.dart';
import '../../service/organization_service.dart';
import '../../service/user_service.dart';
import '../../service/notification_service.dart';
import '../../service/settings_service.dart';
import '../shared/navigation/navigation_component.dart';
import './new_organization/new_organization_component.dart';
import './edit_organization/edit_organization_component.dart';

@Component(
  selector: 'organizations',
  templateUrl: 'organizations_component.html',
  directives: const [coreDirectives, routerDirectives, NavigationComponent, NewOrganizationComponent, EditOrganizationComponent],
  styleUrls: const ['organizations_component.css'],
  providers: const [ClassProvider(UserService), ClassProvider(OrganizationService)],
  exports: const [top_routes.RoutePaths]
)
class OrganizationsComponent implements OnInit {

  User currentUser;
  Settings settings;
  List<Organization> organizations = new List();
  
  final OrganizationService _organizationService;
  final UserService _userService;
  final NotificationService _notificationService;
  final SettingsService _settingsService;
  
  @ViewChild(NewOrganizationComponent)
  NewOrganizationComponent newOrganizationModal;
  
  @ViewChild(EditOrganizationComponent)
  EditOrganizationComponent editOrganizationModal;

  OrganizationsComponent(this._organizationService, this._userService, this._settingsService, this._notificationService) {
  }

  @override
  void ngOnInit() {    
  	document.title = "Cinco Cloud | Organizations";
  
  	_userService.loadUser().then((u){
  		currentUser = u;
  		return _organizationService.getAll().then((orgs){
	  		organizations = orgs;
	  	});
  	});
  	
  	_settingsService.get().then((s){
  		settings = s;
  	});
  }
  
  void addOrganization(dynamic e) {
  	if (e is Organization) {
  	  _notificationService.displayMessage("Organization ${e.name} has been created.", NotificationType.SUCCESS);
  	  newOrganizationModal.close();
  	  organizations.add(e);
  	}
  }
  
  void updateOrganization(dynamic e) {
  	if (e is Organization) {
  		_notificationService.displayMessage("Organization ${e.name} has been updated.", NotificationType.SUCCESS);
  		editOrganizationModal.close();
  		int i = organizations.indexWhere((o) => o.id == e.id);
  		if (i > -1) {
  			organizations[i] = e;
  		}
  	}
  }
  
  void deleteOrganization(Organization org) {
  	_organizationService.delete(org)
  		.then((_){
  			_notificationService.displayMessage("Organization ${org.name} has been deleted.", NotificationType.SUCCESS);
  			organizations.removeWhere((o) => o.id == org.id);
  		}).catchError((_) {
    	  _notificationService.displayMessage("Could not delete organization.", NotificationType.DANGER);
  		});
  }
  
  void leaveOrganization(Organization org) {
  	_organizationService.leave(org)
  	  	.then((_){
  			_notificationService.displayMessage("You have left the organization ${org.name}.", NotificationType.SUCCESS);
  			organizations.removeWhere((o) => o.id == org.id);
  		}).catchError((_) {
    	  _notificationService.displayMessage("Could not leave organization.", NotificationType.DANGER);
  		});
  }
  
  String getProjectsUrl(Organization org) {
  	return top_routes.RoutePaths.organization.toUrl(parameters: {"orgId": org.id.toString()});
  }
}
