import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'cc-file-input',
  templateUrl: './file-input.component.html',
  styleUrls: ['./file-input.component.scss']
})
export class FileInputComponent {

  @Input()
  multiple = false;

  @Output()
  selectFile = new EventEmitter<File[]>();

  @Output()
  clear = new EventEmitter<never>();

  files: File[] = [];

  handleChange(event: any): void {
    const files = event.target.files;
    if (files.length > 0) {
      this.files = files;
      this.selectFile.emit(this.files);
    }
  }

  reset(): void {
    this.files = [];
  }
}
