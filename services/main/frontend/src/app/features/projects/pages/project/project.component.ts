import { Component, OnDestroy, OnInit } from '@angular/core';
import { ProjectStoreService } from '../../services/project-store.service';
import { faCode, faCogs, faHistory, faThLarge, faUsers } from '@fortawesome/free-solid-svg-icons';
import { ActivatedRoute } from '@angular/router';
import { Project } from '../../../../core/models/project';

@Component({
  selector: 'cc-project',
  templateUrl: './project.component.html',
  providers: [ProjectStoreService]
})
export class ProjectComponent implements OnInit, OnDestroy {

  project: Project;

  icons = {
    code: faCode,
    cogs: faCogs,
    history: faHistory,
    thLarge: faThLarge,
    users: faUsers
  };

  constructor(private route: ActivatedRoute,
              private projectStore: ProjectStoreService) {
  }

  ngOnInit(): void {
    this.project = this.route.snapshot.data['project'];
    this.projectStore.setProject(this.project);
    this.projectStore.initWebSocket();
  }

  ngOnDestroy(): void {
    this.projectStore.closeWebSocket();
  }
}
