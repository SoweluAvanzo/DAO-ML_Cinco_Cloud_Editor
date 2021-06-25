package de.jabc.cinco.meta.plugin.pyro.frontend.pages.login

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class LoginComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameLoginComponent()'''login_component.dart'''
	
	def fileNameLoginTemplate()'''login_component.html'''
	
	def contentLoginComponent()
	'''
		import 'package:angular/angular.dart';
		import 'package:angular_forms/angular_forms.dart';
		import 'dart:html';
		«IF gc.authCompound === null»
			import 'dart:convert';
		«ENDIF»
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
				document.title = 'login';
				// try if user is already logged in
				fetchUser();
			}
			«IF gc.authCompound === null»
				
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
						window.localStorage['pyro_token'] =
						jsonDecode(response.responseText)['token'];
						correct = true;
						fetchUser();
					}).catchError((e) {
						correct = false;
					});
				}
			«ENDIF»
			
			fetchUser() {
				return _userService.fetchUser()
					.then((u){
				    	if(window.localStorage.containsKey('pyro_redirect') && window.localStorage['pyro_redirect'] != null) {
				    		var url = window.localStorage['pyro_redirect'];
				    		window.localStorage.remove('pyro_redirect');
				    		
				    		window.location.href = url;
				    	} else {
				    		_router.navigate(Routes.organizations.toUrl());    	
				    	}
				    }).catchError((_) {correct = !tried;});
			}
		}
	'''
	
	def contentLoginTemplate()
	'''
		<form class="form-signin" «IF gc.authCompound === null»(ngSubmit)="login(email.value,pw.value,$event)"«ENDIF»>
		    «IF gc.authCompound === null»
		    <h2 class="form-signin-heading">Please sign in</h2>
		    <div *ngIf="!correct" class="alert alert-danger">
		        Bad credentials
		    </div>
		    <label for="inputEmail" class="sr-only">Username</label>
		    <input #email type="email" autocomplete="name" id="inputEmail" class="form-control" placeholder="Username" required autofocus>
		    <label for="inputPassword" class="sr-only">Password</label>
		    <input #pw type="password" current-password id="inputPassword" class="form-control" placeholder="Password" required>
		    <button (click)="login(email.value,pw.value,$event)" class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
		    «ELSE»
		    <a class="btn btn-lg btn-primary btn-block" href="«gc.authCompound.signinURL»?state=«gc.authCompound.state»&scope=«gc.authCompound.scope»&client_id=«gc.authCompound.clientID»&redirect_uri=«gc.authCompound.callbackURL»">Login with «gc.authCompound.name»</a>
		    «ENDIF»
		</form>
	'''
}
