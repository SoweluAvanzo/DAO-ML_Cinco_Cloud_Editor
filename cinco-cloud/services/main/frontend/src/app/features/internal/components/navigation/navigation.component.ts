import { Component, OnInit } from '@angular/core';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { User } from '../../../../core/models/user';
import { BreakpointObserver } from '@angular/cdk/layout';
import { faUserLock, faUser, faSignOutAlt, faBars } from '@fortawesome/free-solid-svg-icons';

@UntilDestroy()
@Component({
  selector: 'cc-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {
  faUserLock = faUserLock;
  faUser = faUser;
  faSignOutAlt = faSignOutAlt;
  faBars = faBars;

  user: User;
  showMenu = false; // state of the dropdown menu

  constructor(private appStore: AppStoreService,
              private breakpointObserver: BreakpointObserver) {
  }

  ngOnInit(): void {
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.user = user
    });

    this.breakpointObserver.observe([
      '(min-width: 992px)' // this is where bootstrap collapsed the menu
    ]).pipe(untilDestroyed(this)).subscribe({
      next: () => this.showMenu = false
    });
  }
}
