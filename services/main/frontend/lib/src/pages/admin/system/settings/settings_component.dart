import 'dart:html';
import 'package:angular/angular.dart';

import '../../../shared/toggle_button/toggle_button_component.dart';
import '../../../../model/core.dart';
import '../../../../service/user_service.dart';
import '../../../../service/notification_service.dart';
import '../../../../service/settings_service.dart';

@Component(
  selector: 'settings',
  templateUrl: 'settings_component.html',
  directives: const [coreDirectives, ToggleButtonComponent],
  providers: const [],
)
class SettingsComponent implements OnInit {

  final UserService _userService;
  final NotificationService _notificationService;
  final SettingsService _settingsService;
 
  User currentUser;
  Settings settings;
  
  SettingsComponent(this._userService, this._settingsService, this._notificationService) {
  }

  @override
  void ngOnInit() {  	
    _userService.fetchUser().then((user) {
  	  currentUser = user;
  	  
  	  _settingsService.get().then((s) {
  	  	settings = s;
  	  });
  	});
  } 
  
  void save() {
  	_settingsService.update(settings)
  		.then((s) {
  			settings = s;
  			_notificationService.displayMessage("Settings updated", NotificationType.SUCCESS);
  		})
  		.catchError((err) {
  			_notificationService.displayMessage("Settings could not be updated.", NotificationType.DANGER);
  		});
  }
  
  void handleGloballyCreateOrganizationsChanged(bool e) {
  	settings.globallyCreateOrganizations = e;
  }

  void handleAllowPublicUserRegistrationChanged(bool e) {
    settings.allowPublicUserRegistration = e;
  }
}
