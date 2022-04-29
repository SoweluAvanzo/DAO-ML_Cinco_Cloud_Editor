import { BaseEntity } from './base-entity';
import { Project } from './project';

export class WorkspaceImageBuildJob extends BaseEntity {
  status: string;
  startedAt: Date;
  finishedAt: Date;
  project: Project;

  get duration(): string {
    const milliseconds = this.finishedAt.getTime() - this.startedAt.getTime();
    const minutes = Math.round(milliseconds / 60000);
    const seconds = Math.round((milliseconds / 1000) % 60);
    return `${minutes}min ${seconds}s`;
  }
}
