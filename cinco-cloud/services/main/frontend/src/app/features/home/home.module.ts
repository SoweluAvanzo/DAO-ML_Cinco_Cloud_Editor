import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LoginComponent } from './pages/login/login.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { UserNavbarComponent } from './components/user-navbar/user-navbar.component';
import { RegisterComponent } from './pages/register/register.component';
import { AboutComponent } from './pages/about/about.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { ReactiveFormsModule } from '@angular/forms';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faBars } from '@fortawesome/free-solid-svg-icons';
import { RouterModule, Routes } from '@angular/router';
import { LogoutComponent } from './pages/logout/logout.component';
import { CoreModule } from '../../core/core.module';
import { HomeComponent } from './pages/home/home.component';
import { AppFooterComponent } from './components/app-footer/app-footer.component';

const routes: Routes = [
  {
    path: '', component: HomeComponent, children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: WelcomeComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'about', component: AboutComponent },
      { path: 'logout', component: LogoutComponent }
    ]
  }
];

@NgModule({
  declarations: [
    LoginComponent,
    WelcomeComponent,
    UserNavbarComponent,
    RegisterComponent,
    AboutComponent,
    NotFoundComponent,
    LogoutComponent,
    HomeComponent,
    AppFooterComponent
  ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        ReactiveFormsModule,
        FontAwesomeModule,
        CoreModule
    ],
  exports: []
})
export class HomeModule {
  constructor(library: FaIconLibrary) {
    library.addIcons(faBars);
  }
}
