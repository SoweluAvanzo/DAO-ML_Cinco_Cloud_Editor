import { Pipe, PipeTransform } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Pipe({
  name: 'validateFormInput',
  pure: false
})
export class ValidateFormInputPipe implements PipeTransform {

  transform(formControl: AbstractControl): boolean {
    return formControl.valid || !(formControl.dirty || formControl.touched);
  }
}
