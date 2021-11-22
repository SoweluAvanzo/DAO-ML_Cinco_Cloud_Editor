import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';

import '../model/core.dart';
import './base_service.dart';

class OrganizationService extends BaseService {

  OrganizationService(Router router) : super(router);
  
  Future<List<Organization>> getAll() {
  	return HttpRequest.request("${getBaseUrl()}/organization", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
  	    List<Organization> orgs = new List();
  	    Map<String, dynamic> cache = new Map();
        jsonDecode(response.responseText).forEach((org){
        	if(org.containsKey("@ref")){
	            orgs.add(cache[org["@ref"]]);
	        } else {
	            orgs.add(Organization(cache: cache, jsog: org));
	        }
        });
        return orgs;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Organization> getById(String orgId) {
  	return HttpRequest.request("${getBaseUrl()}/organization/${orgId}", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
        return Organization.fromJSON(response.responseText);
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<Organization> create(String name, String description) async {
    Organization org = new Organization();
    org.name = name;
    org.description = description;
            
    return HttpRequest.request("${getBaseUrl()}/organization", sendData:jsonEncode(org.toJSOG(new Map())), method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var newOrg = Organization.fromJSON(response.responseText);
        print("[CINCO_CLOUD] new organization ${newOrg.name}");
        return newOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Organization> update(Organization org) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}", sendData:jsonEncode(org.toJSOG(new Map())), method: "PUT", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var updatedOrg = Organization.fromJSON(response.responseText);
        print("[CINCO_CLOUD] updated organization ${updatedOrg.name}");
        return updatedOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Organization> delete(Organization org) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}", method: "DELETE", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        return org;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Organization> leave(Organization org) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}/leave", method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        return org;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Organization> addOwner(Organization org, User user) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}/addOwner", sendData:jsonEncode(user.toJSOG(new Map())), method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var updatedOrg = Organization.fromJSON(response.responseText);
        print("[CINCO_CLOUD] added user ${user.username} as owner of organization ${updatedOrg.name}");
        return updatedOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Organization> addMember(Organization org, User user) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}/addMember", sendData:jsonEncode(user.toJSOG(new Map())), method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var updatedOrg = Organization.fromJSON(response.responseText);
        print("[CINCO_CLOUD] added user ${user.username} as member of organization ${updatedOrg.name}");
        return updatedOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<Organization> removeUser(Organization org, User user) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}/removeUser", sendData:jsonEncode(user.toJSOG(new Map())), method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var updatedOrg = Organization.fromJSON(response.responseText);
        print("[CINCO_CLOUD] removed user ${user.username} from organization ${updatedOrg.name}");
        return updatedOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}
