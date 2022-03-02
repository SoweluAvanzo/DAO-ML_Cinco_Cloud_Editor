import { Component, OnInit } from '@angular/core';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { User } from '../../../../core/models/user';

@UntilDestroy()
@Component({
  selector: 'cc-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {

  public user: User;

  constructor(private appStore: AppStoreService) {
  }

  ngOnInit(): void {
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.user = user
    });
  }
}
