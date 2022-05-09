import 'package:angular/angular.dart';

import 'package:ng_bootstrap/ng_bootstrap.dart';

import '../../../model/core.dart';
import '../../../pages/profile/profile_image/profile_image_component.dart';

@Component(
    selector: 'active-users',
    templateUrl: 'active_users_component.html',
    directives: const [coreDirectives, bsDropdownDirectives, BsTooltipComponent, ProfileImageComponent],
    providers: const [],
    styleUrls: const ['active_users_component.css']
)
class ActiveUsersComponent {

  final int MAX_DISPLAYED_USERS = 3;

  @Input()
  List<PyroUser> users = new List();

  ActiveUsersComponent() {
  }
  
  List<PyroUser> get topUsers => users.length > MAX_DISPLAYED_USERS ? users.sublist(0, MAX_DISPLAYED_USERS) : users;
  List<PyroUser> get restUsers => users.length < MAX_DISPLAYED_USERS ? [] : users.sublist(MAX_DISPLAYED_USERS, users.length);
}
