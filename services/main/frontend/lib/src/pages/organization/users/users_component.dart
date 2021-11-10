import 'dart:async';
import 'dart:convert';
import 'dart:html';
import 'package:angular/angular.dart';

import '../../../model/core.dart';
import '../../../service/organization_service.dart';
import '../../../service/user_service.dart';
import '../../../service/notification_service.dart';
import './add_user/add_user_component.dart';

@Component(
  selector: 'users',
  templateUrl: 'users_component.html',
  directives: const [coreDirectives, AddUserComponent],
  providers: const [ClassProvider(UserService), ClassProvider(OrganizationService)],
)
class UsersComponent implements OnInit {
  
  @Input("user")
  PyroUser currentUser;
  
  @Input()
  PyroOrganization organization;
  
  @ViewChild(AddUserComponent)
  AddUserComponent addUserModal;
  
  final orgChangedSC = new StreamController();
  @Output()
  Stream get orgChanged => orgChangedSC.stream;
  
  final OrganizationService _organizationService;
  final UserService _userService;
  final NotificationService _notificationService;
  
  UsersComponent(this._organizationService, this._userService, this._notificationService) {
  }

  @override
  void ngOnInit() {     
  }
  
  void addMember(dynamic e) {
    if (e is PyroUser) {
    	if(organization.members.any((m)=>m.id == e.id) ) {
    		_notificationService.displayMessage("User ${e.username} is already present.", NotificationType.WARNING);
    		addUserModal.close();
    		return;
    	}
      _organizationService.addMember(organization, e)
  	    .then((org){
  	  	  organization.merge(org);
  	  	  _notificationService.displayMessage("User ${e.username} has been added as a member of the organization.", NotificationType.SUCCESS);
  	  	  addUserModal.close();
  	  	  orgChangedSC.add(organization);
  	    })
  	    .catchError((err){
  	      window.console.error(err);
  	    });
    }
  }
  
  void addOwner(dynamic e) {
    if (e is PyroUser) {
    	if(organization.owners.any((m)=>m.id == e.id) ) {
    		_notificationService.displayMessage("User ${e.username} is already present.", NotificationType.WARNING);
    		addUserModal.close();
    		return;
    	}
      _organizationService.addOwner(organization, e)
  	    .then((org){
  	  	  organization.merge(org);
  	  	  _notificationService.displayMessage("User ${e.username} has been added as an owner of the organization.", NotificationType.SUCCESS);
  	  	  addUserModal.close();
  	  	  orgChangedSC.add(organization);
  	    })
  	    .catchError((err){
  	      window.console.error(err);
  	    });
    }
  }
  
  void removeUser(PyroUser user) {
  	_organizationService.removeUser(organization, user)
  	  .then((org){
  	  	organization.merge(org);
  	  	_notificationService.displayMessage("User ${user.username} has been removed from the organization.", NotificationType.SUCCESS);
  	  	addUserModal.close();
        orgChangedSC.add(organization);
  	  })
  	  .catchError((err){
  	    window.console.error(err);
  	  });
  }
  
  bool get isOrgOwner => organization.owners.indexWhere((u) => u.id == currentUser.id) > -1;
}
