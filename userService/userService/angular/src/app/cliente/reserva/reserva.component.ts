import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  MesaItem,
  MesaSelectorComponent,
} from './mesa-selector/mesa-selector.component';
import { MesaDto } from '../../shared/dto/MesaDto';
import { MesasServiceService } from '../service/mesas-service.service';
import { Zona } from '../../shared/enums/Zona';
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
}

@Component({
  selector: 'app-reserva',
  standalone: true,
  imports: [CommonModule, MesaSelectorComponent],
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
  availableTimes: TimeSlot[] = [
    { time: '12:00' },
    { time: '12:30' },
    { time: '13:00' },
    { time: '13:30' },
    { time: '14:00' },
    { time: '14:30' },
    { time: '15:00' },
    { time: '15:30' },
    { time: '16:00' },
    { time: '16:30' },
    { time: '17:00' },
    { time: '17:30' },
    { time: '18:00' },
    { time: '18:30' },
    { time: '19:00', unavailable: true },
    { time: '19:30' },
    { time: '20:00' },
    { time: '20:30' },
    { time: '21:00', unavailable: true },
    { time: '21:30' },
    { time: '22:00' },
    { time: '22:30' },
  ];

  alternativeTimes: TimeSlot[] = [
    { time: '12:30' },
    { time: '15:30' },
    { time: '22:30' },
  ];

  selectedTime: TimeSlot | null = null;

  constructor(private mesasService: MesasServiceService) {
    // Inyecta tu servicio aquí
    // private mesasService: MesasServiceService
  }

  ngOnInit(): void {
    this.generateCalendar();
  }

  // Navegación de pasos
  nextStep(): void {
    if (this.currentStep === 1 && this.personas > 0) {
      this.currentStep++;
      this.cargarMesasPorZona(); // Cargar mesas al avanzar al paso 2
    } else if (this.currentStep === 2 && this.mesaSeleccionada) {
      this.currentStep++;
    } else if (this.currentStep === 3 && this.selectedDate) {
      this.currentStep++;
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

    // Simulación para ejemplo (eliminar cuando uses el servicio real)
    /*
    setTimeout(() => {
      // Simulamos diferentes resultados según la zona
      if (this.zonaSeleccionada === 'Z1') {
        this.mesasDisponibles = [
          {
            idMesa: 1,
            numeroMesa: 101,
            capacidad: 4,
            tipo: 'Z1',
            estado: 'LIBRE',
          },
          {
            idMesa: 4,
            numeroMesa: 104,
            capacidad: 4,
            tipo: 'Z1',
            estado: 'LIBRE',
          },
          {
            idMesa: 7,
            numeroMesa: 107,
            capacidad: 4,
            tipo: 'Z1',
            estado: 'OCUPADA',
          },
          {
            idMesa: 9,
            numeroMesa: 109,
            capacidad: 4,
            tipo: 'Z1',
            estado: 'LIBRE',
          },
          {
            idMesa: 11,
            numeroMesa: 111,
            capacidad: 4,
            tipo: 'Z1',
            estado: 'LIBRE',
          },
        ];
        this.mensajeMesas = `Mesas encontradas en zona ${this.zonaSeleccionada} con capacidad para ${this.personas} personas`;
      } else if (this.zonaSeleccionada === 'Z2') {
        this.mesasDisponibles = [
          {
            idMesa: 12,
            numeroMesa: 201,
            capacidad: 4,
            tipo: 'Z2',
            estado: 'LIBRE',
          },
          {
            idMesa: 13,
            numeroMesa: 202,
            capacidad: 4,
            tipo: 'Z2',
            estado: 'LIBRE',
          },
        ];
        this.mensajeMesas = `2 mesas disponibles en zona ${this.zonaSeleccionada}`;
      } else {
        this.mesasDisponibles = [];
        this.mensajeMesas = `No hay mesas disponibles en zona ${this.zonaSeleccionada}`;
      }
      this.cargandoMesas = false;
    }, 1000);
  

  */
  }

  onMesaSeleccionada(mesa: MesaItem): void {
    // Solo aceptamos MesaDto en este caso
    if ('estado' in mesa && 'tipo' in mesa) {
      this.mesaSeleccionada = mesa as MesaDto;
      console.log('Mesa seleccionada:', mesa);
    }
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
}
