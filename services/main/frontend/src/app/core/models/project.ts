import {BaseEntity} from './base-entity';
import {Organization} from './organization';
import {JsonProperty} from 'jsog-typescript';
import {User} from './user';
import {WorkspaceImage} from './workspace-image';
import {ProjectType} from '../enums/project-type';

export class Project extends BaseEntity {
  type: ProjectType;
  name: string;
  description: string;

  @JsonProperty
  owner: User;

  @JsonProperty
  organization: Organization;

  @JsonProperty
  image: WorkspaceImage;

  @JsonProperty
  template: WorkspaceImage;
}
