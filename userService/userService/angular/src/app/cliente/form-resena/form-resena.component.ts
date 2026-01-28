import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormsModule,
} from '@angular/forms';
import { PlatoDto } from '../../shared/dto/PlatoDto';
import { EventoDto } from '../../shared/dto/EventoDto';
import { TipoEntidad } from '../../shared/enums/tipoEntidad.enum';

import { ResenaRequest } from '../../shared/dto/ResenaRequest';
import { AlertService } from '../../util/alert.service';
import { ResenaClienteService } from '../service/resenaClienteService';
import { MenuClienteService } from '../service/menuClienteService';
import { EventoClienteService } from '../service/eventoClienteService';
import { Router } from '@angular/router';

@Component({
  selector: 'app-resena',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './form-resena.component.html',
  styleUrl: './form-resena.component.css',
})
export class FormResenaComponent {
  resenaForm!: FormGroup;
  isSubmitting = false;
  showSuccess = false;
  showError = false;
  errorMessage = '';
  selectedRating = 0;
  hoveredRating = 0;

  platos: PlatoDto[] = [];
  eventos: EventoDto[] = [];
  isLoadingEntidades = false;

  showModal = false;
  selectedItem: PlatoDto | EventoDto | null = null;

  TipoEntidad = TipoEntidad;

  searchTerm: string = '';
  currentPage: number = 1;
  itemsPerPage: number = 6;

  constructor(
    private fb: FormBuilder,
    private resenaService: ResenaClienteService,
    private menuService: MenuClienteService,
    private eventoService: EventoClienteService,
    private router: Router
  ) {}

  ngOnInit() {
    this.initForm();
    this.cargarPlatos();
    this.cargarEventos();
  }

  initForm() {
    this.resenaForm = this.fb.group({
      idUsuario: [1, Validators.required],
      tipoEntidad: ['', Validators.required],
      idEntidad: ['', Validators.required],
      calificacion: [
        0,
        [Validators.required, Validators.min(1), Validators.max(5)],
      ],
      comentario: [
        '',
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(500),
        ],
      ],
    });
  }

  // Type Guards
  isPlato(item: PlatoDto | EventoDto): item is PlatoDto {
    return 'idPlato' in item && 'tipoPlato' in item;
  }

  isEvento(item: PlatoDto | EventoDto): item is EventoDto {
    return 'idEvento' in item;
  }

  // Cargar datos desde servicios
  cargarPlatos() {
    this.menuService.listarPlatos().subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.platos = response.data;
        }
      },
      error: (error) => {
        console.error('Error al cargar platos:', error);
      },
    });
  }

  cargarEventos() {
    this.eventoService.listarEventos().subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.eventos = response.data;
        }
      },
      error: (error) => {
        console.error('Error al cargar eventos:', error);
      },
    });
  }

  // Rating Stars
  setRating(rating: number) {
    this.selectedRating = rating;
    this.resenaForm.patchValue({ calificacion: rating });
  }

  hoverRating(rating: number) {
    this.hoveredRating = rating;
  }

  resetHover() {
    this.hoveredRating = 0;
  }

  getStarClass(position: number): string {
    const rating = this.hoveredRating || this.selectedRating;
    return position <= rating
      ? 'bi-star-fill star-filled'
      : 'bi-star star-empty';
  }

  get entidadesDisponibles(): (PlatoDto | EventoDto)[] {
    const tipo = this.resenaForm.get('tipoEntidad')?.value;
    return tipo === TipoEntidad.PLATO ? this.platos : this.eventos;
  }

  get isTipoPlato(): boolean {
    return this.resenaForm.get('tipoEntidad')?.value === TipoEntidad.PLATO;
  }

  get isTipoEvento(): boolean {
    return this.resenaForm.get('tipoEntidad')?.value === TipoEntidad.EVENTO;
  }

  // Modal para vista previa
  openModal(item: PlatoDto | EventoDto, event?: Event) {
    if (event) {
      event.stopPropagation();
    }
    this.selectedItem = item;
    this.showModal = true;
    document.body.style.overflow = 'hidden';
  }

  closeModal() {
    this.showModal = false;
    this.selectedItem = null;
    document.body.style.overflow = 'auto';
  }

  selectItemFromModal() {
    if (this.selectedItem) {
      const id = this.getItemId(this.selectedItem);
      this.resenaForm.patchValue({ idEntidad: id });
      this.closeModal();
    }
  }

  // Métodos auxiliares para obtener propiedades
  getItemId(item: PlatoDto | EventoDto): number {
    return this.isPlato(item) ? item.idPlato : item.idEvento;
  }

  getItemNombre(item: PlatoDto | EventoDto): string {
    return item.nombre;
  }

  getItemImagen(item: PlatoDto | EventoDto): string {
    return item.imagen;
  }

  getItemDescripcion(item: PlatoDto | EventoDto): string {
    if (this.isPlato(item)) {
      return `Tipo: ${this.traducirTipoPlato(item.tipoPlato)}`;
    }
    return item.descripcion || 'Sin descripción disponible';
  }

  traducirTipoPlato(tipo: string): string {
    switch (tipo) {
      case 'E':
        return 'Entrada';
      case 'S':
        return 'Segundo';
      case 'P':
        return 'Postre';
      case 'B':
        return 'Bebida';
      default:
        return tipo;
    }
  }

  getItemTipoPlato(item: PlatoDto | EventoDto): string | null {
    return this.isPlato(item) ? item.tipoPlato : null;
  }

  // Submit
  onSubmit() {
    if (this.resenaForm.invalid) {
      this.markFormGroupTouched(this.resenaForm);
      return;
    }

    this.isSubmitting = true;
    this.showError = false;
    this.showSuccess = false;

    const resenaRequest: ResenaRequest = this.resenaForm.value;

    this.resenaService.crearResena(resenaRequest).subscribe({
      next: (response) => {
        if (response.valor) {
          AlertService.success(
            response.mensaje || '¡Reseña creada correctamente!',
          );
          this.router.navigate(['/cliente/resenia']);
          this.resetForm();
        }
        this.isSubmitting = false;
      },
      error: (error) => {
        console.error('Error al crear reseña:', error);
        const msg =
          error.error?.mensaje ||
          'Ocurrió un error al enviar tu reseña. Por favor, intenta nuevamente.';
        AlertService.error(msg);
        this.isSubmitting = false;
      },
    });
  }

  resetForm() {
    this.resenaForm.reset({
      idUsuario: 1, // por default por el momento
      tipoEntidad: '',
      idEntidad: '',
      calificacion: 0,
      comentario: '',
    });
    this.selectedRating = 0;
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach((key) => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }
  get entidadesFiltradas(): (PlatoDto | EventoDto)[] {
    const tipo = this.resenaForm.get('tipoEntidad')?.value;
    const entidades = tipo === TipoEntidad.PLATO ? this.platos : this.eventos;

    if (!this.searchTerm.trim()) {
      return entidades;
    }

    return entidades.filter((item) =>
      this.getItemNombre(item)
        .toLowerCase()
        .includes(this.searchTerm.toLowerCase()),
    );
  }

  // Entidades paginadas
  get entidadesPaginadas(): (PlatoDto | EventoDto)[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.entidadesFiltradas.slice(startIndex, endIndex);
  }

  // Total de páginas
  get totalPages(): number {
    return Math.ceil(this.entidadesFiltradas.length / this.itemsPerPage);
  }

  // Array de páginas para la paginación
  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  // Cambiar página
  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  // Página anterior
  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  // Página siguiente
  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  // Limpiar búsqueda
  clearSearch() {
    this.searchTerm = '';
    this.currentPage = 1;
  }

  // Al cambiar tipo de entidad, resetear búsqueda y página
  onTipoEntidadChange() {
    this.resenaForm.patchValue({ idEntidad: '' });
    this.searchTerm = '';
    this.currentPage = 1;
  }

  // Al buscar, resetear a página 1
  onSearch() {
    this.currentPage = 1;
  }
  // Validaciones
  isFieldInvalid(fieldName: string): boolean {
    const field = this.resenaForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.resenaForm.get(fieldName);
    if (field?.errors) {
      if (field.errors['required']) return 'Este campo es obligatorio';
      if (field.errors['minlength'])
        return `Mínimo ${field.errors['minlength'].requiredLength} caracteres`;
      if (field.errors['maxlength'])
        return `Máximo ${field.errors['maxlength'].requiredLength} caracteres`;
      if (field.errors['min']) return 'Debes seleccionar al menos 1 estrella';
    }
    return '';
  }

  onImgError(event: any) {
    event.target.src = '/img/no-imagen.jpg';
  }
}
