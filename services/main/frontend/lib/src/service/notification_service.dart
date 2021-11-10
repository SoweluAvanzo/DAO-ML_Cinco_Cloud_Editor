import 'package:angular/angular.dart';
import 'dart:async';
import 'dart:html';

enum NotificationType {
  SUCCESS, WARNING, INFO, DANGER
}

class Notification {
	String message;
	NotificationType type;
	Duration duration;
	
	Notification(this.message, this.type, this.duration) {}
}

class NotificationService {
  
  StreamController<Notification> _messagesSc;
  Stream<Notification> _onMessage;
  
  NotificationService() {
  	_messagesSc = StreamController();
  	_onMessage = _messagesSc.stream.asBroadcastStream();
  }
  
  void displayMessage(String msg, NotificationType type) {
  	Notification n = new Notification(msg, type, new Duration(seconds: 4));
  	_messagesSc.add(n);
  }

  void displayLongMessage(String msg, NotificationType type) {
  	Notification n = new Notification(msg, type, new Duration(seconds: 8));
  	_messagesSc.add(n);
  }

  Stream<Notification> get onMessage => _onMessage;
}
