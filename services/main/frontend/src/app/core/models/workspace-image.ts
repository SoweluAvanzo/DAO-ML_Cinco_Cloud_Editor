import { BaseEntity } from './base-entity';
import { User } from './user';
import { Project } from './project';
import { JsonProperty } from 'jsog-typescript';

export class WorkspaceImage extends BaseEntity {
  name: string;
  imageName: string;
  imageVersion: string;
  published: boolean;

  @JsonProperty
  user: User;

  @JsonProperty
  project: Project;
}
