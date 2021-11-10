import 'dart:async';
import 'package:angular_router/angular_router.dart';
import 'dart:html';
import 'dart:convert';
import 'base_service.dart';

import '../model/core.dart';

class UserService extends BaseService {
  PyroUser user;

  UserService(Router router) : super(router);

  Future<PyroUser> login(dynamic userJson) async {
    //mockup
    user = PyroUser.fromJSON(userJson);
    print("[PYRO] login as user ${user.username}");
    return new Future.value(user);
  }

  Future<PyroUser> fetchUser() async {
    String basicAuth = 'Basic ' + base64Encode(utf8.encode('philip:12345'));
    requestHeaders['authorization'] = basicAuth;
    return HttpRequest.request("${getBaseUrl()}/user/current/private",
            method: "GET",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      return PyroUser.fromJSON(response.responseText);
    }).catchError((e) {
      throw e;
    });
  }

  Future<List<PyroUser>> findUsers() async {
    return HttpRequest.request("${getBaseUrl()}/users",
            method: "GET",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      if (response.responseText == 'None found') {
        throw new Exception(response.responseText);
      } else {
        var result = jsonDecode(response.responseText);
        List<PyroUser> users = new List();
        result.forEach((u) {
          users.add(PyroUser.fromJSOG(new Map(), u));
        });
        return users;
      }
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroUser> searchUser(String usernameOrEmail) async {
    var search = {"usernameOrEmail": usernameOrEmail};
    return HttpRequest.request("${getBaseUrl()}/users/search",
            method: "POST",
            sendData: jsonEncode(search),
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      if (response.responseText == 'None found') {
        throw new Exception(response.responseText);
      } else {
        PyroUser result =
            PyroUser.fromJSOG(new Map(), jsonDecode(response.responseText));
        return result;
      }
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  Future<PyroUser> addUser(Map map) async {
  	var user = map;
  	return HttpRequest.request("${getBaseUrl()}/register/new/private",
  		method: "POST",
  		sendData: jsonEncode(user),
  		requestHeaders: requestHeaders,
  		withCredentials: true
  	).then((response){
      	PyroUser result = PyroUser.fromJSOG(new Map(), jsonDecode(response.responseText));
      	return result;
      
  	}).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<PyroUser> deleteUser(PyroUser user) async {
    return HttpRequest.request("${getBaseUrl()}/users/${user.id}",
            method: "DELETE",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((_) {
      return user;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroUser> addAdminRole(PyroUser user) async {
    return HttpRequest.request(
            "${getBaseUrl()}/users/${user.id}/roles/addAdmin",
            method: "POST",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      PyroUser result =
          PyroUser.fromJSOG(new Map(), jsonDecode(response.responseText));
      return result;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroUser> removeAdminRole(PyroUser user) async {
    return HttpRequest.request(
            "${getBaseUrl()}/users/${user.id}/roles/removeAdmin",
            method: "POST",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      PyroUser result =
          PyroUser.fromJSOG(new Map(), jsonDecode(response.responseText));
      return result;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroUser> addOrgManagerRole(PyroUser user) async {
    return HttpRequest.request(
            "${getBaseUrl()}/users/${user.id}/roles/addOrgManager",
            method: "POST",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      PyroUser result =
          PyroUser.fromJSOG(new Map(), jsonDecode(response.responseText));
      return result;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroUser> removeOrgManagerRole(PyroUser user) async {
    return HttpRequest.request(
            "${getBaseUrl()}/users/${user.id}/roles/removeOrgManager",
            method: "POST",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      PyroUser result =
          PyroUser.fromJSOG(new Map(), jsonDecode(response.responseText));
      return result;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<PyroUser> loadUser() async {
    return HttpRequest.request("${getBaseUrl()}/user/current/private",
            method: "GET",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      return PyroUser.fromJSON(response.responseText);
    }).catchError((err) {
      if (err is ProgressEvent && err.currentTarget is HttpRequest) {
        HttpRequest request = err.currentTarget;
        if (request.status == 401) {
          return super.handleProgressEvent(err);
        }
        if (request.status == 400) {
          window.console.error("[PYRO] BAD REQUEST");
        }
        if (request.status == 500) {
          window.console.error("[PYRO] SERVER ERROR");
        }
      }
    });
  }

  Future<PyroUser> updateProfile(PyroUser user) async {
    return HttpRequest.request("${getBaseUrl()}/user/current/update/private",
            method: "PUT",
            sendData: jsonEncode(user.toJSOG(new Map())),
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      // received new (possibly changed) token
      window.localStorage['pyro_token'] =
          jsonDecode(response.responseText)['token'];
      return loadUser();
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  // NOTE:ADDED (was missing but empty/unused...deadcode?)
  findUser(String name, String email) {}
}
