import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Project } from '../../../core/models/project';
import { ProjectApiService } from '../../../core/services/api/project-api.service';

@Injectable({
  providedIn: 'root'
})
export class ProjectResolver implements Resolve<Project> {

  constructor(private projectApi: ProjectApiService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Project> {
    const projectId = Number(route.paramMap.get('projectId'));
    return this.projectApi.get(projectId);
  }
}
