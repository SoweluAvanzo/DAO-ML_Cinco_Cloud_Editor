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
import { faCloud, faCog } from '@fortawesome/free-solid-svg-icons';

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
    EditorComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FontAwesomeModule,
    CoreModule
  ]
})
export class ProjectsModule {

  constructor(private library: FaIconLibrary) {
    library.addIcons(faCloud, faCog);
  }
}
