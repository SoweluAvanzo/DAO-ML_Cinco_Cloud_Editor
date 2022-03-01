import {BaseEntity} from './base-entity';
import {User} from './user';
import {JsonProperty} from 'jsog-typescript';
import {Organization} from './organization';
import {OrganizationAccessRight} from '../enums/organization-access-right';

export class OrganizationAccessRightVector extends BaseEntity {

  accessRights: OrganizationAccessRight[] = [];

  @JsonProperty
  user: User;

  @JsonProperty
  organization: Organization;
}
