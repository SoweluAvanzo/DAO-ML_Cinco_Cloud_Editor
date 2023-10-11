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

  files: File[] = [];

  handleChange(event: any): void {
    if (event.target && event.target.files) {
      const files = event.target.files;
      if (files.length > 0) {
        this.files = files;
        this.selectFile.emit(this.files);
      }
    } else {
      this.reset();
    }
  }

  reset(e = null): void {
    if (e) {
      e.stopPropagation();
      e.preventDefault();
    }
    this.files = [];
    this.selectFile.emit([]);
  }
}
