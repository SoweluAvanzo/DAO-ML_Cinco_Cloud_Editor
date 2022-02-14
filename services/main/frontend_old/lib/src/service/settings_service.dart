import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';

import '../model/core.dart';
import './base_service.dart';

class SettingsService extends BaseService {

  SettingsService(Router router) : super(router);

  Future<Settings> get() async {
    return HttpRequest.request("${getBaseUrl()}/settings/public", method: "GET", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var settings = Settings.fromJSON(response.responseText);
      return settings;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<Settings> update(Settings settings) async {
    return HttpRequest.request("${getBaseUrl()}/settings", method: "PUT", sendData:jsonEncode(settings.toJSOG(new Map())), requestHeaders: requestHeaders, withCredentials: true).then((response){
	  var updatedSettings = Settings.fromJSON(response.responseText);
      return updatedSettings;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}
