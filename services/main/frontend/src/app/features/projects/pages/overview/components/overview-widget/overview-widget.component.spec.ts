import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverviewWidgetComponent } from './overview-widget.component';

describe('StatusWidgetComponent', () => {
  let component: OverviewWidgetComponent;
  let fixture: ComponentFixture<OverviewWidgetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OverviewWidgetComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OverviewWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
