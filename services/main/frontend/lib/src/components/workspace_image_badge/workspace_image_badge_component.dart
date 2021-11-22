import 'package:angular/angular.dart';
import '../../model/core.dart';

@Component(
  selector: 'workspace-image-badge',
  templateUrl: 'workspace_image_badge_component.html',
  styleUrls: const ['workspace_image_badge_component.css'],
  directives: const [coreDirectives]
)
class WorkspaceImageBadgeComponent {

  @Input()
  WorkspaceImage image;

  String get name => image.imageName.split(':')[0];
}
