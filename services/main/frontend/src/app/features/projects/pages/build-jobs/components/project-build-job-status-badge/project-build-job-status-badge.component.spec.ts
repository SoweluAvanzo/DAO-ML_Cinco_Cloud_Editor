import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectBuildJobStatusBadgeComponent } from './project-build-job-status-badge.component';

describe('ProjectBuildJobStatusBadgeComponent', () => {
  let component: ProjectBuildJobStatusBadgeComponent;
  let fixture: ComponentFixture<ProjectBuildJobStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectBuildJobStatusBadgeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectBuildJobStatusBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
