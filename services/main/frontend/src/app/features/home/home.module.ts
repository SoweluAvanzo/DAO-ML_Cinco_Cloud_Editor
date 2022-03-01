import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LoginComponent } from './pages/login/login.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { UserNavbarComponent } from './components/user-navbar/user-navbar.component';
import { RegisterComponent } from './pages/register/register.component';
import { AboutComponent } from './pages/about/about.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { HomeRoutingModule } from './home-routing.module';

@NgModule({
  declarations: [
    LoginComponent,
    WelcomeComponent,
    UserNavbarComponent,
    RegisterComponent,
    AboutComponent,
    NotFoundComponent,
  ],
  imports: [
    CommonModule,
    HomeRoutingModule
  ],
  exports: []
})
export class HomeModule {
}
