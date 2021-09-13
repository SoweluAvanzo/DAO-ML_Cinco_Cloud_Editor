import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';

import '../model/core.dart';
import './base_service.dart';

class GraphModelPermissionVectorService extends BaseService {

  GraphModelPermissionVectorService(Router router) : super(router);
  
  Future<List<PyroGraphModelPermissionVector>> getAll() {
  	return HttpRequest.request("${getBaseUrl()}/graphModelPermissions", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
  	  	List<PyroGraphModelPermissionVector> permissions = new List();
  	  	Map<String, dynamic> cache = new Map();
  	  	jsonDecode(response.responseText).forEach((permission){
        	permissions.add(PyroGraphModelPermissionVector.fromJSOG(cache: cache, jsog: permission));
        });
        return permissions;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<List<PyroGraphModelPermissionVector>> getMy() {
  	return HttpRequest.request("${getBaseUrl()}/graphModelPermissions/my", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
        List<PyroGraphModelPermissionVector> permissions = new List();
  	  	Map<String, dynamic> cache = new Map();
  	  	jsonDecode(response.responseText).forEach((permission){
        	permissions.add(PyroGraphModelPermissionVector.fromJSOG(cache: cache, jsog: permission));
        });
        return permissions;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroGraphModelPermissionVector> update(PyroGraphModelPermissionVector permission) {
  	return HttpRequest.request("${getBaseUrl()}/graphModelPermissions/${permission.id}", method: "PUT", sendData:jsonEncode(permission.toJSOG(new Map())), requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
        return PyroGraphModelPermissionVector.fromJSON(response.responseText);
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}
