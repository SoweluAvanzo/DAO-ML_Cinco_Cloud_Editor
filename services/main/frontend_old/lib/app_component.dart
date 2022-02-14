import 'package:angular/angular.dart';
import 'package:angular/core.dart';
import 'package:angular_router/angular_router.dart';
import 'src/routes.dart';
import 'src/service/user_service.dart';
import 'src/service/notification_service.dart';
import 'src/service/settings_service.dart';
import 'src/service/organization_service.dart';
import 'src/service/workspace_image_service.dart';
import 'src/service/project_web_socket_factory_service.dart';
import 'src/components/notification/notification_component.dart';

@Component(
  selector: 'pyro-app',
  templateUrl: 'app_component.html',
  directives: const [routerDirectives, NotificationComponent],
  exports: [RoutePaths, Routes],
  providers: const [
    ClassProvider(UserService), 
    ClassProvider(NotificationService),
    ClassProvider(SettingsService),
    ClassProvider(OrganizationService),
    ClassProvider(WorkspaceImageService),
    ClassProvider(ProjectWebSocketFactoryService)
  ]
)
class AppComponent {
}
