import { FileReference } from '../file-reference';

export interface UpdateProjectInput {
  name: string;
  description: string;
  logo: FileReference;
}
