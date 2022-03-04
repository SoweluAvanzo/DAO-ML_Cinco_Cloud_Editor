import { Component, OnInit } from '@angular/core';
import { ProjectApiService } from '../../../../core/services/api/project-api.service';
import { OrganizationApiService } from '../../../../core/services/api/organization-api.service';
import { combineLatest } from 'rxjs';
import { Project } from '../../../../core/models/project';
import { Organization } from '../../../../core/models/organization';

@Component({
  selector: 'cc-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})
export class OverviewComponent implements OnInit {

  projects: Project[] = [];
  organizations: Organization[] = [];

  constructor(private projectApi: ProjectApiService,
              private organizationApi: OrganizationApiService) { }

  ngOnInit(): void {
    combineLatest([
      this.projectApi.getAll(),
      this.organizationApi.getAll()
    ]).subscribe({
      next: (res: [Project[], Organization[]]) => {
        this.projects = res[0];
        this.organizations = res[1];
      }
    });
  }
}
