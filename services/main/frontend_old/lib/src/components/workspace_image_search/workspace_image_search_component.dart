import 'dart:async';
import 'package:angular/angular.dart';
import '../../model/core.dart';
import '../../service/workspace_image_service.dart';

@Component(
  selector: 'workspace-image-search',
  templateUrl: 'workspace_image_search_component.html',
  styleUrls: const ['workspace_image_search_component.css'],
  directives: const [coreDirectives],
  providers: const [
    ClassProvider(WorkspaceImageService)
  ],
)
class WorkspaceImageSearchComponent {

  final selectImageSC = new StreamController<WorkspaceImage>();
  @Output() Stream<WorkspaceImage> get selectImage => selectImageSC.stream;

  WorkspaceImageService _workspaceImageService;
  Timer _debounce;

  List<WorkspaceImage> results = List();

  WorkspaceImageSearchComponent(WorkspaceImageService this._workspaceImageService) {
  }

  void handleInput(String value) {
    if (_debounce?.isActive ?? false) _debounce.cancel();
    if (value.trim() != "") {
      _debounce = Timer(const Duration(milliseconds: 500), () {
        _workspaceImageService.search(value.trim()).then((r) {
          results = r;
        });
      });
    } else {
      results.clear();
    }
  }

  void handleSelect(WorkspaceImage image) {
    selectImageSC.add(image);
    results.clear();
  }

  String getDisplayName(String imageName) {
    return '@' + imageName.split(':')[0];
  }
}
