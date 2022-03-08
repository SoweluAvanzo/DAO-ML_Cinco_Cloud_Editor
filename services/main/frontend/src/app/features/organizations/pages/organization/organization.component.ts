import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrganizationStoreService } from '../../services/organization-store.service';
import { faCogs, faFolderOpen, faUsers, faUserShield } from '@fortawesome/free-solid-svg-icons';
import { Organization } from '../../../../core/models/organization';

@Component({
  selector: 'cc-organization',
  templateUrl: './organization.component.html',
  styleUrls: ['./organization.component.css']
})
export class OrganizationComponent implements OnInit {

  organization: Organization;

  icons = {
    cogs: faCogs,
    folderOpen: faFolderOpen,
    userShield: faUserShield,
    users: faUsers
  };

  constructor(private route: ActivatedRoute,
              private organizationStore: OrganizationStoreService) {
  }

  ngOnInit(): void {
    this.organization = this.route.snapshot.data['organization'];
    this.organizationStore.setOrganization(this.organization);
  }
}
