import 'package:angular_router/angular_router.dart';

import './route_paths.dart';
import './pages/editor/editor_component.template.dart' as ng;

export 'route_paths.dart';

class Routes {

  
  static final editor = RouteDefinition(
    routePath: RoutePaths.editor,
    component: ng.EditorComponentNgFactory,
  );
  
  
  
  static final all = <RouteDefinition>[
	  RouteDefinition.redirect(
	    path: '/',
	    redirectTo: RoutePaths.editor.toUrl(parameters:{'modelId':'0'}),
	  ),
	  editor
  ];
}
