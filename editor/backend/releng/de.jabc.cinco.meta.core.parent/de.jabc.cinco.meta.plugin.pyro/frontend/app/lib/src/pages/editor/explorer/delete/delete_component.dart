import 'package:angular/angular.dart';
import 'dart:async';
import 'dart:html';

@Component(
    selector: 'delete-entry',
    templateUrl: 'delete_component.html',
    directives: const [coreDirectives]
)
class DeleteComponent implements OnInit {
  
  final deleteSC = new StreamController();
  @Output('delete') Stream get onDelete => deleteSC.stream;

  ElementRef _el;
  dynamic _parentEl;
  
  String type;
  String name;
  bool show = false;
  
  DeleteComponent(this._el) {
  }
  
  @override
  void ngOnInit() {
    _parentEl = _el.nativeElement.parent;
  }
  
  void delete() {
    deleteSC.add(null);
    close();
  }
  
  void open(String type, String name) {
    show = true;
    this.type = type;
    this.name = name;
    window.document.querySelector('body').children.add(_el.nativeElement);
  }
  
  void close() {
    show = false;
    window.document.querySelector('body').children.remove(_el.nativeElement);
    _parentEl.children.add(_el.nativeElement);
  }
}
