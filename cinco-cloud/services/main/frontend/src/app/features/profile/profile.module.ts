import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ProfileComponent } from './profile.component';
import { PasswordChangeComponent } from './components/password-change/password-change.component';
import { PersonalInformationComponent } from './components/personal-information/personal-information.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CoreModule } from '../../core/core.module';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faExclamationTriangle, faInfoCircle, faTrash } from '@fortawesome/free-solid-svg-icons';
import { DeleteUserComponent } from './components/delete-user/delete-user.component';
import { DeleteUserModalComponent } from './components/delete-user/delete-user-modal/delete-user-modal.component';


const routes: Routes = [
  { path: '', component: ProfileComponent }
];

@NgModule({
  declarations: [
    ProfileComponent,
    PasswordChangeComponent,
    PersonalInformationComponent,
    DeleteUserComponent,
    DeleteUserModalComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ReactiveFormsModule,
    CoreModule,
    FontAwesomeModule,
    FormsModule
  ]
})
export class ProfileModule {

  constructor(library: FaIconLibrary) {
    library.addIcons(faTrash, faExclamationTriangle, faInfoCircle);
  }
}
