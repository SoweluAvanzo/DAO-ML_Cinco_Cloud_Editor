import 'package:angular/angular.dart';
import 'dart:async';
import 'package:angular_forms/angular_forms.dart';

import '../../../model/core.dart';
import '../../../service/user_service.dart';

@Component(
    selector: 'find-user',
    directives: const [coreDirectives, formDirectives],
    templateUrl: 'find_user_component.html')
class FindUserComponent implements OnInit {
  final closeSC = new StreamController();
  @Output()
  Stream get close => closeSC.stream;

  @Input()
  PyroUser user;

  bool searching = false;
  bool notFound = false;
  bool found = false;

  final UserService userService;

  FindUserComponent(UserService this.userService) {}

  @override
  void ngOnInit() {
    searching = false;
  }

  void submitSearchUser(String name, String email) {
    notFound = false;
    found = false;
    userService.findUser(name, email).then((n) {
      user.knownUsers.add(n);
      notFound = false;
      found = true;
    }).catchError((e) {
      notFound = true;
      found = false;
    });
  }
}
