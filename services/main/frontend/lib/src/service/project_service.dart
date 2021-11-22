import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';
import '../model/core.dart';
import 'base_service.dart';

class ProjectService extends BaseService {

  ProjectService(Router router) : super(router);
  
  Future<Project> getById(String projectId) async {
  	return HttpRequest.request("${getBaseUrl()}/project/${projectId}", method: "GET",requestHeaders: requestHeaders, withCredentials: true).then((response){
      return Project.fromJSON(response.responseText);
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<Project> create(String name, String description, Organization org, WorkspaceImage template, User user) async {
    Project pp = new Project();
    pp.name = name;
    pp.description = description;
    pp.owner = user;
    pp.organization = org;
    pp.template = template;
    return HttpRequest.request("${getBaseUrl()}/project/create/private",sendData:jsonEncode(pp.toJSOG(new Map())),method: "POST",requestHeaders: requestHeaders, withCredentials: true).then((response){
      var newProject = Project.fromJSON(response.responseText);
      newProject.owner=user;
      user.ownedProjects.add(newProject);
      print("[CINCO_CLOUD] new project ${newProject.name}");
      return newProject;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<ProjectDeployment> deploy(Project project) async {
    return HttpRequest.request("${getBaseUrl()}/project/${project.id}/deployments/private",
        method: "POST",
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      return ProjectDeployment.fromJSON(response.responseText);
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<String> stop(Project project) async {
    return HttpRequest.request("${getBaseUrl()}/project/${project.id}/deployments/private",
        method: "DELETE",
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      return response.responseText;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<Project> update(Project project) async {
    return HttpRequest.request("${getBaseUrl()}/project/update/private",sendData:jsonEncode(project.toJSOG(new Map())),method: "POST",requestHeaders: requestHeaders, withCredentials: true).then((response){
      var newProject = Project.fromJSON(response.responseText);
      print("[CINCO_CLOUD] update project ${newProject.name}");
      return newProject;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<User> remove(Project project, User user) async {
    return HttpRequest.request("${getBaseUrl()}/project/remove/${project.id}/private", method: "GET", requestHeaders: requestHeaders, withCredentials: true).then((response){
      if(user.ownedProjects.contains(project)){
        print("[CINCO_CLOUD] remove project ${project.name}");
        project.owner = null;
        user.ownedProjects.remove(project);
      }
      return user;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Project> triggerService(String name,Project project,Map map) async {
    return HttpRequest.request("${getBaseUrl()}/service/trigger/${name}/${project.id}/private", sendData:jsonEncode(map),method: "POST", requestHeaders: requestHeaders, withCredentials: true).then((response){
      return project;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Map> checkService(Project project) async {
    return HttpRequest.request("${getBaseUrl()}/service/list/${project.id}/private", method: "GET", requestHeaders: requestHeaders, withCredentials: true).then((response){
      return jsonDecode(response.responseText) as Map;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Project> triggerAction(String name,Project project) async {
    return HttpRequest.request("${getBaseUrl()}/service/triggeraction/${name}/${project.id}/private",method: "GET", requestHeaders: requestHeaders, withCredentials: true).then((response){
      return project;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

}
