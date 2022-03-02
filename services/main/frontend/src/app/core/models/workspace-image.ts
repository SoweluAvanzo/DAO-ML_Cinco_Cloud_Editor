import { BaseEntity } from './base-entity';
import { User } from './user';
import { Project } from './project';

export class WorkspaceImage extends BaseEntity {
  name: string;
  imageName: string;
  imageVersion: string;
  published: boolean;
  user: User;
  project: Project;
}
