import 'dart:async';
import 'dart:html';

import 'package:CincoCloud/src/service/notification_service.dart';
import 'package:CincoCloud/src/service/user_service.dart';
import 'package:CincoCloud/src/service/base_service.dart';

class ProjectWebSocketFactoryService {

  UserService _userService;
  NotificationService _notificationService;

  ProjectWebSocketFactoryService(this._userService, this._notificationService);

  Future<WebSocket> create(int projectId) {
    var completer = new Completer<WebSocket>();

    BaseService.getTicket()
        .then((ticket) {
          var socket = new WebSocket(
              '${_userService.getBaseUrl(protocol: 'ws:')}/ws/project/${projectId}/${ticket}/private'
          );

          socket.onOpen.listen((e) {
            window.console.debug("[CINCO_CLOUD] Open projectWebsocket");
            completer.complete(socket);
          });

          socket.onError.listen((e) {
            _notificationService.displayMessage("Failed to connect with websocket.", NotificationType.DANGER);
            window.console.debug("[CINCO_CLOUD] Error on projectWebsocket: ${e.toString()}");
            completer.completeError(e);
          });
        })
        .catchError((e) {
      completer.completeError(e);
    });

    return completer.future;
  }
}
