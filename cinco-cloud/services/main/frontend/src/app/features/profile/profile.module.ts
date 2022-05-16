import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { ProfileComponent } from './profile.component';
import { PasswordChangeComponent } from './components/password-change/password-change.component';
import { PersonalInformationComponent } from './components/personal-information/personal-information.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CoreModule } from '../../core/core.module';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faTrash } from '@fortawesome/free-solid-svg-icons';


const routes: Routes = [
  { path: '', component: ProfileComponent }
];

@NgModule({
  declarations: [
    ProfileComponent,
    PasswordChangeComponent,
    PersonalInformationComponent
  ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        ReactiveFormsModule,
        CoreModule,
        FontAwesomeModule
    ]
})
export class ProfileModule {

  constructor(library: FaIconLibrary) {
    library.addIcons(faTrash);
  }
}
