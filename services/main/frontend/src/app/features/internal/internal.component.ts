import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'cc-internal',
  templateUrl: './internal.component.html',
  styleUrls: ['./internal.component.scss']
})
export class InternalComponent implements OnInit {

  constructor(private router: Router) {
  }

  ngOnInit(): void {
    // this.router.navigate(['app', 'overview'])
  }
}
