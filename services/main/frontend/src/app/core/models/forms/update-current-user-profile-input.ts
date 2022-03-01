import { FileReference } from '../file-reference';
import { JsonProperty } from 'jsog-typescript';

export class UpdateCurrentUserProfileInput {
  name: string;
  email: string;

  @JsonProperty
  profilePicture: FileReference;
}
