package de.jabc.cinco.meta.plugin.pyro.frontend.pages.admin.user_management.users

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class UsersComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameComponent()'''users_component.dart'''
	
	def fileNameTemplate()'''users_component.html'''
	
	def contentComponent()
	'''
		import 'package:angular/angular.dart';
		import 'dart:html';
		
		import '../../../../model/core.dart';
		import '../../../../service/user_service.dart';
		import '../../../../service/notification_service.dart';
		import '../../../../pipes/normalize_enum_string_pipe.dart';
		«IF gc.cpd.hasClosedRegistration»
			import '../create_user/create_user_component.dart';
		«ENDIF»
		
		@Component(
		  selector: 'users',
		  templateUrl: 'users_component.html',
		  directives: const [coreDirectives«IF gc.cpd.hasClosedRegistration»,CreateUserComponent«ENDIF»],
		  providers: const [],
		  pipes: [NormalizeEnumStringPipe]
		)
		class UsersComponent implements OnInit {
		
		  final UserService _userService;
		  final NotificationService _notificationService;
		  
		  List<PyroUser> users = new List();
		        
		  UsersComponent(this._userService, this._notificationService) {
		  }
		
		  @override
		  void ngOnInit() {  	
			_userService.findUsers().then((users) {
			  this.users = users;
			});  		
		  }     
		  
		  void deleteUser(PyroUser user) {
		  	_userService.deleteUser(user).then((_) {
		  		users.removeWhere((u) => u.id == user.id);
		  		_notificationService.displayMessage("User ${user.username} has been deleted.", NotificationType.SUCCESS);
		  	});
		  }
			«IF gc.cpd.hasClosedRegistration»
				bool showCreateUserModal = false;
				
				void addUser(dynamic e) {
					if(e is Map) {
						_userService.addUser(e).then((u){
							users.add(u);
							_notificationService.displayMessage("User ${u.username} has been created.", NotificationType.SUCCESS);
						}).catchError((err) {
							window.console.log(err);
						});
					}
					showCreateUserModal=false;
				}
			«ENDIF»
		}
	'''
	
	def contentTemplate()
	'''
		<div>
			<div class="mb-3 d-flex flex-row align-items-center">
				<h4 class="w-100 m-0">Users</h4>
				«IF gc.cpd.hasClosedRegistration»
					<div>
						<button class="btn btn-sm btn-primary" (click)="showCreateUserModal = true">Add User</button>
					</div>
				«ENDIF»
			</div>
			<table class="table table-striped pyro-table" *ngIf="users.length > 0">
				<thead>
					<tr>
						<th width="1">#</th>
						<th>Username</th>
						<th>Email</th>
						<th>Roles</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<tr *ngFor="let user of users">
						<td>{{user.id}}</td>
						<td>{{user.username}}</td>
						<td>{{user.email}}</td>
						<td>
							<span class="badge badge-secondary mr-1 mb-1" *ngFor="let role of user.systemRoles">{{role|normalizeEnumString}}</span>
						</td>
						<td class="text-right">
							<div class="btn-group btn-group-sm">
								<button class="btn btn-sm btn-dark" (click)="deleteUser(user)">
									<i class="fas fa-trash"></i>
								</button>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		«IF gc.cpd.hasClosedRegistration»
			<create-user
			    *ngIf="showCreateUserModal"
			    (close)="addUser($event)"
			>
			</create-user>
		«ENDIF»
	'''
}
