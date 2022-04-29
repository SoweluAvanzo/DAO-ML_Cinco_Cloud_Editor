import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';

import '../model/core.dart';
import 'base_service.dart';

class EditorGridService extends BaseService {

  EditorGridService(Router router) : super(router);
  
  Future<PyroEditorGrid> get() async {
    return HttpRequest.request("${getBaseUrl()}/editorGrid", method: "GET", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var grid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] get grid ${grid.id}");
      return grid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroEditorGrid> update(PyroEditorGrid grid) async {
    return HttpRequest.request("${getBaseUrl()}/editorGrid/${grid.id}", method: "PUT", sendData:jsonEncode(grid.toJSOG(new Map())), requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] update grid ${grid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
 
  Future<PyroEditorGridItem> createArea(int gridId) async {
    return HttpRequest.request("${getBaseUrl()}/editorGrid/${gridId}/areas", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      return PyroEditorGridItem.fromJSON(response.responseText);
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
	
  Future<PyroEditorGrid> setLayout(int gridId, String layout) async {
    return HttpRequest.request("${getBaseUrl()}/editorGrid/${gridId}/setLayout/${layout}", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] reset layout for grid ${updatedGrid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
	  
  Future<PyroEditorGrid> removeArea(int gridId, int areaId) async {
    return HttpRequest.request("${getBaseUrl()}/editorGrid/${gridId}/areas/${areaId}/remove", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] update grid ${updatedGrid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroEditorGrid> removeWidget(int gridId, int widgetId) async {
    return HttpRequest.request("${getBaseUrl()}/editorGrid/${gridId}/widgets/${widgetId}/remove", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] update grid ${updatedGrid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroEditorGrid> moveWidget(int gridId, int widgetId, int targetAreaId) async {
    return HttpRequest.request("${getBaseUrl()}/editorGrid/${gridId}/widgets/${widgetId}/moveTo/${targetAreaId}", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] update grid ${updatedGrid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}
