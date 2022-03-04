import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkspaceImageSearchInputComponent } from './workspace-image-search-input.component';

describe('WorkspaceImageSearchInputComponent', () => {
  let component: WorkspaceImageSearchInputComponent;
  let fixture: ComponentFixture<WorkspaceImageSearchInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkspaceImageSearchInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkspaceImageSearchInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
