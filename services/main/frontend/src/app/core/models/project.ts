import { BaseEntity } from './base-entity';
import { Organization } from './organization';
import { User } from './user';
import { WorkspaceImage } from './workspace-image';
import { ProjectType } from '../enums/project-type';
import { GraphModelType } from './graph-model-type';

export class Project extends BaseEntity {
  type: ProjectType;
  name: string;
  description: string;
  owner: User;
  members: User[] = [];
  organization: Organization;
  image: WorkspaceImage;
  template: WorkspaceImage;
  graphModelTypes: GraphModelType[] = [];

  isUserMember(user: User): boolean {
    return this.members.findIndex(u => u.id === user.id) > -1;
  }

  get allUsers(): User[] {
    return this.owner != null
        ? [this.owner, ...this.members]
        : this.organization.allUsers
  }
}
