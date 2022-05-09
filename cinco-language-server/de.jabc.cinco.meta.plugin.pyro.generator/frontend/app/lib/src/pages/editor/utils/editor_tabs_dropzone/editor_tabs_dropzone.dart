import 'dart:html';
import 'dart:async';

import 'package:angular/angular.dart';

import '../../../../model/core.dart';

@Component(
    selector: 'editor-tabs-dropzone',
    template: """
    	<div class="dropzone" 
    		 #dropzone
    	>
    		<ng-content></ng-content>
		</div>
	""", 
    directives: const [coreDirectives],
    styleUrls: const ['editor_tabs_dropzone.css'],
)
class EditorTabsDropzoneComponent {

  final dropSC = new StreamController();
  @Output() Stream get drop => dropSC.stream;

  @Input()
  PyroEditorGridItem area;
  
  @ViewChild('dropzone')
  ElementRef dropzoneEl;
  
  EditorTabsDropzoneComponent() {}
  
  handleDrop(e) {
  	e.preventDefault();
  	
    String widgetId = e.dataTransfer.getData("widgetId");
    String areaId = e.dataTransfer.getData("areaId");
    dropzoneEl.nativeElement.classes.remove('over');
        
    if (widgetId != null) {      
      dropSC.add({
      	'widgetId': int.tryParse(widgetId), 
      	'fromAreaId': areaId != null ? int.tryParse(areaId) : null,
      	'toAreaId': area.id
      });
	}
  }
  
  handleDragOver(e) {
    e.preventDefault();
    return false;
  }
  
  handleDragEnter(e) {
    e.preventDefault();
    dropzoneEl.nativeElement.classes.add('over');
    return false;
  }
  
  handleDragLeave(e) {
    e.preventDefault();
    dropzoneEl.nativeElement.classes.remove('over');
    return false;
  }
}
