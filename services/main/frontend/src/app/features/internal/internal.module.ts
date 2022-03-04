import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { InternalComponent } from './internal.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faBars, faUserLock } from '@fortawesome/free-solid-svg-icons';
import { NgbDropdownModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { CoreModule } from '../../core/core.module';
import { UserIsAdminGuard } from '../../core/guards/user-is-admin.guard';
import { OverviewComponent } from './pages/overview/overview.component';

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
      }
    ]
  },
];

@NgModule({
  declarations: [
    InternalComponent,
    NavigationComponent,
    OverviewComponent
  ],
  imports: [
    CommonModule,
    FontAwesomeModule,
    NgbDropdownModule,
    NgbNavModule,
    RouterModule.forChild(routes),
    CoreModule
  ]
})
export class InternalModule {
  constructor(library: FaIconLibrary) {
    library.addIcons(faBars, faUserLock);
  }
}
