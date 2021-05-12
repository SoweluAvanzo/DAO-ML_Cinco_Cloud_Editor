import 'package:angular/core.dart';
import 'package:angular_router/angular_router.dart';
import 'src/routes.dart';
import 'src/service/user_service.dart';
import 'src/service/style_service.dart';
import 'src/service/notification_service.dart';
import 'src/service/settings_service.dart';
import 'src/service/organization_service.dart';
import 'src/service/workspace_image_service.dart';
import 'src/components/notification/notification_component.dart';

@Component(
  selector: 'pyro-app',
  templateUrl: 'app_component.html',
  directives: const [routerDirectives, NotificationComponent],
  exports: [RoutePaths, Routes],
  providers: const [
    ClassProvider(UserService), 
    ClassProvider(StyleService),
    ClassProvider(NotificationService),
    ClassProvider(SettingsService),
    ClassProvider(OrganizationService),
    ClassProvider(WorkspaceImageService),
  ]
)
class AppComponent {
	final SettingsService _settingsService;
	final StyleService _styleService;
	
	AppComponent(this._settingsService, this._styleService) {
		_settingsService.get().then((s){
			_styleService.update(s.style);
		});
	}
}

@Component(
    selector: 'not-found',
    template: '''
		<h1 class="form-signin-heading" style="margin:50px;" >:(</h1>
		<div class="alert alert-danger" style="margin:50px;">
		    Sry, there is nothing to see here.
		</div>
    '''
)
class NotFoundComponent {}
