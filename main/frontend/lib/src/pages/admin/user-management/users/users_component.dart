import 'package:angular/angular.dart';
import 'dart:html';

import '../../../../model/core.dart';
import '../../../../service/user_service.dart';
import '../../../../service/notification_service.dart';
import '../../../../pipes/normalize_enum_string_pipe.dart';

@Component(
  selector: 'users',
  templateUrl: 'users_component.html',
  directives: const [coreDirectives],
  providers: const [],
  pipes: [NormalizeEnumStringPipe]
)
class UsersComponent implements OnInit {

  final UserService _userService;
  final NotificationService _notificationService;
  
  List<PyroUser> users = new List();
        
  UsersComponent(this._userService, this._notificationService) {
  }

  @override
  void ngOnInit() {  	
	_userService.findUsers().then((users) {
	  this.users = users;
	});  		
  }     
  
  void deleteUser(PyroUser user) {
  	_userService.deleteUser(user).then((_) {
  		users.removeWhere((u) => u.id == user.id);
  		_notificationService.displayMessage("User ${user.username} has been deleted.", NotificationType.SUCCESS);
  	});
  }
}
