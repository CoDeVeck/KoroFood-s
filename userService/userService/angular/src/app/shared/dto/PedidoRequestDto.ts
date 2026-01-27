import { DetallePedidoRequestDTO } from "./DetallePedidoRequestDTO";

export interface PedidoRequestoDto{
    idMesa:number;
    idUsuario:number;
    detalles: DetallePedidoRequestDTO[]
}