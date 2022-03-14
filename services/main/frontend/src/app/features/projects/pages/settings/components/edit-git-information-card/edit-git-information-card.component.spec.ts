import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditGitInformationCardComponent } from './edit-git-information-card.component';

describe('EditGitInformationCardComponent', () => {
  let component: EditGitInformationCardComponent;
  let fixture: ComponentFixture<EditGitInformationCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditGitInformationCardComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditGitInformationCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
