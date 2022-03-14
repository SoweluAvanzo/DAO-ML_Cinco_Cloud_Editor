import { Component, Input } from '@angular/core';
import { User } from '../../../../../../core/models/user';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Project } from '../../../../../../core/models/project';
import { ProjectApiService } from '../../../../../../core/services/api/project-api.service';

@Component({
  selector: 'cc-add-user-modal',
  templateUrl: './add-user-modal.component.html'
})
export class AddUserModalComponent {

  @Input()
  project: Project;

  user: User;
  errorMessage: string = '';

  constructor(public modal: NgbActiveModal,
              public projectApi: ProjectApiService) {
  }

  addUser(): void {
    this.projectApi.addMember(this.project.id, this.user).subscribe({
      next: project => this.modal.close(project),
      error: () => this.errorMessage = 'Failed to add user to project'
    });
  }

}
