import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';

import '../model/core.dart';
import './base_service.dart';

class OrganizationAccessRightVectorService extends BaseService {

  OrganizationAccessRightVectorService(Router router) : super(router);
  
  Future<List<PyroOrganizationAccessRightVector>> getAll(String orgId) {
  	return HttpRequest.request("${getBaseUrl()}/organization/${orgId}/accessRights", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
  	  	List<PyroOrganizationAccessRightVector> arvs = new List();
  	  	Map<String, dynamic> cache = new Map();
  	  	jsonDecode(response.responseText).forEach((arv){
        	arvs.add(PyroOrganizationAccessRightVector.fromJSOG(cache: cache, jsog: arv));
        });
        return arvs;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganizationAccessRightVector> getMy(String orgId) {
  	return HttpRequest.request("${getBaseUrl()}/organization/${orgId}/accessRights/my", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
        return PyroOrganizationAccessRightVector.fromJSON(response.responseText);
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganizationAccessRightVector> update(PyroOrganizationAccessRightVector arv) {
  	return HttpRequest.request("${getBaseUrl()}/organization/${arv.organization.id}/accessRights/${arv.id}", method: "PUT", sendData:jsonEncode(arv.toJSOG(new Map())), requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
        return PyroOrganizationAccessRightVector.fromJSON(response.responseText);
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}
