import { Injectable, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { catchError, Observable, tap } from "rxjs";
import { environment } from "environments/environment";
import { User } from "./user.model";

@Injectable({
  providedIn: "root"
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/api/auth`;

  // ✅ Signal global pour suivre le user connecté en temps réel
  public user = signal<User | null>(this.loadUser());

  private loadUser(): User | null {
    const stored = localStorage.getItem("user");
    return stored ? JSON.parse(stored) : null;
  }

  public login(email: string, password: string): Observable<{ token: string; user: User }> {
    return this.http.post<{ token: string; user: User }>(`${this.path}/token`, { email, password }).pipe(
      tap((res) => {
        localStorage.setItem("token", res.token);
        localStorage.setItem("user", JSON.stringify(res.user));
        this.user.set(res.user); // ✅ met à jour le signal
      }),
      catchError((error) => { throw error; })
    );
  }

  public register(user: { username: string; firstname: string; email: string; password: string }): Observable<User> {
    return this.http.post<User>(`${this.path}/account`, user).pipe(
      catchError((error) => { throw error; })
    );
  }

  public logout(): void {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    this.user.set(null); // ✅ met à jour le signal pour que tout le front réagisse
  }

  public getToken(): string | null {
    return localStorage.getItem("token");
  }

  public isLoggedIn(): boolean {
    return !!this.user();
  }

  public getUserEmail(): string | null {
    const user = this.user();
    return user ? user.email : null;
  }

  public isAdmin(): boolean {
    const user = this.user();
    return user?.email === "admin@admin.com";
  }
}
