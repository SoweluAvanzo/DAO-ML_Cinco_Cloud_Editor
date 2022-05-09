import 'package:angular/angular.dart';
import 'dart:async';
import 'dart:html' as html;
import 'dart:convert' as convert;
import '../model/core.dart';
import '../service/base_service.dart';
import '../pages/editor/editor_component.dart';
import 'package:angular_router/angular_router.dart';

@Component(
    selector: 'tree-view',
    styleUrls: const ['tree_view.css'],
    template:'''
		<div class="card pyro-panel" style="height: 100%">
		    <div class="card-header pyro-panel-heading">
		        <strong>{{name}}</strong> <a href style="color: white;" (click)="load(\$event)"><i class="fas fa-sync-alt"></i></a>
		    </div>
		    <div class="card-body pyro-panel-body" style="max-width: inherit;overflow-x: scroll;">
		      <ul *ngIf="treeView!=null" style="list-style-type: none;padding-left: 0;">
		          <li *ngFor="let entry of treeView.layer" style="white-space: nowrap;padding-left: 7;">
		              <tree-view-node
                      [disabled]="isDisabled()"
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
class TreeViewComponent implements OnInit, OnChanges {

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

  @Input()
  EditorComponent parent;
  
  @ViewChildren(TreeViewNodeComponent)
  List<TreeViewNodeComponent> layer = new List<TreeViewNodeComponent>();

  TreeView treeView = null;
  
  final Router _router;
  BaseService _baseService;
  
  bool updating = false;

  TreeViewComponent(this._router) {
  	_baseService = new BaseService(_router);
  }

  @override
  void ngOnInit()
  {
    if(parent != null) {
      parent.registerTreeViewComponent(this);
    }
    load(null);
  }

  void disable() {
    updating = true;
    for(TreeViewNodeComponent l in layer) {
      l.disable();
    }
  }

  void enable() {
    updating = false;
    for(TreeViewNodeComponent l in layer) {
      l.enable();
    }
  }

  bool isDisabled() {
    return updating;
  }

  void load(e) {
    e?.preventDefault();
    disable();
    html.HttpRequest.request(
    	"${_baseService.getBaseUrl()}/${fetchUrl}",
    	method: "GET",
        requestHeaders: _baseService.requestHeaders,
        withCredentials: true
    ).then((response){
      TreeView newTreeView = new TreeView.fromJSOG(convert.jsonDecode(response.responseText));
      if(treeView == null) {
        treeView = newTreeView;
      } else {
        treeView.merge(newTreeView);
      }
    }).then((_) {
      enable();
    }).catchError((e) => enable());
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

  @override
  void ngOnChanges(Map<String, SimpleChange> changes) {}
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
		  [attr.data-reference]="true"
		  >{{node.label}}</a>
		<ul [hidden]="!node.isOpen" style="list-style-type: none;padding-left: 0px">
		    <li *ngFor="let entry of node.children" style="white-space: nowrap;padding-left: 7px">
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

  @Input()
  bool disabled = false;

  @ViewChildren(TreeViewNodeComponent)
  List<TreeViewNodeComponent> children = new List<TreeViewNodeComponent>();

  String getDraggable() => (node.isDragable && !isDisabled())? "true" : "false";

  bool isDisabled() {
    return disabled;
  }

  void disable() {
    if(disabled) {
      return;
    }
    this.disabled = true;
    for(TreeViewNodeComponent l in children) {
      l.disable();
    }
  }

  void enable() {
    if(!disabled) {
      return;
    }
    this.disabled = false;
    for(TreeViewNodeComponent l in children) {
      l.enable();
    }
  }

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

class TreeView extends Mergable {
  List<TreeViewNode> layer = new List();

  merge(TreeView t) {
    layer = mergeLayer(t.layer, layer);
  }

  TreeView.fromJSOG(dynamic jsog) {
    Map cache = new Map();
    for(var e in jsog['layer']) {
      layer.add(new TreeViewNode(e,cache));
    }
  }
}

class TreeViewNode extends Mergable {
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

  merge(TreeViewNode n) {
    if(this.id != n.id) {
      return;
    }
    // update properties
    this.label = n.label;
    this.iconpath = n.iconpath;
    this.type = n.type;

    this.isClickable = n.isClickable;
    this.isDoubleClickable = n.isDoubleClickable;
    this.isDragable = n.isDragable;

    this.children = mergeLayer(n.children, this.children);
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

class Mergable {

  mergeLayer(List<TreeViewNode> newLayer, List<TreeViewNode> oldLayer) {
    // identify new children
    List<TreeViewNode> newChildren = new List();
    for(var newChild in newLayer) {
      var existing = false;
      for(var oldChild in oldLayer) {
        if(oldChild.id == newChild.id) {
          existing = true;
          break;
        }
      }
      if(!existing) {
        newChildren.add(newChild);
      }
    }
    if(!oldLayer.isEmpty) {
      // remove old children, that do not exist anymore
      oldLayer.removeWhere((oldChild) =>
        // no child in n exists with the id of the oldChild, thus does not exist anymore
        newLayer.where((newChild) => oldChild.id == newChild.id).isEmpty
      );
    }
    // merge/update present children
    for(var oldNode in oldLayer) {
      var newNode = newLayer.firstWhere((n) => n.id == oldNode.id, orElse: null);
      if(newNode != null) {
        oldNode.merge(newNode);
      }
    }
    // add new children
    oldLayer.addAll(newChildren);
    return oldLayer;
  }
}
