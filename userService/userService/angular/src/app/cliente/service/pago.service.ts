// services/pago.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { enviroment } from '../../../enviroments/enviroment';
import { 
  CrearPagoRequest, 
  SubirCapturaRequest, 
  PagoResponse 
} from '../../cliente/pago/pagoDto';

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private apiUrl = `${enviroment.apiUrls.pago}/pago`; // Ajusta según tu puerto

  constructor(private http: HttpClient) {}

  // Crear pago y obtener datos para QR
  crearPago(request: CrearPagoRequest): Observable<PagoResponse> {
    return this.http.post<PagoResponse>(this.apiUrl, request)
      .pipe(catchError(this.handleError));
  }


  subirCaptura(request: SubirCapturaRequest): Observable<PagoResponse> {
    return this.http.post<PagoResponse>(`${this.apiUrl}/subir-captura`, request)
      .pipe(catchError(this.handleError));
  }

  // Buscar pago por ID
  buscarPorId(id: number): Observable<PagoResponse> {
    return this.http.get<PagoResponse>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // Buscar pago por referencia
  buscarPorReferencia(referencia: string): Observable<PagoResponse> {
    return this.http.get<PagoResponse>(`${this.apiUrl}/referencia/${referencia}`)
      .pipe(catchError(this.handleError));
  }

  // Manejo de errores
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Ocurrió un error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Error del lado del servidor
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else if (error.status === 0) {
        errorMessage = 'No se pudo conectar con el servidor';
      } else if (error.status === 404) {
        errorMessage = 'Recurso no encontrado';
      } else if (error.status === 400) {
        errorMessage = error.error?.message || 'Datos inválidos';
      } else if (error.status === 500) {
        errorMessage = 'Error interno del servidor';
      } else {
        errorMessage = `Error ${error.status}: ${error.message}`;
      }
    }
    
    console.error('Error en PagoService:', errorMessage, error);
    return throwError(() => new Error(errorMessage));
  }
}