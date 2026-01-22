import { EstadoPedido } from "../enums/estadoPedido.enum";
import { Mesa } from "./mesa.model";
import { Usuario } from "./usuario.model";

export interface Pedido{
    idPedido: number;
    idMesa: Mesa;
    idUsuario: Usuario; // Cliente que realiza el pedido
    fechaHora: string;
    subtotal: number;
    total: number;
    estadoPedido: EstadoPedido;
}