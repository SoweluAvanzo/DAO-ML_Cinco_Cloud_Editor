import 'dart:html';
import 'dart:async';

import 'package:angular/angular.dart';

import '../../../../model/core.dart';
import '../../../../service/context_menu_service.dart';
import '../../../../pages/shared/context_menu/context_menu.dart';

@Component(
  selector: 'editor-tabs-draggable',
  template: """
    <div class="tab-draggable" 
    	 #draggable
	>
      <ng-content></ng-content>
    </div>
  """,
  directives: const [coreDirectives],
  styleUrls: const ['editor_tabs_draggable.css'],
)
class EditorTabsDraggableComponent {

  final closeSC = new StreamController();
  @Output() Stream get close => closeSC.stream;
  
  final detachSC = new StreamController();
  @Output() Stream get detach => detachSC.stream;

  @Input()
  PyroEditorWidget widget;
  
  final ContextMenuService _contextMenuService;
  
  @ViewChild('draggable')
  ElementRef draggableEl;
    
  EditorTabsDraggableComponent(this._contextMenuService) {}
  
  void handleDragStart(e) {
  	e.dataTransfer.setData("widgetId", "${widget.id}");
    e.dataTransfer.setData("areaId", "${widget.area.id}");
  }
  
  void handleContextMenu(dynamic e) {
    e.preventDefault();
    
    ContextMenu menu = ContextMenu(e.client.x, e.client.y, List.of([
        ContextMenuItem('fa-window-restore', 'Detach', true, () {
        	detachSC.add(widget);
        }),
        ContextMenuItem('fa-times', 'Close', true, () {
        	closeSC.add(widget);
        })
  	]));
  	
  	_contextMenuService.show(menu);
  }
}
