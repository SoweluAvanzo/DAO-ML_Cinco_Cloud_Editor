import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { UserApiService } from '../../services/api/user-api.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { catchError, debounceTime, filter, map, mergeMap, of, tap } from 'rxjs';
import { User } from '../../models/user';

@Component({
  selector: 'cc-search-user-input',
  templateUrl: './search-user-input.component.html'
})
export class SearchUserInputComponent implements OnInit {

  @Output()
  user = new EventEmitter<User>();

  searching = false;
  foundUser: User;
  notFound = false;

  form = new FormGroup({
    searchTerm: new FormControl('', [Validators.required])
  });

  constructor(private userApi: UserApiService) {
  }

  ngOnInit(): void {
    this.form.controls['searchTerm'].valueChanges.pipe(
      filter(v => v != null),
      tap(() => {
        this.searching = true;
        this.foundUser = null;
        this.notFound = false;
      }),
      map(v => v.trim()),
      tap(v => this.searching = v !== ''),
      filter(v => v !== ''),
      debounceTime(1000),
      mergeMap(v => this.userApi.search(v).pipe(
        catchError(() => of(null))
      ))
    ).subscribe({
      next: (user: User) => {
        this.foundUser = user;
        this.user.emit(this.foundUser);
        this.searching = false;
        this.notFound = this.foundUser == null;
      },
    });
  }
}
