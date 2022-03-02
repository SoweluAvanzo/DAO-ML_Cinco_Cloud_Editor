import { FileReference } from '../file-reference';

export class UpdateCurrentUserProfileInput {
  name: string;
  email: string;
  profilePicture: FileReference;
}
