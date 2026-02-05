import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MesaItem, MesaSelectorComponent } from './mesa-selector/mesa-selector.component';
import { MesaDto } from '../../shared/dto/MesaDto';
import { MesasServiceService } from '../service/mesas-service.service';
import { ReservaServiceService } from '../service/reserva-service.service';
import { Zona } from '../../shared/enums/Zona';
import { FormsModule } from '@angular/forms';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';

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
  metodoPagoSeleccionado: 'tarjeta' | 'yape' | 'plin' | null = null;

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

  // Plin - Número ficticio
  numeroPlin: string = '987 654 321';

  constructor(
    private mesasService: MesasServiceService,
    private reservaService: ReservaServiceService, // INYECTAR
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

  seleccionarMetodoPago(metodo: 'tarjeta' | 'yape' | 'plin'): void {
    this.metodoPagoSeleccionado = metodo;
    this.limpiarErroresTarjeta();
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

  // Validar pago antes de confirmar
  puedeConfirmarPago(): boolean {
    if (!this.metodoPagoSeleccionado) {
      return false;
    }

    if (this.metodoPagoSeleccionado === 'tarjeta') {
      return this.tarjetaValida;
    }

    // Para Yape y Plin, asumimos que el usuario completó el pago
    return true;
  }

  // Confirmar pago
  confirmarPago(): void {
    if (!this.puedeConfirmarPago()) {
      alert('Por favor complete los datos de pago correctamente');
      return;
    }

    console.log('Procesando pago...');
    console.log('Método:', this.metodoPagoSeleccionado);

    if (this.metodoPagoSeleccionado === 'tarjeta') {
      console.log('Datos de tarjeta:', this.datosTarjeta);
    }

    // Aquí iría la lógica para procesar el pago
    alert('¡Pago procesado exitosamente! Reserva confirmada.');
  }
}
