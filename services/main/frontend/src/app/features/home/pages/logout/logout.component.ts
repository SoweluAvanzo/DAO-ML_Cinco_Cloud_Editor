import { Component, OnInit } from '@angular/core';
import { AuthApiService } from '../../../../core/services/api/auth-api.service';
import { Router } from '@angular/router';
import { delay } from 'rxjs';

@Component({
  selector: 'cc-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  constructor(private authApi: AuthApiService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.authApi.logout()
      .pipe(delay(2000))
      .subscribe({
        complete: () => this.router.navigate(['/login'])
      });
  }
}
