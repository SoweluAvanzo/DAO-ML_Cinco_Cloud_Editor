import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';
import '../model/core.dart';
import 'base_service.dart';

class ProjectService extends BaseService {

  ProjectService(Router router) : super(router);
  
  Future<PyroProject> getById(String projectId) async {
  	return HttpRequest.request("${getBaseUrl()}/project/${projectId}", method: "GET",requestHeaders: requestHeaders, withCredentials: true).then((response){
      return PyroProject.fromJSON(response.responseText);
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroProject> create(String name, String description, PyroOrganization org, PyroWorkspaceImage image, PyroUser user) async {
    PyroProject pp = new PyroProject();
    pp.name = name;
    pp.description = description;
    pp.owner = user;
    pp.organization = org;
    pp.image = image;
    return HttpRequest.request("${getBaseUrl()}/project/create/private",sendData:jsonEncode(pp.toJSOG(new Map())),method: "POST",requestHeaders: requestHeaders, withCredentials: true).then((response){
      var newProject = PyroProject.fromJSON(response.responseText);
      newProject.owner=user;
      user.ownedProjects.add(newProject);
      print("[PYRO] new project ${newProject.name}");
      return newProject;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<String> deploy(PyroProject project) async {
    return HttpRequest.request("${getBaseUrl()}/project/${project.id}/deployments/private",
        method: "POST",
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      return response.responseText;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<String> stop(PyroProject project) async {
    return HttpRequest.request("${getBaseUrl()}/project/${project.id}/deployments/private",
        method: "DELETE",
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      return response.responseText;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroProject> update(PyroProject project) async {
    return HttpRequest.request("${getBaseUrl()}/project/update/private",sendData:jsonEncode(project.toJSOG(new Map())),method: "POST",requestHeaders: requestHeaders, withCredentials: true).then((response){
      var newProject = PyroProject.fromJSON(response.responseText);
      print("[PYRO] update project ${newProject.name}");
      return newProject;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroUser> remove(PyroProject project, PyroUser user) async {
    return HttpRequest.request("${getBaseUrl()}/project/remove/${project.id}/private", method: "GET", requestHeaders: requestHeaders, withCredentials: true).then((response){
      if(user.ownedProjects.contains(project)){
        print("[PYRO] remove project ${project.name}");
        project.owner = null;
        user.ownedProjects.remove(project);
      }
      return user;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroProject> triggerService(String name,PyroProject project,Map map) async {
    return HttpRequest.request("${getBaseUrl()}/service/trigger/${name}/${project.id}/private", sendData:jsonEncode(map),method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      return project;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Map> checkService(PyroProject project) async {
    return HttpRequest.request("${getBaseUrl()}/service/list/${project.id}/private", method: "GET", requestHeaders: requestHeaders, withCredentials: true).then((response){
      return jsonDecode(response.responseText) as Map;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroProject> triggerAction(String name,PyroProject project) async {
    return HttpRequest.request("${getBaseUrl()}/service/triggeraction/${name}/${project.id}/private",method: "GET", requestHeaders: requestHeaders, withCredentials: true).then((response){
      return project;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

}
