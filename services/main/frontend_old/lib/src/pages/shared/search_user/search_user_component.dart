import 'package:angular/angular.dart';
import 'dart:async';

import '../../../service/user_service.dart';
import '../../../model/core.dart';

@Component(
  selector: 'search-user',
  templateUrl: 'search_user_component.html',
  directives: const [coreDirectives],
  styleUrls: const ['search_user_component.css'],
  providers: const [ClassProvider(UserService)],
)
class SearchUserComponent {

  final UserService _userService;
  
  @Input("user")
  User currentUser;
  
  final userSelectedSC = new StreamController();
  @Output() Stream get userSelected => userSelectedSC.stream;
  
  List<User> result = new List();
  User selectedUser;
  bool found;
  
  SearchUserComponent(this._userService) {
  }
  
  void searchAsAdmin(String emailOrUsername, dynamic e) {
  	e.preventDefault(); 
  	reset();
  	_userService.searchUser(emailOrUsername)
  	  .then((user){
  	    found = true;
  		result.add(user);	
  	  })
  	  .catchError((err){
  	    found = false;
  	  });
  }
  
  void reset() {
  	found = null;
  	result.clear();
  }
  
  void selectUser(User user) {
  	selectedUser = selectedUser != null && user.id == selectedUser.id ? null : user;
    userSelectedSC.add(selectedUser);    
  }
  
  void handleEmailOrUsernameChange(String value) {
  	if (value == null || value.trim() == "") {
  		reset();
  	}
  }
  
  void handleEmailAndUsernameChange(String email, String username) {
  	if ((email == null || email.trim() == "") && (username == null || username.trim() == "")) {
  		reset();
  	}
  }
  
}
