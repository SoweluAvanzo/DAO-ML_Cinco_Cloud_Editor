import { Component, OnInit } from '@angular/core';
import { ProjectApiService } from '../../../../core/services/api/project-api.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cc-create-project-modal',
  templateUrl: './create-project-modal.component.html',
  styleUrls: ['./create-project-modal.component.scss']
})
export class CreateProjectModalComponent {

  constructor(private projectApi: ProjectApiService,
              public modal: NgbActiveModal) { }
}
