import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccessManagementComponent } from './access-management.component';

describe('AccessManagementComponent', () => {
  let component: AccessManagementComponent;
  let fixture: ComponentFixture<AccessManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AccessManagementComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccessManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
