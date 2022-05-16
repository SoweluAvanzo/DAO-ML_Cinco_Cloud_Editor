import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from '../../../../../../core/models/user';
import { Organization } from '../../../../../../core/models/organization';
import {
  OrganizationApiService
} from '../../../../../../core/services/api/organization-api.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { ToastService, ToastType } from '../../../../../../core/services/toast.service';

@Component({
  selector: 'cc-add-user-modal',
  templateUrl: './add-user-modal.component.html'
})
export class AddUserModalComponent {

  @Input()
  organization: Organization;

  form = new FormGroup({
    role: new FormControl('member', [Validators.required])
  });

  user: User;
  errorMessage: string = '';

  constructor(public modal: NgbActiveModal,
              public organizationApi: OrganizationApiService,
              private toastService: ToastService) {
  }

  addUser(): void {
    this.errorMessage = '';
    let obs: Observable<Organization>;
    if (this.form.value.role === 'owner') {
      obs = this.organizationApi.addOwner(this.organization, this.user);
    } else {
      obs = this.organizationApi.addMember(this.organization, this.user);
    }
    obs.subscribe({
      next: organization => this.modal.close(organization),
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not add user.`
        });
        console.error(res.data.message);
      }
    });
  }
}
