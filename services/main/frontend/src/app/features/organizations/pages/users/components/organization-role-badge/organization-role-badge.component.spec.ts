import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganizationRoleBadgeComponent } from './organization-role-badge.component';

describe('OrganizationRoleBadgeComponent', () => {
  let component: OrganizationRoleBadgeComponent;
  let fixture: ComponentFixture<OrganizationRoleBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrganizationRoleBadgeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganizationRoleBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
