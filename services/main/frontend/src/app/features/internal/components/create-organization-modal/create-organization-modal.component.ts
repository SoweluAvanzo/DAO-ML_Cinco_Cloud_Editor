import { Component } from '@angular/core';
import { OrganizationApiService } from '../../../../core/services/api/organization-api.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Organization } from '../../../../core/models/organization';

@Component({
  selector: 'cc-create-organization-modal',
  templateUrl: './create-organization-modal.component.html',
  styleUrls: ['./create-organization-modal.component.scss']
})
export class CreateOrganizationModalComponent {

  form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    description: new FormControl('')
  });

  constructor(private organizationApi: OrganizationApiService,
              public modal: NgbActiveModal) { }

  createOrganization(): void {
    const newOrganization = new Organization();
    newOrganization.name = this.form.value.name;
    newOrganization.description = this.form.value.description;
    this.organizationApi.create(newOrganization).subscribe({
      next: org => this.modal.close(org),
      error: console.error
    });
  }
}
