import 'dart:async';

import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../../../filesupport/fileselect.dart';
import '../../../filesupport/fileuploader.dart';
import '../../../model/core.dart';
import '../../../service/base_service.dart';

@Component(
  selector: 'style-form',
  templateUrl: 'style_form_component.html',
  directives: const [coreDirectives, formDirectives, FileSelect],
)
class StyleFormComponent implements OnInit, OnDestroy {
  @Input("style")
  Style style;

  final updatedSC = new StreamController();
  @Output()
  Stream get updated => updatedSC.stream;

  FileUploader uploader = new FileUploader({
    'url': '${BaseService.getUrl()}/files/create',
    'authToken': '${BaseService.getAuthToken()}'
  }, autoUpload: true);

  String navBgColor;
  String navTextColor;
  String bodyBgColor;
  String bodyTextColor;
  String primaryBgColor;
  String primaryTextColor;
  FileReference logo;

  StyleFormComponent() {}

  @override
  void ngOnInit() {
    navBgColor = style.navBgColor;
    navTextColor = style.navTextColor;
    bodyBgColor = style.bodyBgColor;
    bodyTextColor = style.bodyTextColor;
    primaryBgColor = style.primaryBgColor;
    primaryTextColor = style.primaryTextColor;
    logo = style.logo;

    uploader.newFileStream.listen((file) {
      logo = file;
      update();
    });
  }

  @override
  void ngOnDestroy() {
    update();
  }

  void save(dynamic e) {
    e.preventDefault();
  }

  void update() {
    Style s = new Style();
    s.navBgColor = navBgColor;
    s.navTextColor = navTextColor;
    s.bodyBgColor = bodyBgColor;
    s.bodyTextColor = bodyTextColor;
    s.primaryBgColor = primaryBgColor;
    s.primaryTextColor = primaryTextColor;
    s.logo = logo;
    updatedSC.add(s);
  }

  void updateNavBgColor(String value) {
    navBgColor = value.substring(1, value.length);
    if (_isEmpty(navBgColor) || _isValidColor(navBgColor)) {
      update();
    }
  }

  void updateNavTextColor(String value) {
    navTextColor = value.substring(1, value.length);
    if (_isEmpty(navTextColor) || _isValidColor(navTextColor)) {
      update();
    }
  }

  void updateBodyBgColor(String value) {
    bodyBgColor = value.substring(1, value.length);
    if (_isEmpty(bodyBgColor) || _isValidColor(bodyBgColor)) {
      update();
    }
  }

  void updateBodyTextColor(String value) {
    bodyTextColor = value.substring(1, value.length);
    if (_isEmpty(bodyTextColor) || _isValidColor(bodyTextColor)) {
      update();
    }
  }

  void updatePrimaryBgColor(String value) {
    primaryBgColor = value.substring(1, value.length);
    if (_isEmpty(primaryBgColor) || _isValidColor(primaryBgColor)) {
      update();
    }
  }

  void updatePrimaryTextColor(String value) {
    primaryTextColor = value.substring(1, value.length);
    if (_isEmpty(primaryTextColor) || _isValidColor(primaryTextColor)) {
      update();
    }
  }

  void removeLogo() {
    logo = null;
    update();
  }

  bool _isEmpty(String value) {
    return value == null || value.trim() == "";
  }

  bool _isValidColor(String value) {
    RegExp exp = new RegExp("[a-f0-9]{6,6}");
    return exp.hasMatch(value);
  }
}
