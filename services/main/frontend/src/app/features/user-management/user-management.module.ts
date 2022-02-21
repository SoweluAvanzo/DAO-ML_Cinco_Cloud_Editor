import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LoginComponent } from './pages/login/login.component';
import {RouterModule, Routes} from "@angular/router";
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { UserNavbarComponent } from './components/user-navbar/user-navbar.component';

const routes: Routes = [
  { path:'home', component: WelcomeComponent},
  { path:'login', component: LoginComponent},
  { path: '', redirectTo: '/home', pathMatch: 'full' },
]

@NgModule({
  declarations: [
    LoginComponent,
    WelcomeComponent,
    UserNavbarComponent
  ],
    imports: [
        CommonModule,
        RouterModule,
        RouterModule.forRoot(routes)
    ],
  exports: [
    WelcomeComponent
  ]
})
export class UserManagementModule { }
