import 'dart:html';
import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../../../model/core.dart';
import '../../../service/organization_service.dart';
import '../../../service/user_service.dart';
import '../../../service/notification_service.dart';
import '../../../service/organization_access_right_vector_service.dart';
import './access_rights_list/access_rights_list_component.dart';

@Component(
  selector: 'access-rights',
  templateUrl: 'access_rights_component.html',
  directives: const [coreDirectives, formDirectives, AccessRightsListComponent],
  providers: const [ClassProvider(OrganizationService), ClassProvider(OrganizationAccessRightVectorService)],
)
class AccessRightsComponent implements OnInit {
  
  @Input("user")
  User currentUser;
  
  @Input()
  Organization organization;
  
  final OrganizationService _organizationService;
  final UserService _userService;
  final NotificationService _notificationService;
  final OrganizationAccessRightVectorService _orgArvService;
  
  String filter = "";
  Map<int, OrganizationAccessRightVector> orgArvsMap = new Map();
    
  AccessRightsComponent(this._organizationService, this._userService, this._notificationService, this._orgArvService) {
  }

  @override
  void ngOnInit() {  
 	_orgArvService.getAll("${organization.id}").then((arvs){
 		arvs.forEach((arv){
 			orgArvsMap[arv.user.id] = arv;
 		});
 	});
  }
  
  void updateArv(dynamic e, int userId) {
  	if (e is OrganizationAccessRightVector) {
  	  orgArvsMap[userId] = e;
  	}
  }
      
  String getRoleBadgeClass(User user) {
  	return organization.owners.contains(user) ? "badge-primary" : "badge-secondary";
  }
  
  String getRoleBadgeText(User user) {
  	return organization.owners.contains(user) ? "Owner" : "Member";
  }
  
  List<User> get users {
  	List<User> users = organization == null ? [] : []..addAll(organization.owners)..addAll(organization.members);
  	if (filter != "") {
  		users = users.where((u) => u.username.contains(filter) || u.email.contains(filter)).toList();
  	}
  	return users;
  }
}
