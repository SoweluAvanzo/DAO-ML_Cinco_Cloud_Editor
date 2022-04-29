import { Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmModalComponent } from '../../components/confirm-modal/confirm-modal.component';

export interface ConfirmModalData {
  text: string;
  confirmButtonText?: string;
  cancelButtonText?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ModalUtilsService {

  constructor(private modalService: NgbModal) { }

  confirm(data: ConfirmModalData): Promise<any> {
    const ref = this.modalService.open(ConfirmModalComponent);
    ref.componentInstance.text = data.text;
    if (data.confirmButtonText != null) {
      ref.componentInstance.confirmButtonText = data.confirmButtonText;
    }
    if (data.cancelButtonText != null) {
      ref.componentInstance.cancelButtonText = data.cancelButtonText;
    }
    return ref.result;
  }
}
