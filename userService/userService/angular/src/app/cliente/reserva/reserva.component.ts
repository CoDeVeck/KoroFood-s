import { CommonModule} from '@angular/common';
import { Component, OnInit } from '@angular/core';

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
  imports: [CommonModule],
  templateUrl: './reserva.component.html',
  styleUrl: './reserva.component.css',
})
export class ReservaComponent implements OnInit {
  currentStep: number = 1;

  // Paso 1: Personas
  personas: number = 1;
  quickNumbers: number[] = [1, 2, 3, 4];

  // Paso 2: Fecha
  currentMonth: Date = new Date();
  weekDays: string[] = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  calendarDays: CalendarDay[] = [];
  selectedDate: Date | null = null;

  // Paso 3: Hora
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

  constructor() {}

  ngOnInit(): void {
    this.generateCalendar();
  }

  // Navegación de pasos
  nextStep(): void {
    if (this.currentStep < 4) {
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

  // Paso 2: Calendario
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
      const isToday = new Date().toDateString() === date.toDateString();
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

  // Paso 3: Hora
  selectTime(time: TimeSlot): void {
    if (time.unavailable) return;
    this.selectedTime = time;
  }
}
