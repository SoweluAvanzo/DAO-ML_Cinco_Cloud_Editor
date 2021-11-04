import 'dart:convert';
import 'dart:html';
import 'package:angular_router/angular_router.dart';

class BaseService {
  final Router _router;
  static const String tokenKey = 'pyro_token';

  BaseService(this._router) {}

  Map<String, String> get requestHeaders {
    return REQUEST_HEADERS;
  }

  static Map<String, String> get REQUEST_HEADERS {
    Map<String, String> rh = {'Content-Type': 'application/json'};
    /*
      This is nolonger needed:
      
      if (window.localStorage.containsKey(tokenKey)) {
        rh['Authorization'] = 'Bearer ' + window.localStorage[tokenKey];
      }
    */
    return rh;
  }

  dynamic handleProgressEvent(dynamic e) {
    if (e is ProgressEvent && e.currentTarget is HttpRequest) {
      HttpRequest r = e.currentTarget as HttpRequest;
      if (r.status == 401) {
        window.localStorage.remove(tokenKey);
        window.localStorage['pyro_redirect'] = window.location.href;
        // FIXME: Routes.login.toUrl() does not return "/home/login" but "/login" which does not exist and nothing happens
        _router.navigate("/home/login");
      }
    }
    throw e;
  }
  

  String getBaseUrl({String protocol: null}) {
  	return '${getBase(protocol:protocol)}/api';
  }

  static String getBase({String protocol: null}) {
  	if(window.location.protocol.contains("https")) {
    	return '${protocol == null ? window.location.protocol : protocol+"s"}//${window.location.host}${getBaseHref()}';
    }
    return '${protocol == null ? window.location.protocol : protocol}//${window.location.host}${getBaseHref()}';
  }

  static String getBaseHref() {
    var baseHref = window.document.querySelector('base').getAttribute('href');
    var cleanBaseHref = baseHref.substring(0, baseHref.length - 1);
    print("BASE_HREF: "+cleanBaseHref);
    return cleanBaseHref;
  }
  
  static String getUrl({String protocol: null}) {
    return '${getBase(protocol:protocol)}/api';
  }

  static Future<dynamic> logout() async {
    if (window.localStorage.containsKey(tokenKey)) {
      // logout on the backend-side
      return await HttpRequest.request(getUrl() + "/user/current/logout",
              method: "GET",
              requestHeaders: REQUEST_HEADERS,
              withCredentials: true)
          .then((response) {
        // destroy token
        window.localStorage.remove(tokenKey);
        return response.status;
      });
    }
  }

  /**
   * requesting a onetime-ticket from an authorized login,
   * realizing a mapping between the ticket and the logged in user,
   * resulting in a security expanse to e.g websockets,
   * without exposing the jwt token (pyro_token).
   */
  static Future<dynamic> getTicket() async {
    return await HttpRequest.request(getUrl() + "/ticket",
            method: "GET",
            requestHeaders: REQUEST_HEADERS,
            withCredentials: true)
        .then((response) {
          return json.decode(response.responseText)['ticket'];
        });
  }

  @deprecated
  static String getAuthToken() {
    if (window.localStorage.containsKey(tokenKey)) {
      return 'Bearer ' + window.localStorage[tokenKey];
    }
    return null;
  }
}
