import 'package:angular/angular.dart';
import 'dart:html';

import '../../../../model/core.dart';
import '../../../../service/user_service.dart';
import '../../../../service/notification_service.dart';
import '../search_user/search_user_component.dart';

@Component(
  selector: 'organization-managers',
  templateUrl: 'organization_managers_component.html',
  directives: const [coreDirectives, SearchUserComponent],
  providers: const [ClassProvider(UserService)],
)
class OrganizationManagersComponent implements OnInit {

  final UserService _userService;
  final NotificationService _notificationService;
  
  List<PyroUser> managers = new List();
  bool showFindUserModal = false;
        
  OrganizationManagersComponent(this._userService, this._notificationService) {
  }

  @override
  void ngOnInit() {  	
	_userService.findUsers().then((users) {
		managers = users.where((u) => u.systemRoles.contains(PyroSystemRole.ORGANIZATION_MANAGER)).toList();
	});  		
  }   
  
  void addOrgManagerRole(dynamic e) {
  	if (e is PyroUser) {
  		_userService.addOrgManagerRole(e).then((u){
  			managers.add(u);
  		}).catchError((err) {
  			window.console.log(err);
  		});
  	}
  	showFindUserModal=false;
  }
  
  void removeOrgManagerRole(PyroUser user) {
  	_userService.removeOrgManagerRole(user)
  		.then((u){
  			_notificationService.displayMessage("User ${user.username} is no longer an organization manager", NotificationType.SUCCESS);
  			managers.removeWhere((a) => a.username == user.username);
  		})
  		.catchError((err){
  			window.console.log(err);
		});
  }
  
}
