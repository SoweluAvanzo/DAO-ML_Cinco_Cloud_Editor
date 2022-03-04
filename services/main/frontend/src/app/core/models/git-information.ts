import { GitInformationType } from '../enums/git-information-type';

export class GitInformation {
  private type: GitInformationType;
  private repositoryUrl: string;
  private username: string;
  private password: string;
  private branch: string;
  private genSubdirectory: string;
  private projectId: number;
}
