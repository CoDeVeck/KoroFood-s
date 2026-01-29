

export interface Tematica {
  idTematica: number;
  nombre: string;
  activo: boolean;
}

export interface Evento {
  idEvento: number;
  nombre: string;
  descripcion: string;
  tematica: Tematica | null;
  fecha: string; // ISO string
  costo: number;
  imagen: string | null;
  activo: boolean;
}

export interface EventoRequest {
  nombre: string;
  descripcion: string;
  idTematica: number | null;
  fecha: string; // ISO string
  costo: number;
  imagen: string | null;
}

export interface TematicaResponse {
  idTematica: number;
  nombre: string;
  activo: boolean;
}

export interface EventoResponse {
  idEvento: number;
  nombre: string;
  descripcion: string;
  tematica: TematicaResponse | null;
  fecha: string;
  costo: number;
  imagen: string | null;
  activo: boolean;
}