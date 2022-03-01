import {BaseEntity} from './base-entity';
import {Project} from './project';
import {JsonProperty} from 'jsog-typescript';

export class WorkspaceImageBuildJob extends BaseEntity {
  status: string;
  startedAt: Date;
  finishedAt: Date;

  @JsonProperty
  project: Project;
}
