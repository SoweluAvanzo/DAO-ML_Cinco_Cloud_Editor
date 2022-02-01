import 'dart:core';

import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../../../model/core.dart';
import '../../../service/base_service.dart';
import '../../../service/organization_service.dart';
import '../../../service/notification_service.dart';
import '../../../filesupport/fileselect.dart';
import '../../../filesupport/fileuploader.dart';

@Component(
  selector: 'appearance',
  templateUrl: 'appearance_component.html',
  directives: const [coreDirectives, formDirectives, FileSelect],
  providers: const [ClassProvider(OrganizationService)],
)
class AppearanceComponent implements OnInit {

  @Input("user")
  User currentUser;

  @Input()
  Organization organization;

  final OrganizationService _organizationService;
  final NotificationService _notificationService;

  FileReference logo;

  FileUploader uploader = new FileUploader({
    'url': '${BaseService.getUrl()}/files/create',
    'authToken': '${BaseService.getAuthToken()}'
  }, autoUpload: true);

  AppearanceComponent(this._organizationService, this._notificationService) {}

  @override
  void ngOnInit() {
    logo = organization.logo;

    uploader.newFileStream.listen((file) {
      logo = file;
    });
  }

  void reset() {
    logo = organization.logo;
    save(null);
  }

  void removeLogo() {
    logo = null;
  }

  void save(dynamic e) {
    if (e != null) {
      e.preventDefault();
    }

    organization.logo = logo;

    _organizationService.update(organization).then((updatedOrg) {
      organization = updatedOrg;
      _notificationService.displayMessage(
          "The appearance properties have been updated.",
          NotificationType.SUCCESS);
    });
  }
}
