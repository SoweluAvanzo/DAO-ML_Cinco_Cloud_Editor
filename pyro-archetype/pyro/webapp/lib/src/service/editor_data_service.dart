import 'dart:async';
import 'dart:html';

import '../model/core.dart';

/**
 * Service that contains data that is relevant for the editor and its child components
 */
class EditorDataService {
	PyroUser user;
	PyroEditorGrid grid;
	
	StreamController<WebSocket> graphModelWebSocketSC;
	Stream<WebSocket> _graphModelWebSocketStream;
	
	EditorDataService() {
	  graphModelWebSocketSC = StreamController<WebSocket>();
	  _graphModelWebSocketStream = graphModelWebSocketSC.stream.asBroadcastStream();
	}
	
	Stream<WebSocket> get graphModelWebSocketStream => _graphModelWebSocketStream;
}
