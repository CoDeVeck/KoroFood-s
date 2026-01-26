import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { environment } from '../../../enviroments/enviroment';
import { PlatoDto } from '../../shared/dto/PlatoDto';

@Injectable({
  providedIn: 'root',
})
export class MenuService {
  private baseUrl = `${environment.apiUrls.menu}/menu`;

  constructor(private http: HttpClient) {}
  listarPlatos(): Observable<ResultadoResponse<PlatoDto[]>> {
    return this.http.get<ResultadoResponse<PlatoDto[]>>(`${this.baseUrl}`);
  }

  descargarMenuPdf(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/pdf`, {
      responseType: 'blob',
    });
  }
}
