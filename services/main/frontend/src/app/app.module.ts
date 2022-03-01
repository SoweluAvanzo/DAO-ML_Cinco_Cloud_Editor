import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { UserManagementModule } from './features/user-management/user-management.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome'

import { RouterModule, Routes } from '@angular/router'
import { LoginComponent} from "./features/user-management/pages/login/login.component";
import {WelcomeComponent} from "./features/user-management/pages/welcome/welcome.component";
import {CoreModule} from "./core/core.module";

const routes: Routes = [
  { path:'home', component: WelcomeComponent},
  { path:'login', component: LoginComponent},
  { path: '', redirectTo: '/home', pathMatch: 'full' },
]

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    UserManagementModule,
    FontAwesomeModule,
    RouterModule.forRoot(routes),
    CoreModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
