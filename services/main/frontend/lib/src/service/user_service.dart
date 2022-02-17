import 'dart:async';
import 'package:angular_router/angular_router.dart';
import 'dart:html';
import 'dart:convert';
import 'base_service.dart';

import '../model/core.dart';

class UserService extends BaseService {
  User user;

  UserService(Router router) : super(router);

  Future<User> login(dynamic userJson) async {
    //mockup
    user = User.fromJSON(userJson);
    print("[CINCO_CLOUD] login as user ${user.username}");
    return new Future.value(user);
  }

  Future<User> fetchUser() async {
    return HttpRequest.request("${getBaseUrl()}/user/current/private",
            method: "GET",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      return User.fromJSON(response.responseText);
    }).catchError((e) {
      throw e;
    });
  }

  Future<List<User>> findUsers() async {
    return HttpRequest.request("${getBaseUrl()}/users",
            method: "GET",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      if (response.responseText == 'None found') {
        throw new Exception(response.responseText);
      } else {
        return transformResponseList(response.responseText, (cache, user) => User(cache: cache, jsog: user));
      }
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<User> searchUser(String usernameOrEmail) async {
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
        User result =
            User.fromJSOG(new Map(), jsonDecode(response.responseText));
        return result;
      }
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  Future<User> addUser(Map map) async {
  	var user = map;
  	return HttpRequest.request("${getBaseUrl()}/register/new/private",
  		method: "POST",
  		sendData: jsonEncode(user),
  		requestHeaders: requestHeaders,
  		withCredentials: true
  	).then((response){
      	User result = User.fromJSOG(new Map(), jsonDecode(response.responseText));
      	return result;
      
  	}).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<User> createUser(dynamic user) async {
    return HttpRequest.request("${getBaseUrl()}/users/private",
        method: "POST",
        sendData: jsonEncode(user),
        requestHeaders: requestHeaders,
        withCredentials: true
    ).then((response) {
      return User.fromJSOG(new Map(), jsonDecode(response.responseText));;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }
  
  Future<User> deleteUser(User user) async {
    return HttpRequest.request("${getBaseUrl()}/users/${user.id}",
            method: "DELETE",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((_) {
      return user;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<User> addAdminRole(User user) async {
    return HttpRequest.request(
            "${getBaseUrl()}/users/${user.id}/roles/addAdmin",
            method: "POST",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      User result =
          User.fromJSOG(new Map(), jsonDecode(response.responseText));
      return result;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<User> removeAdminRole(User user) async {
    return HttpRequest.request(
            "${getBaseUrl()}/users/${user.id}/roles/removeAdmin",
            method: "POST",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      User result =
          User.fromJSOG(new Map(), jsonDecode(response.responseText));
      return result;
    }).catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<User> loadUser() async {
    return HttpRequest.request("${getBaseUrl()}/user/current/private",
            method: "GET",
            requestHeaders: requestHeaders,
            withCredentials: true)
        .then((response) {
      return User.fromJSON(response.responseText);
    }).catchError((err) {
      if (err is ProgressEvent && err.currentTarget is HttpRequest) {
        HttpRequest request = err.currentTarget;
        if (request.status == 401) {
          return super.handleProgressEvent(err);
        }
        if (request.status == 400) {
          window.console.error("[CINCO_CLOUD] BAD REQUEST");
        }
        if (request.status == 500) {
          window.console.error("[CINCO_CLOUD] SERVER ERROR");
        }
      }
    });
  }

  Future<User> updateProfile(UpdateCurrentUserInput input) async {
    return HttpRequest.request("${getBaseUrl()}/user/current/private",
            method: "PUT",
            sendData: input.toJSON(),
            requestHeaders: requestHeaders,
            withCredentials: true
    )
        .then((response) => _renewAuthToken(response))
        .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  Future<User> updatePassword(UpdateCurrentUserPasswordInput input) async {
    return HttpRequest.request("${getBaseUrl()}/user/current/password/private",
        method: "PUT",
        sendData: input.toJSON(),
        requestHeaders: requestHeaders,
        withCredentials: true
    )
        .then((response) => _renewAuthToken(response))
        .catchError(super.handleProgressEvent, test: (e) => e is ProgressEvent);
  }

  // NOTE:ADDED (was missing but empty/unused...deadcode?)
  findUser(String name, String email) {}

  Future<User> _renewAuthToken(dynamic response) {
    window.localStorage['pyro_token'] = jsonDecode(response.responseText)['token'];
    return loadUser();
  }
}
