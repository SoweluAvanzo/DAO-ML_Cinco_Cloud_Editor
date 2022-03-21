import { BaseEntity } from './base-entity';
import { FileReference } from './file-reference';
import { User } from './user';
import { Project } from './project';

export class Organization extends BaseEntity {
  name: string;
  description: string;
  logo: FileReference;
  owners: User[] = [];
  members: User[] = [];
  projects: Project[] = [];

  get allUsers(): User[] {
    return [...this.owners, ...this.members];
  }

  isUserMember(user: User): boolean {
    return this.members.findIndex(u => u.id === user.id) > -1;
  }

  isUserOwner(user: User): boolean {
    return this.owners.findIndex(u => u.id === user.id) > -1;
  }
}
