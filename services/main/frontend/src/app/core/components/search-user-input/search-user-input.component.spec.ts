import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchUserInputComponent } from './search-user-input.component';

describe('SearchUserModalComponent', () => {
  let component: SearchUserInputComponent;
  let fixture: ComponentFixture<SearchUserInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SearchUserInputComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchUserInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
