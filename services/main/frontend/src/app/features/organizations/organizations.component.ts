import { Component } from '@angular/core';
import { OrganizationStoreService } from './services/organization-store.service';
import { UntilDestroy } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'cc-organizations',
  templateUrl: './organizations.component.html',
  providers: [OrganizationStoreService],
  styleUrls: ['./organizations.component.scss']
})
export class OrganizationsComponent {
}
