import 'dart:async';
import 'dart:html';

import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';
import '../model/core.dart';
import '../model/check.dart';
import 'base_service.dart';

class CheckService extends BaseService {

  Map<int,StreamController> graphModelUpdate = new Map();

  Map<String,CheckResults> results = new Map();
  Map<int,StreamController<CheckResults>> checkListeners = new Map();
  
  CheckService(Router router) : super(router);

  Stream register(int id) {
    if(graphModelUpdate.containsKey(id)){
      graphModelUpdate[id].close();
    }
    graphModelUpdate[id] = new StreamController();
    return graphModelUpdate[id].stream;
  }

  void recheck(int id) {
    if(graphModelUpdate.containsKey(id)) {
      graphModelUpdate[id].add({});
    } else {
      print("NO UPDATE");
    }
  }

  Stream<CheckResults> listen(int id) {
    checkListeners[id] = new StreamController<CheckResults>();
    return checkListeners[id].stream;
  }

  Future<CheckResults> read(String type,GraphModel gm) async {
    return HttpRequest.request(
	    	"${getBaseUrl()}/${type}/checks/${gm.id.toString()}/private",
	    	method: "GET",
	        requestHeaders: requestHeaders,
	        withCredentials: true
        )
    	.then((response){
      		var cr = CheckResults.fromJSON(response.responseText);
          //refresh checks on canvas
          if(checkListeners.containsKey(gm.id)) {
            checkListeners[gm.id].add(cr);
          }
      		return cr;
    	})
    	.catchError((e) {
    		// ignore 404 responses
    		if (e.currentTarget.status != 404) {
    	  		super.handleProgressEvent(e);
    	  	}
    	}, test: (e) => e is ProgressEvent);
  }
}
