import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';
import '../model/core.dart';
import 'base_service.dart';

class WorkspaceImageBuildJobService extends BaseService {

  WorkspaceImageBuildJobService(Router router) : super(router);

  Future<List<PyroWorkspaceImageBuildJob>> getAll(int projectId, int page, int size) async {
    return HttpRequest.request(
        "${getBaseUrl()}/projects/${projectId}/build-jobs/private?page=${page}&size=${size}",
        method: "GET",
        requestHeaders: requestHeaders,
        withCredentials: true
    )
        .then(transformResponseList)
        .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroWorkspaceImageBuildJob> update(int projectId, PyroWorkspaceImageBuildJob job) async {
    return HttpRequest.request(
        "${getBaseUrl()}/projects/${projectId}/build-jobs/private",
        method: "PUT",
        requestHeaders: requestHeaders,
        withCredentials: true,
        sendData:jsonEncode(job.toJSOG(new Map()))
    ).then((response) {
      var newJob = PyroWorkspaceImageBuildJob.fromJSON(response.responseText);
      print("[PYRO] update build job ${newJob.id}");
      return newJob;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroWorkspaceImageBuildJob> remove(int projectId, PyroWorkspaceImageBuildJob job) async {
    return HttpRequest.request(
        "${getBaseUrl()}/projects/${projectId}/build-jobs/${job.id}/private",
        method: "DELETE",
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      return job;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroWorkspaceImageBuildJob> abort(int projectId, PyroWorkspaceImageBuildJob job) async {
    return HttpRequest.request(
        "${getBaseUrl()}/projects/${projectId}/build-jobs/${job.id}/abort/private",
        method: "POST",
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      var updatedJob = PyroWorkspaceImageBuildJob.fromJSON(response.responseText);
      print("[PYRO] update build job ${updatedJob.id}");
      return updatedJob;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  List<PyroWorkspaceImageBuildJob> transformResponseList(dynamic response) {
    List<PyroWorkspaceImageBuildJob> jobs = new List();
    Map<String, dynamic> cache = new Map();
    jsonDecode(response.responseText).forEach((job) {
      if (job.containsKey("@ref")) {
        jobs.add(cache[job["@ref"]]);
      } else {
        jobs.add(PyroWorkspaceImageBuildJob(cache: cache, jsog: job));
      }
    });
    return jobs;
  }
}