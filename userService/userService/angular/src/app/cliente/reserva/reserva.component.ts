import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MesaItem, MesaSelectorComponent } from './mesa-selector/mesa-selector.component';
import { MesaDto } from '../../shared/dto/MesaDto';
import { MesasServiceService } from '../service/mesas-service.service';
import { ReservaServiceService } from '../service/reserva-service.service';
import { Zona } from '../../shared/enums/Zona';
import { FormsModule } from '@angular/forms';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { CrearPagoRequest} from '../../cliente/pago/pagoDto';
import { ReservaRequest } from '../../shared/request/ReservaRequest';
import { Router } from '@angular/router';
import { PagoService } from '../service/pago.service';
import * as QRCode from 'qrcode';

interface CalendarDay {
  day: number;
  enabled: boolean;
  selected: boolean;
  otherMonth: boolean;
  date: Date;
}

interface TimeSlot {
  time: string;
  unavailable?: boolean;
  dateTime?: string; // Guardar el datetime completo del backend
}

@Component({
  selector: 'app-reserva',
  standalone: true,
  imports: [CommonModule, MesaSelectorComponent, FormsModule],
  templateUrl: './reserva.component.html',
  styleUrl: './reserva.component.css',
})
export class ReservaComponent implements OnInit {
  currentStep: number = 1;

  qrImagenUrl: string = '';
  // Paso 1: Personas
  personas: number = 1;
  quickNumbers: number[] = [1, 2, 3, 4];

  // Paso 2: Mesa
  zonas: string[] = ['Z1', 'Z2', 'Z3', 'Z4'];
  zonaSeleccionada: string = 'Z1';
  mesasDisponibles: MesaDto[] = [];
  mesaSeleccionada: MesaDto | null = null;
  cargandoMesas: boolean = false;
  mensajeMesas: string = '';

  // Paso 3: Fecha
  currentMonth: Date = new Date();
  weekDays: string[] = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  calendarDays: CalendarDay[] = [];
  selectedDate: Date | null = null;

  // Paso 4: Hora
  availableTimes: TimeSlot[] = []; // INICIALIZAR VACÍO
  cargandoSlots: boolean = false; // NUEVO
  mensajeSlots: string = ''; // NUEVO

  alternativeTimes: TimeSlot[] = [];
  selectedTime: TimeSlot | null = null;

  

  // Paso 5: Métodos de pago
  metodoPagoSeleccionado: 'TARJETA' | 'YAPE' | 'PLIN' | null = null;

  pagoCreado: any = null;
imagenPreview: string | null = null;
imagenBase64: string | null = null;
procesandoPago: boolean = false;
errorPago: string = '';

  // Tarjeta
  datosTarjeta = {
    numero: '',
    nombre: '',
    fechaExpiracion: '',
    cvv: '',
  };
  erroresTarjeta = {
    numero: '',
    nombre: '',
    fechaExpiracion: '',
    cvv: '',
  };
  tarjetaValida: boolean = false;

  // Yape - QR ficticio
  qrYape: string =
    'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=yape://pago/korofood/deposito/15.00';

   // QR DINÁMICO

  mostrandoQR: boolean = false;
  idReservaCreada: number | null = null; // Guardar ID de reserva creada


  // Plin - Número ficticio
  numeroPlin: string = '986425458';

   // AGREGAR ESTADOS
  loading: boolean = false;
  depositoRequerido: number = 15.00;

  constructor(
    private mesasService: MesasServiceService,
    private reservaService: ReservaServiceService, // INYECTAR
    private pagoService: PagoService, // ✅ INYECTAR
    private router: Router
  ) {}

  ngOnInit(): void {
    this.generateCalendar();
  }

  // Navegación de pasos
  nextStep(): void {
    if (this.currentStep === 1 && this.personas > 0) {
      this.currentStep++;
      this.cargarMesasPorZona();
    } else if (this.currentStep === 2 && this.mesaSeleccionada) {
      this.currentStep++;
    } else if (this.currentStep === 3 && this.selectedDate) {
      this.currentStep++;
      this.cargarSlotsDisponibles(); // CARGAR SLOTS AL AVANZAR AL PASO 4
    } else if (this.currentStep === 4 && this.selectedTime) {
      this.currentStep++;
    }
  }

  previousStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  // Paso 1: Personas
  incrementPersonas(): void {
    if (this.personas < 20) {
      this.personas++;
    }
  }

  decrementPersonas(): void {
    if (this.personas > 1) {
      this.personas--;
    }
  }

  setPersonas(num: number): void {
    this.personas = num;
  }

  // Paso 2: Mesas
  seleccionarZona(zona: string): void {
    this.zonaSeleccionada = zona;
    this.mesaSeleccionada = null;
    this.cargarMesasPorZona();
  }

  cargarMesasPorZona(): void {
    this.cargandoMesas = true;
    console.log(
      '🔍 Cargando mesas para:',
      this.zonaSeleccionada,
      'personas:',
      this.personas,
    );

    this.mesasService
      .obtenerMesasPorZona(this.zonaSeleccionada as Zona, this.personas)
      .subscribe({
        next: (response) => {
          console.log('✅ Response completo:', response);
          console.log('📊 Datos:', response.data);
          console.log('💬 Mensaje:', response.mensaje);

          this.mesasDisponibles = response.data;
          this.mensajeMesas = response.mensaje!;
          this.cargandoMesas = false;
        },
        error: (error) => {
          console.error('❌ Error completo:', error);
          console.error('📍 Status:', error.status);
          console.error('📍 StatusText:', error.statusText);
          console.error('📍 URL:', error.url);
          console.error('📍 Headers:', error.headers);

          this.mesasDisponibles = [];
          this.mensajeMesas = 'Error al cargar las mesas disponibles';
          this.cargandoMesas = false;
        },
      });
  }

  onMesaSeleccionada(mesa: MesaItem): void {
    if ('estado' in mesa && 'tipo' in mesa) {
      this.mesaSeleccionada = mesa as MesaDto;
      console.log('Mesa seleccionada:', mesa);
    }
  }

  cargarSlotsDisponibles(): void {
    if (!this.mesaSeleccionada || !this.selectedDate) {
      console.error('❌ Falta mesa o fecha seleccionada');
      return;
    }

    this.cargandoSlots = true;
    this.availableTimes = [];
    this.selectedTime = null;

    const desde = new Date(this.selectedDate);
    desde.setHours(12 - 5, 0, 0, 0); // 12:00 - 5 horas = 7:00 (que en UTC será 12:00)

    const hasta = new Date(this.selectedDate);
    hasta.setHours(23 - 5, 0, 0, 0); // 23:00 - 5 horas = 18:00 (que en UTC será 23:00)

    const desdeISO = desde.toISOString();
    const hastaISO = hasta.toISOString();

    console.log('🕐 Cargando slots para:');
    console.log('  Mesa ID:', this.mesaSeleccionada.idMesa);
    console.log('  Desde:', desdeISO);
    console.log('  Hasta:', hastaISO);

    this.reservaService
      .obtenerSlotsDisponibles(
        this.mesaSeleccionada.idMesa!,
        desdeISO,
        hastaISO,
      )
      .subscribe({
        next: (response) => {
          console.log('✅ Slots recibidos:', response);

          if (response.data && response.data.length > 0) {
            this.availableTimes = response.data.map((dateTimeStr) => {
              const date = new Date(dateTimeStr);
              const hours = date.getHours().toString().padStart(2, '0');
              const minutes = date.getMinutes().toString().padStart(2, '0');

              return {
                time: `${hours}:${minutes}`,
                unavailable: false,
                dateTime: dateTimeStr,
              };
            });

            this.mensajeSlots = response.mensaje || 'Slots cargados';
            console.log('📅 Slots procesados:', this.availableTimes);
          } else {
            this.availableTimes = [];
            this.mensajeSlots = 'No hay horarios disponibles para esta fecha';
          }

          this.cargandoSlots = false;
        },
        error: (error) => {
          console.error('❌ Error al cargar slots:', error);
          this.availableTimes = [];
          this.mensajeSlots = 'Error al cargar horarios disponibles';
          this.cargandoSlots = false;
        },
      });
  }

  // Paso 3: Calendario
  generateCalendar(): void {
    const year = this.currentMonth.getFullYear();
    const month = this.currentMonth.getMonth();

    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const prevLastDay = new Date(year, month, 0);

    const firstDayOfWeek = firstDay.getDay();
    const lastDateOfMonth = lastDay.getDate();
    const prevLastDate = prevLastDay.getDate();

    this.calendarDays = [];

    // Días del mes anterior
    for (let i = firstDayOfWeek - 1; i >= 0; i--) {
      this.calendarDays.push({
        day: prevLastDate - i,
        enabled: false,
        selected: false,
        otherMonth: true,
        date: new Date(year, month - 1, prevLastDate - i),
      });
    }

    // Días del mes actual
    for (let i = 1; i <= lastDateOfMonth; i++) {
      const date = new Date(year, month, i);
      const isPast = date < new Date(new Date().setHours(0, 0, 0, 0));

      this.calendarDays.push({
        day: i,
        enabled: !isPast,
        selected: this.selectedDate?.toDateString() === date.toDateString(),
        otherMonth: false,
        date: date,
      });
    }

    // Días del siguiente mes
    const remainingDays = 42 - this.calendarDays.length;
    for (let i = 1; i <= remainingDays; i++) {
      this.calendarDays.push({
        day: i,
        enabled: false,
        selected: false,
        otherMonth: true,
        date: new Date(year, month + 1, i),
      });
    }
  }

  previousMonth(): void {
    this.currentMonth = new Date(
      this.currentMonth.getFullYear(),
      this.currentMonth.getMonth() - 1,
    );
    this.generateCalendar();
  }

  nextMonth(): void {
    this.currentMonth = new Date(
      this.currentMonth.getFullYear(),
      this.currentMonth.getMonth() + 1,
    );
    this.generateCalendar();
  }

  getMonthYear(): string {
    const months = [
      'Enero',
      'Febrero',
      'Marzo',
      'Abril',
      'Mayo',
      'Junio',
      'Julio',
      'Agosto',
      'Septiembre',
      'Octubre',
      'Noviembre',
      'Diciembre',
    ];
    return `${months[this.currentMonth.getMonth()]} ${this.currentMonth.getFullYear()}`;
  }

  selectDate(day: CalendarDay): void {
    if (!day.enabled || day.otherMonth) return;

    this.selectedDate = day.date;
    this.generateCalendar();
  }

  formatDate(date: Date): string {
    const days = ['dom', 'lun', 'mar', 'mié', 'jue', 'vie', 'sáb'];
    const months = [
      'ene',
      'feb',
      'mar',
      'abr',
      'may',
      'jun',
      'jul',
      'ago',
      'sep',
      'oct',
      'nov',
      'dic',
    ];

    return `${days[date.getDay()]}, ${date.getDate()} ${months[date.getMonth()]}`;
  }

  // Paso 4: Hora
  selectTime(time: TimeSlot): void {
    if (time.unavailable) return;
    this.selectedTime = time;
  }

  // Obtener número de mesa para el resumen
  getMesaNumero(): string {
    return this.mesaSeleccionada
      ? `#${this.mesaSeleccionada.numeroMesa}`
      : 'No seleccionada';
  }

  // Obtener zona de la mesa para el resumen
  getMesaZona(): string {
    return this.mesaSeleccionada ? this.mesaSeleccionada.tipo : '-';
  }

 seleccionarMetodoPago(metodo: 'TARJETA' | 'YAPE' | 'PLIN'): void {
  if (this.pagoCreado) return; // No permitir cambio si ya hay un pago creado
  
  this.metodoPagoSeleccionado = metodo;
  this.limpiarErroresTarjeta();
  this.imagenPreview = null;
  this.imagenBase64 = null;
  this.errorPago = '';
}

onFileSelected(event: any): void {
  const file = event.target.files[0];
  if (!file) return;

  // Validar tipo
  if (!file.type.startsWith('image/')) {
    alert('Solo se permiten imágenes');
    return;
  }

  // Validar tamaño (10MB)
  if (file.size > 10 * 1024 * 1024) {
    alert('La imagen no puede superar 10MB');
    return;
  }

  this.errorPago = '';

  // Leer imagen
  const reader = new FileReader();
  reader.onload = (e: any) => {
    this.imagenPreview = e.target.result;
    this.imagenBase64 = e.target.result; // Incluye data:image/...
  };
  reader.readAsDataURL(file);
}
  
  // Validación de tarjeta
  validarNumeroTarjeta(): void {
    const numero = this.datosTarjeta.numero.replace(/\s/g, '');

    if (numero.length === 0) {
      this.erroresTarjeta.numero = 'El número de tarjeta es requerido';
    } else if (!/^\d+$/.test(numero)) {
      this.erroresTarjeta.numero = 'Solo se permiten números';
    } else if (numero.length < 13 || numero.length > 19) {
      this.erroresTarjeta.numero = 'Número de tarjeta inválido (13-19 dígitos)';
    } else if (!this.validarLuhn(numero)) {
      this.erroresTarjeta.numero = 'Número de tarjeta inválido';
    } else {
      this.erroresTarjeta.numero = '';
    }

    this.verificarTarjetaValida();
  }

  validarNombreTitular(): void {
    const nombre = this.datosTarjeta.nombre.trim();

    if (nombre.length === 0) {
      this.erroresTarjeta.nombre = 'El nombre del titular es requerido';
    } else if (nombre.length < 3) {
      this.erroresTarjeta.nombre = 'El nombre debe tener al menos 3 caracteres';
    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(nombre)) {
      this.erroresTarjeta.nombre = 'Solo se permiten letras y espacios';
    } else {
      this.erroresTarjeta.nombre = '';
    }

    this.verificarTarjetaValida();
  }

  validarFechaExpiracion(): void {
    const fecha = this.datosTarjeta.fechaExpiracion;

    if (fecha.length === 0) {
      this.erroresTarjeta.fechaExpiracion =
        'La fecha de expiración es requerida';
    } else if (!/^\d{2}\/\d{2}$/.test(fecha)) {
      this.erroresTarjeta.fechaExpiracion = 'Formato inválido (MM/AA)';
    } else {
      const [mes, anio] = fecha.split('/').map(Number);
      const anioCompleto = 2000 + anio;
      const fechaExpiracion = new Date(anioCompleto, mes - 1);
      const hoy = new Date();

      if (mes < 1 || mes > 12) {
        this.erroresTarjeta.fechaExpiracion = 'Mes inválido (01-12)';
      } else if (fechaExpiracion < hoy) {
        this.erroresTarjeta.fechaExpiracion = 'Tarjeta expirada';
      } else {
        this.erroresTarjeta.fechaExpiracion = '';
      }
    }

    this.verificarTarjetaValida();
  }

  validarCVV(): void {
    const cvv = this.datosTarjeta.cvv;

    if (cvv.length === 0) {
      this.erroresTarjeta.cvv = 'El CVV es requerido';
    } else if (!/^\d{3,4}$/.test(cvv)) {
      this.erroresTarjeta.cvv = 'CVV inválido (3-4 dígitos)';
    } else {
      this.erroresTarjeta.cvv = '';
    }

    this.verificarTarjetaValida();
  }

  // Algoritmo de Luhn para validar número de tarjeta
  private validarLuhn(numero: string): boolean {
    let suma = 0;
    let alternar = false;

    for (let i = numero.length - 1; i >= 0; i--) {
      let digito = parseInt(numero.charAt(i), 10);

      if (alternar) {
        digito *= 2;
        if (digito > 9) {
          digito -= 9;
        }
      }

      suma += digito;
      alternar = !alternar;
    }

    return suma % 10 === 0;
  }

  // Formatear número de tarjeta automáticamente
  formatearNumeroTarjeta(event: any): void {
    let valor = event.target.value.replace(/\s/g, '');
    let valorFormateado = '';

    for (let i = 0; i < valor.length && i < 16; i++) {
      if (i > 0 && i % 4 === 0) {
        valorFormateado += ' ';
      }
      valorFormateado += valor[i];
    }

    this.datosTarjeta.numero = valorFormateado;
    this.validarNumeroTarjeta();
  }

  // Formatear fecha de expiración automáticamente
  formatearFechaExpiracion(event: any): void {
    let valor = event.target.value.replace(/\D/g, '');

    if (valor.length >= 2) {
      valor = valor.substring(0, 2) + '/' + valor.substring(2, 4);
    }

    this.datosTarjeta.fechaExpiracion = valor;
    this.validarFechaExpiracion();
  }

  // Solo permitir números
  soloNumeros(event: KeyboardEvent): boolean {
    const charCode = event.which ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      event.preventDefault();
      return false;
    }
    return true;
  }

  // Verificar si todos los campos de tarjeta son válidos
  private verificarTarjetaValida(): void {
    this.tarjetaValida =
      this.datosTarjeta.numero.replace(/\s/g, '').length >= 13 &&
      this.datosTarjeta.nombre.trim().length >= 3 &&
      this.datosTarjeta.fechaExpiracion.length === 5 &&
      this.datosTarjeta.cvv.length >= 3 &&
      !this.erroresTarjeta.numero &&
      !this.erroresTarjeta.nombre &&
      !this.erroresTarjeta.fechaExpiracion &&
      !this.erroresTarjeta.cvv;
  }

  // Limpiar errores
  private limpiarErroresTarjeta(): void {
    this.erroresTarjeta = {
      numero: '',
      nombre: '',
      fechaExpiracion: '',
      cvv: '',
    };
  }

  // Limpiar formulario de tarjeta
  limpiarFormularioTarjeta(): void {
    this.datosTarjeta = {
      numero: '',
      nombre: '',
      fechaExpiracion: '',
      cvv: '',
    };
    this.limpiarErroresTarjeta();
    this.tarjetaValida = false;
  }

  // Copiar número de Plin
  copiarNumeroPlin(): void {
    navigator.clipboard
      .writeText(this.numeroPlin.replace(/\s/g, ''))
      .then(() => {
        alert('Número copiado al portapapeles');
      })
      .catch(() => {
        alert('Error al copiar el número');
      });
  }

  

  construirFechaReservaISO(): string {
  if (!this.selectedDate || !this.selectedTime) {
    throw new Error('Fecha u hora no seleccionada');
  }

  // Usar el dateTime del slot seleccionado
  if (this.selectedTime.dateTime) {
    return this.selectedTime.dateTime;
  }

  // Fallback
  const fecha = new Date(this.selectedDate);
  const [hora, minuto] = this.selectedTime.time.split(':');
  fecha.setHours(parseInt(hora), parseInt(minuto), 0, 0);
  return fecha.toISOString();
}



  confirmarPago(): void {
  if (!this.puedeConfirmarPago()) {
    alert('Por favor completa todos los datos');
    return;
  }

  // Si es Yape o Plin, procesar captura
  if (this.metodoPagoSeleccionado === 'YAPE' || this.metodoPagoSeleccionado === 'PLIN') {
    this.procesarPagoConCaptura();
  } else if (this.metodoPagoSeleccionado === 'TARJETA') {
    // TODO: Implementar pago con tarjeta
    alert('Pago con tarjeta - En desarrollo');
  }
}

procesarPagoConCaptura(): void {
  if (!this.imagenBase64 || !this.metodoPagoSeleccionado) {
    alert('Debes subir el comprobante de pago');
    return;
  }

  this.procesandoPago = true;
  this.errorPago = '';

  // 1. Crear reserva primero
  const reservaRequest = {
    idUsuario: 1, // TODO: Obtener del auth
    idMesa: this.mesaSeleccionada!.idMesa!,
    fechaHora: this.construirFechaReservaISO(),
    cantidadPersonas: this.personas,
    observaciones: `Reserva para ${this.personas} personas`
  };

  this.reservaService.crearReserva(reservaRequest).subscribe({
    next: (result) => {
      if (result.valor && result.data) {
        const idReserva = result.data;

        // 2. Crear pago
        const pagoRequest = {
          idReserva: idReserva,
          idUsuario: 1,
          tipoPago: 'DR',
          monto: 15.00,
          metodoPago: this.metodoPagoSeleccionado!,
          observaciones: `Depósito - ${this.metodoPagoSeleccionado}`
        };

        this.pagoService.crearPago(pagoRequest).subscribe({
          next: (pago) => {
            this.pagoCreado = pago;

            // 3. Subir captura
            const capturaRequest = {
              idPago: pago.idPago,
              imagenBase64: this.imagenBase64!,
              metodoPago: this.metodoPagoSeleccionado!
            };

            this.pagoService.subirCaptura(capturaRequest).subscribe({
              next: (pagoValidado) => {
                this.procesandoPago = false;

                if (pagoValidado.estado === 'PAGADO') {
                  alert('¡Pago confirmado! Tu reserva ha sido registrada.');
                  // TODO: Redirigir a confirmación
                  this.router.navigate(['/']);
                } else if (pagoValidado.estado === 'RECHAZADO') {
                  this.errorPago = pagoValidado.motivoRechazo || 'Pago rechazado';
                }
              },
              error: (err) => {
                this.procesandoPago = false;
                this.errorPago = err.message;
              }
            });
          },
          error: (err) => {
            this.procesandoPago = false;
            alert('Error al crear pago: ' + err.message);
          }
        });
      }
    },
    error: (err) => {
      this.procesandoPago = false;
      alert('Error al crear reserva: ' + err.message);
    }
  });
}


intentarNuevamente(): void {
  this.imagenPreview = null;
  this.imagenBase64 = null;
  this.errorPago = '';
  this.procesandoPago = false;
}

// ========== ACTUALIZAR puedeConfirmarPago ==========
puedeConfirmarPago(): boolean {
  if (!this.metodoPagoSeleccionado) return false;

  if (this.metodoPagoSeleccionado === 'TARJETA') {
    return this.tarjetaValida;
  }

  if (this.metodoPagoSeleccionado === 'YAPE' || this.metodoPagoSeleccionado === 'PLIN') {
    return this.imagenBase64 !== null;
  }

  return false;
}

}