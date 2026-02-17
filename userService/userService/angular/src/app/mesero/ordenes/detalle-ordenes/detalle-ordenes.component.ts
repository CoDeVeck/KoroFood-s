import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { DetallePedidoServiceService } from '../../../cliente/service/detalle-pedido-service.service';
import { WebsocketPedidosService } from '../../../cliente/pedido/websocket-pedidos.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../auth/service/auth.service';
import { DetallePedidoUsuarioResponse } from '../../../shared/response/detallePedidoUsuarioResponse';
import { Subscription } from 'rxjs';
import { DetallePedidoResponse } from '../../../shared/response/detallePedidoResponse';
import { PedidoWebSocketMessage } from '../../../cliente/pedido/detalle-pedido/detalle-pedido.component';
import iziToast from 'izitoast';
import { AlertIziToast } from '../../../util/iziToastAlert.service';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../../util/alert.service';

@Component({
  selector: 'app-detalle-ordenes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detalle-ordenes.component.html',
  styleUrl: './detalle-ordenes.component.css',
})
export class DetalleOrdenesComponent implements OnInit, OnDestroy {
  private detallePedidoService = inject(DetallePedidoServiceService);
  private wsPedidoService = inject(WebsocketPedidosService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);

  idPedido: number | null = null;
  token: string | null = null;
  idUsuario: number | null = null;

  detalles: DetallePedidoResponse[] = [];
  cliente: DetallePedidoUsuarioResponse | null = null;

  // Estados
  loading: boolean = false;
  wsConnected: boolean = false;
  error: string | null = null;

  private subscriptions: Subscription = new Subscription();

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.idPedido = +params['id'];

      if (!this.idPedido || isNaN(this.idPedido)) {
        console.error('ID de pedido inválido');
        this.router.navigate(['/mesero/pedido']);
        return;
      }
      this.verificarSesion();
    });
  }
  verificarSesion(): void {
    this.token = this.authService.getToken();

    if (!this.token) {
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: `/mesero/pedido/${this.idPedido}` },
      });
      return;
    }

    this.authService.getUsuario().subscribe({
      next: (response) => {
        this.idUsuario = response.idUsuario;
        this.initializeWebSocket(this.idUsuario!, this.token!);
      },
      error: (error) => {
        console.error('Error al obtener usuario:', error);
        this.router.navigate(['/auth/login'], {
          queryParams: { returnUrl: `/mesero/pedido/${this.idPedido}` },
        });
      },
    });
  }

  private initializeWebSocket(userId: number, token: string): void {
    console.log('🔌 Conectando WebSocket Pedidos para mesero:', userId);
    this.wsPedidoService.connect(userId, token);

    this.subscriptions.add(
      this.wsPedidoService.isConnected().subscribe((connected) => {
        this.wsConnected = connected;
        if (connected && this.idPedido) {
          this.wsPedidoService.subscribeToPedidoMesero(this.idPedido);
          this.cargarDetallesPedido();
          this.cargarCliente();
        }
      }),
    );
    this.subscriptions.add(
      this.wsPedidoService.onPedidoMesero().subscribe((message) => {
        if (message) {
          this.actualizarVistaMesero(message);
        }
      }),
    );
    this.subscriptions.add(
      this.wsPedidoService.onPedidoError().subscribe((error) => {
        if (error) {
          console.error('Error:', error);
          this.error = error.mensaje || 'Error desconocido';
          setTimeout(() => (this.error = null), 5000);
        }
      }),
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.wsPedidoService.unsubscribeFromPedido();
  }
  cargarDetallesPedido(): void {
    if (!this.idPedido) {
      console.error('ERROR NO SE OBTUVO EL ID: ', this.idPedido);
      return;
    }

    this.loading = true;
    this.detallePedidoService.obtenerDetallePorPedido(this.idPedido).subscribe({
      next: (data) => {
        if (data.valor) {
          this.detalles = data.data;
          console.log(
            'Se obuvo la lista: ' + this.detalles.length + this.detalles,
          );
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar detalles:', error);
        this.error = 'Error al cargar los detalles del pedido';
        this.loading = false;
      },
    });
  }
  cargarCliente(): void {
    if (!this.idPedido) return;

    this.detallePedidoService.obtenerCliente(this.idPedido).subscribe({
      next: (response) => {
        if (response.valor) {
          this.cliente = response.data;
          console.log('Cliente cargado:', this.cliente);
        }
      },
      error: (err) => {
        console.error('Error al cargar cliente:', err);
        this.error = 'Error al cargar información del cliente';
      },
    });
  }

  obtenerDetallePorId(idDetalle: number): DetallePedidoResponse | undefined {
    return this.detalles.find((dt) => dt.idDetalle === idDetalle);
  }

  entregarPlato(idDetalle: number): void {
    if (!this.idPedido) return;

    if (!this.wsConnected) {
      this.error = 'WebSocket no conectado. Intenta recargar la página.';
      setTimeout(() => (this.error = null), 3000);
      return;
    }

    var dtObtenido = this.obtenerDetallePorId(idDetalle);

    if (dtObtenido) {
      AlertService.confirm(
        `cancelar este pedido ${dtObtenido.nombre.split(' ')[0]}`,
      ).then((result: any) => {
        if (result.isConfirmed || result === true) {
          this.wsPedidoService.entregarPlato(this.idPedido!, idDetalle);
          AlertIziToast.success(
            'Exito!',
            `Entregaste la orden ${dtObtenido?.nombre.split(' ')[0]}`,
          );
        } else {
          AlertIziToast.info(
            'Info!',
            `no entregaste la orden ${dtObtenido?.nombre.split(' ')[0]}`,
          );
        }
      });
    }
  }

  private actualizarVistaMesero(message: PedidoWebSocketMessage): void {
    if (message.detalles) {
      this.detalles = message.detalles;
    }

    if (message.infoCliente) {
      this.cliente = message.infoCliente;
    }
  }

  volver(): void {
    this.router.navigate(['/mesero/ordenes']);
  }

  procederAlPago(): void {
    console.log('Proceder al pago del pedido:', this.idPedido);
    AlertIziToast.info('Proximamente', 'Funcionalidad de pago en desarrollo');
  }

  getEstadoClase(estado: string): string {
    switch (estado) {
      case 'PED':
        return 'estado-pedido';
      case 'ENT':
        return 'estado-entregado';
      case 'CAN':
        return 'estado-cancelado';
      default:
        return '';
    }
  }

  getEstadoTexto(estado: string): string {
    switch (estado) {
      case 'PED':
        return 'Pedido';
      case 'ENT':
        return 'Entregado';
      case 'CAN':
        return 'Cancelado';
      default:
        return estado;
    }
  }

  getEstadoIcono(estado: string): string {
    const iconos: { [key: string]: string } = {
      PED: 'fas fa-clock',
      ENT: 'fas fa-check-circle',
      CAN: 'fas fa-times-circle',
    };
    return iconos[estado] || 'fas fa-question-circle';
  }

  // Obtener platos por estado
  get platosPendientes(): DetallePedidoResponse[] {
    return this.detalles.filter((d) => d.estado === 'PED');
  }

  get platosEntregados(): DetallePedidoResponse[] {
    return this.detalles.filter((d) => d.estado === 'ENT');
  }

  get platosCancelados(): DetallePedidoResponse[] {
    return this.detalles.filter((d) => d.estado === 'CAN');
  }

  calcularTotal(): number {
    return this.detalles
      .filter((detalle) => detalle.estado === 'ENT')
      .reduce((sum, detalle) => sum + detalle.subTotal, 0);
  }
}
