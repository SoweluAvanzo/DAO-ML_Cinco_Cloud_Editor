class ContextMenu {
  int x;
  int y;
  List<ContextMenuEntry> entries;
  
  ContextMenu(this.x, this.y, this.entries) {
  }
}

class ContextMenuEntry {
}

class ContextMenuItem extends ContextMenuEntry {
  String icon;
  String text;
  bool enabled;
  Function onClick;
  List<ContextMenuEntry> subItems;
  
  ContextMenuItem(this.icon, this.text, this.enabled, this.onClick, {List<ContextMenuEntry> subItems}) {
    this.subItems = subItems == null ? new List() : subItems;
  }
}

class ContextMenuSeparator extends ContextMenuEntry {
}

class ContextMenuGroup extends ContextMenuEntry {
  String title;
  
  ContextMenuGroup(this.title) {
  } 
}
