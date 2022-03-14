import { Component, Input, OnInit } from '@angular/core';
import { Project } from '../../../../../../core/models/project';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ProjectStoreService } from '../../../../services/project-store.service';
import { UpdateProjectInput } from '../../../../../../core/models/forms/update-project-input';

@Component({
  selector: 'cc-edit-project-card',
  templateUrl: './edit-project-card.component.html'
})
export class EditProjectCardComponent implements OnInit {

  @Input()
  project: Project;

  form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    description: new FormControl('')
  });

  constructor(private projectStore: ProjectStoreService) {
  }

  ngOnInit(): void {
    this.form.get('name').setValue(this.project.name);
    this.form.get('description').setValue(this.project.description);
  }

  update(): void {
    const input: UpdateProjectInput = this.form.value;
    this.projectStore.updateProject(input);
  }
}
