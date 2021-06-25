import 'package:angular/angular.dart';

import '../../../model/core.dart';

@Component(
    selector: 'profile-image',
    templateUrl: 'profile_image_component.html',
    directives: const [coreDirectives],
    styleUrls: const ['profile_image_component.css'])
class ProfileImageComponent {
  @Input()
  PyroUser user;

  ProfileImageComponent() {}

  String get imageUrl {
    if (user == null) {
      return "https://www.gravatar.com/avatar/?d=mp";
    } else if (user.profilePicture == null) {
      return "https://www.gravatar.com/avatar/${user.emailHash}?d=retro";
    } else {
      // TODO: SAMI: security
      return user.profilePicture.downloadPath;
    }
  }
}
