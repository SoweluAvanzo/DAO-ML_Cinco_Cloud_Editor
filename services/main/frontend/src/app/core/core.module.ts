import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { UserProfileImageComponent } from './components/user-profile-image/user-profile-image.component';
import { WorkspaceImageSearchInputComponent } from './components/workspace-image-search-input/workspace-image-search-input.component';
import { ReactiveFormsModule } from '@angular/forms';
import { WorkspaceImageBadgeComponent } from './components/workspace-image-badge/workspace-image-badge.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { SidebarItemComponent } from './components/sidebar-item/sidebar-item.component';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faAngleDoubleLeft, faAngleDoubleRight } from '@fortawesome/free-solid-svg-icons';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [
    UserProfileImageComponent,
    WorkspaceImageSearchInputComponent,
    WorkspaceImageBadgeComponent,
    SidebarComponent,
    SidebarItemComponent
  ],
  providers: [],
  exports: [
    UserProfileImageComponent,
    WorkspaceImageSearchInputComponent,
    WorkspaceImageBadgeComponent,
    SidebarComponent,
    SidebarItemComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    HttpClientModule,
    ReactiveFormsModule,
    FontAwesomeModule
  ]
})
export class CoreModule {

  constructor(private library: FaIconLibrary) {
    library.addIcons(faAngleDoubleLeft, faAngleDoubleRight);
  }
}
