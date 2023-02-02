import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Page } from '../../models/page';

@Component({
  selector: 'cc-pagination',
  templateUrl: './pagination.component.html'
})
export class PaginationComponent {

  @Input()
  page: Page<any>

  @Output()
  previousPage = new EventEmitter<void>();

  @Output()
  nextPage = new EventEmitter<void>();
}
