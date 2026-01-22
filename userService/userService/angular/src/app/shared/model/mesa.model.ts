import { EstadoMesa } from "../enums/estadoMesa.enum";
import { TipoMesa } from "../enums/tipoMesa.enum";

export interface Mesa{
    idMesa: number;
    numeroMesa: number;
    capacidad: number;
    tipo: TipoMesa;
    estado: EstadoMesa; 
}