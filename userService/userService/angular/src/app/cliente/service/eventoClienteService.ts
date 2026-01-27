import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { environment } from '../../../enviroments/enviroment';
import { EventoDto } from '../../shared/dto/EventoDto';

@Injectable({
  providedIn: 'root',
})
export class EventoClienteService {
  private baseUrl = `${environment.apiUrls.eventos}/evento`;

  constructor(private http: HttpClient) {}
  listarEventos(): Observable<ResultadoResponse<EventoDto[]>> {
    return this.http.get<ResultadoResponse<EventoDto[]>>(`${this.baseUrl}`);
  }
}
