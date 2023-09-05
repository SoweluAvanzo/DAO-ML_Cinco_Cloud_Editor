import { Component } from '@angular/core';
import { OrganizationApiService } from '../../../../core/services/api/organization-api.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { Organization } from '../../../../core/models/organization';
import { ToastService, ToastType } from '../../../../core/services/toast.service';

@Component({
  selector: 'cc-create-organization-modal',
  templateUrl: './create-organization-modal.component.html'
})
export class CreateOrganizationModalComponent {

  form = new UntypedFormGroup({
    name: new UntypedFormControl('', [Validators.required]),
    description: new UntypedFormControl('')
  });

  errorMessage: string = null

  constructor(private organizationApi: OrganizationApiService,
              private toastService: ToastService,
              public modal: NgbActiveModal) {
  }

  createOrganization(): void {
    this.errorMessage = null;
    const newOrganization = new Organization();
    newOrganization.name = this.form.value.name;
    newOrganization.description = this.form.value.description;
    this.organizationApi.create(newOrganization).subscribe({
      next: createdOrganization => {
        this.toastService.show({
          message: `The organization "${createdOrganization.name}" has been created.`,
          type: ToastType.SUCCESS
        });
        this.modal.close(createdOrganization);
      },
      error: res => {
        this.toastService.show({
          message: `The organization could not be created: ${res.error.message}`,
          type: ToastType.DANGER
        });
        console.log(res);
      }
    });
  }
}
