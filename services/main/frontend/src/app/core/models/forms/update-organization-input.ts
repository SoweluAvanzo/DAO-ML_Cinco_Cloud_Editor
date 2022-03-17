import { FileReference } from '../file-reference';

export interface UpdateOrganizationInput {
  name: string;
  description: string;
  logo: FileReference;
}
