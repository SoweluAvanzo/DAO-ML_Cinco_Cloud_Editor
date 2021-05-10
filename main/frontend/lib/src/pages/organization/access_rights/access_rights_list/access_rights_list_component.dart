import 'dart:html';
import 'dart:async';
import 'package:angular/angular.dart';
import 'package:ng_bootstrap/ng_bootstrap.dart';

import '../../../../model/core.dart';
import '../../../../service/organization_access_right_vector_service.dart';
import '../../../../pipes/normalize_enum_string_pipe.dart';

@Component(
  selector: 'access-rights-list',
  templateUrl: 'access_rights_list_component.html',
  directives: const [coreDirectives, bsDirectives],
  styleUrls: const ['access_rights_list_component.css'],
  providers: const[ClassProvider(OrganizationAccessRightVectorService)],
  pipes: [NormalizeEnumStringPipe]
)
class AccessRightsListComponent implements OnInit {

  final _updatedSc = new StreamController();
  @Output() Stream get updated => _updatedSc.stream;
  
  @Input()
  PyroUser user;
  
  @Input()
  PyroOrganizationAccessRightVector accessRights;
  
  List<String> availableArs = new List();
  
  OrganizationAccessRightVectorService _orgArvService;
    
  AccessRightsListComponent(this._orgArvService) {
  }

  @override
  void ngOnInit() {     
  	availableArs.add(PyroOrganizationAccessRight.CREATE_PROJECTS);
  	availableArs.add(PyroOrganizationAccessRight.EDIT_PROJECTS);
  	availableArs.add(PyroOrganizationAccessRight.DELETE_PROJECTS);
  	for (String ar in accessRights.accessRights) {
	  availableArs.remove(ar);
  	}
  }
  
  void addAccessRight(dynamic e, String ar) {
  	e.preventDefault();
  	accessRights.accessRights.add(ar);
  	availableArs.remove(ar);
  	_orgArvService.update(accessRights).then((updatedArv){
  	  _updatedSc.add(updatedArv);
  	});
  }
  
  void removeAccessRight(String ar) {
  	accessRights.accessRights.remove(ar);
  	availableArs.add(ar);
  	_orgArvService.update(accessRights).then((updatedArv){
  	  _updatedSc.add(updatedArv);
  	});
  }
}
