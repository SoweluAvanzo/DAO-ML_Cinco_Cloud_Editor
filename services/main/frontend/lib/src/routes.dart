import 'package:angular_router/angular_router.dart';

import './route_paths.dart';
import './pages/main/main_component.template.dart' as ng;
import './pages/admin/admin_component.template.dart' as ng;
import './pages/logout/logout_component.template.dart' as ng;
import './pages/organizations/organizations_component.template.dart' as ng;
import './pages/organization/organization_component.template.dart' as ng;
import './pages/project/project_component.template.dart' as ng;
import './pages/project-build-jobs/project_build_jobs_component.template.dart' as ng;
import './pages/project-members/project_members_component.template.dart' as ng;
import './pages/profile/profile_component.template.dart' as ng;
import './pages/overview/overview_component.template.dart' as ng;

export 'route_paths.dart';

class Routes {
  static final home = RouteDefinition(
    routePath: RoutePaths.home,
    component: ng.MainComponentNgFactory,
  );

  static final admin = RouteDefinition(
    routePath: RoutePaths.admin,
    component: ng.AdminComponentNgFactory,
  );
  
  static final logout = RouteDefinition(
    routePath: RoutePaths.logout,
    component: ng.LogoutComponentNgFactory
  );

  static final overview = RouteDefinition(
      routePath: RoutePaths.overview,
      component: ng.OverviewComponentNgFactory
  );
  
  static final organizations = RouteDefinition(
    routePath: RoutePaths.organizations,
    component: ng.OrganizationsComponentNgFactory
  );
  
  static final organization = RouteDefinition(
    routePath: RoutePaths.organization,
    component: ng.OrganizationComponentNgFactory
  );

  static final project = RouteDefinition(
      routePath: RoutePaths.project,
      component: ng.ProjectComponentNgFactory
  );

  static final projectBuildJobs = RouteDefinition(
      routePath: RoutePaths.projectBuildJobs,
      component: ng.ProjectBuildJobsComponentNgFactory
  );

  static final projectMembers = RouteDefinition(
      routePath: RoutePaths.projectMembers,
      component: ng.ProjectMembersComponentNgFactory
  );

  static final profile = RouteDefinition(
    routePath: RoutePaths.profile,
    component: ng.ProfileComponentNgFactory
  );
  
  static final all = <RouteDefinition>[
	  RouteDefinition.redirect(
	    path: '/',
	    redirectTo: RoutePaths.home.toUrl(),
	  ),
	  home,
	  admin,
	  logout,
    overview,
	  organizations,
	  organization,
	  profile,
    project,
    projectBuildJobs,
    projectMembers
  ];
}
