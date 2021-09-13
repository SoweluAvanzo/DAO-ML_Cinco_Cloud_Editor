import 'package:angular/angular.dart';
import '../../../service/notification_service.dart';
import 'dart:async';
import 'dart:html';

@Component(
    selector: 'notification',
    templateUrl: 'notification_component.html',
    styleUrls: const ['notification_component.css'],
    directives: const [coreDirectives],
    providers: const [],
)
class NotificationComponent implements OnInit {

	NotificationService notificationService;
	
	Notification currentNotification;
	
	Timer currentTimer;
	
	bool show = false;
	
	NotificationComponent(this.notificationService) {
	}
	
	@override
	void ngOnInit() {
		notificationService.onMessage.listen(handleOnMessage);
	}

	void close() {
		currentTimer.cancel();
		currentTimer = null;
		show = false;
	}
	
	void handleOnMessage(Notification n) {
		if (currentTimer != null) {
			currentTimer.cancel();
		}
	
		currentNotification = n;
		currentTimer = new Timer(n.duration, close);
		show = true;
	}
	
	String getAlertClass() {
		if (currentNotification == null) return "";
	
		switch (currentNotification.type) {
	    	case NotificationType.INFO:
	      		return "alert-info";
	    	case NotificationType.SUCCESS: 
	      		return "alert-success";
	      	case NotificationType.WARNING:
	      		return "alert-warning";
	    	case NotificationType.DANGER: 
	      		return "alert-danger";
	      	default:
	      		return "";
	  	}
	}
}
