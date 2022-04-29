import 'package:angular/core.dart';
import 'package:angular_router/angular_router.dart';
import 'src/routes.dart';
import 'src/service/user_service.dart';
import 'src/service/style_service.dart';
import 'src/service/notification_service.dart';
import 'src/service/settings_service.dart';
import 'src/service/context_menu_service.dart';
import 'src/service/graph_model_permission_vector_service.dart';
import 'src/pages/editor/notification/notification_component.dart';
import 'src/pages/shared/context_menu/context_menu_component.dart';

@Component(
  selector: 'pyro-app',
  templateUrl: 'app_component.html',
  directives: const [routerDirectives, NotificationComponent, ContextMenuComponent],
  exports: [RoutePaths, Routes],
  providers: const [
    ClassProvider(UserService), 
    ClassProvider(StyleService),
    ClassProvider(NotificationService),
    ClassProvider(SettingsService),
    ClassProvider(GraphModelPermissionVectorService),
    ClassProvider(ContextMenuService),
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
