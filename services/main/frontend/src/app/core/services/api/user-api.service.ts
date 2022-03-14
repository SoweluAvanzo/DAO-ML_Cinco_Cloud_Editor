import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { UserRegisterInput } from '../../models/forms/user-register-input';
import { User } from '../../models/user';
import { UpdateCurrentUserProfileInput } from '../../models/forms/update-current-user-profile-input';
import { UpdateCurrentUserPasswordInput } from '../../models/forms/update-current-user-password-input';
import { fromJsog, fromJsogList } from '../../utils/jsog-utils';

@Injectable({
  providedIn: 'root'
})
export class UserApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public register(input: UserRegisterInput): Observable<string> {
    const options: any = { ...this.defaultHttpOptions, ...{ responseType: 'text' } };
    return this.http.post(`${this.apiUrl}/register/new/public`, input, options).pipe(
      map(body => body.toString())
    );
  }

  public create(input: UserRegisterInput): Observable<User> {
    return this.http.post(`${this.apiUrl}/users/private`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public getCurrent(): Observable<User> {
    return this.http.get(`${this.apiUrl}/user/current/private`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public getAll(): Observable<User[]> {
    return this.http.get(`${this.apiUrl}/users`, this.defaultHttpOptions).pipe(
      map((body: any) => this.transformList(body))
    );
  }

  public search(usernameOrEmail: string): Observable<User> {
    const input = { usernameOrEmail };
    return this.http.post(`${this.apiUrl}/users/search`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public delete(user: User): Observable<User> {
    return this.http.delete(`${this.apiUrl}/users/${user.id}`, this.defaultHttpOptions).pipe(
      map(body => user)
    );
  }

  public addAdminRole(user: User): Observable<User> {
    return this.http.post(`${this.apiUrl}/users/${user.id}/roles/addAdmin`, {}, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public removeAdminRole(user: User): Observable<User> {
    return this.http.post(`${this.apiUrl}/users/${user.id}/roles/removeAdmin`, {}, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public updateProfile(input: UpdateCurrentUserProfileInput): Observable<User> {
    return this.http.put(`${this.apiUrl}/user/current/private`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public updatePassword(input: UpdateCurrentUserPasswordInput): Observable<User> {
    return this.http.put(`${this.apiUrl}/user/current/password/private`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformSingle(body: any): User {
    return fromJsog(body, User);
  }

  private transformList(body: any[]): User[] {
    return fromJsogList(body, User);
  }
}
