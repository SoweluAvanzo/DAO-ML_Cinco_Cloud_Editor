import { BaseEntity } from './base-entity';
import { FileReference } from './file-reference';
import { User } from './user';
import { Project } from './project';

export class Organization extends BaseEntity {
  name: string;
  description: string;
  logo: FileReference;
  owners: User[] = [];
  members: User[] = [];
  projects: Project[] = [];
}
