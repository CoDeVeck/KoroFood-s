// crudEventos/evento-form/evento-form.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EventoService } from '../../service/evento.service';
import { EventoRequest, TematicaResponse } from '../../models/evento.model';

@Component({
  selector: 'app-evento-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './evento.form.component.html',
  styleUrl: './evento.form.component.css'
})
export class EventoFormComponent implements OnInit {
  eventoForm: FormGroup;
  tematicas: TematicaResponse[] = [];
  isEditMode: boolean = false;
  eventoId: number | null = null;
  loading: boolean = false;
  error: string = '';

  constructor(
    private fb: FormBuilder,
    private eventoService: EventoService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.eventoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(500)]],
      idTematica: [null],
      fecha: ['', [Validators.required]],
      costo: [0, [Validators.required, Validators.min(0.01)]],
      imagen: ['']
    });
  }

  ngOnInit(): void {
    this.cargarTematicas();
    
    // Verificar si es modo edición
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.eventoId = +params['id'];
        this.cargarEvento(this.eventoId);
      }
    });
  }

  cargarTematicas(): void {
    this.eventoService.listarTematicas().subscribe({
      next: (data) => {
        this.tematicas = data;
      },
      error: (err) => {
        console.error('Error al cargar temáticas:', err);
      }
    });
  }

  cargarEvento(id: number): void {
    this.loading = true;
    this.eventoService.buscarPorId(id).subscribe({
      next: (evento) => {
        // Convertir fecha de ISO a formato datetime-local
        const fechaLocal = this.convertirADateTimeLocal(evento.fecha);
        
        this.eventoForm.patchValue({
          nombre: evento.nombre,
          descripcion: evento.descripcion,
          idTematica: evento.tematica?.idTematica || null,
          fecha: fechaLocal,
          costo: evento.costo,
          imagen: evento.imagen
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.eventoForm.invalid) {
      this.marcarCamposComoTocados();
      return;
    }

    this.loading = true;
    this.error = '';

    const eventoData: EventoRequest = {
      ...this.eventoForm.value,
      fecha: this.convertirAISO(this.eventoForm.value.fecha)
    };

    const request = this.isEditMode
      ? this.eventoService.actualizar(this.eventoId!, eventoData)
      : this.eventoService.crear(eventoData);

    request.subscribe({
      next: () => {
        const mensaje = this.isEditMode 
          ? 'Evento actualizado exitosamente' 
          : 'Evento creado exitosamente';
        alert(mensaje);
        this.router.navigate(['/admin/eventos']);
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/admin/eventos']);
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.eventoForm.controls).forEach(key => {
      this.eventoForm.get(key)?.markAsTouched();
    });
  }

  private convertirADateTimeLocal(isoString: string): string {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  private convertirAISO(dateTimeLocal: string): string {
    return new Date(dateTimeLocal).toISOString();
  }

  // Helpers para validaciones en el template
  get nombre() { return this.eventoForm.get('nombre'); }
  get descripcion() { return this.eventoForm.get('descripcion'); }
  get fecha() { return this.eventoForm.get('fecha'); }
  get costo() { return this.eventoForm.get('costo'); }
}