import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Organization } from '../../../core/models/organization';
import { OrganizationApiService } from '../../../core/services/api/organization-api.service';
import { User } from '../../../core/models/user';
import { OrganizationAccessRightVectorApiService } from '../../../core/services/api/organization-access-right-vector-api.service';
import { OrganizationAccessRight } from '../../../core/enums/organization-access-right';
import { OrganizationAccessRightVector } from '../../../core/models/organization-access-right-vector';
import { UpdateOrganizationInput } from '../../../core/models/forms/update-organization-input';
import { fromJsog, toJsog } from '../../../core/utils/jsog-utils';
import { Router } from '@angular/router';
import { ModalUtilsService } from '../../../core/services/utils/modal-utils.service';
import { ToastService, ToastType } from '../../../core/services/toast.service';

@Injectable()
export class OrganizationStoreService {

  private organization = new BehaviorSubject<Organization>(null);
  private organizationAccessRights = new BehaviorSubject<Map<number, OrganizationAccessRightVector>>(new Map());
  private userOrganizationAccessRights = new BehaviorSubject<OrganizationAccessRightVector>(null);

  constructor(private organizationApi: OrganizationApiService,
              private organizationARVApi: OrganizationAccessRightVectorApiService,
              private router: Router,
              private modalUtils: ModalUtilsService,
              private toastService: ToastService) {
  }

  get organization$(): Observable<Organization> {
    return this.organization.asObservable();
  }

  get organizationAccessRights$(): Observable<Map<number, OrganizationAccessRightVector>> {
    return this.organizationAccessRights.asObservable();
  }

  get userOrganizationAccessRights$(): Observable<OrganizationAccessRightVector> {
    return this.userOrganizationAccessRights.asObservable();
  }

  setOrganization(organization: Organization): void {
    this.organization.next(organization);
    this.organizationARVApi.getAll(organization.id).subscribe({
      next: accessRights => {
        const map = new Map<number, OrganizationAccessRightVector>();
        accessRights.forEach(arv => map.set(arv.user.id, arv));
        this.organizationAccessRights.next(map);
      },
      error: console.error
    });
    this.organizationARVApi.getMy(organization.id).subscribe({
      next: accessRights => this.userOrganizationAccessRights.next(accessRights),
      error: console.error
    });
  }

  removeUserFromOrganization(user: User): void {
    this.modalUtils.confirm({
      text: 'Do you really want to remove this user from the organization?',
      confirmButtonText: 'Remove'
    }).then(() => {
      this.organizationApi.removeUser(this.organization.value, user).subscribe({
        next: organization => {
          this.toastService.show({ type: ToastType.SUCCESS, message: `${user.name} has been removed from the organization.` });
          this.organization.next(organization);
          const arvMap = this.organizationAccessRights.value;
          arvMap.delete(user.id);
          this.organizationAccessRights.next(arvMap);
        },
        error: res => {
          this.toastService.show({
            type: ToastType.SUCCESS,
            message: `${user.name} could not be removed from the organization. ${res.data.message}`
          });
        }
      });
    }).catch(() => {});
  }

  makeUserMemberOfOrganization(user: User): void {
    this.organizationApi.addMember(this.organization.value, user).subscribe({
      next: organization => {
        this.toastService.show({ type: ToastType.SUCCESS, message: `${user.name} is now a member of the organization.` });
        this.setOrganization(organization)
      },
      error: res => {
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: `Could not make ${user.name} a member of the organization. ${res.data.message}`
        });
      }
    });
  }

  makeUserOwnerOfOrganization(user: User): void {
    this.organizationApi.addOwner(this.organization.value, user).subscribe({
      next: organization => {
        this.toastService.show({ type: ToastType.SUCCESS, message: `${user.name} is now an owner of the organization.` });
        this.setOrganization(organization)
      },
      error: res => {
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: `Could not make ${user.name} an owner of the organization. ${res.data.message}`
        });
      }
    });
  }

  addAccessRight(arv: OrganizationAccessRightVector, accessRight: OrganizationAccessRight): void {
    arv.accessRights.push(accessRight);
    this.organizationARVApi.update(arv).subscribe({
      next: updatedArv => {
        const arvMap = this.organizationAccessRights.value;
        arvMap.set(updatedArv.user.id, arv);
        this.organizationAccessRights.next(arvMap);
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not give user right ${accessRight}. ${res.data.message}`
        });
      }
    });
  }

  removeAccessRight(arv: OrganizationAccessRightVector, accessRight: OrganizationAccessRight): void {
    arv.accessRights = arv.accessRights.filter(ar => ar != accessRight);
    this.organizationARVApi.update(arv).subscribe({
      next: updatedArv => {
        const arvMap = this.organizationAccessRights.value;
        arvMap.set(updatedArv.user.id, arv);
        this.organizationAccessRights.next(arvMap);
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could remove right ${accessRight}. ${res.data.message}`
        });
      }
    });
  }

  updateOrganization(input: UpdateOrganizationInput): void {
    const copy: Organization = fromJsog(toJsog(this.organization.value), Organization)
    copy.name = input.name.trim();
    copy.description = input.description.trim();
    copy.logo = input.logo;
    this.organizationApi.update(copy).subscribe({
      next: updatedOrganization => {
        this.toastService.show({ type: ToastType.SUCCESS, message: `Organization ${updatedOrganization.name} has been updated.` });
        this.organization.next(updatedOrganization)
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Organization ${copy.name} could not be updated. ${res.data.message}`
        });
      }
    });
  }

  deleteOrganization(): void {
    this.modalUtils.confirm({
      text: 'Do you really want to delete this organization?',
      confirmButtonText: 'Delete'
    }).then(() => {
      this.organizationApi.delete(this.organization.value).subscribe({
        next: () => {
          this.toastService.show({
            type: ToastType.SUCCESS,
            message: `The organization ${this.organization.value.name} has been deleted.`
          });
          this.organization.next(null);
          this.router.navigate(['/app']);
        },
        error: res => {
          this.toastService.show({
            type: ToastType.DANGER,
            message: `The organization ${this.organization.value.name} could not be deleted. ${res.data.message}`
          });
        }
      });
    }).catch(() => {});
  }

  leaveOrganization(): void {
    this.modalUtils.confirm({
      text: 'Do you really want to leave this organization?',
      confirmButtonText: 'Leave'
    }).then(() => {
      this.organizationApi.leave(this.organization.value).subscribe({
        next: () => {
          this.toastService.show({
            type: ToastType.SUCCESS,
            message: `You left the organization ${this.organization.value.name}.`
          });
          this.organization.next(null);
          this.router.navigate(['/app']);
        },
        error: res => {
          this.toastService.show({
            type: ToastType.DANGER,
            message: `Failed to leave organization ${this.organization.value.name}. ${res.data.message}`
          });
        }
      });
    }).catch(() => {});
  }

  canDeleteOrganization(user: User): boolean {
    return this.canUpdateOrganization(user);
  }

  canAddUsers(user: User): boolean {
    return this.canUpdateOrganization(user);
  }

  canUpdateAccessRights(user: User): boolean {
    return this.canUpdateOrganization(user);
  }

  canUpdateOrganization(user: User): boolean {
    return user != null && this.organization.value.isUserOwner(user);
  }

  canLeaveOrganization(user: User): boolean {
    return user != null
      && (this.organization.value.isUserMember(user)
        || (this.organization.value.isUserOwner(user) && this.organization.value.owners.length > 1));
  }

  canCreateProjects(user: User): boolean {
    return this.hasAccessRightForProjects(user, OrganizationAccessRight.CREATE_PROJECTS);
  }

  canDeleteProjects(user: User): boolean {
    return this.hasAccessRightForProjects(user, OrganizationAccessRight.DELETE_PROJECTS);
  }

  canUpdateProjects(user: User): boolean {
    return this.hasAccessRightForProjects(user, OrganizationAccessRight.EDIT_PROJECTS);
  }

  private hasAccessRightForProjects(user: User, ar: OrganizationAccessRight): boolean {
    const accessRights = this.userOrganizationAccessRights.value;
    return user != null
      && accessRights != null
      && accessRights.user.id === user.id
      && accessRights.accessRights.includes(ar)
  }
}
