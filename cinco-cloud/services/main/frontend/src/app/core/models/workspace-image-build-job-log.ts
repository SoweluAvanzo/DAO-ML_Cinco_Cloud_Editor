import { BaseEntity } from './base-entity';

export class WorkspaceImageBuildJobLog extends BaseEntity {
  jobId: number;
  logStatus: string;
  log: string;
}
