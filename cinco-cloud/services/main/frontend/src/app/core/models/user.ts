import { BaseEntity } from './base-entity';
import { FileReference } from './file-reference';
import { Project } from './project';
import { UserSystemRole } from '../enums/user-system-role';

export class User extends BaseEntity {
  name: string;
  username: string;
  email: string;
  emailHash: string;
  systemRoles: UserSystemRole[] = [];
  profilePicture: FileReference;
  ownedProjects: Project[] = [];

  get isAdmin(): boolean {
    return this.systemRoles.includes(UserSystemRole.ADMIN);
  }
}
