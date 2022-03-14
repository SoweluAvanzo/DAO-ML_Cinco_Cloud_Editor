import { Component, Input, OnInit } from '@angular/core';
import { Organization } from '../../../../../../core/models/organization';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { OrganizationStoreService } from '../../../../services/organization-store.service';
import { UpdateOrganizationInput } from '../../../../../../core/models/forms/update-organization-input';

@Component({
  selector: 'cc-edit-organization-card',
  templateUrl: './edit-organization-card.component.html'
})
export class EditOrganizationCardComponent implements OnInit {

  @Input()
  organization: Organization

  form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    description: new FormControl('')
  });

  constructor(private organizationStore: OrganizationStoreService) {
  }

  ngOnInit(): void {
    this.form.get('name').setValue(this.organization.name);
    this.form.get('description').setValue(this.organization.description)
  }

  update(): void {
    const input: UpdateOrganizationInput = this.form.value;
    this.organizationStore.updateOrganization(input);
  }
}
