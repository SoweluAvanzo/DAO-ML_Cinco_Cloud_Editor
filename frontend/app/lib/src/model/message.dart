import 'core.dart';
import 'dart:convert';
import 'command.dart';
import '../deserializer/property_deserializer.dart';

const String BASIC_MESSAGE_TYPE = "basic";
const String UNDO_MESSAGE_TYPE = "undo";
const String REDO_MESSAGE_TYPE = "redo";

const String BASIC_ANSWER_TYPE = "basic_answer";
const String BASIC_VALID_ANSWER_TYPE = "basic_valid_answer";
const String BASIC_INVALID_ANSWER_TYPE = "basic_invalid_answer";
const String UNDO_VALID_ANSWER_TYPE = "undo_valid_answer";
const String UNDO_INVALID_ANSWER_TYPE = "undo_invalid_answer";
const String REDO_VALID_ANSWER_TYPE = "redo_valid_answer";
const String REDO_INVALID_ANSWER_TYPE = "redo_invalid_answer";

abstract class Message {
  String messageType;
  int senderId;

  static Message fromJSOG(Map jsog) {
    if (jsog['messageType'] == 'property') {
      return PropertyMessage.fromJSOG(jsog, new Map());
    }
    if (jsog['messageType'] == 'command') {
      return CompoundCommandMessage.fromJSOG(jsog);
    }
    return null;
  }

  static Message fromJSON(String s) {
    return fromJSOG(jsonDecode(s));
  }

  String toJSON() {
    return jsonEncode(this.toJSOG());
  }

  Map toJSOG();
}



abstract class GraphMessage extends Message {
  int graphModelId;
}

class PropertyMessage extends GraphMessage {
  String messageType = "property";
  String graphModelType;
  IdentifiableElement delegate;

  PropertyMessage(
      int graphModelId, this.graphModelType, this.delegate, int senderId) {
    super.senderId = senderId;
    super.graphModelId = graphModelId;
  }

  static PropertyMessage fromJSOG(Map jsog, Map cache) {
    PropertyMessage pm = new PropertyMessage(
        jsog['graphModelId'],
        jsog['graphModelType'],
        PropertyDeserializer.deserialize(
            jsog['delegate'], jsog['graphModelType'], cache),
        jsog["senderId"]);

    return pm;
  }

  Map toJSOG() {
    Map jsog = new Map();
    jsog['runtimeType'] = "info.scce.pyro.core.command.types.PropertyMessage";
    jsog['messageType'] = 'property';
    jsog['graphModelId'] = graphModelId;
    jsog["senderId"] = senderId;
    jsog['graphModelType'] = graphModelType;
    jsog['delegate'] = delegate.toJSOG(new Map());
    return jsog;
  }
}

class DisplayMessages {
	List<DisplayMessage> messages = new List();
	static DisplayMessages fromJSOG(Map jsog) {
		var dm = new DisplayMessages();
		for(var m in jsog['messages']) {
	      dm.messages.add(DisplayMessage.fromJSOG(m));
	    }
	    return dm;
	}
}

class DisplayMessage {
	String type;
	String content;
	
	static DisplayMessage fromJSOG(Map jsog) {
		var d = new DisplayMessage();
		d.type = jsog["messageType"];
		d.content = jsog["content"];
		return d;
	}
	
}

class CompoundCommandMessage extends GraphMessage {
  String messageType = "command";
  CompoundCommand cmd;
  String type;
  List<HighlightCommand> highlightings = new List();
  OpenFileCommand openFile = null;
  List<RewriteRule> rewriteRules = new List();

  CompoundCommandMessage(int grapModelId, this.type, this.cmd, int senderId,
      List<HighlightCommand> highlightings) {
    super.senderId = senderId;
    super.graphModelId = graphModelId;
    this.highlightings = highlightings;
  }

  static CompoundCommandMessage fromJSOG(Map jsog) {
    List<HighlightCommand> hs = new List();
    for (var h in jsog['highlightings']) {
      hs.add(HighlightCommand.fromJSOG(h));
    }
    

    List<RewriteRule> rewriteRule = new List();
    for (var rr in jsog['rewriteRule']) {
      rewriteRule.add(new RewriteRule(rr));
    }

    CompoundCommandMessage ccm = new CompoundCommandMessage(
        jsog['graphModelId'],
        jsog['type'],
        CompoundCommand.fromJSOG(jsog['cmd']),
        jsog["senderId"],
        hs
    );

    
    
    if(jsog.containsKey('openFile') && jsog['openFile'] != null) {
    	ccm.openFile = OpenFileCommand.fromJSOG(jsog['openFile']);
    }

    ccm.rewriteRules = rewriteRule;

    return ccm;
  }

  Map toJSOG() {
    Map jsog = new Map();
    jsog['runtimeType'] =
        "info.scce.pyro.core.command.types.CompoundCommandMessage";
    jsog['messageType'] = 'command';
    jsog['graphModelId'] =
        graphModelId != null ? graphModelId : -1;
    jsog["senderId"] = senderId;
    jsog['cmd'] = cmd.toJSOG();
    jsog['type'] = type;
    jsog['highlightings'] = highlightings.map((n) => n.toJSOG()).toList();
    jsog['rewriteRule'] = [];
    return jsog;
  }

  CompoundCommandMessage customCommands() {
    CompoundCommand cc = new CompoundCommand();
    if (this.cmd.queue.length > 1) {
      cc.queue.addAll(this.cmd.queue.sublist(1));
    }
    CompoundCommandMessage ccm = new CompoundCommandMessage(
        this.graphModelId, this.type, cc, this.senderId, this.highlightings);
    ccm.openFile = this.openFile;
    return ccm;
  }
}

class RewriteRule {
  int oldId;
  int newId;

  RewriteRule(Map jsog) {
    oldId = int.parse(jsog['oldId'].toString());
    newId = int.parse(jsog['newId'].toString());
  }
}

class ValidCreatedMessage extends Message {
  int id;

  ValidCreatedMessage(int senderId, this.id) {
    super.senderId = senderId;
  }

  @override
  Map toJSOG() {
    Map jsog = new Map();
    jsog['messageType'] = 'node_created';
    jsog['id'] = id;
    jsog["senderId"] = senderId;
    return jsog;
  }
}

class ValidMessage extends Message {
  ValidMessage(int senderId) {
    super.senderId = senderId;
  }

  @override
  Map toJSOG() {
    Map jsog = new Map();
    jsog['messageType'] = 'valid_message';
    jsog["senderId"] = senderId;
    return jsog;
  }
}
