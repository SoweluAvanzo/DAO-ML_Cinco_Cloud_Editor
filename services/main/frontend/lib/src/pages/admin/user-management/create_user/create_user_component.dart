import 'package:angular/angular.dart';
import 'dart:async';
import 'dart:html';
import 'package:angular_forms/angular_forms.dart';

import '../../../../model/core.dart';
import '../../../../service/user_service.dart';

@Component(
    selector: 'create-user',
    directives: const [coreDirectives, formDirectives],
    providers: const [ClassProvider(UserService)],
    templateUrl: 'create_user_component.html'
)
class CreateUserComponent {

  final closeSC = new StreamController();
  @Output() Stream get close => closeSC.stream;
  
  final UserService _userService;

  bool notFound = false;
  
  User result;

  CreateUserComponent(this._userService){
  }

  void submitCreateUser(String nameOrEmail, dynamic e) {
  	e.preventDefault();
  	result = null;
    notFound=false;
    _userService.searchUser(nameOrEmail).then((n){
      notFound=false;
      result = n;
    }).catchError((e){
      closeSC.add({'username':nameOrEmail});
    });
  }
  
  void selectUser() {
    closeSC.add(result);
    notFound = false;
    result = null;
  }

}

