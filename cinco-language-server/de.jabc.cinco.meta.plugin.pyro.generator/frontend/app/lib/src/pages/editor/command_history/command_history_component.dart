import 'dart:async';
import 'dart:html';
import 'dart:convert';
import 'package:angular/angular.dart';

import '../../../model/core.dart';
import '../../../model/command.dart';
import '../editor_component.dart';
import '../../../model/command_graph.dart';

@Component(
	selector: 'command-history',
	templateUrl: 'command_history_component.html',
	directives: const [coreDirectives],
	providers: const [],
	styleUrls: const ['command_history_component.css'])
class CommandHistoryComponent implements OnInit, OnDestroy {
	final revertedSC = new StreamController();
	final redoSC = new StreamController();

	@Input()
	EditorComponent parent;

	@Input()
	GraphModel currentGraphModel;

	@Output()
	Stream get reverted => revertedSC.stream;
	@Output()
	Stream get redone => redoSC.stream;

	CommandGraph get commandGraph => parent.commandGraph;
	List<CompoundCommand> get commandHistory => commandGraph?.commandStack;
	List<CompoundCommand> get redoHistory => commandGraph?.undoneCommandStack;

	CommandHistoryComponent() {}

	@override
	void ngOnInit() {}

	@override
	void ngOnDestroy() {}

	void revert() {
		revertedSC.add(null);
	}

	void redo() {
		redoSC.add(null);
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

  String getCommandName(Command cmd, bool reversed) {
    if(isUpdateCommand(cmd)) {
      return "Update";
    } else if(isAppearanceCommand(cmd)) {
      return "Appearance";
    } else if(isCreateEdgeCommand(cmd)) {
      return reversed? "Remove Edge" : "Create Edge";
    } else if(isCreateNodeCommand(cmd)) {
      return reversed? "Remove Node" : "Create Node";
    } else if(isMoveNodeCommand(cmd)) {
      return "Move Node";
    } else if(isReconnectEdgeCommand(cmd)) {
      return "Reconnect Edge";
    } else if(isRemoveEdgeCommand(cmd)) {
      return reversed? "Create Edge" : "Remove Edge";
    } else if(isRemoveNodeCommand(cmd)) {
      return reversed? "Create Node" : "Remove Node";
    } else if(isResizeNodeCommand(cmd)) {
      return "Resize Node";
    } else if(isRotateNodeCommand(cmd)) {
      return "Rotate Node";
    } else if(isUpdateBendPointCommand(cmd)) {
      return "Update Bendpoint";
    } else {
      return "Unknown Command";
    }
  }

  onCaretClick(Event e) {
    var target = e.currentTarget as HtmlElement;
    target.parent.querySelector(".cmd-nested").classes.toggle("active");
    target.classes.toggle("cmd-caret-down");
  }
}
