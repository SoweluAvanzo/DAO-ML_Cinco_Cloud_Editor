import 'dart:convert';

class WebSocketEvents {
  static final String UPDATE_BUILD_JOB_STATUS = 'project:buildJobs:updateStatus';
  static final String UPDATE_POD_DEPLOYMENT_STATUS = 'project:podDeploymentStatus';
}

class WebSocketMessage {
  int senderId;
  String event;
  dynamic content;

  WebSocketMessage(dynamic jsog) {
    senderId = jsog['senderId'];
    event = jsog['event'];
    content = jsog['content'];
  }

  static WebSocketMessage fromJSON(String json) {
    return WebSocketMessage(jsonDecode(json));
  }
}
