import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkspaceImageBadgeComponent } from './workspace-image-badge.component';

describe('WorkspaceImageBadgeComponent', () => {
  let component: WorkspaceImageBadgeComponent;
  let fixture: ComponentFixture<WorkspaceImageBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkspaceImageBadgeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkspaceImageBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
