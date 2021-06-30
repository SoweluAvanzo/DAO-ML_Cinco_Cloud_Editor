import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';

import '../model/core.dart';
import 'base_service.dart';

class EditorGridService extends BaseService {

  EditorGridService(Router router) : super(router);
  
  Future<PyroEditorGrid> get(int projectId) async {
    return HttpRequest.request("${getBaseUrl()}/project/${projectId}/editorGrid", method: "GET", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var grid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] get grid ${grid.id}");
      return grid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroEditorGrid> update(PyroEditorGrid grid) async {
    return HttpRequest.request("${getBaseUrl()}/project/${grid.project.id}/editorGrid/${grid.id}", method: "PUT", sendData:jsonEncode(grid.toJSOG(new Map())), requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] update grid ${grid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
 
  Future<PyroEditorGridItem> createArea(int projectId, int gridId) async {
    return HttpRequest.request("${getBaseUrl()}/project/${projectId}/editorGrid/${gridId}/areas", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      return PyroEditorGridItem.fromJSON(response.responseText);
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
	
  Future<PyroEditorGrid> setLayout(int projectId, int gridId, String layout) async {
    return HttpRequest.request("${getBaseUrl()}/project/${projectId}/editorGrid/${gridId}/setLayout/${layout}", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] reset layout for grid ${updatedGrid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
	  
  Future<PyroEditorGrid> removeArea(int projectId, int gridId, int areaId) async {
    return HttpRequest.request("${getBaseUrl()}/project/${projectId}/editorGrid/${gridId}/areas/${areaId}/remove", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] update grid ${updatedGrid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroEditorGrid> removeWidget(int projectId, int gridId, int widgetId) async {
    return HttpRequest.request("${getBaseUrl()}/project/${projectId}/editorGrid/${gridId}/widgets/${widgetId}/remove", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] update grid ${updatedGrid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroEditorGrid> moveWidget(int projectId, int gridId, int widgetId, int targetAreaId) async {
    return HttpRequest.request("${getBaseUrl()}/project/${projectId}/editorGrid/${gridId}/widgets/${widgetId}/moveTo/${targetAreaId}", method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      var updatedGrid = PyroEditorGrid.fromJSON(response.responseText);
      print("[PYRO] update grid ${updatedGrid.id}");
      return updatedGrid;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}
