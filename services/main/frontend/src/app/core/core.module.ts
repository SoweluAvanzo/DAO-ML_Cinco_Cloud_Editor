import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { UserProfileImageComponent } from './components/user-profile-image/user-profile-image.component';
import { WorkspaceImageSearchInputComponent } from './components/workspace-image-search-input/workspace-image-search-input.component';
import { ReactiveFormsModule } from '@angular/forms';
import { WorkspaceImageBadgeComponent } from './components/workspace-image-badge/workspace-image-badge.component';

@NgModule({
  declarations: [
    UserProfileImageComponent,
    WorkspaceImageSearchInputComponent,
    WorkspaceImageBadgeComponent
  ],
  providers: [],
  exports: [
    UserProfileImageComponent,
    WorkspaceImageSearchInputComponent,
    WorkspaceImageBadgeComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    ReactiveFormsModule
  ]
})
export class CoreModule {
}
