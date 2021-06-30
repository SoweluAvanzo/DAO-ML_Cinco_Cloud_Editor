import 'dart:async';
import 'dart:html';
import 'dart:convert';

import 'package:angular_router/angular_router.dart';

import '../model/core.dart';
import './base_service.dart';

class OrganizationService extends BaseService {

  OrganizationService(Router router) : super(router);
  
  Future<List<PyroOrganization>> getAll() {
  	return HttpRequest.request("${getBaseUrl()}/organization", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
  	    List<PyroOrganization> orgs = new List();
  	    Map<String, dynamic> cache = new Map();
        jsonDecode(response.responseText).forEach((org){
        	if(org.containsKey("@ref")){
	            orgs.add(cache[org["@ref"]]);
	        } else {
	            orgs.add(PyroOrganization(cache: cache, jsog: org));
	        }
        });
        return orgs;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganization> getById(String orgId) {
  	return HttpRequest.request("${getBaseUrl()}/organization/${orgId}", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
        return PyroOrganization.fromJSON(response.responseText);
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<List<PyroGraphModelPermissionVector>> getMyPermissions(String orgId) {
  	return HttpRequest.request("${getBaseUrl()}/organization/${orgId}/graphModelPermissions/my", method: "GET", requestHeaders: requestHeaders, withCredentials: true)
  	  .then((response){
  	    List<PyroGraphModelPermissionVector> permissions = new List();
  	    Map<String, dynamic> cache = new Map();
        jsonDecode(response.responseText).forEach((p){
        	permissions.add(PyroGraphModelPermissionVector.fromJSOG(cache: cache, jsog: p));
        });
        return permissions;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganization> create(String name, String description) async {
    PyroOrganization org = new PyroOrganization();
    org.name = name;
    org.description = description;
            
    return HttpRequest.request("${getBaseUrl()}/organization", sendData:jsonEncode(org.toJSOG(new Map())), method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var newOrg = PyroOrganization.fromJSON(response.responseText);
        print("[PYRO] new organization ${newOrg.name}");
        return newOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganization> update(PyroOrganization org) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}", sendData:jsonEncode(org.toJSOG(new Map())), method: "PUT", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var updatedOrg = PyroOrganization.fromJSON(response.responseText);
        print("[PYRO] updated organization ${updatedOrg.name}");
        return updatedOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganization> delete(PyroOrganization org) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}", method: "DELETE", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        return org;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganization> leave(PyroOrganization org) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}/leave", method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        return org;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganization> addOwner(PyroOrganization org, PyroUser user) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}/addOwner", sendData:jsonEncode(user.toJSOG(new Map())), method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var updatedOrg = PyroOrganization.fromJSON(response.responseText);
        print("[PYRO] added user ${user.username} as owner of organization ${updatedOrg.name}");
        return updatedOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganization> addMember(PyroOrganization org, PyroUser user) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}/addMember", sendData:jsonEncode(user.toJSOG(new Map())), method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var updatedOrg = PyroOrganization.fromJSON(response.responseText);
        print("[PYRO] added user ${user.username} as member of organization ${updatedOrg.name}");
        return updatedOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroOrganization> removeUser(PyroOrganization org, PyroUser user) async {
  	return HttpRequest.request("${getBaseUrl()}/organization/${org.id}/removeUser", sendData:jsonEncode(user.toJSOG(new Map())), method: "POST", requestHeaders: requestHeaders, withCredentials: true)
      .then((response){
        var updatedOrg = PyroOrganization.fromJSON(response.responseText);
        print("[PYRO] removed user ${user.username} from organization ${updatedOrg.name}");
        return updatedOrg;
      })
      .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
}
