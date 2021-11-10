import 'dart:async';
import 'dart:html';
import 'dart:convert';
import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../../../model/core.dart';
import '../../../service/organization_service.dart';

@Component(
    selector: 'edit-organization',
    directives: const [coreDirectives, formDirectives],
    templateUrl: 'edit_organization_component.html',
    providers: const [ClassProvider(OrganizationService)]
)
class EditOrganizationComponent {

  final dismissSC = new StreamController();
  @Output() Stream get dismiss => dismissSC.stream;
  
  final updatedSC = new StreamController();
  @Output() Stream get updated => updatedSC.stream;

  OrganizationService _organizationService;
  
  PyroOrganization organization;
  bool show = false;
  String name;
  String description;

  EditOrganizationComponent(this._organizationService) {
  }
  
  void updateOrganization() {
   	organization.name = name;
  	organization.description = description;
  
  	_organizationService.update(organization)
  	  .then((updatedOrg){
  		updatedSC.add(updatedOrg);
  	  }).catchError((err){
  		window.console.log(err);
  	  });
  }
  
  void open(PyroOrganization org) {
    organization = org;
    name = org.name;
    description = org.description;
  	show = true;
  }
  
  void close() {
    organization = null;
  	show = false;
  }
  
}
