import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ProjectsComponent } from './projects.component';
import { UsersComponent } from './pages/users/users.component';
import { BuildJobsComponent } from './pages/build-jobs/build-jobs.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { ProjectComponent } from './pages/project/project.component';
import { ProjectResolver } from './resolvers/project.resolver';
import { CoreModule } from '../../core/core.module';
import { EditorComponent } from './pages/editor/editor.component';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faBriefcase, faCalendar, faCheck,
  faCircleNotch, faClock,
  faCloud,
  faCode,
  faCog, faEllipsisV,
  faEnvelope, faPause, faTimes,
  faTrash,
  faUser
} from '@fortawesome/free-solid-svg-icons';
import { OverviewComponent } from './pages/overview/overview.component';
import { EditorWidgetComponent } from './pages/overview/components/editor-widget/editor-widget.component';
import {NgbDropdownModule, NgbModalModule, NgbNavModule} from '@ng-bootstrap/ng-bootstrap';
import { EditProjectCardComponent } from './pages/settings/components/edit-project-card/edit-project-card.component';
import { EditGitInformationCardComponent } from './pages/settings/components/edit-git-information-card/edit-git-information-card.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AddUserModalComponent } from './pages/users/components/add-user-modal/add-user-modal.component';
import { ProjectBuildJobStatusBadgeComponent } from './pages/build-jobs/components/project-build-job-status-badge/project-build-job-status-badge.component';
import { OverviewWidgetComponent } from './pages/overview/components/overview-widget/overview-widget.component';
import { StatusWidgetComponent } from './pages/overview/components/status-widget/status-widget.component';

const routes: Routes = [
  {
    path: '',
    component: ProjectsComponent,
    children: [
      {
        path: ':projectId',
        component: ProjectComponent,
        resolve: {
          project: ProjectResolver
        },
        children: [
          {
            path: '',
            component: OverviewComponent
          },
          {
            path: 'users',
            component: UsersComponent
          },
          {
            path: 'editor',
            component: EditorComponent
          },
          {
            path: 'build-jobs',
            component: BuildJobsComponent
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
    ProjectsComponent,
    UsersComponent,
    BuildJobsComponent,
    SettingsComponent,
    ProjectComponent,
    EditorComponent,
    OverviewComponent,
    EditorWidgetComponent,
    EditProjectCardComponent,
    EditGitInformationCardComponent,
    AddUserModalComponent,
    ProjectBuildJobStatusBadgeComponent,
    OverviewWidgetComponent,
    StatusWidgetComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FontAwesomeModule,
    ReactiveFormsModule,
    NgbNavModule,
    NgbModalModule,
    NgbDropdownModule,
    CoreModule
  ]
})
export class ProjectsModule {

  constructor(private library: FaIconLibrary) {
    library.addIcons(faCloud, faCog, faCode, faCircleNotch, faEnvelope, faUser, faTrash, faBriefcase, faEllipsisV,
      faCheck, faPause, faTimes, faCircleNotch, faCalendar, faClock);
  }
}
