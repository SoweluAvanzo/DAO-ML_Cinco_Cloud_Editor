import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';
import '../model/core.dart';
import 'base_service.dart';

class WorkspaceImageService extends BaseService {

  WorkspaceImageService(Router router) : super(router);

  Future<List<WorkspaceImage>> search(String query) async {
    return HttpRequest.request(
        "${getBaseUrl()}/image-registry/search?q=${query}",
        method: "GET",
        requestHeaders: requestHeaders,
        withCredentials: true
    )
        .then(transformResponseList)
        .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<List<WorkspaceImage>> getAll() async {
    return HttpRequest.request(
        "${getBaseUrl()}/image-registry/images",
        method: "GET",
        requestHeaders: requestHeaders,
        withCredentials: true
    )
        .then(transformResponseList)
        .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<WorkspaceImage> update(WorkspaceImage image) async {
    return HttpRequest.request(
        "${getBaseUrl()}/image-registry/images/${image.id}",
        sendData: jsonEncode(image.toJSOG(new Map())),
        method: "PUT",
        requestHeaders: requestHeaders,
        withCredentials: true
    )
        .then((response) => WorkspaceImage.fromJSON(response.responseText))
        .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  List<WorkspaceImage> transformResponseList(dynamic response) {
    List<WorkspaceImage> images = new List();
    Map<String, dynamic> cache = new Map();
    jsonDecode(response.responseText).forEach((image) {
      if (image.containsKey("@ref")) {
        images.add(cache[image["@ref"]]);
      } else {
        images.add(WorkspaceImage(cache: cache, jsog: image));
      }
    });
    return images;
  }
}
