import 'package:angular/angular.dart';
import 'dart:html';
import 'dart:async';
import '../../service/base_service.dart';

@Component(
  selector: 'logout',
  directives: const [coreDirectives],
  templateUrl: 'logout_component.html',
)
class LogoutComponent implements OnInit {
  @override
  void ngOnInit() {
    logout(null);
  }

  void logout(dynamic e) {
    if (e != null) {
      e.preventDefault();
    }
    BaseService.logout().then((status) => {
          new Timer(new Duration(seconds: 2), () {
            window.location.href = BaseService.getBase() + "/#/home";
          })
        });
  }
}
