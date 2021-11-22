import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../../model/core.dart';
import '../../service/base_service.dart';
import '../../service/user_service.dart';
import '../../service/notification_service.dart';
import '../../filesupport/fileselect.dart';
import '../../filesupport/fileuploader.dart';
import '../shared/navigation/navigation_component.dart';
import './profile_image/profile_image_component.dart';

@Component(
    selector: 'profile',
    templateUrl: 'profile_component.html',
    directives: const [
      coreDirectives,
      formDirectives,
      NavigationComponent,
      ProfileImageComponent,
      FileSelect
    ],
    styleUrls: const [
      'profile_component.css'
    ])
class ProfileComponent implements OnInit {
  User user;

  FileUploader uploader;

  final UserService _userService;
  final NotificationService _notificationService;

  ProfileComponent(this._userService, this._notificationService) {
    uploader = new FileUploader({
      'url': '${BaseService.getUrl()}/files/create',
      'authToken': '${BaseService.getAuthToken()}'
    }, autoUpload: true);
  }

  @override
  void ngOnInit() {
    _userService.loadUser().then((u) {
      user = u;
    });

    uploader.newFileStream.listen((file) {
      user.profilePicture = file;
      _updateProfile();
    });
  }

  removeProfileImage() {
    user.profilePicture = null;
    _updateProfile();
  }

  updateUser(dynamic e, String email) {
    e.preventDefault();

    user.email = email;
    _updateProfile();
  }

  _updateProfile() {
    this._userService.updateProfile(user).then((u) {
      user = u;
      _notificationService.displayMessage(
          "Your profile has been updated.", NotificationType.SUCCESS);
    }).catchError((_) {
      _notificationService.displayMessage(
          "Your profile could not be updated.", NotificationType.DANGER);
    });
  }
}
