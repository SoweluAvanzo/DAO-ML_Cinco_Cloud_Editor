import 'command.dart';
import 'core.dart';
import 'message.dart';
import 'dart:async' as async;

abstract class CommandGraph {
  final editorCommandSC = new async.StreamController();
  async.Stream get editorCommand => editorCommandSC.stream;

  List<CompoundCommand> commandStack;

  List<CompoundCommand> undoneCommandStack;

  GraphModel currentGraphModel;

  List<HighlightCommand> highlightings;

  CommandGraph(GraphModel this.currentGraphModel,
      List<HighlightCommand> this.highlightings,
      {Map jsog}) {
    if (jsog == null) {
      this.commandStack = new List();
      this.undoneCommandStack = new List();
    } else {
      if (jsog.containsKey("commandStack")) {
        for (var c in jsog["commandStack"]) {
          this.commandStack.add(CompoundCommand.fromJSOG(c));
        }
      } else {
        this.commandStack = new List();
      }
      if (jsog.containsKey("undoneCommandStack")) {
        for (var c in jsog["undoneCommandStack"]) {
          this.undoneCommandStack.add(CompoundCommand.fromJSOG(c));
        }
      } else {
        this.undoneCommandStack = new List();
      }
    }
  }

  IdentifiableElement findElement(id) {
    if (id == currentGraphModel.id) return currentGraphModel;
    var allElements = currentGraphModel.allElements();
    return allElements.firstWhere((e) => e.id == id, orElse: () => null);
  }

  /// action triggered by the server
  /// after a valid command has been propagated
  /// and the local business models has to be modified
  /// if propagation is enabled, the canvas is updated as well
  void _execCreateNodeCommand(CreateNodeCommand cmd, bool propagate) {
    var prEleme = null;
    if (cmd.primeElement != null) {
      var elements = currentGraphModel
          .allElements()
          .where((n) => n.id == cmd.primeElement.id);
      if (elements.isNotEmpty) {
        prEleme = elements.first;
      } else {
        prEleme = cmd.primeElement;
      }
    }
    Node newNode = execCreateNodeType(cmd.type, prEleme);
    //set position
    newNode.x = cmd.x;
    newNode.y = cmd.y;
    newNode.id = cmd.delegateId;
    ModelElementContainer mec = findElement(cmd.containerId);
    if (mec == null) {
      return;
    }
    //set containment
    newNode.container = mec;
    mec.addElement(newNode);

    //set prev element if provided
    if (cmd.element != null) {
      newNode.merge(cmd.element, structureOnly: true);
    }

    if (propagate) {
      // call canvas
      execCreateNodeCommandCanvas(cmd);
    }
  }

  Node execCreateNodeType(String type, PyroElement primeElement);

  void execCreateNodeCommandCanvas(CreateNodeCommand cmd);

  /// actions triggered by the js canvas
  /// creating commands that are send to the server
  CreateNodeCommand _createNodeCommand(String type, int x, int y,
      int containerId, String containerType, int width, int height,
      {int primeId: 0, IdentifiableElement primeElement: null}) {
    CreateNodeCommand cmd = new CreateNodeCommand();
    cmd.containerId = containerId;
    cmd.containerType = containerType;
    cmd.x = x;
    cmd.y = y;
    cmd.type = type;
    cmd.height = height;
    cmd.width = width;
    cmd.primeId = primeId;
    cmd.primeElement = primeElement;
    return cmd;
  }

  CompoundCommandMessage sendCreateNodeCommand(
      String type,
      int x,
      int y,
      int containerId,
      String containerType,
      int width,
      int height,
      PyroUser user,
      {int primeId: 0,
      IdentifiableElement primeElement: null}) {
    return _send(
        _createNodeCommand(
            type, x, y, containerId, containerType, width, height,
            primeId: primeId, primeElement: primeElement),
        user);
  }

  RemoveNodeCommand _invertCreateNodeCommand(CreateNodeCommand cmd) {
    //create remove node command
    //exec command with propagation
    return _removeNodeCommand(cmd.delegateId, cmd.containerId,
        cmd.containerType, cmd.x, cmd.y, cmd.width, cmd.height,
        primeId: cmd.primeId, primeElement: cmd.primeElement);
  }

  void execRemoveNodeCanvas(int id, String type);
  void execRemoveEdgeCanvas(int id, String type);

  void _execRemoveNodeCommand(RemoveNodeCommand cmd, bool propagate) {
    Node node = findElement(cmd.delegateId);
    if (node == null) {
      return;
    }
    ModelElementContainer mec =
        (findElement(cmd.containerId) as ModelElementContainer);
    if (mec == null) {
      return;
    }
    mec.modelElements.remove(node);
    node.container = null;
    if (propagate) {
      // call canvas
      execRemoveNodeCommandCanvas(cmd);
    }
  }

  void execRemoveNodeCommandCanvas(RemoveNodeCommand cmd) {
    execRemoveNodeCanvas(cmd.delegateId, cmd.type);
  }

  RemoveNodeCommand _removeNodeCommand(int nodeId, int containerId,
      String containerType, int x, int y, int width, int height,
      {int primeId: 0,
      IdentifiableElement primeElement: null,
      IdentifiableElement prevElement}) {
    RemoveNodeCommand cmd = new RemoveNodeCommand();
    cmd.delegateId = nodeId;
    cmd.containerId = containerId;
    cmd.containerType = containerType;
    Node node = findElement(nodeId);
    if (node == null) {
      return null;
    }
    cmd.type = node.$type();
    cmd.x = x;
    cmd.y = y;
    cmd.width = width;
    cmd.height = height;
    cmd.primeId = primeId;
    cmd.primeElement = primeElement;
    cmd.element = prevElement;
    return cmd;
  }

  CompoundCommandMessage sendRemoveNodeCommand(int nodeId, PyroUser user,
      {Set edgeCache}) {
    Node node = findElement(nodeId);

    if (node == null) {
      return null;
    }
    var ccm = _send(
        _removeNodeCommand(nodeId, node.container.id, node.container.$type(),
            node.x, node.y, node.width, node.height),
        user);
    return ccm;
  }

  CreateNodeCommand _invertRemoveNodeCommand(RemoveNodeCommand cmd) {
    var cc = _createNodeCommand(cmd.type, cmd.x, cmd.y, cmd.containerId,
        cmd.containerType, cmd.width, cmd.height);
    cc.delegateId = cmd.delegateId;
    cc.primeId = cmd.primeId;
    cc.primeElement = cmd.primeElement;
    cc.element = cmd.element;
    return cc;
  }

  void _execMoveNode(MoveNodeCommand cmd, bool propagate) {
    Node node = findElement(cmd.delegateId);
    if (node == null) {
      return;
    }
    node.x = cmd.x;
    node.y = cmd.y;
    if (cmd.containerId != node.container.id) {
      //remove from old container
      node.container.modelElements.remove(node);
      //add to new container
      ModelElementContainer mec = findElement(cmd.containerId);
      if (mec == null) {
        return;
      }
      node.container = mec;
      mec.addElement(node);
    }
    if (propagate) {
      // call canvas
      execMoveNodeCanvas(cmd);
    }
  }

  void execMoveNodeCanvas(MoveNodeCommand cmd);

  MoveNodeCommand _moveNodeCommand(
      int id, int newX, int newY, int containerId) {
    MoveNodeCommand cmd = new MoveNodeCommand();
    cmd.delegateId = id;
    cmd.containerId = containerId;
    ModelElementContainer newContainer = findElement(containerId);
    cmd.containerType = newContainer.$type();
    Node node = findElement(id);
    cmd.oldContainerId = node.container.id;
    cmd.oldContainerType = node.container.$type();
    if (node == null) {
      return null;
    }

    cmd.type = node.$type();
    cmd.x = newX;
    cmd.y = newY;
    cmd.oldX = node.x;
    cmd.oldY = node.y;
    return cmd;
  }

  CompoundCommandMessage sendMoveNodeCommand(
      int id, int newX, int newY, int containerId, PyroUser user) {
    return _send(_moveNodeCommand(id, newX, newY, containerId), user);
  }

  MoveNodeCommand _invertMoveNodeCommand(MoveNodeCommand cmd) {
    return _moveNodeCommand(
        cmd.delegateId, cmd.oldX, cmd.oldY, cmd.oldContainerId);
  }

  void _execResizeNodeCommand(ResizeNodeCommand cmd, bool propagate) {
    Node node = findElement(cmd.delegateId);
    if (node == null) {
      return;
    }
    node.width = cmd.width;
    node.height = cmd.height;
    if (propagate) {
      // call canvas
      execResizeNodeCommandCanvas(cmd);
    }
  }

  void execResizeNodeCommandCanvas(ResizeNodeCommand cmd);

  ResizeNodeCommand _resizeNodeCommand(
      int id, int newWidth, int newHeight, String direction) {
    ResizeNodeCommand cmd = new ResizeNodeCommand();
    Node node = findElement(id);
    if (node == null) {
      return null;
    }
    cmd.direction = direction;
    cmd.delegateId = id;
    cmd.type = node.$type();
    cmd.width = newWidth;
    cmd.oldWidth = node.width;
    cmd.height = newHeight;
    cmd.oldHeight = node.height;
    return cmd;
  }

  CompoundCommandMessage sendResizeNodeCommand(
      int id, int newWidth, int newHeight, String direction, PyroUser user) {
    return _send(_resizeNodeCommand(id, newWidth, newHeight, direction), user);
  }

  ResizeNodeCommand _invertResizeNode(ResizeNodeCommand cmd) {
    return _resizeNodeCommand(
        cmd.delegateId, cmd.oldWidth, cmd.oldHeight, cmd.direction);
  }

  void _execRotateNodeCommand(RotateNodeCommand cmd, bool propagate) {
    Node node = findElement(cmd.delegateId);
    if (node == null) {
      return;
    }
    node.angle = cmd.angle;
    if (propagate) {
      // call canvas
      execRotateNodeCommandCanvas(cmd);
    }
  }

  void execRotateNodeCommandCanvas(RotateNodeCommand cmd);

  RotateNodeCommand _rotateNodeCommand(int id, int newAngle) {
    RotateNodeCommand cmd = new RotateNodeCommand();
    cmd.delegateId = id;
    Node node = findElement(id);

    if (node == null) {
      return null;
    }

    cmd.type = node.$type();
    cmd.angle = newAngle;
    cmd.oldAngle = node.angle;
    return cmd;
  }

  CompoundCommandMessage sendRotateNodeCommand(
      int id, int newAngle, PyroUser user) {
    return _send(_rotateNodeCommand(id, newAngle), user);
  }

  RotateNodeCommand _invertRotateNodeCommand(RotateNodeCommand cmd) {
    return _rotateNodeCommand(cmd.delegateId, cmd.oldAngle);
  }

  void _execCreateEdgeCommand(CreateEdgeCommand cmd, bool propagate) {
    Edge edge = execCreateEdgeType(cmd.type);
    edge.id = cmd.delegateId;
    Node source = findElement(cmd.sourceId);
    if (source == null) {
      return;
    }
    Node target = findElement(cmd.targetId);
    if (target == null) {
      return;
    }
    // set source
    edge.source = source;
    source.outgoing.add(edge);
    // set target
    edge.target = target;
    target.incoming.add(edge);
    // set container
    edge.container = currentGraphModel;
    currentGraphModel.addElement(edge);

    edge.bendingPoints = new List.from(cmd.positions);

    //set prev element if provided
    if (cmd.element != null) {
      edge.merge(cmd.element, structureOnly: true);
    }

    if (propagate) {
      // call canvas
      execCreateEdgeCommandCanvas(cmd);
    }
  }

  Edge execCreateEdgeType(String type);

  void execCreateEdgeCommandCanvas(CreateEdgeCommand cmd);

  CreateEdgeCommand _createEdgeCommand(String type, int targetId, int sourceId,
      String targetType, String sourceType, List<BendingPoint> bendpoints) {
    CreateEdgeCommand cmd = new CreateEdgeCommand();
    cmd.positions = bendpoints;
    cmd.sourceId = sourceId;
    cmd.targetId = targetId;
    cmd.sourceType = sourceType;
    cmd.targetType = targetType;
    cmd.type = type;
    return cmd;
  }

  CompoundCommandMessage sendCreateEdgeCommand(
      String type,
      int targetId,
      int sourceId,
      String targetType,
      String sourceType,
      List<BendingPoint> positions,
      PyroUser user) {
    return _send(
        _createEdgeCommand(
            type, targetId, sourceId, targetType, sourceType, positions),
        user);
  }

  RemoveEdgeCommand _invertCreateEdgeCommand(CreateEdgeCommand cmd) {
    return _removeEdgeCommand(cmd.delegateId, cmd.sourceId, cmd.targetId,
        cmd.sourceType, cmd.targetType);
  }

  void _execRemoveEdgeCommand(RemoveEdgeCommand cmd, bool propagate) {
    Edge edge = findElement(cmd.delegateId);
    if (edge == null) {
      return;
    }
    edge.bendingPoints.clear();
    Node source = (findElement(cmd.sourceId) as Node);
    Node target = (findElement(cmd.targetId) as Node);
    if (source == null || target == null) {
      return;
    }
    target.incoming.remove(edge);
    source.outgoing.remove(edge);

    currentGraphModel.modelElements.remove(edge);
    if (propagate) {
      // call canvas
      execRemoveEdgeCommandCanvas(cmd);
    }
  }

  void execRemoveEdgeCommandCanvas(RemoveEdgeCommand cmd) {
    execRemoveEdgeCanvas(cmd.delegateId, cmd.type);
  }

  RemoveEdgeCommand _removeEdgeCommand(int id, int sourceId, int targetId,
      String sourceType, String targetType) {
    RemoveEdgeCommand cmd = new RemoveEdgeCommand();
    Edge edge = findElement(id);
    if (edge == null) {
      return null;
    }
    cmd.delegateId = id;
    cmd.positions = new List.from(edge.bendingPoints);
    cmd.type = edge.$type();
    cmd.sourceId = sourceId;
    cmd.targetId = targetId;
    cmd.sourceType = sourceType;
    cmd.targetType = targetType;
    return cmd;
  }

  CompoundCommandMessage sendRemoveEdgeCommand(
      int id,
      int sourceId,
      int targetId,
      String sourceType,
      String targetType,
      String type,
      PyroUser user) {
    return _send(
        _removeEdgeCommand(id, sourceId, targetId, sourceType, targetType),
        user);
  }

  CreateEdgeCommand _invertRemoveEdgeCommand(RemoveEdgeCommand cmd) {
    var cec = _createEdgeCommand(cmd.type, cmd.targetId, cmd.sourceId,
        cmd.targetType, cmd.sourceType, cmd.positions);
    cec.delegateId = cmd.delegateId;
    cec.element = cmd.element;
    return cec;
  }

  void _execReconnectEdgeCommand(ReconnectEdgeCommand cmd, bool propagate) {
    Edge edge = findElement(cmd.delegateId);
    if (edge == null) {
      return null;
    }

    if (cmd.sourceId != edge.source.id) {
      edge.source.outgoing.remove(edge);
      Node newSource = findElement(cmd.sourceId);
      if (newSource == null) {
        return null;
      }
      newSource.outgoing.add(edge);
      edge.source = newSource;
      //reset container
      if (edge.container.id != newSource.container.id) {
        edge.container.modelElements.remove(edge);
        edge.container = newSource.container;
      }
    }
    if (cmd.targetId != edge.target.id) {
      edge.target.incoming.remove(edge);
      Node newTarget = findElement(cmd.targetId);
      if (newTarget == null) {
        return null;
      }
      newTarget.incoming.add(edge);
      edge.target = newTarget;
    }
    if (propagate) {
      // call canvas
      execReconnectEdgeCommandCanvas(cmd);
    }
  }

  void execReconnectEdgeCommandCanvas(ReconnectEdgeCommand cmd);

  ReconnectEdgeCommand _reconnectEdgeCommand(
      int edgeId, int newSourceId, int newTargetId) {
    ReconnectEdgeCommand cmd = new ReconnectEdgeCommand();
    cmd.delegateId = edgeId;
    Edge edge = findElement(cmd.delegateId);
    if (edge == null) {
      return null;
    }
    cmd.type = edge.$type();

    cmd.oldSourceId = edge.source.id;
    if (cmd.oldSourceId != null) {
      IdentifiableElement oldSource = findElement(cmd.oldSourceId);
      cmd.oldSourceType = oldSource.$type();
    } else {
      cmd.oldSourceType = null;
    }

    cmd.oldTargetId = edge.target.id;
    if (cmd.oldTargetId != null) {
      IdentifiableElement oldTarget = findElement(cmd.oldTargetId);
      cmd.oldTargetType = oldTarget.$type();
    } else {
      cmd.oldTargetType = null;
    }

    IdentifiableElement source = findElement(newSourceId);
    cmd.sourceId = newSourceId;
    cmd.sourceType = source.$type();

    IdentifiableElement target = findElement(newTargetId);
    cmd.targetId = newTargetId;
    cmd.targetType = target.$type();

    return cmd;
  }

  CompoundCommandMessage sendReconnectEdgeCommand(
      int edgeId, int newSourceId, int newTargetId, PyroUser user) {
    return _send(_reconnectEdgeCommand(edgeId, newSourceId, newTargetId), user);
  }

  ReconnectEdgeCommand _invertReconnectEdgeCommand(ReconnectEdgeCommand cmd) {
    return _reconnectEdgeCommand(
        cmd.delegateId, cmd.oldSourceId, cmd.oldTargetId);
  }

  void _execUpdateBendPoint(UpdateBendPointCommand cmd, bool propagate) {
    Edge edge = findElement(cmd.delegateId);
    if (edge == null) {
      return;
    }
    edge.bendingPoints = new List();
    cmd.positions.forEach((n) {
      BendingPoint bp = new BendingPoint();
      bp.x = n.x;
      bp.y = n.y;
      edge.bendingPoints.add(bp);
    });
    if (propagate) {
      // call canvas
      execUpdateBendPointCanvas(cmd);
    }
  }

  void execUpdateBendPointCanvas(UpdateBendPointCommand cmd);

  UpdateBendPointCommand updateBendPointCommand(
      int edgeId, List<BendingPoint> positions, List oldPositions) {
    UpdateBendPointCommand cmd = new UpdateBendPointCommand();
    Edge edge = findElement(edgeId);
    if (edge == null) {
      return null;
    }
    cmd.delegateId = edgeId;
    cmd.type = edge.$type();
    cmd.positions = positions;
    cmd.oldPositions = oldPositions;
    return cmd;
  }

  CompoundCommandMessage sendUpdateBendPointCommand(int edgeId, List positions,
      List<BendingPoint> oldPositions, PyroUser user) {
    return _send(updateBendPointCommand(edgeId, positions, oldPositions), user);
  }

  UpdateBendPointCommand _invertUpdateBendPointCommand(
      UpdateBendPointCommand cmd) {
    return updateBendPointCommand(
        cmd.delegateId, cmd.oldPositions, cmd.positions);
  }

  void execUpdateElementCanvas(UpdateCommand cmd);

  void _execUpdateElementCommand(UpdateCommand cmd, bool propagate) {
    IdentifiableElement element = findElement(cmd.delegateId);

    if (element == null) {
      return;
    }
    element.merge(cmd.element, structureOnly: true);
    cmd.element = element;
    if (propagate) {
      // call canvas
      execUpdateElementCanvas(cmd);
    }
  }

  UpdateCommand _updateElementCommand(
      IdentifiableElement element, IdentifiableElement prevElement) {
    UpdateCommand cmd = new UpdateCommand();
    cmd.delegateId = element.id;
    cmd.type = element.$type();
    cmd.element = element.propertyCopy();
    cmd.prevElement = prevElement.propertyCopy();
    return cmd;
  }

  UpdateCommand _invertUpdateCommand(UpdateCommand cmd) {
    return _updateElementCommand(cmd.prevElement, cmd.element);
  }

  void execAppearanceCanvas(AppearanceCommand cmd);

  void _execAppearanceCommand(AppearanceCommand cmd, bool propagate) {
    execAppearanceCanvas(cmd);
  }

  void execHighlightCanvas(HighlightCommand cmd);

  void revertHighlightCanvas(HighlightCommand cmd);

  Command _invertCommand(Command cmd) {
    if (cmd is CreateNodeCommand) return _invertCreateNodeCommand(cmd);
    if (cmd is UpdateBendPointCommand)
      return _invertUpdateBendPointCommand(cmd);
    if (cmd is UpdateCommand) return _invertUpdateCommand(cmd);
    if (cmd is CreateEdgeCommand) return _invertCreateEdgeCommand(cmd);
    if (cmd is MoveNodeCommand) return _invertMoveNodeCommand(cmd);
    if (cmd is RemoveNodeCommand) return _invertRemoveNodeCommand(cmd);
    if (cmd is RemoveEdgeCommand) return _invertRemoveEdgeCommand(cmd);
    if (cmd is ResizeNodeCommand) return _invertResizeNode(cmd);
    if (cmd is RotateNodeCommand) return _invertRotateNodeCommand(cmd);
    if (cmd is ReconnectEdgeCommand) return _invertReconnectEdgeCommand(cmd);
    print("Cannot invert ${cmd}");
    return null;
  }

  void _execCommand(Command cmd, bool propagate) {
    if (cmd is CreateNodeCommand) _execCreateNodeCommand(cmd, propagate);
    if (cmd is UpdateBendPointCommand) _execUpdateBendPoint(cmd, propagate);
    if (cmd is CreateEdgeCommand) _execCreateEdgeCommand(cmd, propagate);
    if (cmd is MoveNodeCommand) _execMoveNode(cmd, propagate);
    if (cmd is RemoveNodeCommand) _execRemoveNodeCommand(cmd, propagate);
    if (cmd is RemoveEdgeCommand) _execRemoveEdgeCommand(cmd, propagate);
    if (cmd is ResizeNodeCommand) _execResizeNodeCommand(cmd, propagate);
    if (cmd is RotateNodeCommand) _execRotateNodeCommand(cmd, propagate);
    if (cmd is ReconnectEdgeCommand) _execReconnectEdgeCommand(cmd, propagate);
    if (cmd is UpdateCommand) _execUpdateElementCommand(cmd, propagate);
    if (cmd is AppearanceCommand) _execAppearanceCommand(cmd, propagate);
  }

  void _execCommandCanvas(Command cmd) {
    if (cmd == null) return;
    if (cmd is CreateNodeCommand) execCreateNodeCommandCanvas(cmd);
    if (cmd is UpdateBendPointCommand) execUpdateBendPointCanvas(cmd);
    if (cmd is CreateEdgeCommand) execCreateEdgeCommandCanvas(cmd);
    if (cmd is MoveNodeCommand) execMoveNodeCanvas(cmd);
    if (cmd is RemoveNodeCommand) execRemoveNodeCommandCanvas(cmd);
    if (cmd is RemoveEdgeCommand) execRemoveEdgeCommandCanvas(cmd);
    if (cmd is ResizeNodeCommand) execResizeNodeCommandCanvas(cmd);
    if (cmd is RotateNodeCommand) execRotateNodeCommandCanvas(cmd);
    if (cmd is ReconnectEdgeCommand) execReconnectEdgeCommandCanvas(cmd);
    if (cmd is UpdateCommand) execUpdateElementCanvas(cmd);
    if (cmd is AppearanceCommand) execAppearanceCanvas(cmd);
  }

  void revert(CompoundCommandMessage ccm) {
    ccm.cmd.queue.reversed
        .map((c) => _invertCommand(c))
        .where((c) => c != null)
        .forEach((c) => _execCommandCanvas(c));
  }

  CompoundCommandMessage undo(PyroUser user) {
    if (commandStack.isEmpty) {
      return null;
    }
    //take last executed command
    CompoundCommand cc = commandStack.last;

    //invert command
    CompoundCommand undoCC = new CompoundCommand();
    cc.queue.reversed
        .map((c) => _invertCommand(c))
        .where((c) => c != null)
        .forEach((c) => undoCC.queue.add(c));
    //send command
    return new CompoundCommandMessage(currentGraphModel.id, UNDO_MESSAGE_TYPE,
        undoCC, user.id, highlightings);
  }

  void _receiveValidUndo(CompoundCommand cc) {
    // add to undone commands
    undoneCommandStack.add(cc);
    // remove from stack
    commandStack.removeLast();
    // execute the valid undo command
    // on the business model
    cc.queue.forEach((c) {
      _execCommand(c, true);
    });
  }

  void _receiveInvalidUndo(CompoundCommand cc) {
    // show message
  }

  CompoundCommandMessage redo(PyroUser user) {
    if (undoneCommandStack.isNotEmpty) {
      //take the last undone command
      CompoundCommand cc = undoneCommandStack.last;

      //invert command
      CompoundCommand redoCC = new CompoundCommand();
      cc.queue.reversed
          .map((c) => _invertCommand(c))
          .where((c) => c != null)
          .forEach((c) => redoCC.queue.add(c));

      return new CompoundCommandMessage(currentGraphModel.id, REDO_MESSAGE_TYPE,
          redoCC, user.id, highlightings);
    }
    return null;
  }

  void _receiveValidRedo(CompoundCommand cc) {
    // put the redone command back to the command stack
    commandStack.add(cc);
    // remove the redone command from the undone
    undoneCommandStack.removeLast();
    // execute the valid redo command
    // on the business model
    cc.queue.forEach((c) {
      _execCommand(c, true);
    });
  }

  void _receiveInvalidRedo(CompoundCommand cc) {
    // show message
  }

  void _addToQueue(CompoundCommand cc) {
    if (!worthToAdd(cc)) return;
    print("Add to queue ${cc.queue.length}");
    this.commandStack.add(cc);
    this.undoneCommandStack.clear();
  }

  /**
   * certain commands like the appearanceCommand should
   * not be added to the commandStack for undo/redo.
   */
  bool worthToAdd(CompoundCommand cc) {
    return cc.queue.where((c) => c is! AppearanceCommand).length > 0;
  }

  void _receiveMyValidCommand(CompoundCommand cc) {
    // execute valid commands on the business model
    cc.queue.forEach((c) {
      _execCommand(c, false);
    });
    //add to stack
    if (cc.queue.isNotEmpty) {
      _addToQueue(cc);
    }
  }

  void _receiveOtherValidCommand(CompoundCommand cc) {
    // execute valid commands on the business model
    cc.queue.forEach((c) {
      _execCommand(c, true);
    });
  }

  void _receiveMyInvalidCommand(CompoundCommand cc) {
    //propagate to the canvas the inverted commands
    cc.queue.forEach((n) {
      // invert command
      Command c = _invertCommand(n);
      // execute inverted command only on canvas
      // since it has not been persisted in the local
      // business model
      if (c != null) {
        _execCommandCanvas(c);
      }
    });
  }

  CompoundCommandMessage _send(Command cmd, PyroUser user) {
    return new CompoundCommandMessage(currentGraphModel.id, BASIC_MESSAGE_TYPE,
        new CompoundCommand(first: cmd), user.id, highlightings);
  }

  void receiveCommand(CompoundCommandMessage ccm, {bool forceExecute: false}) {
    //execute rewritings
    this.commandStack.forEach((cc) => cc.rewrite(ccm.rewriteRules));
    this.undoneCommandStack.forEach((cc) => cc.rewrite(ccm.rewriteRules));
    if (forceExecute) {
      _receiveOtherValidCommand(ccm.cmd);
    } else {
      switch (ccm.type) {
        case BASIC_ANSWER_TYPE:
          _receiveOtherValidCommand(ccm.cmd);
          break;
        case BASIC_INVALID_ANSWER_TYPE:
          _receiveMyInvalidCommand(ccm.cmd);
          break;
        case BASIC_VALID_ANSWER_TYPE:
          _receiveMyValidCommand(ccm.cmd);
          break;
        case UNDO_VALID_ANSWER_TYPE:
          _receiveValidUndo(ccm.cmd);
          break;
        case UNDO_MESSAGE_TYPE:
          _receiveValidUndo(ccm.cmd);
          break;
        case UNDO_INVALID_ANSWER_TYPE:
          _receiveInvalidUndo(ccm.cmd);
          break;
        case REDO_VALID_ANSWER_TYPE:
          _receiveValidRedo(ccm.cmd);
          break;
        case REDO_MESSAGE_TYPE:
          _receiveValidRedo(ccm.cmd);
          break;
        case REDO_INVALID_ANSWER_TYPE:
          _receiveInvalidRedo(ccm.cmd);
          break;
      }
    }
    //revert highlightings
    this.highlightings.forEach((hc) => revertHighlightCanvas(hc));
    this.highlightings.clear();
    //set new highlighting
    ccm.highlightings.forEach((hc) => execHighlightCanvas(hc));

    if (ccm.openFile != null) {
      editorCommandSC.add(ccm.openFile);
    }
  }

  void storeCommand(CompoundCommand cc) {
    print("Store");
    _addToQueue(cc);
  }
}
