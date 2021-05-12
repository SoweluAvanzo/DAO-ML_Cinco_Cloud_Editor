import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';
import '../model/core.dart';
import 'base_service.dart';

class WorkspaceImageService extends BaseService {

  WorkspaceImageService(Router router) : super(router);

  Future<List<PyroWorkspaceImage>> getAll(String query) async {
    return HttpRequest.request(
        "${getBaseUrl()}/image-registry/images?q=${query}",
        method: "GET",
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      List<PyroWorkspaceImage> images = new List();
      Map<String, dynamic> cache = new Map();
      jsonDecode(response.responseText).forEach((image) {
        if (image.containsKey("@ref")) {
          images.add(cache[image["@ref"]]);
        } else {
          images.add(PyroWorkspaceImage(cache: cache, jsog: image));
        }
      });
      return images;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}