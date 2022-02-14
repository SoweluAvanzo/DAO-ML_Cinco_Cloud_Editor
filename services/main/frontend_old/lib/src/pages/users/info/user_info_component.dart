import 'package:angular/angular.dart';
import 'dart:async';
import 'package:angular_forms/angular_forms.dart';

import '../../../model/core.dart';


@Component(
    selector: 'user-info',
    directives: const [coreDirectives, formDirectives],
    templateUrl: 'user_info_component.html'
)
class UserInfoComponent {

  final closeSC = new StreamController();
  @Output() Stream get close => closeSC.stream;

  @Input()
  User user;

}
