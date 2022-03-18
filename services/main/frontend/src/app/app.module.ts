import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { RouterModule, Routes } from '@angular/router';
import { CoreModule } from './core/core.module';
import { HomeModule } from './features/home/home.module';
import { NotFoundComponent } from './features/home/pages/not-found/not-found.component';
import { UserIsLoggedInGuard } from './core/guards/user-is-logged-in.guard';
import { UnauthenticatedInterceptor } from './core/interceptors/unauthenticated.interceptor';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import {HashLocationStrategy, LocationStrategy} from "@angular/common";

const routes: Routes = [
  { path: '', loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule) },
  {
    path: 'app',
    loadChildren: () => import('./features/internal/internal.module').then(m => m.InternalModule),
    canActivate: [UserIsLoggedInGuard],
    canActivateChild: [UserIsLoggedInGuard],
  },
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HomeModule,
    FontAwesomeModule,
    RouterModule.forRoot(routes),
    CoreModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: UnauthenticatedInterceptor,
      multi: true
    },
    {
      provide: LocationStrategy,
      useClass: HashLocationStrategy
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
