import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'dart:html';
import 'dart:convert';
import '../../service/user_service.dart';
import 'package:angular_router/angular_router.dart';
import '../../routes.dart';

@Component(
  selector: 'login',
  directives: const [coreDirectives, formDirectives],
  styleUrls: const ['login_component.css'],
  templateUrl: 'login_component.html'
)
class LoginComponent implements OnInit{

	bool correct = true;
	bool tried = false;
	
	final UserService _userService;
	final Router _router;
	
	LoginComponent(this._userService,this._router){}

	ngOnInit() {
		document.title = 'Cinco Cloud | Login';
		// try if user is already logged in
		fetchUser();
	}
	
	void login(String username, String pw, dynamic e) {
		e.preventDefault();
		tried = true;
		correct = true;
		Map<String, String> body = new Map();
		body['email'] = username;
		body['password'] = pw;
		HttpRequest.request('${_userService.getBaseUrl()}/user/current/login',
			method: "POST",
			requestHeaders: _userService.requestHeaders,
			sendData: jsonEncode(body)
		).then((response) {
			window.localStorage['pyro_token'] = jsonDecode(response.responseText)['token'];
			correct = true;
			fetchUser();
		}).catchError((e) {
			correct = false;
		});
	}

	fetchUser() {
		return _userService.fetchUser()
			.then((u){
				var redirectUrl = window.localStorage['pyro_redirect'];
				if (redirectUrl != null && redirectUrl.trim() != "" && !redirectUrl.endsWith("/home/login")) {
					window.localStorage.remove('pyro_redirect');
					window.location.href = redirectUrl;
				} else {
					_router.navigate(Routes.overview.toUrl());
				}
			}).catchError((_) {correct = !tried;});
		}
	}
