import 'package:angular/angular.dart';
import 'dart:async';
import 'dart:html' as html;
import 'dart:convert' as convert;
import '../model/core.dart';
import '../service/base_service.dart';
import 'package:angular_router/angular_router.dart';

@Component(
    selector: 'tree-view',
    styleUrls: const ['tree_view.css'],
    template:'''
<div class="card pyro-panel">
    <div class="card-header pyro-panel-heading">
        <strong>{{name}}</strong> <a href style="color: white;" (click)="load(\$event)"><i class="fas fa-sync-alt"></i></a>
    </div>
    <div class="card-body pyro-panel-body" style="max-width: inherit;
    overflow-x: scroll;">
      <ul *ngIf="treeView!=null" style="LIST-STYLE-TYPE: none;">
          <li *ngFor="let entry of treeView.layer" style="white-space: nowrap;">
              <tree-view-node
                  [node]="entry"
                  (click)="fireClick(\$event)"
                  (doubleClick)="fireDoubleClick(\$event)"
              ></tree-view-node>
          </li>
      </ul>
    </div>
</div>
    ''',
    directives: const [coreDirectives,TreeViewNodeComponent]
)
class TreeViewComponent implements OnInit {

  @Input()
  String fetchUrl;

  @Input()
  String clickUrl;

  @Input()
  String dbClickUrl;

  @Input()
  PyroUser user;

  @Input()
  GraphModel currentGraphModel;

  @Input()
  String name;

  @ContentChildren(TreeViewNodeComponent)
  List<TreeViewNodeComponent> layer;

  TreeView treeView = null;
  
  final Router _router;
  BaseService _baseService;
  
  TreeViewComponent(this._router) {
  	_baseService = new BaseService(_router);
  }

  @override
  void ngOnInit()
  {
    load(null);
  }

  void load(e) {
    e?.preventDefault();
    html.HttpRequest.request(
    	"${_baseService.getBaseUrl()}/${fetchUrl}",
    	method: "GET",
        requestHeaders: _baseService.requestHeaders,
        withCredentials: true
    ).then((response){
      treeView = new TreeView.fromJSOG(convert.jsonDecode(response.responseText));
    });
  }

  void fireClick(TreeViewNode e) {
    if(clickUrl.isNotEmpty){
      html.HttpRequest.request(
      	"${_baseService.getBaseUrl()}/${clickUrl}",
      	sendData:convert.jsonEncode(e.toJSOG()),
      	method: "POST",
        requestHeaders: _baseService.requestHeaders,
        withCredentials: true
      ).then((response){
        print("[PYRO] tree view send click");
      });
    }
  }

  void fireDoubleClick(TreeViewNode e) {
    if(clickUrl.isNotEmpty) {
      html.HttpRequest.request(
      	"${_baseService.getBaseUrl()}/${dbClickUrl}",
      	sendData: convert.jsonEncode(e.toJSOG()),
      	method: "POST",	
        requestHeaders: _baseService.requestHeaders,
        withCredentials: true
      ).then((response) {
        print("[PYRO] tree view send db click");
      });
    }
  }
}


@Component(
    selector: 'tree-view-node',
    template:'''
<img *ngIf="hasIcon()" [src]="node.iconpath" style="width: 16px;height: 16px;margin-right: 5px;">
<i
        *ngIf="node.children.isNotEmpty"
        (click)="openFolder(\$event)"
        [ngClass]="getFolderClass()"
></i> <a
  href
  (dblclick)="fireDoubleClick(\$event)"
  (click)="fireClick(\$event)"
  style="color: white;"
  [attr.draggable]="getDraggable()"
  ondragstart="start_drag_element(event)"
  [attr.data-typename]="node.type"
  [attr.data-elementid]="node.id"
  >{{node.label}}</a>
<ul [hidden]="!node.isOpen" style="LIST-STYLE-TYPE: none;">
    <li *ngFor="let entry of node.children" style="white-space: nowrap;">
        <tree-view-node
            [node]="entry"
            (click)="clickSC.add(\$event)"
            (doubleClick)="doubleClickSC.add(\$event)"
        ></tree-view-node>
    </li>
</ul>
    ''',
    directives: const [coreDirectives,TreeViewNodeComponent]
)
class TreeViewNodeComponent {


  final clickSC = new StreamController();
  @Output() Stream get click => clickSC.stream;

  final doubleClickSC = new StreamController();
  @Output() Stream get doubleClick => doubleClickSC.stream;

  @Input()
  TreeViewNode node;

  @ContentChildren(TreeViewNodeComponent)
  List<TreeViewNodeComponent> children;


  String getDraggable() => node.isDragable?"true":"false";

  void fireClick(dynamic e) {
    e.preventDefault();
    clickSC.add(node);
  }

  void fireDoubleClick(dynamic e) {
    e.preventDefault();
    doubleClickSC.add(node);
  }

  String getFolderClass()
  {
    if(node.isOpen){
      return "fas fa-chevron-down";
    }
    return "fas fa-chevron-right";
  }

  void openFolder(dynamic e)
  {
    node.isOpen = !node.isOpen;
    e.preventDefault();
  }

  bool hasIcon() => node.iconpath!=null;

}

class TreeView {
  List<TreeViewNode> layer = new List();

  TreeView.fromJSOG(dynamic jsog) {
    Map cache = new Map();
    for(var e in jsog['layer']) {
      layer.add(new TreeViewNode(e,cache));
    }
  }
}

class TreeViewNode {
  String label;
  String iconpath;
  int id = -1;
  String type = null;

  bool isOpen = false;

  bool isClickable = false;
  bool isDoubleClickable = false;
  bool isDragable = true;

  List<TreeViewNode> children = new List();



  TreeViewNode(dynamic jsog,Map cache) {
    if (cache == null) {
      cache = new Map();
    }
    // default constructor
    if (jsog != null) {
      String jsogId = jsog['@id'];
      cache[jsogId] = this;

      this.id = jsog['id'];

      this.label = jsog['label'];
      this.iconpath = jsog['iconpath'];
      this.type = jsog['__type'];

      this.isClickable = jsog['isClickable'];
      this.isDoubleClickable = jsog['isDoubleClickable'];
      this.isDragable = jsog['isDragable'];

      for(var e in jsog['children']) {
        children.add(new TreeViewNode(e,cache));
      }
    }
  }

  Map toJSOG() {
    Map map = new Map();
    map['@id'] = this.id;
    map['id'] = this.id;

    map['label'] = this.label;
    map['iconpath'] = this.iconpath;
    map['__type'] = this.type;

    map['isClickable'] = this.isClickable;
    map['isDoubleClickable'] = this.isDoubleClickable;
    map['isDragable'] = this.isDragable;

    return map;

  }

}
