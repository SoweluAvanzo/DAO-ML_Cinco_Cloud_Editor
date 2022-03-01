import { BaseEntity } from './base-entity';
import { FileReference } from './file-reference';
import { JsonProperty } from 'jsog-typescript';
import { Project } from './project';
import { UserSystemRole } from '../enums/user-system-role';

export class User extends BaseEntity {
  name: string;
  username: string;
  email: string;
  emailHash: string;
  systemRoles: UserSystemRole[] = [];

  @JsonProperty
  profilePicture: FileReference;

  @JsonProperty(Project)
  ownedProjects: Project[] = [];
}
