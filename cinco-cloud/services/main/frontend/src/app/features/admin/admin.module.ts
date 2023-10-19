import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin.component';
import { UsersComponent } from './pages/users/users.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { CoreModule } from '../../core/core.module';
import { NgbNavModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { UserListComponent } from './components/user-list/user-list.component';
import { AdminListComponent } from './components/admin-list/admin-list.component';
import { CreateUserModalComponent } from './components/create-user-modal/create-user-modal.component';
import { AddAdminModalComponent } from './components/add-admin-modal/add-admin-modal.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ProjectsComponent } from './pages/projects/projects.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'users'
  },
  {
    path: '',
    pathMatch: 'prefix',
    component: AdminComponent,
    children: [
      {
        path: 'users',
        component: UsersComponent
      },
      {
        path: 'settings',
        component: SettingsComponent
      },
      {
        path: 'projects',
        component: ProjectsComponent
      }
    ]
  }
];

@NgModule({
  declarations: [
    AdminComponent,
    UsersComponent,
    SettingsComponent,
    UserListComponent,
    AdminListComponent,
    CreateUserModalComponent,
    AddAdminModalComponent,
    ProjectsComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    CoreModule,
    NgbNavModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    NgbTooltipModule
  ]
})
export class AdminModule {
}
