import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReservaDto } from '../../shared/dto/ReservaDto';
import { PlatoDto } from '../../shared/dto/PlatoDto';
import { ReservaMeseroService } from '../service/reservaMeseroService';
import { MenuMeseroService } from '../service/menuMeseroService';
import { PedidoMeseroService } from '../service/pedidoMeseroService';
import { Router } from '@angular/router';
import { DetallePedidoRequestDTO } from '../../shared/dto/DetallePedidoRequestDTO';
import { PedidoRequestoDto } from '../../shared/dto/PedidoRequestDto';
import { trigger, transition, style, animate } from '@angular/animations';
import { AlertService } from '../../util/alert.service';
interface PlatoSeleccionado {
  plato: PlatoDto;
  cantidad: number;
}
@Component({
  selector: 'app-form-orden',
  imports: [CommonModule, FormsModule],
  templateUrl: './form-orden.component.html',
  styleUrl: './form-orden.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate(
          '300ms ease-out',
          style({ opacity: 1, transform: 'translateY(0)' }),
        ),
      ]),
    ]),
    trigger('slideIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.9)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'scale(1)' })),
      ]),
    ]),
  ],
})
export class FormOrdenComponent implements OnInit {
  currentStep = 1;

  reservaSearchId: string | null = null;
  reservaEncontrada: ReservaDto | null = null;
  searching = false;
  searchError: string | null = null;

  platos: PlatoDto[] = [];
  platosFiltrados: PlatoDto[] = [];
  platosSeleccionados: PlatoSeleccionado[] = [];
  loadingPlatos = false;
  errorPlatos: string | null = null;
  filtroPlato = '';
  filtroTipo = '';
  tiposPlato: string[] = [];

  creandoOrden = false;
  ordenCreada: any = null;

  constructor(
    private reservaService: ReservaMeseroService,
    private menuService: MenuMeseroService,
    private pedidoService: PedidoMeseroService,
    private router: Router,
  ) {}

  ngOnInit(): void {}

  buscarReserva(): void {
    if (!this.reservaSearchId) {
      this.searchError = 'Por favor ingrese un ID de reserva';
      return;
    }

    this.searching = true;
    this.searchError = null;
    this.reservaEncontrada = null;

    this.reservaService.getReservationById(this.reservaSearchId).subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.reservaEncontrada = response.data;
        } else {
          this.searchError = response.mensaje || 'No se encontró la reserva';
        }
        this.searching = false;
      },
      error: (err) => {
        this.searching = false;
        const mensajeError =
          err?.error?.mensaje || err?.message || 'Error al crear la orden';
        this.searchError = mensajeError;
      },
    });
  }

  continuarAPlatos(): void {
    this.currentStep = 2;
    this.cargarPlatos();
  }

  cargarPlatos(): void {
    this.loadingPlatos = true;
    this.errorPlatos = null;

    this.menuService.listarPlatos().subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.platos = response.data;
          this.platosFiltrados = [...this.platos];
          this.extraerTiposPlato();
        } else {
          this.errorPlatos = response.mensaje || 'Error al cargar platos';
        }
        this.loadingPlatos = false;
      },
      error: (err) => {
        this.errorPlatos = 'Error al cargar el menú';
        this.loadingPlatos = false;
        console.error('Error:', err);
      },
    });
  }

  extraerTiposPlato(): void {
    const tipos = new Set(this.platos.map((p) => p.tipoPlato));
    this.tiposPlato = Array.from(tipos);
  }

  platosFiltradosMetodo(): PlatoDto[] {
    let filtrados = [...this.platos];

    if (this.filtroPlato) {
      const busqueda = this.filtroPlato.toLowerCase();
      filtrados = filtrados.filter((p) =>
        p.nombre.toLowerCase().includes(busqueda),
      );
    }

    if (this.filtroTipo) {
      filtrados = filtrados.filter((p) => p.tipoPlato === this.filtroTipo);
    }

    return filtrados;
  }

  agregarPlato(plato: PlatoDto): void {
    this.platosSeleccionados.push({
      plato: plato,
      cantidad: 1,
    });
  }

  removerPlato(idPlato: number): void {
    this.platosSeleccionados = this.platosSeleccionados.filter(
      (item) => item.plato.idPlato !== idPlato,
    );
  }

  isPlatoSeleccionado(idPlato: number): boolean {
    return this.platosSeleccionados.some(
      (item) => item.plato.idPlato === idPlato,
    );
  }

  getCantidad(idPlato: number): number {
    const item = this.platosSeleccionados.find(
      (item) => item.plato.idPlato === idPlato,
    );
    return item ? item.cantidad : 0;
  }

  incrementarCantidad(idPlato: number): void {
    const item = this.platosSeleccionados.find(
      (item) => item.plato.idPlato === idPlato,
    );
    if (item) {
      item.cantidad++;
    }
  }

  decrementarCantidad(idPlato: number): void {
    const item = this.platosSeleccionados.find(
      (item) => item.plato.idPlato === idPlato,
    );
    if (item) {
      if (item.cantidad > 1) {
        item.cantidad--;
      } else {
        this.removerPlato(idPlato);
      }
    }
  }

  actualizarCantidad(idPlato: number, event: any): void {
    const cantidad = parseInt(event.target.value);
    if (cantidad > 0) {
      const item = this.platosSeleccionados.find(
        (item) => item.plato.idPlato === idPlato,
      );
      if (item) {
        item.cantidad = cantidad;
      }
    }
  }

  calcularSubtotal(): number {
    return this.platosSeleccionados.reduce(
      (sum, item) => sum + item.plato.precio * item.cantidad,
      0,
    );
  }

  calcularTotal(): number {
    // Por ahora es igual al subtotal, pero puedes agregar impuestos, descuentos, etc.
    return this.calcularSubtotal();
  }

  volverABusqueda(): void {
    this.currentStep = 1;
    this.platosSeleccionados = [];
  }

  // ============ CREAR ORDEN ============

  crearOrden(): void {
    if (!this.reservaEncontrada || this.platosSeleccionados.length === 0) {
      return;
    }

    this.creandoOrden = true;

    const detalles: DetallePedidoRequestDTO[] = this.platosSeleccionados.map(
      (item) => ({
        idPlato: item.plato.idPlato,
        cantidad: item.cantidad,
      }),
    );

    const pedidoRequest: PedidoRequestoDto = {
      idMesa: this.reservaEncontrada.mesa,
      idUsuario: this.reservaEncontrada.idUsuario, // luego se va cambiar por el mesero
      idReserva: this.reservaEncontrada.idReserva,
      detalles: detalles,
    };

    this.pedidoService.crearPedido(pedidoRequest).subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.ordenCreada = response.data;
          AlertService.success(
            response.mensaje || 'Pedido registrado correctamente',
          );
          this.irAOrdenes();
        } else {
          AlertService.error(response.mensaje || 'Error al crear la orden.');
        }
        this.creandoOrden = false;
      },
      error: (err) => {
        this.creandoOrden = false;
        const mensajeError =
          err?.error?.mensaje || err?.message || 'Error al crear la orden';
        AlertService.error(mensajeError);
      },
    });
  }

  irAOrdenes(): void {
    this.router.navigate(['/mesero/ordenes']);
  }

  obtenerNombreTipo(tipo: string): string {
    const tipos: { [key: string]: string } = {
      E: 'Entrada',
      S: 'Segundo',
      P: 'Postre',
      B: 'Bebida',
    };
    return tipos[tipo] || tipo;
  }

  onImgError(event: any) {
    event.target.src = '/img/no-imagen.jpg';
  }
}
