import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'cc-toggle-button',
  templateUrl: './toggle-button.component.html',
  styleUrls: ['./toggle-button.component.scss']
})
export class ToggleButtonComponent {

  @Input()
  enabled = false;

  @Input()
  enabledTooltip = null;

  @Input()
  disabledTooltip = null;

  @Output()
  toggle = new EventEmitter<boolean>();
}
