import { Component, OnInit } from '@angular/core';
import { ProjectStoreService } from '../../services/project-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { Project } from '../../../../core/models/project';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@UntilDestroy()
@Component({
  selector: 'cc-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  project: Project;

  constructor(private projectStore: ProjectStoreService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.projectStore.project$.pipe(untilDestroyed(this)).subscribe({
      next: project => this.project = project
    });
  }
}
