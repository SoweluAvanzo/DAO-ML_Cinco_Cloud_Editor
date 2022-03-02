import { Component, Input } from '@angular/core';
import { User } from '../../models/user';

@Component({
  selector: 'cc-user-profile-image',
  templateUrl: './user-profile-image.component.html',
  styleUrls: ['./user-profile-image.component.css']
})
export class UserProfileImageComponent {

  @Input()
  user: User;

  get imageUrl(): string {
    if (this.user == null) {
      return "https://www.gravatar.com/avatar/?d=mp";
    } else if (this.user.profilePicture == null) {
      return "https://www.gravatar.com/avatar/${user.emailHash}?d=retro";
    } else {
      // TODO: SAMI: security
      return this.user.profilePicture.downloadPath;
    }
  }
}
