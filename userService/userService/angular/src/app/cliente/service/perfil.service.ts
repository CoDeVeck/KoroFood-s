import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { enviroment } from '@envs/enviroment';
import { PerfilClienteResponse } from '../../shared/response/perfilCllienteResponse.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PerfilService {
  private baseUrl = `${enviroment.apiUrls.usuarios}/cliente`;
  constructor(private http: HttpClient) {}

  getPerfilCliente(idUsuario: number): Observable<PerfilClienteResponse[]> {
    return this.http.get<PerfilClienteResponse[]>(
      `${this.baseUrl}/perfil?idUsuario=${idUsuario}`,
    );
  }
}
