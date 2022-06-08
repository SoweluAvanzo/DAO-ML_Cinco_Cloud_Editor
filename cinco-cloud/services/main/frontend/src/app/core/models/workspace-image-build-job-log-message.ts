import { BaseEntity } from './base-entity';

export class WorkspaceImageBuildJobLogMessage extends BaseEntity {
  jobId: number;
  logMessages: string[] = [];
}
