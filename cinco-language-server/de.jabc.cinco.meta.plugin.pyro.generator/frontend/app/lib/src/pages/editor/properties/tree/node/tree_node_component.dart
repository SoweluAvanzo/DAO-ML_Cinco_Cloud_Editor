
import 'package:angular/angular.dart';
import 'dart:async';

import '../../../../../model/tree_view.dart';
import '../../../../../service/context_menu_service.dart';
import '../../../../../pages/shared/context_menu/context_menu.dart';


@Component(
    selector: 'tree-node',
    templateUrl: 'tree_node_component.html',
    directives: const [coreDirectives,TreeNodeComponent]
)
class TreeNodeComponent {

  final hasNewSC = new StreamController();
  @Output() Stream get hasNew => hasNewSC.stream;
  
  final hasDeletedSC = new StreamController();
  @Output() Stream get hasDeleted => hasDeletedSC.stream;
  
  final hasSelectedSC = new StreamController();
  @Output() Stream get hasSelected => hasSelectedSC.stream;
  
  @ViewChild('treenodeEl')
  ElementRef treeNodeEl;
  
  final ContextMenuService _contextMenuService;

  @Input()
  TreeNode currentNode;
  
  @Input()
  bool isModal = false;

  bool open;


  TreeNodeComponent(this._contextMenuService) {
    open = false;
  }
  
  showContextMenu(dynamic e) {
    e.preventDefault();
    
    List<ContextMenuItem> items = new List();
    currentNode.getPossibleChildren().forEach((n){
    	items.add(ContextMenuItem('fa-plus', "New ${n}", true, () {
	        createChildren(n);
	    }));
    });
    
    if(currentNode.canRemove()) {
    	items.add(ContextMenuItem('fa-trash', "Delete", true, () {
	        delete();
	    }));
    }
    
    if(items.isEmpty) {
    	return;
    }
    
    ContextMenu menu = ContextMenu(e.client.x, e.client.y, items);
    
    _contextMenuService.show(menu);
  }

  String getStatusSign()
  {
    if(currentNode.children.isEmpty){
      return "fas fa-ellipsis-v";
    }
    if(open){
      return "fas fa-chevron-down";
    }
    return "fas fa-chevron-right";
  }

  void clickEntry(dynamic e)
  {
    e.preventDefault();
    if(currentNode.isSelectable()){
      hasSelectedSC.add(currentNode);
    }

  }

  void createChildren(String name)
  {
    currentNode.createChildren(name);
    hasNewSC.add(currentNode);
  }

  void delete()
  {
    if(currentNode.parent!=null){
      currentNode.parent.removeChild(currentNode);
    }
    hasDeletedSC.add(currentNode);
  }

  void removeChild(TreeNode node)
  {
    //currentNode.removeChild(node);
    hasDeletedSC.add(node);
  }

  void clickOpen(dynamic e)
  {
    e.preventDefault();
    open = !open;
  }


}
