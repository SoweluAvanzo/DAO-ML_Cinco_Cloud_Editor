import '../../../../service/graph_service.dart';
import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import 'dart:async';
@Component(
    selector: 'message-dialog',
    templateUrl: 'message_dialog.html',
    directives: const [coreDirectives,formDirectives]
)
class MessageDialog implements OnInit {

  final closeSC = new StreamController();
  @Output() Stream get close => closeSC.stream;

  @Input()
  Map content;

  @Input()
  String dialog;
  
  String type;

  String message;
  
  String title;

  List<String> buttons;

  final GraphService graphService;

  String closeLabel;

  int timeLeft;

  MessageDialog(GraphService this.graphService)
  {
    buttons = new List();

  }

  String getDialogClass() {
    if(type!=null){
      switch(type.toLowerCase()){
        case "primary":return "#337ab7";
        case "info":return "#5bc0de";
        case "warning":return "#f0ad4e";
        case "danger":return "#d9534f";
      }
    }
    return "ffffff";
  }

  @override
  void ngOnInit()
  {

    timeLeft = -1;
    message = content['message'];
    title = content['title'];
    type = content['type'];
    closeLabel = 'Close';
    if(dialog=='one_answer'){
      buttons.addAll(content['choices']);
      //start timer
      timeLeft = 10;
      new Timer.periodic(new Duration(seconds: 1),(Timer timer){
        timeLeft--;
        if(timeLeft<0){
          closeDialog(null);
          timer.cancel();
        }
      });
    }

  }

  void clickButton(String btn,dynamic e){
    e.preventDefault();
    if(dialog=='one_answer') {
    	closeSC.add({
	    	'answer':btn,
	    	'dialogId':content['id']
	    });
    } else {
	    closeSC.add({
	    	'answer':null
	    });
    }
  }

  void closeDialog(dynamic e)
  {
    e?.preventDefault();
    if(dialog=='one_answer') {
    	closeSC.add({
	    	'answer':null,
	    	'dialogId':content['id']
	    });
    } else {
	    closeSC.add({
	    	'answer':null
	    });
    }
  }


}

