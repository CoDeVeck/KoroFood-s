import { Pedido } from "./pedido.model";
import { Plato } from "./plato.model";

export interface DetallePedido{
    idDetalle: number;
    idPedido: Pedido;
    idPlato: Plato;
    cantidad: number;
    precioUnitario: number;
    subTotal: number;
    // estado pedido falta
}