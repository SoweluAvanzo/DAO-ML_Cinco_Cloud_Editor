import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';
import '../model/core.dart';
import 'base_service.dart';

class GitInformationService extends BaseService {

  GitInformationService(Router router) : super(router);

  Future<GitInformation> getByProjectId(int projectId) async {
    return HttpRequest.request("${getBaseUrl()}/project/${projectId}/git-information", method: "GET",requestHeaders: requestHeaders, withCredentials: true).then((response){
      return GitInformation.fromJSON(response.responseText);
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<GitInformation> update(GitInformation gitInformation) async {
    return HttpRequest.request("${getBaseUrl()}/project/${gitInformation.projectId}/git-information", sendData:jsonEncode(gitInformation.toJSOG(new Map())),method: "POST",requestHeaders: requestHeaders, withCredentials: true).then((response){
      var newGitInformation = GitInformation.fromJSON(response.responseText);
      print("[CINCO_CLOUD] update GitInformation for project ${gitInformation.projectId}");
      return newGitInformation;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}