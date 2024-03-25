import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Page } from '../../models/page';
import { faAngleLeft, faAngleRight } from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'cc-pagination',
  templateUrl: './pagination.component.html'
})
export class PaginationComponent {
  faAngleLeft = faAngleLeft;
  faAngleRight = faAngleRight;

  @Input()
  page: Page<any>

  @Output()
  previousPage = new EventEmitter<void>();

  @Output()
  nextPage = new EventEmitter<void>();
}
