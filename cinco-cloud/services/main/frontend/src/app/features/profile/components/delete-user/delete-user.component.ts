import { Component } from '@angular/core';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DeleteUserModalComponent } from './delete-user-modal/delete-user-modal.component';

@Component({
  selector: 'cc-delete-user',
  templateUrl: './delete-user.component.html'
})
export class DeleteUserComponent {

  constructor(private appStore: AppStoreService,
              private modalService: NgbModal) { }

  openDeleteModal(): void {
    const ref = this.modalService.open(DeleteUserModalComponent);
    ref.componentInstance.user = this.appStore.getUser();
  }
}
