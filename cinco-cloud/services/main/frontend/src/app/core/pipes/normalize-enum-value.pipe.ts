import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'normalizeEnumValue'
})
export class NormalizeEnumValuePipe implements PipeTransform {

  transform(value: string, ...args: unknown[]): string {
    return value.split("_")
      .map(v => v.substring(0, 1) + v.substring(1, v.length).toLowerCase())
      .join(" ");
  }
}
