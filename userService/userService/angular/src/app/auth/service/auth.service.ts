import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';

// Entornos para el despliegue de docker
import { environment } from '@envs/enviroment';
import { ResultadoResponse } from '../../shared/response/resultadoResponse.models';
import { Usuario } from '../../shared/model/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.apiUrls.usuarios}/auth/login`;
  private registerUrl = `${environment.apiUrls.usuarios}/auth/register`;
  private userUrl = `${environment.apiUrls.usuarios}/auth/me`;
  constructor(private http: HttpClient) {}

  login(credentials: { correo: string; clave: string }): Observable<any> {
    console.log('Login payload:', credentials);

    return this.http.post<{ token: string }>(this.apiUrl, credentials, {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  register(usuario: Usuario): Observable<ResultadoResponse> {
    return this.http.post<ResultadoResponse>(this.registerUrl, usuario, {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  getUsuario(): Observable<any> {
    const token = this.getToken();
    if (!token) throw new Error('No token found');

    return this.http.get(this.userUrl, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  saveToken(token: string): void {
    localStorage.setItem('auth_token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  getRolesFromToken(): string[] {
    const token = this.getToken();
    if (!token) {
      return [];
    }
    const decoded: any = jwtDecode(token);
    return decoded.roles || [];
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  logout(): void {
    localStorage.removeItem('auth_token');
  }
}
