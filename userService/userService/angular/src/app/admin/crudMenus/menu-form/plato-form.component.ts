
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PlatoService } from '../../service/plato.service';
import { PlatoRequest, TIPOS_PLATO, EtiquetaResponse } from '../../models/plato.model';

@Component({
  selector: 'app-plato-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './plato-form.component.html',
  styleUrl: './plato-form.component.css'
})
export class PlatoFormComponent implements OnInit {
  platoForm: FormGroup;
  etiquetas: EtiquetaResponse[] = [];
  tiposPlato = TIPOS_PLATO;
  isEditMode: boolean = false;
  platoId: number | null = null;
  loading: boolean = false;
  error: string = '';

  constructor(
    private fb: FormBuilder,
    private platoService: PlatoService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.platoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      precio: [0, [Validators.required, Validators.min(0.01)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      tipoPlato: ['', [Validators.required]],
      imagen: ['']
    });
  }

  ngOnInit(): void {
    this.cargarEtiquetas();
    
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.platoId = +params['id'];
        this.cargarPlato(this.platoId);
      }
    });
  }

  cargarEtiquetas(): void {
    this.platoService.listarEtiquetas().subscribe({
      next: (data) => {
        this.etiquetas = data;
      },
      error: (err) => {
        console.error('Error al cargar etiquetas:', err);
      }
    });
  }

  cargarPlato(id: number): void {
    this.loading = true;
    this.platoService.buscarPorId(id).subscribe({
      next: (plato) => {
        this.platoForm.patchValue({
          nombre: plato.nombre,
          precio: plato.precio,
          stock: plato.stock,
          tipoPlato: plato.tipoPlato,
          imagen: plato.imagen
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
    if (this.platoForm.invalid) {
      this.marcarCamposComoTocados();
      return;
    }

    this.loading = true;
    this.error = '';

    const platoData: PlatoRequest = this.platoForm.value;

    const request = this.isEditMode
      ? this.platoService.actualizar(this.platoId!, platoData)
      : this.platoService.crear(platoData);

    request.subscribe({
      next: () => {
        const mensaje = this.isEditMode 
          ? 'Plato actualizado exitosamente' 
          : 'Plato creado exitosamente';
        alert(mensaje);
        this.router.navigate(['/admin/menus']);
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/admin/menus']);
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.platoForm.controls).forEach(key => {
      this.platoForm.get(key)?.markAsTouched();
    });
  }

  get nombre() { return this.platoForm.get('nombre'); }
  get precio() { return this.platoForm.get('precio'); }
  get stock() { return this.platoForm.get('stock'); }
  get tipoPlato() { return this.platoForm.get('tipoPlato'); }
}