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
  organization: Organization;
  image: WorkspaceImage;
  template: WorkspaceImage;
  graphModelTypes: GraphModelType[] = [];
}
