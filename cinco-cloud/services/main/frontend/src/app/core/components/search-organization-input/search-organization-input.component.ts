import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { User } from '../../models/user';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { UserApiService } from '../../services/api/user-api.service';
import { catchError, debounceTime, filter, map, mergeMap, Observable, of, tap } from 'rxjs';
import { Organization } from '../../models/organization';
import { OrganizationApiService } from '../../services/api/organization-api.service';

@Component({
  selector: 'cc-search-organization-input',
  templateUrl: './search-organization-input.component.html'
})
export class SearchOrganizationInputComponent implements OnInit {

  @Output()
  organization = new EventEmitter<Organization>();

  searching = false;
  foundOrganization: Organization;
  notFound = false;

  user: User;
  orgs: Organization[];

  form = new UntypedFormGroup({
    searchTerm: new UntypedFormControl('', [Validators.required])
  });

  constructor(private userApi: UserApiService,
              private organizationApi: OrganizationApiService) {
  }

  ngOnInit(): void {
    this.userApi.getCurrent().subscribe({
      next: user => this.user = user,
      error: res => console.log(res)
    })
    this.organizationApi.getAll().subscribe({
      next: orgs => this.orgs = orgs,
      error: res => console.log(res)
    })

    this.form.controls['searchTerm'].valueChanges.pipe(
      filter(v => v != null),
      tap(() => {
        this.searching = true;
        this.foundOrganization = null;
        this.notFound = false;
      }),
      map(v => v.trim()),
      tap(v => this.searching = v !== ''),
      filter(v => v !== ''),
      debounceTime(1000),
      mergeMap(v => this.searchOrg(v).pipe(
        catchError(() => of(null))
      ))
    ).subscribe({
      next: (org: Organization) => {
        this.foundOrganization = org;
        this.organization.emit(this.foundOrganization);
        this.searching = false;
        this.notFound = this.foundOrganization == null;
      },
    });
  }

  searchOrg(v: String): Observable<Organization> {
    return new Observable<Organization>((observer) => {
      observer.next(this.orgs.find((org) => {
        return org.name.toLowerCase() == v.toLowerCase();
      }))
    })
  }
}
