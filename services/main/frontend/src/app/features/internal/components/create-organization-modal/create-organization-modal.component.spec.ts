import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateOrganizationModalComponent } from './create-organization-modal.component';

describe('CreateOrganizationModalComponent', () => {
  let component: CreateOrganizationModalComponent;
  let fixture: ComponentFixture<CreateOrganizationModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateOrganizationModalComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateOrganizationModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
