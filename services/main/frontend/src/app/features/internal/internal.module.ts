import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { InternalComponent } from './internal.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faBars, faCode, faImage, faPlus, faProjectDiagram, faUserLock } from '@fortawesome/free-solid-svg-icons';
import { NgbDropdownModule, NgbModalModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { CoreModule } from '../../core/core.module';
import { UserIsAdminGuard } from '../../core/guards/user-is-admin.guard';
import { OverviewComponent } from './pages/overview/overview.component';
import { CreateProjectModalComponent } from './components/create-project-modal/create-project-modal.component';
import { CreateOrganizationModalComponent } from './components/create-organization-modal/create-organization-modal.component';
import { ReactiveFormsModule } from '@angular/forms';
import { OrganizationListComponent } from './components/organization-list/organization-list.component';
import { ProjectListComponent } from './components/project-list/project-list.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'overview'
  },
  {
    path: '',
    pathMatch: 'exact',
    component: InternalComponent,
    children: [
      {
        path: 'admin',
        loadChildren: () => import('../admin/admin.module').then(m => m.AdminModule),
        canActivate: [UserIsAdminGuard],
        canActivateChild: [UserIsAdminGuard]
      },
      {
        path: 'overview',
        component: OverviewComponent
      },
      {
        path: 'profile',
        loadChildren: () => import('../profile/profile.module').then(m => m.ProfileModule)
      },
      {
        path: 'organizations',
        loadChildren: () => import('../organizations/organizations.module').then(m => m.OrganizationsModule)
      },
      {
        path: 'projects',
        loadChildren: () => import('../projects/projects.module').then(m => m.ProjectsModule)
      },
    ]
  },
];

@NgModule({
  declarations: [
    InternalComponent,
    NavigationComponent,
    OverviewComponent,
    CreateProjectModalComponent,
    CreateOrganizationModalComponent,
    OrganizationListComponent,
    ProjectListComponent
  ],
  imports: [
    CommonModule,
    FontAwesomeModule,
    NgbDropdownModule,
    NgbNavModule,
    NgbModalModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    CoreModule
  ]
})
export class InternalModule {
  constructor(library: FaIconLibrary) {
    library.addIcons(faBars, faUserLock, faPlus, faImage, faCode, faProjectDiagram);
  }
}
