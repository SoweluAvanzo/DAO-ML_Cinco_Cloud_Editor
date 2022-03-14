import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectRoleBadgeComponent } from './project-role-badge.component';

describe('ProjectRoleBadgeComponent', () => {
  let component: ProjectRoleBadgeComponent;
  let fixture: ComponentFixture<ProjectRoleBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProjectRoleBadgeComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectRoleBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
