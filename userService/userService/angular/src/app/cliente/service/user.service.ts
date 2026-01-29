import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private user: any = null;

  constructor() {
    const storedUser = sessionStorage.getItem('user');
    if (storedUser) {
      this.user = JSON.parse(storedUser);
    }
  }
  setUser(usuario: any): void {
    this.user = usuario;
    sessionStorage.setItem('usuario', JSON.stringify(usuario));
  }

  getUser(): any {
    return this.user;
  }

  clearUser(): void {
    this.user = null;
    sessionStorage.removeItem('usuario');
  }

  isLoggedIn(): boolean {
    return this.user !== null;
  }

  getRol(): string {
    return this.user?.rol || '';
  }
}
