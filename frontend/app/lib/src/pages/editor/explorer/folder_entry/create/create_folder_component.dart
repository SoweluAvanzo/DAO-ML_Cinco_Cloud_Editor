import 'dart:async';
import 'dart:html';
import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../../../../../model/core.dart';
import '../../../../../service/graph_service.dart';

@Component(
    selector: 'create-folder',
    templateUrl: 'create_folder_component.html',
    directives: const [coreDirectives,formDirectives]
)
class CreateFolderComponent implements OnInit {

  PyroFolder folder;
  
  ElementRef _el;
  dynamic _parentEl;
  
  bool show = false;
  bool showError = false;

  final GraphService _graphService;

  CreateFolderComponent(this._graphService, this._el) {
  }
  
  @override
  void ngOnInit() {
    _parentEl = _el.nativeElement.parent;
  }

  void create(String name, dynamic e) {
    showError = false;
    if(e!=null)e.preventDefault();
    if(name !=null && name?.isNotEmpty) {
      if(folder.innerFolders.where((fol)=>fol.name==name).isNotEmpty) {
        showError = true;
        return;
      }
	  PyroFolder pf = new PyroFolder();
	  pf.name = name;
	  _graphService.createFolder(pf,folder)
	  	.then((f)=>close());
    }
  }
  
  void open(PyroFolder parent) {
    show = true;
    folder = parent;
    window.document.querySelector('body').children.add(_el.nativeElement);
  }
  
  void close() {
    show = false;
    folder = null;
    window.document.querySelector('body').children.remove(_el.nativeElement);
    _parentEl.children.add(_el.nativeElement);
  }
}

