import 'package:angular/angular.dart';

import '../../../../model/core.dart';
import '../../../../service/user_service.dart';
import '../../../../service/notification_service.dart';
import '../../../../service/settings_service.dart';
import '../../../../service/style_service.dart';
import '../../../../pages/shared/style_form/style_form_component.dart';

@Component(
  selector: 'appearance',
  templateUrl: 'appearance_component.html',
  directives: const [coreDirectives, StyleFormComponent],
  providers: const [],
)
class AppearanceComponent implements OnInit {
  final UserService _userService;
  final NotificationService _notificationService;
  final SettingsService _settingsService;
  final StyleService _styleService;

  PyroUser currentUser;
  PyroSettings settings;
  PyroStyle style;

  AppearanceComponent(this._userService, this._notificationService,
      this._settingsService, this._styleService) {}

  @override
  void ngOnInit() {
    _userService.fetchUser().then((user) {
      currentUser = user;

      _settingsService.get().then((s) {
        settings = s;
        style = settings.style;
      });
    });
  }

  void handleStyleUpdated(dynamic e) {
    if (e is PyroStyle) {
      style = e;
      update();
    }
  }

  void update() {
    _styleService.updateGlobal(style);
  }

  void save() {
    settings.style.navBgColor = style.navBgColor;
    settings.style.navTextColor = style.navTextColor;
    settings.style.bodyBgColor = style.bodyBgColor;
    settings.style.bodyTextColor = style.bodyTextColor;
    settings.style.primaryBgColor = style.primaryBgColor;
    settings.style.primaryTextColor = style.primaryTextColor;
    settings.style.logo = style.logo;

    _settingsService.update(settings).then((updatedSettings) {
      _notificationService.displayMessage(
          "The appearance settings have been updated.",
          NotificationType.SUCCESS);
      settings = updatedSettings;
      style = settings.style;
      update();
    });
  }

  void reset() {
    style = settings.style;
    update();
  }
}
