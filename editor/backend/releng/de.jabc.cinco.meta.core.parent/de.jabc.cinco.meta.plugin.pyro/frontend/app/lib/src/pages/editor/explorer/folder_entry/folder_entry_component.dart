import 'package:angular/angular.dart';
import 'dart:async';
import 'package:dnd/dnd.dart';
import 'package:angular_forms/angular_forms.dart';

import '../../../../model/core.dart';
import '../graph_entry/file_entry_component.dart';
import '../../../../service/graph_service.dart';
import '../../../../service/context_menu_service.dart';
import '../../../../pages/shared/context_menu/context_menu.dart';
import '../delete/delete_component.dart';

@Component(
    selector: 'folder-entry',
    templateUrl: 'folder_entry_component.html',
    directives: const [
      coreDirectives,
      formDirectives,
      FolderEntryComponent,
      FileEntryComponent,
      DeleteComponent
    ],
    styleUrls: const [
      '../explorer_component.css'
    ])
class FolderEntryComponent implements OnInit {
  final openFileSC = new StreamController();
  @Output()
  Stream get openFile => openFileSC.stream;

  final deleteSC = new StreamController();
  @Output()
  Stream get delete => deleteSC.stream;

  final hasChangedSC = new StreamController();
  @Output()
  Stream get hasChanged => hasChangedSC.stream;

  final hasDeletedSC = new StreamController();
  @Output()
  Stream get hasDeleted => hasDeletedSC.stream;

  final createFileSC = new StreamController();
  @Output()
  Stream get createFile => createFileSC.stream;

  final createFolderSC = new StreamController();
  @Output()
  Stream get createFolder => createFolderSC.stream;

  @Input()
  PyroFolder folder;

  @Input()
  PyroProject project;

  @Input()
  PyroFile currentFile;

  @Input()
  Function handleDrop;

  @Input()
  List<PyroGraphModelPermissionVector> permissionVectors;

  @ViewChildren(FolderEntryComponent)
  List<FolderEntryComponent> childFolders;

  @ViewChild('folderEl')
  ElementRef folderEl;

  @ViewChild('deleteFolderModal')
  DeleteComponent deleteFolderModal;

  bool open = false;
  bool editMode = false;

  final GraphService graphService;

  final ContextMenuService _contextMenuService;

  FolderEntryComponent(this.graphService, this._contextMenuService) {
    permissionVectors = new List();
  }

  @override
  void ngOnInit() {
    Draggable draggable = new Draggable(folderEl.nativeElement,
        avatarHandler: new AvatarHandler.clone(), draggingClass: 'dragging');
    Dropzone dropzone = new Dropzone(folderEl.nativeElement, overClass: 'over');
    dropzone.onDrop.listen(handleDrop);
  }

  showContextMenu(dynamic e) {
    e.preventDefault();

    ContextMenu menu = ContextMenu(
        e.client.x,
        e.client.y,
        List.of([
          ContextMenuItem('fa-folder-plus', 'New Folder', true, () {
            createInnerFolder(folder);
          }),
          ContextMenuItem('fa-file', 'New File', true, () {
            createInnerFile(folder);
          }),
          ContextMenuSeparator(),
          ContextMenuItem('fa-edit', 'Edit', true, () {
            editEntry(null);
          }),
          ContextMenuItem('fa-trash', 'Delete', true, () {
            deleteFolderModal.open('Folder', folder.name);
          })
        ]));

    _contextMenuService.show(menu);
  }

  String getFolderClass() {
    return "fas fa-fw fa-" + (open ? "minus-square" : "plus-square");
  }

  void setOpenDeep(bool o) {
    open = o;
    childFolders.forEach((f) {
      f.setOpenDeep(o);
    });
  }

  void openFolder(dynamic e) {
    open = !open;
    e.preventDefault();
  }

  void removeFolder(PyroFolder folder) {
    graphService
        .removeFolder(folder, this.folder);
        // handled by projectWebSocket, since file-deletion could be blocked
        // .then((f) => hasDeletedSC.add(folder));
  }

  void deleteFile(PyroFile file) {
    if (file is PyroModelFile) {
      graphService
          .removeGraph(file, this.folder)
          .then((f) => hasDeletedSC.add(file));
    } else {
      graphService
          .removeFile(file, this.folder)
          .then((f) => hasDeletedSC.add(file));
    }
  }

  void editEntry(dynamic e) {
    if (e != null) {
      e.preventDefault();
    }
    editMode = true;
  }

  void save(dynamic e) {
    e?.preventDefault();
    graphService.updateFolder(folder).then((f) {
      editMode = false;
      hasChangedSC.add(e);
    });
  }

  void createInnerFolder(PyroFolder folder) {
    open = true;
    createFolderSC.add(folder);
  }

  void createInnerFile(PyroFolder folder) {
    open = true;
    createFileSC.add(folder);
  }
}
