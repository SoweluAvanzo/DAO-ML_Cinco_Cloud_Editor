import { Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'cc-validation-error-message',
  templateUrl: './validation-error-message.component.html'
})
export class ValidationErrorMessageComponent {

  @Input()
  form: AbstractControl;

  @Input()
  name: string;

  get errorType(): string {
    return Object.keys(this.form.errors)[0];
  }

  isValidFormControl(formControl: AbstractControl): boolean {
    return formControl.valid || !(formControl.dirty || formControl.touched);
  }
}
