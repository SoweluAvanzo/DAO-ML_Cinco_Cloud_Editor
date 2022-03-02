import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { UserProfileImageComponent } from './components/user-profile-image/user-profile-image.component';

@NgModule({
  declarations: [
    UserProfileImageComponent
  ],
  providers: [],
  exports: [
    UserProfileImageComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule
  ]
})
export class CoreModule {
}
