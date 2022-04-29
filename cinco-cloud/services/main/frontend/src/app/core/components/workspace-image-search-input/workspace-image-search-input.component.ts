import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { WorkspaceImage } from '../../models/workspace-image';
import { WorkspaceImageApiService } from '../../services/api/workspace-image-api.service';
import { FormControl, FormGroup } from '@angular/forms';
import { debounceTime } from 'rxjs';

@Component({
  selector: 'cc-workspace-image-search-input',
  templateUrl: './workspace-image-search-input.component.html',
  styleUrls: ['./workspace-image-search-input.component.scss']
})
export class WorkspaceImageSearchInputComponent implements OnInit {

  @Output()
  selectImage = new EventEmitter<WorkspaceImage>();

  form = new FormGroup({
    input: new FormControl('')
  });

  results: WorkspaceImage[] = [];

  constructor(private workspaceImageApi: WorkspaceImageApiService) {
  }

  ngOnInit(): void {
    this.form.controls['input'].valueChanges.pipe(
      debounceTime(500)
    ).subscribe({
      next: value => {
        if (value.trim() != "") {
          this.searchImage(value)
        } else {
          this.results = [];
        }
      }
    });
  }

  private searchImage(term: string): void {
    this.workspaceImageApi.search(term).subscribe({
      next: results => this.results = results
    });
  }

  handleSelect(image: WorkspaceImage): void {
    this.selectImage.emit(image);
    this.results = [];
  }
}
