import { BaseEntity } from './base-entity';

export class Settings extends BaseEntity {
  allowPublicUserRegistration: boolean;
  autoActivateUsers: boolean;
  createDefaultProjects: boolean;
  sendMails: boolean;
  persistentDeployments: boolean;
  archetypeImage: string;
}
