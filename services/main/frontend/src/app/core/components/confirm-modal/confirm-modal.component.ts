import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cc-confirm-modal',
  templateUrl: './confirm-modal.component.html',
  styleUrls: ['./confirm-modal.component.scss']
})
export class ConfirmModalComponent {

  @Input()
  text: string;

  @Input()
  confirmButtonText = 'Ok';

  @Input()
  cancelButtonText = 'Cancel';

  constructor(public modal: NgbActiveModal) { }
}
