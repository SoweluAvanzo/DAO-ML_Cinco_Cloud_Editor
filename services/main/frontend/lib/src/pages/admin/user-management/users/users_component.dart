import 'package:angular/angular.dart';
import 'dart:html';

import '../../../../model/core.dart';
import '../../../../service/user_service.dart';
import '../../../../service/notification_service.dart';
import '../../../../pipes/normalize_enum_string_pipe.dart';
import '../create_user/create_user_component.dart';

@Component(
  selector: 'users',
  templateUrl: 'users_component.html',
  directives: const [coreDirectives, CreateUserComponent],
  providers: const [],
  pipes: [NormalizeEnumStringPipe]
)
class UsersComponent implements OnInit {

  final UserService _userService;
  final NotificationService _notificationService;
  
  List<User> users = new List();
  bool showCreateUserModal = false;
        
  UsersComponent(this._userService, this._notificationService) {
  }

  @override
  void ngOnInit() {  	
    _userService.findUsers().then((users) {
      this.users = users;
    });
  }

  void handleUserCreated(User user) {
    users.add(user);
    handleModalClose(null);
  }

  void handleModalClose(dynamic e) {
    showCreateUserModal = false;
  }
  
  void deleteUser(User user) {
  	_userService.deleteUser(user).then((_) {
  		users.removeWhere((u) => u.id == user.id);
  		_notificationService.displayMessage("User ${user.username} has been deleted.", NotificationType.SUCCESS);
  	});
  }
}
