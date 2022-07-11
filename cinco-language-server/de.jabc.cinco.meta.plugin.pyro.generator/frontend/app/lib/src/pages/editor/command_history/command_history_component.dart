import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular/angular.dart';

import '../../../deserializer/command_property_deserializer.dart';
import '../../../model/core.dart';
import '../../../model/command.dart';
import '../../../service/editor_data_service.dart';

@Component(
    selector: 'command-history',
    templateUrl: 'command_history_component.html',
    directives: const [coreDirectives],
    providers: const [],
    styleUrls: const []
)
class CommandHistoryComponent implements OnInit, OnDestroy {
  
  final revertedSC = new StreamController();
  @Output() Stream get reverted => revertedSC.stream;
  
  EditorDataService _editorDataService;
    
  WebSocket ws;
  
  List<Command> commandHistory = new List();
  
  StreamSubscription sub;

  CommandHistoryComponent(this._editorDataService) {
  }
  
  @override
  void ngOnInit() {
    sub = _editorDataService.graphModelWebSocketStream.listen((s) {
      ws = s;
      commandHistory = new List();
      if (ws != null) {
        ws.onMessage.listen(handleOnMessage);
      }
    });
  }
  
  @override
  void ngOnDestroy() {
    sub.cancel();
  }
  
  void handleOnMessage(MessageEvent e) {
    var data = jsonDecode(e.data);
    var content = data['content'];
    var messageType = content['messageType'];

    if (data['event'] == '' && messageType == 'command') {
      List<Command> cmds = List.from(content['cmd']['queue']
          .map((c) => CommandPropertyDeserializer.deserialize(c, new Map())));
      if (cmds.length > 1) {
        if (cmds.elementAt(0) is UpdateCommand) {
          cmds.removeAt(0);
        }
      }
      if (content["type"].startsWith("undo")) {
        if (commandHistory.length >= cmds.length) {
          commandHistory.replaceRange(0, 1, []);
        } else {
          commandHistory.clear();
        }
      } else {
        commandHistory.insertAll(0, cmds);
      }
    }
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
