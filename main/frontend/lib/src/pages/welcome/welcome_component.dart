import 'package:angular/angular.dart';
import 'dart:html';
import 'package:angular_router/angular_router.dart';
import '../main/routes.dart';
import '../main/route_paths.dart';

@Component(
  selector: 'welcome',
  styleUrls: const ['welcome_component.css'],
  directives: const [coreDirectives,routerDirectives],
  templateUrl: 'welcome_component.html',
  exports: const [RoutePaths, Routes]
)
class WelcomeComponent implements OnInit {

  ngOnInit() {
    document.title = 'Cinco Cloud | Home';
  }
  
}
