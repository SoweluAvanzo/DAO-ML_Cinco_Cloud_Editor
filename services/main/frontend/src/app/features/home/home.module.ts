import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LoginComponent } from './pages/login/login.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { UserNavbarComponent } from './components/user-navbar/user-navbar.component';
import { RegisterComponent } from './pages/register/register.component';
import { AboutComponent } from './pages/about/about.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { HomeRoutingModule } from './home-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faBars } from '@fortawesome/free-solid-svg-icons';

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
    HomeRoutingModule,
    ReactiveFormsModule,
    FontAwesomeModule
  ],
  exports: []
})
export class HomeModule {
  constructor(library: FaIconLibrary) {
    library.addIcons(faBars);
  }
}
