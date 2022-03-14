import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProjectCardComponent } from './edit-project-card.component';

describe('EditProjectCardComponent', () => {
  let component: EditProjectCardComponent;
  let fixture: ComponentFixture<EditProjectCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditProjectCardComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProjectCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
