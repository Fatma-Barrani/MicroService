import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private gatewayBase = 'http://localhost:8956';
  private apiUrl = this.gatewayBase + '/api/auth';

  constructor(private http: HttpClient) {}

  register(data: any): Observable<any> {
    return this.http.post(`${this.gatewayBase}/api/inscriptions/register`, data);
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, data).pipe(
      tap((response: any) => {
        if (response?.access_token) {
          localStorage.setItem('token', response.access_token);
        }
        if (response?.user) {
          localStorage.setItem('user', JSON.stringify(response.user));
        }
      })
    );
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  getUser(): any {
    const raw = localStorage.getItem('user');
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }

  hasRole(role: string): boolean {
    const user = this.getUser();
    return !!user && user.role === role;
  }

  hasAnyRole(roles: string[]): boolean {
    const user = this.getUser();
    return !!user && roles.some((role) => role === user.role);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
}
