import 'package:angular/angular.dart';
import 'dart:async';
import 'package:angular_forms/angular_forms.dart';
import 'dart:html';
import 'dart:convert';
import 'package:angular_router/angular_router.dart';
import '../main/routes.dart';
import '../main/route_paths.dart';
import '../../service/base_service.dart';

@Component(
  selector: 'registration',
  styleUrls: const ['registration_component.css'],
  directives: const [coreDirectives, formDirectives,routerDirectives],
  templateUrl: 'registration_component.html',
  exports: const [RoutePaths, Routes]
)
class RegistrationComponent {

  bool pending = false;
  bool registrationConfirmed = false;
  bool registrationInvalid = false;
  bool pwNotEqual = false;
  bool notFilled = false;
  bool passwordToShort = false;
  final Router _router;
  BaseService _baseService;

  RegistrationComponent(Router this._router)
  {
  	_baseService = new BaseService(_router);
  }

  void register(String name, String email,String username,String pw1,String pw2,dynamic e)
  {
    e.preventDefault();
    //check all entered
    notFilled = false;
    if(name.isEmpty || email.isEmpty || username.isEmpty || pw1.isEmpty || pw2.isEmpty){
      notFilled = true;
      return;
    }
    //check password length
    passwordToShort = false;
    if(pw1.length<5||pw2.length<5){
      passwordToShort = true;
      return;
    }
    //check password equallity
    pwNotEqual = false;
    if(pw1!=pw2){
      pwNotEqual = true;
      return;
    }


    pending = true;
    registrationConfirmed = false;
    registrationInvalid = false;
    var data = {
      'username' : username,
      'password' : pw1,
      'passwordConfirm' : pw2,
      'name' : name,
      'email' : email
    };
    HttpRequest.request("${_baseService.getBaseUrl()}/register/new/public",sendData:jsonEncode(data),method: "POST",requestHeaders: _baseService.requestHeaders).then((response){
      registrationConfirmed = true;
    }).catchError((_){
      registrationInvalid = true;
    }).whenComplete(()=>pending=false);


  }
}

