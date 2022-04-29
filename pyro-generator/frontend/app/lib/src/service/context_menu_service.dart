import 'dart:async';

import '../pages/shared/context_menu/context_menu.dart';

class ContextMenuService {
  
  final onMenuSC = new StreamController.broadcast();  
  Stream get onMenu => onMenuSC.stream;
  
  show(ContextMenu menu) {
    onMenuSC.add(menu);
  }
  
  hide() {
    onMenuSC.add(null);
  }
}
