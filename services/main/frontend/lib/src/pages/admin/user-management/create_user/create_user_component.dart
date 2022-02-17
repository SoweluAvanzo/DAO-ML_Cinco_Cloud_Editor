import 'package:angular/angular.dart';
import 'dart:async';
import 'dart:html';
import 'package:angular_forms/angular_forms.dart';

import '../../../../model/core.dart';
import '../../../../service/user_service.dart';
import '../../../../service/notification_service.dart';

@Component(
    selector: 'create-user',
    directives: const [coreDirectives, formDirectives],
    providers: const [],
    templateUrl: 'create_user_component.html'
)
class CreateUserComponent {

  final closeSC = new StreamController();
  @Output() Stream get close => closeSC.stream;

  final createdSC = new StreamController();
  @Output() Stream get created => createdSC.stream;
  
  final UserService _userService;
  final NotificationService _notificationService;
  bool error = false;

  CreateUserComponent(this._userService, this._notificationService) {
  }

  ControlGroup form = FormBuilder.controlGroup({
    'name': Control<String>('', Validators.required),
    'username': Control<String>('', Validators.required),
    'email': Control<String>('', Validators.required),
    'password': Control<String>('', Validators.compose([Validators.required, Validators.minLength(5)]))
  });

  void createUser() {
    error = false;

    var data = form.value;
    data['passwordConfirm'] = data['password'];

    _userService.createUser(data).then((user) {
      _notificationService.displayMessage("User ${user.username} has been created.", NotificationType.SUCCESS);
      createdSC.add(user);
    }).catchError((err) {
      error = true;
    });
  }

  void closeModal(dynamic e) {
    e.preventDefault();
    closeSC.add(null);
  }
}

