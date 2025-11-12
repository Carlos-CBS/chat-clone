import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface User {
  authenticated?: boolean;
  name?: string;
  email?: string;
  sub?: string;
  picture?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';

  // BehaviorSubject: Stores and emits the current user

  private userSubject = new BehaviorSubject<User | null>(null); //! only the service can call .next() to modify the current user state
  public user$ = this.userSubject.asObservable(); // read-only Observable that components can subscribe to.

  constructor(private http: HttpClient) {
    this.checkAuthStatus();
  }
   

  checkAuthStatus(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/user`)
    .pipe(
      tap(user => this.userSubject.next(user))
    );
  }

  login(): void {
    window.location.href = `${this.apiUrl}/auth/login`;
  }

  logout(): Observable<{message: string}> {
    return this.http.post<{message: string}>(`${this.apiUrl}/auth/logout`, {}).pipe(
      tap(() => {
        this.userSubject.next(null);
      })
    );
  }

  getCurrentUser(): User | null {
    return this.userSubject.value;
  }

  isAuthenticated(): boolean {
    const user = this.userSubject.value;
    return user !== null && user.authenticated === true;
  }
}