import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';
import '../model/core.dart';
import 'base_service.dart';

class WorkspaceImageBuildJobService extends BaseService {

  WorkspaceImageBuildJobService(Router router) : super(router);

  Future<Page<WorkspaceImageBuildJob>> getAll(int projectId, int page, int size) async {
    return HttpRequest.request(
        "${getBaseUrl()}/projects/${projectId}/build-jobs/private?page=${page}&size=${size}",
        method: "GET",
        requestHeaders: requestHeaders,
        withCredentials: true
    )
        .then(transformPage)
        .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<WorkspaceImageBuildJob> update(int projectId, WorkspaceImageBuildJob job) async {
    return HttpRequest.request(
        "${getBaseUrl()}/projects/${projectId}/build-jobs/private",
        method: "PUT",
        requestHeaders: requestHeaders,
        withCredentials: true,
        sendData:jsonEncode(job.toJSOG(new Map()))
    ).then((response) {
      var newJob = WorkspaceImageBuildJob.fromJSON(response.responseText);
      print("[CINCO_CLOUD] update build job ${newJob.id}");
      return newJob;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<WorkspaceImageBuildJob> remove(int projectId, WorkspaceImageBuildJob job) async {
    return HttpRequest.request(
        "${getBaseUrl()}/projects/${projectId}/build-jobs/${job.id}/private",
        method: "DELETE",
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      return job;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<WorkspaceImageBuildJob> abort(int projectId, WorkspaceImageBuildJob job) async {
    return HttpRequest.request(
        "${getBaseUrl()}/projects/${projectId}/build-jobs/${job.id}/abort/private",
        method: "POST",
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      var updatedJob = WorkspaceImageBuildJob.fromJSON(response.responseText);
      print("[CINCO_CLOUD] update build job ${updatedJob.id}");
      return updatedJob;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Page<WorkspaceImageBuildJob> transformPage(dynamic response) {
    Map<String, dynamic> cache = new Map();
    dynamic pageJsog = jsonDecode(response.responseText);

    Page<WorkspaceImageBuildJob> page = new Page(cache: cache, jsog: pageJsog, resolveTypeFn: (item) {
      return WorkspaceImageBuildJob(cache: cache, jsog: item);
    });

    return page;
  }
}
