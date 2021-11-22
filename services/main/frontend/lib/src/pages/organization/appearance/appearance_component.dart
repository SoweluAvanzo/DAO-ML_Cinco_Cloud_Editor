import 'dart:core';

import 'package:angular/angular.dart';

import '../../../model/core.dart';
import '../../../service/organization_service.dart';
import '../../../service/notification_service.dart';
import '../../../service/style_service.dart';
import '../../../pages/shared/style_form/style_form_component.dart';

@Component(
  selector: 'appearance',
  templateUrl: 'appearance_component.html',
  directives: const [coreDirectives, StyleFormComponent],
  providers: const [ClassProvider(OrganizationService)],
)
class AppearanceComponent implements OnInit, OnDestroy {
  @Input("user")
  User currentUser;

  @Input()
  Organization organization;

  Style style;

  final OrganizationService _organizationService;
  final StyleService _styleService;
  final NotificationService _notificationService;

  AppearanceComponent(this._organizationService, this._styleService,
      this._notificationService) {}

  @override
  void ngOnInit() {
    style = organization.style;
  }

  @override
  void ngOnDestroy() {}

  void updatePreview() {
    Organization org = new Organization();
    org.style = style;
    _styleService.update(org.style);
  }

  void reset() {
    style = organization.style;
    updatePreview();
  }

  void handleStyleUpdated(dynamic e) {
    if (e is Style) {
      style = e;
      updatePreview();
    }
  }

  void save(dynamic e) {
    if (e != null) {
      e.preventDefault();
    }

    organization.style.navBgColor = style.navBgColor;
    organization.style.navTextColor = style.navTextColor;
    organization.style.bodyBgColor = style.bodyBgColor;
    organization.style.bodyTextColor = style.bodyTextColor;
    organization.style.primaryBgColor = style.primaryBgColor;
    organization.style.primaryTextColor = style.primaryTextColor;
    organization.style.logo = style.logo;

    _organizationService.update(organization).then((updatedOrg) {
      organization = updatedOrg;
      _notificationService.displayMessage(
          "The appearance properties have been updated.",
          NotificationType.SUCCESS);
      _styleService.update(updatedOrg.style);
    });
  }
}
