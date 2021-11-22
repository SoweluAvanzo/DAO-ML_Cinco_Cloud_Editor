import 'dart:async';
import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../../../../model/core.dart';
import '../../../../service/organization_service.dart';
import '../../../shared/search_user/search_user_component.dart';

enum OrganizationRole { OWNER, MEMBER }

@Component(
    selector: 'add-user',
    directives: const [coreDirectives, formDirectives, SearchUserComponent],
    templateUrl: 'add_user_component.html',
    providers: const [ClassProvider(OrganizationService)],
    exports: const [OrganizationRole])
class AddUserComponent {
  final dismissSC = new StreamController();
  @Output()
  Stream get dismiss => dismissSC.stream;

  final addMemberSC = new StreamController();
  @Output()
  Stream get addMember => addMemberSC.stream;

  final addOwnerSC = new StreamController();
  @Output()
  Stream get addOwner => addOwnerSC.stream;

  OrganizationService _organizationService;

  bool show = false;
  User user;
  User currentUser;
  String selectedRole;

  AddUserComponent(this._organizationService) {}

  void addUser() {

  	if (user != null) {
  	  if (selectedRole == OrganizationRole.MEMBER.toString()) {
  	    addMemberSC.add(user);
  	  } else if (selectedRole == OrganizationRole.OWNER.toString()) {
  		addOwnerSC.add(user);
  	  }
  	}
  	user = null;
  }

  selectUser(dynamic e) {
    user = e is User ? e : null;
  }

  void open(User user) {
    currentUser = user;
    user = null;
    selectedRole = OrganizationRole.MEMBER.toString();
    show = true;
  }

  void close() {
    show = false;
  }
}
