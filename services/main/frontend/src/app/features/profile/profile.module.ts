import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { ProfileComponent } from './profile.component';
import { PasswordChangeComponent } from './components/password-change/password-change.component';
import { PersonalInformationComponent } from './components/personal-information/personal-information.component';
import {ReactiveFormsModule} from "@angular/forms";


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
    ReactiveFormsModule
  ]
})
export class ProfileModule { }
