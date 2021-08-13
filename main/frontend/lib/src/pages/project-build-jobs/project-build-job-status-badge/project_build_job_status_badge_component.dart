import 'package:angular/angular.dart';
import 'dart:html';
import '../../../model/core.dart';

@Component(
    selector: 'project-build-job-status-badge',
    templateUrl: 'project_build_job_status_badge_component.html',
    directives: const [
      coreDirectives,
    ]
)
class ProjectBuildJobStatusBadgeComponent {

  @Input()
  PyroWorkspaceImageBuildJob job;
}
