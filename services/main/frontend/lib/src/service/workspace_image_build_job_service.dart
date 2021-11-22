import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';
import '../model/core.dart';
import 'base_service.dart';

class WorkspaceImageBuildJobService extends BaseService {

  WorkspaceImageBuildJobService(Router router) : super(router);

  Future<List<WorkspaceImageBuildJob>> getAll(int projectId, int page, int size) async {
    return HttpRequest.request(
        "${getBaseUrl()}/projects/${projectId}/build-jobs/private?page=${page}&size=${size}",
        method: "GET",
        requestHeaders: requestHeaders,
        withCredentials: true
    )
        .then(transformResponseList)
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

  List<WorkspaceImageBuildJob> transformResponseList(dynamic response) {
    List<WorkspaceImageBuildJob> jobs = new List();
    Map<String, dynamic> cache = new Map();
    jsonDecode(response.responseText).forEach((job) {
      if (job.containsKey("@ref")) {
        jobs.add(cache[job["@ref"]]);
      } else {
        jobs.add(WorkspaceImageBuildJob(cache: cache, jsog: job));
      }
    });
    return jobs;
  }
}
