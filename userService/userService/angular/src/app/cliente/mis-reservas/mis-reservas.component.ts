import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReservaServiceService } from '../service/reserva-service.service';
import { CodigoVerificacionService } from '../service/codigo-verificacion.service';
import { AuthService } from '../../auth/service/auth.service';
import { ReservaResponseDTO } from '../../shared/dto/ReservaResponseDTO';
import { EstadoReserva } from '../../shared/enums/estadoReserva.enum';
import { TipoReserva } from '../../shared/enums/tipoReserva.enum';
import { UserService } from '../service/user.service';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { EnviarCodigoRequest } from '../../shared/dto/EnviarCodigoRequest';



@Component({
  selector: 'app-mis-reservas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mis-reservas.component.html',
  styleUrls: ['./mis-reservas.component.css'],
})

export class MisReservasComponent implements OnInit {
  reservas: ReservaResponseDTO[] = [];
  reservasFiltradas: ReservaResponseDTO[] = [];
  reservaSeleccionada: ReservaResponseDTO | null = null;
  loading = false;
  error: string | null = null;

  // Filtros
  filtroFecha: string = '';
  filtroEstado: string = '';
  estadosDisponibles = Object.values(EstadoReserva);

  // Modales
  mostrarModalDetalle = false;
  mostrarModalEnviarCodigo = false;
  mostrarModalCancelar = false;
  mostrarMensaje = false;
  mensaje: string = '';
  tipoMensaje: 'success' | 'error' | 'info' = 'info';

  // Usuario
  idUsuario: number | null = null;

  // Enums para el template
  EstadoReserva = EstadoReserva;
  TipoReserva = TipoReserva;

  constructor(
    private reservaService: ReservaServiceService,
    private codigoService: CodigoVerificacionService,
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.verificarSesion();
  }

  verificarSesion(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.userService.currentUser$.subscribe({
      next: (user) => {
        if (user && user.idUsuario) {
          this.idUsuario = user.idUsuario;
          this.cargarReservas();
        } else {
          // Si no hay usuario en el servicio, obtenerlo
          this.authService.getUsuario().subscribe({
            next: (userData) => {
              this.idUsuario = userData.idUsuario;
              this.userService.setUser(userData);
              this.cargarReservas();
            },
            error: (err) => {
              console.error('Error al obtener usuario:', err);
              this.router.navigate(['/login']);
            },
          });
        }
      },
      error: (err) => {
        console.error('Error en suscripción de usuario:', err);
        this.router.navigate(['/login']);
      },
    });
  }

  cargarReservas(): void {
    if (!this.idUsuario) return;

    this.loading = true;
    this.error = null;

    this.reservaService.listarMisReservas(this.idUsuario).subscribe({
      next: (response: ResultadoResponse<ReservaResponseDTO[]>) => {
        this.reservas = response.data;
        this.aplicarFiltros();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar reservas:', err);
        this.error = 'Error al cargar las reservas';
        this.loading = false;
      },
    });
  }

  aplicarFiltros(): void {
    this.reservasFiltradas = this.reservas.filter((reserva) => {
      let cumpleFecha = true;
      let cumpleEstado = true;

      if (this.filtroFecha) {
        const fechaReserva = new Date(reserva.fechaHora)
          .toISOString()
          .split('T')[0];
        cumpleFecha = fechaReserva === this.filtroFecha;
      }

      if (this.filtroEstado) {
        cumpleEstado = reserva.estado === this.filtroEstado;
      }

      return cumpleFecha && cumpleEstado;
    });
  }

  onFiltroFechaChange(): void {
    this.aplicarFiltros();
  }

  onFiltroEstadoChange(): void {
    this.aplicarFiltros();
  }

  limpiarFiltros(): void {
    this.filtroFecha = '';
    this.filtroEstado = '';
    this.aplicarFiltros();
  }

  verDetalle(reserva: ReservaResponseDTO): void {
    this.reservaSeleccionada = reserva;
    this.mostrarModalDetalle = true;
  }

  cerrarModalDetalle(): void {
    this.mostrarModalDetalle = false;
    this.reservaSeleccionada = null;
  }

  puedeEnviarCodigo(estado: EstadoReserva): boolean {
    return estado === EstadoReserva.PAGADA;
  }

  puedeCancelarReserva(estado: EstadoReserva): boolean {
    return (
      estado === EstadoReserva.PENDIENTE || estado === EstadoReserva.PAGADA
    );
  }

  abrirModalEnviarCodigo(reserva: ReservaResponseDTO): void {
    this.reservaSeleccionada = reserva;
    this.mostrarModalEnviarCodigo = true;
  }

  enviarCodigoVerificacion(tipoEnvio: 'SMS' | 'EMAIL'): void {
    if (!this.reservaSeleccionada) return;

    const request: EnviarCodigoRequest = {
      reservaId: this.reservaSeleccionada.idReserva,
      tipoEnvio: tipoEnvio,
    };

    this.codigoService.enviarCodigo(request).subscribe({
      next: (response) => {
        this.mostrarModalEnviarCodigo = false;
        this.mostrarMensajeExito(
          `Código de verificación enviado por ${tipoEnvio} exitosamente`,
        );
      },
      error: (err) => {
        console.error('Error al enviar código:', err);
        this.mostrarMensajeError('Error al enviar el código de verificación');
      },
    });
  }

  abrirModalCancelar(reserva: ReservaResponseDTO): void {
    this.reservaSeleccionada = reserva;
    this.mostrarModalCancelar = true;
  }

  confirmarCancelacion(): void {
    if (!this.reservaSeleccionada) return;

    this.reservaService
      .cancelarReserva(this.reservaSeleccionada.idReserva)
      .subscribe({
        next: (response) => {
          this.mostrarModalCancelar = false;
          this.mostrarMensajeExito('Reserva cancelada exitosamente');
          this.cargarReservas(); // Recargar la lista
        },
        error: (err) => {
          console.error('Error al cancelar reserva:', err);
          this.mostrarMensajeError('Error al cancelar la reserva');
        },
      });
  }

  cerrarModalEnviarCodigo(): void {
    this.mostrarModalEnviarCodigo = false;
    this.reservaSeleccionada = null;
  }

  cerrarModalCancelar(): void {
    this.mostrarModalCancelar = false;
    this.reservaSeleccionada = null;
  }

  mostrarMensajeExito(mensaje: string): void {
    this.mensaje = mensaje;
    this.tipoMensaje = 'success';
    this.mostrarMensaje = true;
    setTimeout(() => {
      this.mostrarMensaje = false;
    }, 4000);
  }

  mostrarMensajeError(mensaje: string): void {
    this.mensaje = mensaje;
    this.tipoMensaje = 'error';
    this.mostrarMensaje = true;
    setTimeout(() => {
      this.mostrarMensaje = false;
    }, 4000);
  }

  cerrarMensaje(): void {
    this.mostrarMensaje = false;
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    const opciones: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    };
    return date.toLocaleDateString('es-ES', opciones);
  }

  formatearFechaCorta(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  }

  formatearHora(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  getEstadoClass(estado: EstadoReserva): string {
    const clases: { [key in EstadoReserva]: string } = {
      [EstadoReserva.PENDIENTE]: 'estado-pendiente',
      [EstadoReserva.PAGADA]: 'estado-pagada',
      [EstadoReserva.ASISTIDA]: 'estado-asistida',
      [EstadoReserva.CANCELADA]: 'estado-cancelada',
      [EstadoReserva.VENCIDA]: 'estado-vencida',
    };
    return clases[estado];
  }

  getTipoReservaClass(tipo: TipoReserva): string {
    return tipo === TipoReserva.ESPECIAL ? 'tipo-especial' : 'tipo-simple';
  }

  getNombreCompleto(reserva: ReservaResponseDTO): string {
    return `${reserva.nombreCli} ${reserva.apellidoPa} ${reserva.apellidoMa}`;
  }
}
