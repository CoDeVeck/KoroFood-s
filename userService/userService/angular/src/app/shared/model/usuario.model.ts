import { Distrito } from "./distrito.model";
import { Rol } from "./rol.model";

export interface Usuario{
    idUsuario: number;
    nombre: string;
    apePaterno: string;
    apeMaterno: string;
    correo: string;
    clave: string;
    tipoDocumento: string; // DNI, Carnet de Extranjeria, Pasaporte, Cartilla Militar FALTA AGREGAR ENUM
    nroDoc: string;
    imagen?: string;
    direccion: string;
    telefono: string;
    distrito: Distrito;
    rol: Rol;
    fechaRegistro: string;
    estado: boolean;

}