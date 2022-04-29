import { Component, OnInit } from '@angular/core';
import { ProjectStoreService } from '../../services/project-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { filter, fromEvent } from 'rxjs';
import { Project } from '../../../../core/models/project';

@UntilDestroy()
@Component({
  selector: 'cc-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})
export class OverviewComponent implements OnInit {

  project: Project;
  projectWebSocket: WebSocket;

  constructor(private projectStore: ProjectStoreService) {
  }

  ngOnInit(): void {
    this.projectStore.project$.pipe(untilDestroyed(this)).subscribe({
      next: project => this.project = project
    });

    this.projectStore.projectWebSocket$.pipe(
      untilDestroyed(this),
      filter(ws => ws != null)
    ).subscribe({
      next: ws => {
        this.projectWebSocket = ws;
        this.listenToProjectWebSocket();
      }
    });
  }

  private listenToProjectWebSocket(): void {
    fromEvent(this.projectWebSocket, 'message').pipe(untilDestroyed(this)).subscribe({
      next: (e: any) => {
        console.log(JSON.parse(e.data));
      }
    });
  }
}
