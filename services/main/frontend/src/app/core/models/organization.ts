import {BaseEntity} from './base-entity';
import {FileReference} from './file-reference';
import {JsonProperty} from 'jsog-typescript';
import {User} from './user';
import {Project} from './project';

export class Organization extends BaseEntity {
  name: string;
  description: string;

  @JsonProperty
  logo: FileReference;

  @JsonProperty(User)
  owners: User[] = [];

  @JsonProperty(User)
  members: User[] = [];

  @JsonProperty(User)
  projects: Project[] = [];
}
