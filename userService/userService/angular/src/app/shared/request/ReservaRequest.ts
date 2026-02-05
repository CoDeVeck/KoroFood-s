export interface ReservaRequest {
    idUsuario: number;
    idMesa: number;
    fechaHora: string;
    idEvento: number; // null si es reserva normal
    observaciones: string;
}