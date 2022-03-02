import { BaseEntity } from './base-entity';
import { Project } from './project';

export class WorkspaceImageBuildJob extends BaseEntity {
  status: string;
  startedAt: Date;
  finishedAt: Date;
  project: Project;
}
