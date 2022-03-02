import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { InternalComponent } from './internal.component';

const routes: Routes = [
  { path: '', component: InternalComponent }
];

@NgModule({
  declarations: [
    InternalComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ]
})
export class InternalModule { }
