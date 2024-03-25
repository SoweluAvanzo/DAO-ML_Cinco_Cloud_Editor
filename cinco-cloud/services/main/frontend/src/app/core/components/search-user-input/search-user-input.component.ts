import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { UserApiService } from '../../services/api/user-api.service';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { catchError, debounceTime, filter, map, mergeMap, of, tap } from 'rxjs';
import { User } from '../../models/user';
import { Page } from '../../models/page';
import { faEnvelope, faUser } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'cc-search-user-input',
  templateUrl: './search-user-input.component.html'
})
export class SearchUserInputComponent implements OnInit {
  faEnvelope = faEnvelope;
  faUser = faUser;

  @Output()
  user = new EventEmitter<User>();

  searching = false;
  matches: User[] = [];
  notFound = false;
  selectedUser: User;

  form = new UntypedFormGroup({
    searchTerm: new UntypedFormControl('', [Validators.required])
  });

  constructor(private userApi: UserApiService) {
  }

  ngOnInit(): void {
    this.form.controls['searchTerm'].valueChanges.pipe(
      filter(v => v != null),
      tap(() => {
        this.searching = true;
        this.matches = [];
        this.notFound = false;
      }),
      map(v => v.trim()),
      tap(v => this.searching = v !== ''),
      filter(v => v !== ''),
      debounceTime(1000),
      mergeMap((v: string) => this.userApi.search(0, 10, v).pipe(
        catchError(() => of(null))
      ))
    ).subscribe({
      next: (userPage: Page<User>) => {
        this.matches = userPage.items;
        this.searching = false;
        this.notFound = this.matches.length === 0;
      },
    });
  }

  select(user: User): void {
    if (this.selectedUser == null) {
      this.selectedUser = user;
    } else if (this.selectedUser.id === user.id) {
      this.selectedUser = null;
    } else {
      this.selectedUser = user;
    }
    this.user.emit(this.selectedUser);
  }
}
