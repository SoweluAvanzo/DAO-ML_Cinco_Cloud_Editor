import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { OrganizationsComponent } from './organizations.component';


const routes: Routes = [
  { path: '', component: OrganizationsComponent }
];

@NgModule({
  declarations: [
    OrganizationsComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ]
})
export class OrganizationsModule { }
