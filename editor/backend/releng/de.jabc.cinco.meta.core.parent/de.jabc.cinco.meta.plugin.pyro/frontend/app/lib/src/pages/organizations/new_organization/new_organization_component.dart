import 'dart:async';
import 'dart:html';
import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../../../model/core.dart';
import '../../../service/organization_service.dart';

@Component(
    selector: 'new-organization',
    directives: const [coreDirectives, formDirectives],
    templateUrl: 'new_organization_component.html',
    providers: const [ClassProvider(OrganizationService)]
)
class NewOrganizationComponent {

  final dismissSC = new StreamController();
  @Output() Stream get dismiss => dismissSC.stream;
  
  final createdSC = new StreamController();
  @Output() Stream get created => createdSC.stream;

  OrganizationService _organizationService;
  
  bool show = false;

  NewOrganizationComponent(this._organizationService) {
  }
  
  void createNewOrganization(String name, String description) {
  	_organizationService.create(name, description)
  	  .then((newOrg){
  		createdSC.add(newOrg);
  	  }).catchError((err){
  		window.console.log(err);
  	  });
  }
  
  void open() {
  	show = true;
  }
  
  void close() {
  	show = false;
  }
  
}
