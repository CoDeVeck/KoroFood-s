import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { enviroment } from '../../../enviroments/enviroment';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { PedidoResumenDto } from '../../shared/dto/PedidoResumenDto';
import { PedidoRequestoDto } from '../../shared/dto/PedidoRequestDto';
import { Pedido } from '../../shared/model/pedido.model';
import { DetallePedidoPagar } from '../../shared/response/detallePedidoPagar.model';

@Injectable({
  providedIn: 'root',
})
export class PedidoMeseroService {
  private baseUrl = `${enviroment.apigateway}/pedido`;

  constructor(private http: HttpClient) {}

  listarPedidos(
    estado?: string,
  ): Observable<ResultadoResponse<PedidoResumenDto[]>> {
    let url = `${this.baseUrl}`;
    if (estado) {
      url += `?estado=${estado}`;
    }
    return this.http.get<ResultadoResponse<PedidoResumenDto[]>>(url);
  }

  crearPedido(dto: PedidoRequestoDto): Observable<ResultadoResponse<Pedido>> {
    return this.http.post<ResultadoResponse<Pedido>>(`${this.baseUrl}`, dto);
  }

  pagarResponse(
    idPedido: number,
  ): Observable<ResultadoResponse<DetallePedidoPagar>> {
    return this.http.get<ResultadoResponse<DetallePedidoPagar>>(
      `${this.baseUrl}/procederPago?idPedido=${idPedido}`,
    );
  }
}
