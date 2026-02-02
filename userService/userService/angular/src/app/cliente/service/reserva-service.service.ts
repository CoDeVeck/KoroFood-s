import { Injectable } from '@angular/core';
import { enviroment } from '../../../enviroments/enviroment';
import { HttpClient } from '@angular/common/http';
import { ReservaRequest } from '../../shared/request/ReservaRequest';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { Reserva } from '../../shared/model/reserva.model';

@Injectable({
  providedIn: 'root',
})
export class ReservaServiceService {
  private baseUrl = `${enviroment.apiUrls.resenas}/reserva`;

  constructor(private http: HttpClient) {}

  crearReserva(reserva: ReservaRequest): Observable<ResultadoResponse<number>> {
    return this.http.post<ResultadoResponse<number>>(
      `${this.baseUrl}/registro`,
      reserva,
    );
  }

  validarReserva(
    mesaId: number,
    fechaHora: string, // ISO: '2026-02-01T18:30:00'
    esEvento: boolean = false,
  ): Observable<ResultadoResponse<boolean>> {
    const params = {
      mesaId,
      fechaHora,
      esEvento,
    };

    return this.http.get<ResultadoResponse<boolean>>(
      `${this.baseUrl}/ocupada`,
      { params },
    );
  }

  obtenerSlotsDisponibles(
    mesaId: number,
    desde: string, // ISO
    hasta: string, // ISO
    eventoId?: number,
  ): Observable<ResultadoResponse<string[]>> {
    let params: any = {
      mesaId,
      desde,
      hasta,
    };

    if (eventoId) {
      params.eventoId = eventoId;
    }

    return this.http.get<ResultadoResponse<string[]>>(
      `${this.baseUrl}/slots-disponibles`,
      { params },
    );
  }
}
