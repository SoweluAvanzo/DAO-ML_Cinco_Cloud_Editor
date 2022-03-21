import { BaseEntity } from './base-entity';

export class Settings extends BaseEntity {
  globallyCreateOrganizations: boolean;
  allowPublicUserRegistration: boolean;
}
