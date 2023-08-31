import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Project } from '../../../core/models/project';
import { ProjectApiService } from '../../../core/services/api/project-api.service';

@Injectable({
  providedIn: 'root'
})
export class ProjectResolver  {

  constructor(private projectApi: ProjectApiService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Project> {
    const projectId = Number(route.paramMap.get('projectId'));
    return this.projectApi.get(projectId);
  }
}
