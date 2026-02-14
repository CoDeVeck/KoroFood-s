export interface ReservaRequest {
   idUsuario: number;
  idMesa: number;
  idEvento?: number | null;
  fechaHora: string; // ISO format: "2026-02-09T12:30:00"
  cantidadPersonas: number;
  observaciones?: string;
}