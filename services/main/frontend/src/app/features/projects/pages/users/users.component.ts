import { Component, OnInit } from '@angular/core';
import { ProjectStoreService } from '../../services/project-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { Project } from '../../../../core/models/project';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { User } from '../../../../core/models/user';
import { AddUserModalComponent } from './components/add-user-modal/add-user-modal.component';

@UntilDestroy()
@Component({
  selector: 'cc-users',
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {

  project: Project;
  currentUser: User;

  constructor(public projectStore: ProjectStoreService,
              private appStore: AppStoreService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.projectStore.project$.pipe(untilDestroyed(this)).subscribe({
      next: project => this.project = project
    });
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.currentUser = user
    });
  }

  openSearchUserModal(): void {
    const ref = this.modalService.open(AddUserModalComponent);
    ref.componentInstance.project = this.project;
    ref.result.then(updatedProject => {
      this.projectStore.setProject(updatedProject);
    }).catch(() => {
    });
  }
}
