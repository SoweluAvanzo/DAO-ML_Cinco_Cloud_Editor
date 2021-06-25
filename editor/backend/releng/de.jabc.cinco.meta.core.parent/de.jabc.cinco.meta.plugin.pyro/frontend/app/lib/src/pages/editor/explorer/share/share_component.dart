import 'package:angular/angular.dart';
import '../../../../model/core.dart';
import 'dart:async';
import 'dart:html';

@Component(
    selector: 'share-entry',
    templateUrl: 'share_component.html',
    directives: const [coreDirectives]
)
class ShareComponent implements OnInit {
  
  final shareSC = new StreamController();
  @Output('share') Stream get onShare => shareSC.stream;

  ElementRef _el;
  dynamic _parentEl;
  
  GraphModel g;
  bool show = false;
  
  ShareComponent(this._el) {
  }
  
  @override
  void ngOnInit() {
    _parentEl = _el.nativeElement.parent;
  }
  
  void share() {
    shareSC.add(null);
    close();
  }
  
  void open(GraphModel g) {
    show = true;
    this.g = g;
    window.document.querySelector('body').children.add(_el.nativeElement);
  }
  
  void close() {
    show = false;
    window.document.querySelector('body').children.remove(_el.nativeElement);
    _parentEl.children.add(_el.nativeElement);
  }
}
