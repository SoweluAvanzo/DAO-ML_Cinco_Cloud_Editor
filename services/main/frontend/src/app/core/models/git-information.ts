import { GitInformationType } from '../enums/git-information-type';

export class GitInformation {
  type: GitInformationType;
  repositoryUrl: string;
  username: string;
  password: string;
  branch: string;
  genSubdirectory: string;
  projectId: number;
}
