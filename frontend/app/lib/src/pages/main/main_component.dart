import 'package:angular/angular.dart';
import 'dart:html';
import 'package:angular_router/angular_router.dart';

import 'routes.dart';

@Component(
  selector: 'main-component',
  styleUrls: const ['main_component.css'],
  exports: [RoutePaths, Routes],
  directives: const [routerDirectives,coreDirectives],
  templateUrl: 'main_component.html',
)
class MainComponent {

  bool isActive(String s) => document.title.endsWith(s);
  
}
