import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { OrganizationsComponent } from './organizations.component';
import { OrganizationComponent } from './pages/organization/organization.component';
import { OrganizationResolver } from './resolvers/organization.resolver';
import { CoreModule } from '../../core/core.module';
import { UsersComponent } from './pages/users/users.component';
import { AccessManagementComponent } from './pages/access-management/access-management.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { ProjectsComponent } from './pages/projects/projects.component';
import { InternalModule } from '../internal/internal.module';
import { AddUserModalComponent } from './pages/users/components/add-user-modal/add-user-modal.component';
import { ReactiveFormsModule } from '@angular/forms';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faEllipsisV, faEnvelope, faTimes, faTrash, faUser } from '@fortawesome/free-solid-svg-icons';
import { NgbDropdownModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { EditOrganizationCardComponent } from './pages/settings/components/edit-organization-card/edit-organization-card.component';

const routes: Routes = [
  {
    path: '',
    component: OrganizationsComponent,
    children: [
      {
        path: ':organizationId',
        component: OrganizationComponent,
        resolve: {
          organization: OrganizationResolver
        },
        children: [
          {
            path: '',
            pathMatch: 'full',
            redirectTo: 'projects'
          },
          {
            path: 'projects',
            component: ProjectsComponent
          },
          {
            path: 'users',
            component: UsersComponent
          },
          {
            path: 'access-management',
            component: AccessManagementComponent
          },
          {
            path: 'settings',
            component: SettingsComponent
          }
        ]
      }
    ]
  }
];

@NgModule({
  declarations: [
    OrganizationsComponent,
    OrganizationComponent,
    ProjectsComponent,
    UsersComponent,
    AccessManagementComponent,
    SettingsComponent,
    AddUserModalComponent,
    EditOrganizationCardComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ReactiveFormsModule,
    FontAwesomeModule,
    NgbDropdownModule,
    NgbNavModule,
    CoreModule,
    InternalModule
  ]
})
export class OrganizationsModule {

  constructor(library: FaIconLibrary) {
    library.addIcons(faEnvelope, faUser, faEllipsisV, faTimes, faTrash);
  }
}
