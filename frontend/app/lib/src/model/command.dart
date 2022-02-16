import '../deserializer/command_property_deserializer.dart';
import '../model/core.dart';
import '../model/message.dart' as message;
import '../deserializer/property_deserializer.dart';
import '../model/dispatcher.dart';

abstract class Command {
  int delegateId;
  String type;

  Map toJSOG();

  void rewrite(message.RewriteRule rule);
}

class CompoundCommand {
  List<Command> queue;
  CompoundCommand({Command first}) {
    queue = new List();
    if (first != null) {
      queue.add(first);
    }
  }

  void rewrite(List<message.RewriteRule> rewritings) {
    rewritings.forEach((rr) => queue.forEach((c) => c.rewrite(rr)));
  }

  static CompoundCommand fromJSOG(jsog) {
    CompoundCommand cc = new CompoundCommand();
    Map<String, dynamic> cache = new Map();
    cc.queue = jsog['queue']
        .map((n) => CommandPropertyDeserializer.deserialize(n, cache))
        .toList()
        .cast<Command>();
    return cc;
  }

  Map toJSOG() {
    Map map = new Map();
    map['queue'] = queue.map((n) => n.toJSOG()).toList();
    return map;
  }
}

abstract class NodeCommand extends Command {}

abstract class EdgeCommand extends Command {}

/// Node Commands
class CreateNodeCommand extends NodeCommand {
  int x;
  int y;
  int width;
  int height;
  int containerId;
  String containerType;
  int primeId;
  PyroElement primeElement;
  IdentifiableElement element;

  static CreateNodeCommand fromJSOG(
      Map<String, dynamic> jsog, Map<String, dynamic> cache) {
    CreateNodeCommand cmd = new CreateNodeCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];

    cmd.x = jsog['x'];
    cmd.y = jsog['y'];
    if (jsog.containsKey('primeId')) {
      cmd.primeId = jsog['primeId'];
    }
    if (jsog.containsKey('primeElement')) {
      if (jsog['primeElement'] != null) {
        if (jsog['primeElement'].containsKey("@ref")) {
          print(cache.containsKey(jsog['primeElement']['@ref']));
          cmd.primeElement = cache[jsog['primeElement']['@ref']];
        } else {
          cmd.primeElement =
              GraphModelDispatcher.dispatchElement(jsog['primeElement'], cache);
          var primeId = jsog['primeElement']['@id'];
          var primeType = jsog['primeElement']['__type'];
          var cacheKey = primeType + ":" + primeId;
          cache[cacheKey] = cmd.primeElement;
        }
      }
    }
    if (jsog.containsKey('element')) {
      if (jsog['element'] != null) {
        cmd.element =
            GraphModelDispatcher.dispatchElement(jsog['element'], new Map());
      }
    }
    cmd.height = jsog['height'];
    cmd.width = jsog['width'];
    cmd.containerId = jsog['containerId'];
    cmd.containerType = jsog['containerType'];

    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] = "info.scce.pyro.core.command.types.CreateNodeCommand";
    map['delegateId'] = delegateId != null ? delegateId : -1;
    map['type'] = type;

    map['x'] = x;
    map['y'] = y;
    map['width'] = width;
    map['height'] = height;
    map['primeId'] = primeId;
    if (primeElement != null) {
      map['primeElement'] = primeElement.toJSOG(new Map());
    } else
      map['primeElement'] = null;
    if (element != null) {
      map['element'] = element.toJSOG(new Map());
      map['element'].remove("@id");
    } else
      map['element'] = null;
    map['containerId'] = containerId != null ? containerId : -1;
    map['containerType'] = containerType;

    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.containerId == rule.oldId) {
      this.containerId = rule.newId;
    }
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
    if (this.primeId == rule.oldId) {
      this.primeId = rule.newId;
    }
    if (primeElement != null && this.primeElement.id == rule.oldId) {
      this.primeElement.id = rule.newId;
    }
    if (element != null && this.element.id == rule.oldId) {
      this.element.id = rule.newId;
    }
  }
}

class MoveNodeCommand extends NodeCommand {
  int oldX;
  int oldY;
  int oldContainerId;
  String oldContainerType;
  int x;
  int y;
  int containerId;
  String containerType;

  static MoveNodeCommand fromJSOG(Map<String, dynamic> jsog) {
    MoveNodeCommand cmd = new MoveNodeCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];

    cmd.x = jsog['x'];
    cmd.y = jsog['y'];
    cmd.containerId = jsog['containerId'];
    cmd.containerType = jsog['containerType'];

    cmd.oldX = jsog['oldX'];
    cmd.oldY = jsog['oldY'];
    cmd.oldContainerId = jsog['oldContainerId'];
    cmd.oldContainerType = jsog['oldContainerType'];

    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] = "info.scce.pyro.core.command.types.MoveNodeCommand";
    map['delegateId'] = delegateId;
    map['type'] = type;

    map['x'] = x;
    map['y'] = y;
    map['containerId'] = containerId;
    map['containerType'] = containerType;

    map['oldX'] = oldX;
    map['oldY'] = oldY;
    map['oldContainerId'] = oldContainerId;
    map['oldContainerType'] = oldContainerType;

    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.containerId == rule.oldId) {
      this.containerId = rule.newId;
    }
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
    if (this.oldContainerId == rule.oldId) {
      this.oldContainerId = rule.newId;
    }
  }
}

class RemoveNodeCommand extends NodeCommand {
  int x;
  int y;
  int width;
  int height;
  int containerId;
  String containerType;
  int primeId;
  PyroElement primeElement;
  IdentifiableElement element;

  static RemoveNodeCommand fromJSOG(
      Map<String, dynamic> jsog, Map<String, dynamic> cache) {
    RemoveNodeCommand cmd = new RemoveNodeCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];

    cmd.x = jsog['x'];
    cmd.y = jsog['y'];
    if (jsog.containsKey('primeId')) {
      cmd.primeId = jsog['primeId'];
    }
    if (jsog.containsKey('primeElement')) {
      if (jsog['primeElement'] != null) {
        if (jsog['primeElement'].containsKey("@ref")) {
          print(cache.containsKey(jsog['primeElement']['@ref']));
          cmd.primeElement = cache[jsog['primeElement']['@ref']];
        } else {
          cmd.primeElement =
              GraphModelDispatcher.dispatchElement(jsog['primeElement'], cache);
          var primeId = jsog['primeElement']['@id'];
          var primeType = jsog['primeElement']['__type'];
          var cacheKey = primeType + ":" + primeId;
          cache[cacheKey] = cmd.primeElement;
        }
      }
    }
    if (jsog.containsKey('element')) {
      if (jsog['element'] != null) {
        cmd.element =
            GraphModelDispatcher.dispatchElement(jsog['element'], new Map());
      }
    }
    cmd.height = jsog['height'];
    cmd.width = jsog['width'];
    cmd.containerId = jsog['containerId'];
    cmd.containerType = jsog['containerType'];

    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] = "info.scce.pyro.core.command.types.RemoveNodeCommand";
    map['delegateId'] = delegateId;
    map['type'] = type;

    map['x'] = x;
    map['y'] = y;
    map['primeId'] = primeId;
    if (primeElement != null) {
      map['primeElement'] = primeElement.toJSOG(new Map());
      map['primeElement'].remove("@id");
    }
    if (element != null) {
      map['element'] = element.toJSOG(new Map());
      map['element'].remove("@id");
    }
    map['width'] = width;
    map['height'] = height;
    map['containerId'] = containerId;
    map['containerType'] = containerType;

    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.containerId == rule.oldId) {
      this.containerId = rule.newId;
    }
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
    if (this.primeId == rule.oldId) {
      this.primeId = rule.newId;
    }
    if (primeElement != null && this.primeElement.id == rule.oldId) {
      this.primeElement.id = rule.newId;
    }
    if (element != null && this.element.id == rule.oldId) {
      this.element.id = rule.newId;
    }
  }
}

class ResizeNodeCommand extends NodeCommand {
  int oldWidth;
  int oldHeight;
  int width;
  int height;
  String direction;

  static ResizeNodeCommand fromJSOG(Map<String, dynamic> jsog) {
    ResizeNodeCommand cmd = new ResizeNodeCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];

    cmd.oldWidth = jsog['oldWidth'];
    cmd.oldHeight = jsog['oldHeight'];
    cmd.width = jsog['width'];
    cmd.height = jsog['height'];
    cmd.direction = jsog['direction'];
    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] = "info.scce.pyro.core.command.types.ResizeNodeCommand";
    map['delegateId'] = delegateId;
    map['type'] = type;

    map['oldWidth'] = oldWidth;
    map['oldHeight'] = oldHeight;
    map['width'] = width;
    map['height'] = height;
    map['direction'] = direction;

    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
  }
}

class RotateNodeCommand extends NodeCommand {
  int oldAngle;
  int angle;

  static RotateNodeCommand fromJSOG(Map<String, dynamic> jsog) {
    RotateNodeCommand cmd = new RotateNodeCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];

    cmd.oldAngle = jsog['oldAngle'];
    cmd.angle = jsog['angle'];
    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] = "info.scce.pyro.core.command.types.RotateNodeCommand";
    map['delegateId'] = delegateId;
    map['type'] = type;

    map['oldAngle'] = oldAngle;
    map['angle'] = angle;

    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
  }
}

/// Edge Commands
class CreateEdgeCommand extends EdgeCommand {
  int sourceId;
  int targetId;
  String sourceType;
  String targetType;
  List<BendingPoint> positions;
  IdentifiableElement element;

  static CreateEdgeCommand fromJSOG(Map<String, dynamic> jsog) {
    CreateEdgeCommand cmd = new CreateEdgeCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];

    cmd.sourceId = jsog['sourceId'];
    cmd.targetId = jsog['targetId'];
    cmd.sourceType = jsog['sourceType'];
    cmd.targetType = jsog['targetType'];
    cmd.positions = new List();
    for (var b in jsog['positions']) {
      cmd.positions.add(new BendingPoint(jsog: b));
    }
    if (jsog.containsKey('element')) {
      if (jsog['element'] != null) {
        cmd.element =
            GraphModelDispatcher.dispatchElement(jsog['element'], new Map());
      }
    }
    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] = "info.scce.pyro.core.command.types.CreateEdgeCommand";
    map['delegateId'] = delegateId;
    map['type'] = type;

    map['sourceId'] = sourceId;
    map['targetId'] = targetId;
    map['sourceType'] = sourceType;
    map['targetType'] = targetType;

    map['positions'] = positions.map((b) => b.toJSOG(new Map())).toList();
    if (element != null) {
      map['element'] = element.toJSOG(new Map());
      map['element'].remove("@id");
    }

    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.sourceId == rule.oldId) {
      this.sourceId = rule.newId;
    }
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
    if (this.targetId == rule.oldId) {
      this.targetId = rule.newId;
    }
    if (this.element != null && this.element.id == rule.oldId) {
      this.element.id = rule.newId;
    }
  }
}

class RemoveEdgeCommand extends EdgeCommand {
  int sourceId;
  int targetId;
  String sourceType;
  String targetType;
  List<BendingPoint> positions;
  IdentifiableElement element;

  static RemoveEdgeCommand fromJSOG(Map<String, dynamic> jsog) {
    RemoveEdgeCommand cmd = new RemoveEdgeCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];

    cmd.positions = new List();
    for (var b in jsog['positions']) {
      cmd.positions.add(new BendingPoint(jsog: b));
    }
    if (jsog.containsKey('element')) {
      if (jsog['element'] != null) {
        cmd.element =
            GraphModelDispatcher.dispatchElement(jsog['element'], new Map());
      }
    }

    cmd.sourceId = jsog['sourceId'];
    cmd.targetId = jsog['targetId'];
    cmd.sourceType = jsog['sourceType'];
    cmd.targetType = jsog['targetType'];

    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] = "info.scce.pyro.core.command.types.RemoveEdgeCommand";
    map['delegateId'] = delegateId;
    map['type'] = type;

    map['sourceId'] = sourceId;
    map['targetId'] = targetId;
    map['sourceType'] = sourceType;
    map['targetType'] = targetType;
    map['positions'] = positions.map((b) => b.toJSOG(new Map())).toList();
    if (element != null) {
      map['element'] = element.toJSOG(new Map());
      map['element'].remove("@id");
    }
    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.sourceId == rule.oldId) {
      this.sourceId = rule.newId;
    }
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
    if (this.targetId == rule.oldId) {
      this.targetId = rule.newId;
    }
    if (this.element != null && element.id == rule.oldId) {
      this.element.id = rule.newId;
    }
  }
}

class ReconnectEdgeCommand extends EdgeCommand {
  int oldSourceId;
  int oldTargetId;
  int sourceId;
  int targetId;
  String oldSourceType;
  String oldTargetType;
  String sourceType;
  String targetType;

  static ReconnectEdgeCommand fromJSOG(Map<String, dynamic> jsog) {
    ReconnectEdgeCommand cmd = new ReconnectEdgeCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];

    cmd.sourceId = jsog['sourceId'];
    cmd.sourceType = jsog['sourceType'];
    cmd.targetId = jsog['targetId'];
    cmd.targetType = jsog['targetType'];
    cmd.oldSourceId = jsog['oldSourceId'];
    cmd.oldSourceType = jsog['oldSourceType'];
    cmd.oldTargetId = jsog['oldTargetId'];
    cmd.oldTargetType = jsog['oldTargetType'];

    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] =
        "info.scce.pyro.core.command.types.ReconnectEdgeCommand";
    map['delegateId'] = delegateId;
    map['type'] = type;

    map['sourceId'] = sourceId;
    map['sourceType'] = sourceType;
    map['targetId'] = targetId;
    map['targetType'] = targetType;

    map['oldSourceId'] = oldSourceId;
    map['oldSourceType'] = oldSourceType;
    map['oldTargetId'] = oldTargetId;
    map['oldTargetType'] = oldTargetType;

    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.sourceId == rule.oldId) {
      this.sourceId = rule.newId;
    }
    if (this.oldSourceId == rule.oldId) {
      this.oldSourceId = rule.newId;
    }
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
    if (this.targetId == rule.oldId) {
      this.targetId = rule.newId;
    }
    if (this.oldTargetId == rule.oldId) {
      this.oldTargetId = rule.newId;
    }
  }
}

class UpdateBendPointCommand extends Command {
  List<BendingPoint> positions;
  List<BendingPoint> oldPositions;

  UpdateBendPointCommand() {
    positions = new List();
    oldPositions = new List();
  }

  static UpdateBendPointCommand fromJSOG(Map<String, dynamic> jsog) {
    UpdateBendPointCommand cmd = new UpdateBendPointCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];
    cmd.positions = new List();
    for (var value in jsog['positions']) {
      cmd.positions.add(new BendingPoint(jsog: value));
    }
    cmd.oldPositions = new List();
    for (var value in jsog['oldPositions']) {
      cmd.oldPositions.add(new BendingPoint(jsog: value));
    }
    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] =
        "info.scce.pyro.core.command.types.UpdateBendPointCommand";
    map['delegateId'] = delegateId;
    map['type'] = type;

    map['positions'] = positions.map((n) => n.toJSOG(new Map())).toList();
    map['oldPositions'] = oldPositions.map((n) => n.toJSOG(new Map())).toList();

    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
  }
}

class UpdateCommand extends Command {
  IdentifiableElement element;
  IdentifiableElement prevElement;

  UpdateCommand() {}

  static UpdateCommand fromJSOG(
      Map<String, dynamic> jsog, Map<String, dynamic> cache) {
    UpdateCommand cmd = new UpdateCommand();
    cmd.delegateId = jsog['delegateId'];
    cmd.type = jsog['type'];
    var type = cmd.type.substring(0, cmd.type.indexOf("."));
    cmd.element =
        PropertyDeserializer.deserialize(jsog['element'], type, cache);
    cmd.prevElement =
        PropertyDeserializer.deserialize(jsog['prevElement'], type, cache);

    return cmd;
  }

  @override
  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] = "info.scce.pyro.core.command.types.UpdateCommand";
    map['delegateId'] = this.delegateId;
    map['type'] = this.type;
    map['element'] = this.element.toJSOG(new Map());
    map['element'].remove("@id");
    //to ensure that the objects from the
    //prev element are not taken from the cache but
    //the IDs are consistent
    map['prevElement'] = this.prevElement.toJSOG(new Map());
    map['prevElement'].remove("@id");
    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }

    if (element != null && this.element.id == rule.oldId) {
      this.element.id = rule.newId;
    }
    if (this.prevElement != null && prevElement.id == rule.oldId) {
      this.prevElement.id = rule.newId;
    }
  }
}

class AppearanceCommand extends Command {
  String shapeId;
  String name;

  int background_r;
  int background_g;
  int background_b;

  int foreground_r;
  int foreground_g;
  int foreground_b;

  bool lineInVisible;
  String lineStyle;

  double transparency;
  int lineWidth;
  String filled;
  double angle;

  String fontName;
  int fontSize;
  bool fontBold;
  bool fontItalic;

  String imagePath;

  static AppearanceCommand fromJSOG(Map<String, dynamic> map) {
    Map<String, dynamic> jsog = map['appearance'];
    AppearanceCommand cmd = new AppearanceCommand();
    cmd.delegateId = int.parse(map['delegateId'].toString());
    cmd.type = map['type'];
    cmd.shapeId = jsog['id'];
    cmd.name = jsog['name'];
    cmd.background_r = int.parse(jsog['background_r'].toString());
    cmd.background_g = int.parse(jsog['background_g'].toString());
    cmd.background_b = int.parse(jsog['background_b'].toString());
    cmd.foreground_r = int.parse(jsog['foreground_r'].toString());
    cmd.foreground_g = int.parse(jsog['foreground_g'].toString());
    cmd.foreground_b = int.parse(jsog['foreground_b'].toString());
    cmd.lineInVisible =
        jsog['lineInVisible'] == 'true' || jsog['lineInVisible'] == true;
    cmd.lineStyle = jsog['lineStyle'];
    cmd.transparency = num.parse(jsog['transparency'].toString());
    cmd.lineWidth = int.parse(jsog['lineWidth'].toString());
    cmd.filled = jsog['filled'];
    cmd.angle = num.parse(jsog['angle'].toString());
    cmd.fontName = jsog['fontName'];
    cmd.fontSize = int.parse(jsog['fontSize'].toString());
    cmd.fontBold = jsog['fontBold'] == true || jsog['fontBold'] == 'true';
    cmd.fontItalic = jsog['fontItalic'] == true || jsog['fontItalic'] == 'true';
    cmd.imagePath = jsog['imagePath'];

    return cmd;
  }

  @override
  Map toJSOG() {
    Map map = new Map();
    map['delegateId'] = this.delegateId;
    map['type'] = this.type;
    map['background_r'] = this.background_r;
    map['background_g'] = this.background_g;
    map['background_b'] = this.background_b;
    map['foreground_r'] = this.foreground_r;
    map['foreground_g'] = this.foreground_g;
    map['foreground_b'] = this.foreground_b;
    map['lineInVisible'] = this.lineInVisible;
    map['lineStyle'] = this.lineStyle;
    map['transparency'] = this.transparency;
    map['lineWidth'] = this.lineWidth;
    map['filled'] = this.filled;
    map['fontName'] = this.fontName;
    map['fontSize'] = this.fontSize;
    map['fontBold'] = this.fontBold;
    map['fontItalic'] = this.fontItalic;
    map['imagePath'] = this.imagePath;
    return map;
  }

  @override
  void rewrite(message.RewriteRule rule) {
    if (this.delegateId == rule.oldId) {
      this.delegateId = rule.newId;
    }
  }
}

class HighlightCommand {
  int id = -1;
  String lightType = "";

  int background_r = 0;
  int background_g = 0;
  int background_b = 0;

  int foreground_r = 0;
  int foreground_g = 0;
  int foreground_b = 0;

  int pre_background_r = 0;
  int pre_background_g = 0;
  int pre_background_b = 0;

  int pre_foreground_r = 0;
  int pre_foreground_g = 0;
  int pre_foreground_b = 0;

  void setPre(Map<String, dynamic> jsog) {
    var b = jsog['background'].toString();
    var b_rgb = b.substring(4, b.length - 1).split(',');
    var f = jsog['foreground'].toString();
    var f_rgb = f.substring(4, f.length - 1).split(',');
    pre_background_r = int.parse(b_rgb[0].toString());
    pre_background_g = int.parse(b_rgb[1].toString());
    pre_background_b = int.parse(b_rgb[2].toString());
    pre_foreground_r = int.parse(f_rgb[0].toString());
    pre_foreground_g = int.parse(f_rgb[1].toString());
    pre_foreground_b = int.parse(f_rgb[2].toString());
  }

  static HighlightCommand fromJSOG(Map<String, dynamic> jsog) {
    HighlightCommand cmd = new HighlightCommand();
    cmd.id = int.parse(jsog['id'].toString());
    cmd.lightType = jsog['lightType'];
    cmd.background_r = int.parse(jsog['background_r'].toString());
    cmd.background_g = int.parse(jsog['background_g'].toString());
    cmd.background_b = int.parse(jsog['background_b'].toString());
    cmd.foreground_r = int.parse(jsog['foreground_r'].toString());
    cmd.foreground_g = int.parse(jsog['foreground_g'].toString());
    cmd.foreground_b = int.parse(jsog['foreground_b'].toString());

    return cmd;
  }

  Map toJSOG() {
    Map map = new Map();
    map['runtimeType'] = 'info.scce.pyro.core.command.types.HighlightCommand';
    map['id'] = this.id;
    map['lightType'] = this.lightType;
    map['background_r'] = this.background_r;
    map['background_g'] = this.background_g;
    map['background_b'] = this.background_b;
    map['foreground_r'] = this.foreground_r;
    map['foreground_g'] = this.foreground_g;
    map['foreground_b'] = this.foreground_b;
    return map;
  }
}

class OpenFileCommand {
  int id = -1;

  static OpenFileCommand fromJSOG(Map<String, dynamic> jsog) {
    OpenFileCommand cmd = new OpenFileCommand();
    cmd.id = int.parse(jsog['id'].toString());

    return cmd;
  }
}
