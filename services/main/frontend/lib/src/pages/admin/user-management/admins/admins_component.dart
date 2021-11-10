import 'package:angular/angular.dart';
import 'dart:html';

import '../../../../model/core.dart';
import '../../../../service/user_service.dart';
import '../../../../service/notification_service.dart';
import '../search_user/search_user_component.dart';

@Component(
  selector: 'admins',
  templateUrl: 'admins_component.html',
  directives: const [coreDirectives, SearchUserComponent],
  providers: const [ClassProvider(UserService)],
)
class AdminsComponent implements OnInit {

  final UserService _userService;
  final NotificationService _notificationService;
  
  List<PyroUser> admins = new List();
    
  bool showFindUserModal = false;
    
  AdminsComponent(this._userService, this._notificationService) {
  }

  @override
  void ngOnInit() {  	
	_userService.findUsers().then((users) {
		admins = users.where((u) => u.systemRoles.contains(PyroSystemRole.ADMIN)).toList();
	});
  } 
    
  void removeAdminRole(PyroUser user) {
  	_userService.removeAdminRole(user)
  		.then((u){
  			_notificationService.displayMessage("User ${user.username} is no longer an admin", NotificationType.SUCCESS);
  			admins.removeWhere((a) => a.username == user.username);
  		})
  		.catchError((e){
  			window.console.error(e);
		});
  }
  
  void addAdminRole(dynamic e) {
    if (e is PyroUser) {
      _userService.addAdminRole(e).then((n){
        _notificationService.displayMessage("User ${e.username} is now an admin", NotificationType.SUCCESS);
        if (admins.indexWhere((a) => a.username == e.username) == -1) {
      	  admins.add(e);
        } else {
      	  _notificationService.displayMessage("User ${e.username} already is an admin", NotificationType.INFO);
        }
      }).catchError((e){
        window.console.log(e);
      });
    }
  	showFindUserModal=false;
  }
  
}
