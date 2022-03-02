import { Component, OnInit } from '@angular/core';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';

@Component({
  selector: 'cc-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  constructor(private appStore: AppStoreService) {
  }

  ngOnInit(): void {
    this.appStore.logout().subscribe({
      error: console.error
    });
  }
}
