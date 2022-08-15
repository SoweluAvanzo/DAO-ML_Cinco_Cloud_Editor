import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular/angular.dart';

import '../../../deserializer/command_property_deserializer.dart';
import '../../../model/core.dart';
import '../../../model/command.dart';
import '../../../service/editor_data_service.dart';
import '../editor_component.dart';

@Component(
    selector: 'command-history',
    templateUrl: 'command_history_component.html',
    directives: const [coreDirectives],
    providers: const [],
    styleUrls: const []
)
class CommandHistoryComponent implements OnInit, OnDestroy {
  
  final revertedSC = new StreamController();
  @Input()
  EditorComponent parent;

  @Input()
  GraphModel currentGraphModel;

  @Output() 
  Stream get reverted => revertedSC.stream;

  List<Command> get commandHistory => parent.commandHistory;


  CommandHistoryComponent() {
  }
  
  @override
  void ngOnInit() {
  }
  
  @override
  void ngOnDestroy() {
  //  sub.cancel();
  }
  
  void revert() {
  	revertedSC.add(null);
  }
  
  // node commands
  bool isCreateNodeCommand(Command cmd) {
    return cmd is CreateNodeCommand;
  }
  bool isMoveNodeCommand(Command cmd) {
    return cmd is MoveNodeCommand;
  }
  bool isRemoveNodeCommand(Command cmd) {
    return cmd is RemoveNodeCommand;
  }
  bool isResizeNodeCommand(Command cmd) {
    return cmd is ResizeNodeCommand;
  }
  bool isRotateNodeCommand(Command cmd) {
    return cmd is RotateNodeCommand;
  }
  
  // edge commands
  bool isCreateEdgeCommand(Command cmd) {
    return cmd is CreateEdgeCommand;
  }
  bool isReconnectEdgeCommand(Command cmd) {
    return cmd is ReconnectEdgeCommand;
  }
  bool isUpdateBendPointCommand(Command cmd) {
    return cmd is UpdateBendPointCommand;
  }
  bool isRemoveEdgeCommand(Command cmd) {
    return cmd is RemoveEdgeCommand;
  }
  
  // other
  bool isUpdateCommand(Command cmd) {
    return cmd is UpdateCommand;
  }
  bool isAppearanceCommand(Command cmd) {
    return cmd is AppearanceCommand;
  }
}
