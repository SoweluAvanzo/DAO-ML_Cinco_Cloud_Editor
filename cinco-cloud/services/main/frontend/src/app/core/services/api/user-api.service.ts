import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { UserRegisterInput } from '../../models/forms/user-register-input';
import { User } from '../../models/user';
import { UpdateCurrentUserProfileInput } from '../../models/forms/update-current-user-profile-input';
import { UpdateCurrentUserPasswordInput } from '../../models/forms/update-current-user-password-input';
import { fromJsog, fromJsogList } from '../../utils/jsog-utils';
import { Page } from '../../models/page';
import { UpdateCurrentUserProfilePictureInput } from "../../models/forms/update-current-user-profile-picture-input";

@Injectable({
  providedIn: 'root'
})
export class UserApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public register(input: UserRegisterInput): Observable<User> {
    const options: any = { ...this.defaultHttpOptions, ...{ responseType: 'text' } };
    return this.http.post(`${this.apiUrl}/register`, input, options).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public activate(userId: String, activationToken: String): Observable<User> {
    return this.http.put(`${this.apiUrl}/users/${userId}/rpc/activate`, { activationToken: activationToken }, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public deactivate(userId: number): Observable<User> {
    return this.http.put(`${this.apiUrl}/users/${userId}/rpc/deactivate`, {}, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public create(input: UserRegisterInput): Observable<User> {
    return this.http.post(`${this.apiUrl}/users`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public getCurrent(): Observable<User> {
    return this.http.get(`${this.apiUrl}/users/current`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public getAll(page = 0, size = 25, role: string|null = null): Observable<Page<User>> {
    const params: any = { page, size };
    if (role) params.role = role;
    const options = {
      ...this.defaultHttpOptions,
      params
    };
    return this.http.get(`${this.apiUrl}/users`, options).pipe(
      map((body: any) => this.transformPage(body))
    );
  }

  public search(page = 0, size = 25, search: string|null = null): Observable<Page<User>> {
    const params: any = { page, size };
    if (search) params.search = search;
    const options = {
      ...this.defaultHttpOptions,
      params
    };
    return this.http.get(`${this.apiUrl}/users`, options).pipe(
      map((body: any) => this.transformPage(body))
    );
  }

  public delete(user: User): Observable<User> {
    return this.http.delete(`${this.apiUrl}/users/${user.id}`, this.defaultHttpOptions).pipe(
      map(body => user)
    );
  }

  public addAdminRole(user: User): Observable<User> {
    return this.http.put(`${this.apiUrl}/users/${user.id}/roles`, { admin: true }, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public removeAdminRole(user: User): Observable<User> {
    return this.http.put(`${this.apiUrl}/users/${user.id}/roles`, { admin: false }, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public updateProfile(user: User, input: UpdateCurrentUserProfileInput): Observable<User> {
    return this.http.put(`${this.apiUrl}/users/${user.id}`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public updateProfilePicture(user: User, input: UpdateCurrentUserProfilePictureInput): Observable<User> {
    return this.http.put(`${this.apiUrl}/users/${user.id}/picture`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public updatePassword(user: User, input: UpdateCurrentUserPasswordInput): Observable<User> {
    return this.http.put(`${this.apiUrl}/users/${user.id}/password`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformSingle(body: any): User {
    return fromJsog(body, User);
  }

  private transformList(body: any[]): User[] {
    return fromJsogList(body, User);
  }

  private transformPage(body: any): Page<User> {
    return Page.fromObject(body, this.transformList(body.items));
  }
}
