import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditOrganizationCardComponent } from './edit-organization-card.component';

describe('EditOrganizationCardComponent', () => {
  let component: EditOrganizationCardComponent;
  let fixture: ComponentFixture<EditOrganizationCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditOrganizationCardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditOrganizationCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
