import 'package:angular/angular.dart';
import 'dart:async';
import 'dart:html';
import 'package:angular_forms/angular_forms.dart';

import '../../../../model/core.dart';
import '../../../../service/user_service.dart';

@Component(
    selector: 'search-user',
    directives: const [coreDirectives, formDirectives],
    providers: const [ClassProvider(UserService)],
    templateUrl: 'search_user_component.html'
)
class SearchUserComponent {

  final closeSC = new StreamController();
  @Output() Stream get close => closeSC.stream;
  
  final UserService _userService;

  bool searching = false;
  bool notFound = false;
  
  User result;

  SearchUserComponent(this._userService){
  }

  void submitSearchUser(String nameOrEmail, dynamic e) {
  	e.preventDefault();
  	result = null;
    notFound=false;
    _userService.searchUser(nameOrEmail).then((n){
      notFound=false;
      result = n;
    }).catchError((e){
      notFound=true;
    });
  }
  
  void selectUser() {
    closeSC.add(result);
    notFound = false;
    result = null;
  }

}

