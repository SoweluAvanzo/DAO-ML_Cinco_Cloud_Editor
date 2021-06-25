import '../../../../model/message.dart';
import 'package:angular/angular.dart';
import 'package:angular/security.dart';
import 'dart:html';
import 'dart:js' as js;

import 'dart:async';
@Component(
    selector: 'display-dialog',
    templateUrl: 'display_dialog.html',
    directives: const [coreDirectives]
)
class DisplayDialog implements OnInit {

  @Input()
  DisplayMessages messages;
  
  final closeSC = new StreamController();
  @Output() Stream get close => closeSC.stream;
  
  final saveSC = new StreamController();
  @Output() Stream get save => saveSC.stream;
  
  final downloadSC = new StreamController();
  @Output() Stream get download => downloadSC.stream;

  ElementRef _el;
  dynamic _parentEl;
  
  final DomSanitizationService domSanitizationService;
  
   @ViewChild('contentContainer') HtmlElement cc;

  DisplayDialog(this.domSanitizationService,this._el)
  {

  }
  
  void ngOnInit() {
  	_parentEl = _el.nativeElement.parent;
  	window.document.querySelector('body').children.add(_el.nativeElement);
  }
  
  trustedHTML(String content) {
  	return domSanitizationService.bypassSecurityTrustHtml(content);
  }
  
  trustedURL(String url) {
  	return domSanitizationService.bypassSecurityTrustUrl(url);
  }
  
  void closeEL() {
  	window.document.querySelector('body').children.remove(_el.nativeElement);
    _parentEl.children.add(_el.nativeElement);
  	
  }
  
  void clickSave(){
  	var content = "<html><body>${cc.innerHtml}</body></html>";
  	closeEL();
    saveSC.add(content);
  }
  
  void clickDownload(){
  	js.context.callMethod('downloadContent',["result.html","<html><body>${cc.innerHtml}</body></html>"]);
  	closeEL();
    downloadSC.add({});
  }
  
  void clickClose(){
  	closeEL();
    closeSC.add({});
  }

}

