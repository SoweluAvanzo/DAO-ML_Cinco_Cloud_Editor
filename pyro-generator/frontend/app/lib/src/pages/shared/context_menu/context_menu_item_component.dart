import 'dart:html';

import 'package:angular/angular.dart';

import '../../../service/context_menu_service.dart';
import './context_menu.dart';

@Component(
  selector: 'context-menu-item',
  templateUrl: 'context_menu_item_component.html',
  directives: const [coreDirectives, ContextMenuItemComponent],
  styleUrls: const ['./context_menu_component.css'],
)
class ContextMenuItemComponent {

  final ContextMenuService _contextMenuService;
  
  @Input()
  ContextMenuItem item;
  
  @ViewChild('subMenu')
  ElementRef subMenuEl;
  
  @ViewChild('menuItem')
  ElementRef menuItemEl;
  
  bool showSubMenu = false;
  
  ContextMenuItemComponent(this._contextMenuService) {
  }
  
  handleMouseenter() {
  	showSubMenu = true;
  }
    
  handleClick() {
  	// prevent click action on items that have sub menus
    if (item.onClick != null && item.enabled && item.subItems.length == 0) {
      item.onClick();
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
  
  int get offsetRight {  	
  	int subMenuWidth = subMenuEl.nativeElement.clientWidth;
  	bool openRight = menuItemEl.nativeElement.clientWidth + menuItemEl.nativeElement.getBoundingClientRect().x + subMenuWidth <= window.innerWidth;
    return openRight ? -1 * subMenuWidth : subMenuWidth;
  }
 }