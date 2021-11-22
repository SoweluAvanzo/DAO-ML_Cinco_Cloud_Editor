import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';

import '../model/core.dart';
import './base_service.dart';

class OrganizationAccessRightVectorService extends BaseService {

  OrganizationAccessRightVectorService(Router router) : super(router);
  
  Future<List<OrganizationAccessRightVector>> getAll(String orgId) {
  	return HttpRequest.request("${getBaseUrl()}/organization/${orgId}/accessRights", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
  	  	List<OrganizationAccessRightVector> arvs = new List();
  	  	Map<String, dynamic> cache = new Map();
  	  	jsonDecode(response.responseText).forEach((arv){
        	arvs.add(OrganizationAccessRightVector.fromJSOG(cache: cache, jsog: arv));
        });
        return arvs;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<OrganizationAccessRightVector> getMy(String orgId) {
  	return HttpRequest.request("${getBaseUrl()}/organization/${orgId}/accessRights/my", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
        return OrganizationAccessRightVector.fromJSON(response.responseText);
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<OrganizationAccessRightVector> update(OrganizationAccessRightVector arv) {
  	return HttpRequest.request("${getBaseUrl()}/organization/${arv.organization.id}/accessRights/${arv.id}", method: "PUT", sendData:jsonEncode(arv.toJSOG(new Map())), requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
        return OrganizationAccessRightVector.fromJSON(response.responseText);
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}
