import 'dart:html';
import 'package:angular/angular.dart';

import './context_menu.dart';
import './context_menu_item_component.dart';
import '../../../service/context_menu_service.dart';

@Component(
  selector: 'context-menu',
  templateUrl: 'context_menu_component.html',
  directives: const [coreDirectives, ContextMenuItemComponent],
  styleUrls: const ['context_menu_component.css'],
)
class ContextMenuComponent implements OnInit {
  
  final ContextMenuService _contextMenuService;
  
  ContextMenu menu;
  
  @ViewChild('menuEl')
  ElementRef menuEl;
  
  ContextMenuComponent(this._contextMenuService) {
  }
  
  @override
  ngOnInit() {
    _contextMenuService.onMenu.listen((m) {
      menu = m;
      
      // remove old listeners
      window.removeEventListener('click', handleWindowClick);
      window.removeEventListener('contextmenu', handleWindowClick);
        
      if (menu != null) {
        window.addEventListener('click', handleWindowClick);
        window.addEventListener('contextmenu', handleWindowClick);
      }
    });
  }
  
  handleWindowClick(dynamic e) {   
    int x = e.client.x;
    int y = e.client.y;
    
    if (menu != null && menuEl != null) {
	    List<Element> menuElements = new List();
	    menuElements.add(menuEl.nativeElement);
	    menuElements.addAll(menuEl.nativeElement.querySelectorAll('.context-sub-menu'));
	    
	    // is click inside menu or sub menus?
	    for (Element el in menuElements) {
	      var rect = el.getBoundingClientRect();
	      bool inMenu = 
	      		x >= rect.left && x <= rect.left + el.clientWidth && 
	            y >= rect.top && y <= rect.top + el.clientHeight;	               
	      if (inMenu) {
	      	e.preventDefault();
	      	return false;
	      }
	    }
      	_contextMenuService.hide();
    }
  }
  
  bool isItem(ContextMenuEntry entry) {
    return entry is ContextMenuItem;
  }
  
  bool isSeparator(ContextMenuEntry entry) {
    return entry is ContextMenuSeparator;
  }
  
  bool isGroup(ContextMenuEntry entry) {
    return entry is ContextMenuGroup;
  }
  
  ContextMenuGroup asGroup(val) { return val; }
}
