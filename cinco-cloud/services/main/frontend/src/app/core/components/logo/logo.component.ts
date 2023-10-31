import {Component, Input} from '@angular/core';

@Component({
  selector: 'cc-logo',
  templateUrl: './logo.component.html',
  styleUrls: ['./logo.component.scss']
})
export class LogoComponent {

  @Input()
  height: string;

  @Input()
  showName: boolean;
}
