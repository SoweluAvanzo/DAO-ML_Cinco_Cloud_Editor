import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { RouterModule, Routes } from '@angular/router';
import { CoreModule } from './core/core.module';
import { HomeModule } from './features/home/home.module';
import { NotFoundComponent } from './features/home/pages/not-found/not-found.component';

const routes: Routes = [
  { path: '', loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule) },
  { path: 'app', loadChildren: () => import('./features/internal/internal.module').then(m => m.InternalModule) },
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
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
