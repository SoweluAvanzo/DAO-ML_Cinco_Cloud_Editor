import { BaseEntity } from './base-entity';
import { User } from './user';
import { Organization } from './organization';
import { OrganizationAccessRight } from '../enums/organization-access-right';

export class OrganizationAccessRightVector extends BaseEntity {
  accessRights: OrganizationAccessRight[] = [];
  user: User;
  organization: Organization;
}
