import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Organization } from '../../../../../../core/models/organization';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { OrganizationStoreService } from '../../../../services/organization-store.service';
import { FileApiService } from '../../../../../../core/services/api/file-api.service';
import { ToastService, ToastType } from '../../../../../../core/services/toast.service';
import { FileReference } from '../../../../../../core/models/file-reference';
import { FileInputComponent } from '../../../../../../core/components/file-input/file-input.component';

@Component({
  selector: 'cc-edit-organization-card',
  templateUrl: './edit-organization-card.component.html'
})
export class EditOrganizationCardComponent implements OnInit {

  @Input()
  organization: Organization

  @ViewChild('input')
  input: FileInputComponent

  logo: File;
  logoReference: FileReference;
  logoChanged = false;

  form = new UntypedFormGroup({
    name: new UntypedFormControl('', [Validators.required, Validators.minLength(1)]),
    description: new UntypedFormControl('')
  });

  constructor(private organizationStore: OrganizationStoreService,
              private toastService: ToastService,
              private fileApi: FileApiService) {
  }

  ngOnInit(): void {
    this.form.get('name').setValue(this.organization.name);
    this.form.get('description').setValue(this.organization.description);
    this.logoReference = this.organization.logo;
  }

  update(): void {
    const formValue = this.form.value;

    if (this.logo != null) {
      this.fileApi.create(this.logo).subscribe({
        next: (file: FileReference) => {
          this.organizationStore.updateOrganization({
            logoId: file.id,
            name: formValue.name,
            description: formValue.description
          });
          this.logoReference = file;
          this.input.reset();
        },
        error: () => {
          this.toastService.show({ type: ToastType.DANGER, message: 'The logo could not be uploaded.' })
        }
      });
    } else {
      this.organizationStore.updateOrganization({
        logoId: this.logoChanged ? this.logoReference?.id : this.organization.logo?.id,
        name: formValue.name,
        description: formValue.description
      });
    }
  }

  handleFileSelect(files: File[]): void {
    this.logo = files.length === 0 ? null : files[0];
    this.logoChanged = true;
  }

  removeLogo(e): void {
    if (e) e.preventDefault();
    this.logo = null;
    this.logoReference = null;
    this.logoChanged = true;
  }

  get logoStyle(): any {
    return {
      backgroundImage: `url(${this.organization.logo?.downloadPath})`,
      backgroundSize: 'cover',
      width: '100px',
      height: '100px'
    };
  }
}
