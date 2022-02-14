import 'dart:html';

import 'package:CincoCloud/src/model/core.dart';
import 'package:CincoCloud/src/service/settings_service.dart';
import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';

import 'routes.dart';

@Component(
  selector: 'main-component',
  styleUrls: const ['main_component.css'],
  exports: [RoutePaths, Routes],
  directives: const [routerDirectives, coreDirectives],
  templateUrl: 'main_component.html',
)
class MainComponent implements OnInit {
  final SettingsService _settingsService;

  Settings settings = null;

  MainComponent(this._settingsService) {}

  @override
  void ngOnInit() {
    _settingsService.get().then((s) => settings = s);
  }

  bool isActive(String s) => document.title.endsWith(s);

  bool get showRegistrationLink {
    return settings != null && settings.allowPublicUserRegistration;
  }
}
