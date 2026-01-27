import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { environment } from '../../../enviroments/enviroment';
import { ReservaDto } from '../../shared/dto/ReservaDto';

@Injectable({
  providedIn: 'root',
})
export class ReservaMeseroService {
  private baseUrl = `${environment.apiUrls.reserva}/reserva`;

  constructor(private http: HttpClient) {}

  getReservationById(id: number): Observable<ResultadoResponse<ReservaDto>> {
    return this.http.get<ResultadoResponse<ReservaDto>>(
      `${this.baseUrl}/${id}`,
    );
  }
}
