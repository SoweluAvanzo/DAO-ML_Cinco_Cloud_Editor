import { TestBed } from '@angular/core/testing';

import { UserIsLoggedInGuard } from './user-is-logged-in.guard';

describe('UserIsLoggedInGuard', () => {
  let guard: UserIsLoggedInGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(UserIsLoggedInGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
