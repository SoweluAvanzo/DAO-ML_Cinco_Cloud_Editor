import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { InternalComponent } from './internal.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faBars } from '@fortawesome/free-solid-svg-icons';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { CoreModule } from '../../core/core.module';

const routes: Routes = [
  {
    path: '',
    component: InternalComponent,
  }
];

@NgModule({
  declarations: [
    InternalComponent,
    NavigationComponent
  ],
  imports: [
    CommonModule,
    FontAwesomeModule,
    NgbDropdownModule,
    RouterModule.forChild(routes),
    CoreModule
  ]
})
export class InternalModule {
  constructor(library: FaIconLibrary) {
    library.addIcons(faBars);
  }
}
