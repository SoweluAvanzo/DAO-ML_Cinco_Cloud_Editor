import { Component, Input, OnInit } from '@angular/core';
import { Project } from '../../../../../../core/models/project';
import { User } from '../../../../../../core/models/user';
import { Organization } from '../../../../../../core/models/organization';
import { ProjectStoreService } from '../../../../services/project-store.service';
import { ToastService, ToastType } from '../../../../../../core/services/toast.service';
import { ModalUtilsService } from '../../../../../../core/services/utils/modal-utils.service';
import { AppStoreService } from '../../../../../../core/services/stores/app-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'cc-edit-organization-ownership-card',
  templateUrl: './edit-organization-ownership-card.component.html'
})
export class EditOrganizationOwnershipCardComponent implements OnInit {

  @Input()
  project: Project;

  user: User;

  newOrg: Organization;

  constructor(private projectStore: ProjectStoreService,
              private toastService: ToastService,
              private modalUtils: ModalUtilsService,
              private appStoreService: AppStoreService) {
  }

  ngOnInit(): void {
    this.appStoreService.user$.pipe(untilDestroyed(this)).subscribe({
      next: value => {
        this.user = value
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not fetch the current user.\n${res.error.message}`
        })
      }
    });
  }

  transferOrganization(): void {
    this.modalUtils.confirm({
      text: `Do you want to transfer the project ownership to ${this.newOrg.name}?`,
      confirmButtonText: 'Transfer'
    }).then(() => {
      this.projectStore.transferProjectToOrganization(this.newOrg);
    })
  }
}
