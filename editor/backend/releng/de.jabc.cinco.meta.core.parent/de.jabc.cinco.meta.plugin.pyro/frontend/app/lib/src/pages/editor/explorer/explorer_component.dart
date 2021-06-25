import 'package:angular/angular.dart';
import 'package:dnd/dnd.dart';
import 'dart:async';
import 'dart:html';

import '../../../model/core.dart';
import 'graph_entry/file_entry_component.dart';
import 'folder_entry/folder_entry_component.dart';
import 'folder_entry/create/create_folder_component.dart';
import 'graph_entry/create/create_file_component.dart';
import '../../../service/graph_service.dart';
import '../../../service/context_menu_service.dart';
import '../../../service/notification_service.dart';
import '../../../pages/shared/context_menu/context_menu.dart';

@Component(
    selector: 'explorer',
    templateUrl: 'explorer_component.html',
    directives: const [coreDirectives,FolderEntryComponent,FileEntryComponent,CreateFileComponent,CreateFolderComponent],
    styleUrls: const ['../editor_component.css', './explorer_component.css']
)
class ExplorerComponent implements OnInit {

  final openFileSC = new StreamController();
  @Output() Stream get openFile => openFileSC.stream;
  
  final hasDeletedSC = new StreamController();
  @Output() Stream get hasDeleted => hasDeletedSC.stream;
  
  final hasChangedSC = new StreamController();
  @Output() Stream get hasChanged => hasChangedSC.stream;

  @Input()
  PyroUser user;
  @Input()
  PyroProject project;
  @Input()
  PyroFile currentFile;
  @Input()
  List<PyroGraphModelPermissionVector> permissionVectors;

  @ViewChildren(FolderEntryComponent)
  List<FolderEntryComponent> childFolders;
  
  @ViewChild('explorerEl')
  ElementRef explorerEl;
  
  @ViewChild('createFolderModal')
  CreateFolderComponent createFolderModal;
  
  @ViewChild('createFileModal')
  CreateFileComponent createFileModal;

  final GraphService graphService;
  final ContextMenuService _contextMenuService;
  final NotificationService _notificationService;

  ExplorerComponent(this.graphService, this._contextMenuService, this._notificationService){
    permissionVectors = new List();
  }
  
  @override
  ngOnInit() {
    initDragAndDrop();
  }
  
  void handleDrop(e) {
    var draggableElement = e.draggableElement;
      var dropzoneElement = e.dropzoneElement;
      
      int sourceId;
      bool sourceIsFolder = false;
      if (draggableElement.attributes['data-folder-id'] != null) {
        sourceId = int.tryParse(draggableElement.attributes['data-folder-id']);
        sourceIsFolder = true;
      } else {
        sourceId = int.tryParse(draggableElement.attributes['data-file-id']);
      }
      int targetId = int.tryParse(dropzoneElement.attributes['data-folder-id']);
                        
      if (sourceIsFolder) {
        if (sourceId != targetId) {
          graphService.moveFolder(sourceId, targetId).then((_){ 
	        PyroFolder folder = findFolderById(sourceId);
	        PyroFolder parentFolder = findParentOfFolderById(sourceId);
	        PyroFolder targetFolder = findFolderById(targetId);
	        parentFolder.innerFolders.remove(folder);
            targetFolder.innerFolders.add(folder);
          }).catchError((err){
	        _notificationService.displayMessage('Could not move folder', NotificationType.DANGER);
	      });  
        }
      } else {
        graphService.moveFile(sourceId, targetId).then((_){
          PyroFile file = findFileById(sourceId);
          PyroFolder parentFolder = findParentOfFileById(sourceId);
          PyroFolder targetFolder = findFolderById(targetId);
          parentFolder.files.remove(file);
          targetFolder.files.add(file);
        }).catchError((err){
          _notificationService.displayMessage('Could not move file', NotificationType.DANGER);
        });
      }
  }
  
  void initDragAndDrop() {    
    var droppableElements = explorerEl.nativeElement.querySelectorAll('.dropzone');
    Dropzone dropzone = new Dropzone(droppableElements, overClass: 'over');
    dropzone.onDrop.listen(handleDrop);
  }
  
  PyroFolder findParentOfFileById(int id) {
    if (project.files.indexWhere((f) => f.id == id) > -1) return project;
    
    List<PyroFolder> queue = new List();
    queue.addAll(project.innerFolders);
    while (queue.length > 0) {
      PyroFolder parent = queue.removeAt(0); 
      if (parent.files.indexWhere((f) => f.id == id) > -1) return parent;
      queue.addAll(parent.innerFolders);
    }
    
    return null;
  }
  
  PyroFolder findParentOfFolderById(int id) {
    if (project.innerFolders.indexWhere((f) => f.id == id) > -1) return project;
    
    List<PyroFolder> queue = new List();
    queue.addAll(project.innerFolders);
    while (queue.length > 0) {
      PyroFolder parent = queue.removeAt(0); 
      if (parent.innerFolders.indexWhere((f) => f.id == id) > -1) return parent;
      queue.addAll(parent.innerFolders);
    }
    
    return null;
  }
  
  PyroFile findFileById(int id) {
    for (int i = 0; i < project.files.length; i++) {
      if (project.files[i].id == id) return project.files[i];
    }
    
    List<PyroFolder> queue = new List();
    queue.addAll(project.innerFolders);
    while (queue.length > 0) {
      PyroFolder folder = queue.removeAt(0); 
      for (int i = 0; i < folder.files.length; i++) {
        if (folder.files[i].id == id) return folder.files[i];
      }     
      queue.addAll(folder.innerFolders);
    }
    
    return null;
  }
  
  PyroFolder findFolderById(int id) {
    if (id == project.id) {
      return project;
    }
  
    List<PyroFolder> queue = new List();
    queue.addAll(project.innerFolders);
    while (queue.length > 0) {
      PyroFolder folder = queue.removeAt(0);
      if (folder.id == id) {
        return folder;
      } else {
        queue.addAll(folder.innerFolders);
      }
    }    
    
    return null;
  }
    
  showContextMenu(dynamic e) {
    e.preventDefault();
    
    ContextMenu menu = ContextMenu(e.client.x, e.client.y, List.of([
      ContextMenuItem('fa-folder-plus', 'New Folder', true, () {
        createFolder(project);
      }),
      ContextMenuItem('fa-file', 'New File', true, () {
        createFile(project);
      })
    ]));
    
    _contextMenuService.show(menu);
  }

  void removeFolder(PyroFolder folder)
  {
    graphService.removeFolder(folder,project).then((_)=>hasDeletedSC.add(folder));
  }

  void deleteFile(PyroFile file)
  {
    if(file is PyroModelFile) {
      graphService.removeGraph(file,this.project).then((f)=>hasDeletedSC.add(file));
    } else {
      graphService.removeFile(file,this.project).then((f)=>hasDeletedSC.add(file));
    }
  }

  void createEntry(dynamic e)
  {
    e.preventDefault();
  }

  void createFolder(PyroFolder folder) {
    createFolderModal.open(folder);
  }

  void createFile(PyroFolder folder) {
    createFileModal.open(folder, permissionVectors).listen((n)=>openFileSC.add(n));
  }

  void expandAll(dynamic e)
  {
    e?.preventDefault();
    changeFolderStatus(true);
  }

  void collapseAll(dynamic e)
  {
    e?.preventDefault();
    changeFolderStatus(false);
  }

  void changeFolderStatus(bool o)
  {
    childFolders.forEach((n) {
      n.setOpenDeep(o);
    });
  }


}

