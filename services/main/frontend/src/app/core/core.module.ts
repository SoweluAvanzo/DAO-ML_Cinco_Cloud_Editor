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
import {
  faAngleDoubleLeft,
  faAngleDoubleRight, faBriefcase, faChevronRight,
  faCircleNotch,
  faCloudUploadAlt
} from '@fortawesome/free-solid-svg-icons';
import { RouterModule } from '@angular/router';
import { SearchUserInputComponent } from './components/search-user-input/search-user-input.component';
import { NormalizeEnumValuePipe } from './pipes/normalize-enum-value.pipe';
import { OrganizationRoleBadgeComponent } from './components/organization-role-badge/organization-role-badge.component';
import { ProjectRoleBadgeComponent } from './components/project-role-badge/project-role-badge.component';
import { ConfirmModalComponent } from './components/confirm-modal/confirm-modal.component';
import { ToastsComponent } from './components/toasts/toasts.component';
import { NgbToastModule } from '@ng-bootstrap/ng-bootstrap';
import { FileInputComponent } from './components/file-input/file-input.component';
import { SidebarHeaderComponent } from './components/sidebar-header/sidebar-header.component';

@NgModule({
  declarations: [
    OrganizationRoleBadgeComponent,
    UserProfileImageComponent,
    WorkspaceImageSearchInputComponent,
    WorkspaceImageBadgeComponent,
    SidebarComponent,
    SidebarItemComponent,
    SearchUserInputComponent,
    NormalizeEnumValuePipe,
    ProjectRoleBadgeComponent,
    ConfirmModalComponent,
    ToastsComponent,
    FileInputComponent,
    SidebarHeaderComponent
  ],
  providers: [],
  exports: [
    OrganizationRoleBadgeComponent,
    UserProfileImageComponent,
    WorkspaceImageSearchInputComponent,
    WorkspaceImageBadgeComponent,
    SidebarComponent,
    SidebarItemComponent,
    SearchUserInputComponent,
    NormalizeEnumValuePipe,
    ProjectRoleBadgeComponent,
    ToastsComponent,
    FileInputComponent,
    SidebarHeaderComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    HttpClientModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    NgbToastModule
  ]
})
export class CoreModule {

  constructor(private library: FaIconLibrary) {
    library.addIcons(faAngleDoubleLeft, faAngleDoubleRight, faCircleNotch, faCloudUploadAlt, faChevronRight, faBriefcase);
  }
}
