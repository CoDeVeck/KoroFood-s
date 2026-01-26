import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ResenaListResponse } from '../../shared/dto/ResenaListResponse';
import { ResenaService } from '../service/resenaService';

@Component({
  selector: 'app-resena-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './resena.component.html',
  styleUrl: './resena.component.css',
})
export class ResenaComponent implements OnInit {
  resenas: ResenaListResponse[] = [];
  resenasFiltradas: ResenaListResponse[] = [];
  isLoading = true;
  error = '';

  // Paginación
  currentPage = 1;
  itemsPerPage = 9;

  // Filtros
  verSoloMias = false;
  idUsuarioActual = 1; // Debe venir del servicio de autenticación

  // Filtro de calificación
  filtroCalificacion: number | null = null;

  constructor(
    private resenaService: ResenaService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.cargarResenas();
  }

  cargarResenas() {
    this.isLoading = true;
    this.error = '';

    this.resenaService.listarResenas().subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.resenas = response.data;
          this.aplicarFiltros();
          this.isLoading = false;
        }
      },
      error: (err) => {
        console.error('Error al cargar reseñas:', err);
        this.error = 'No se pudieron cargar las reseñas';
        this.isLoading = false;
      },
    });
  }

  cargarMisResenas() {
    this.isLoading = true;
    this.error = '';

    this.resenaService.listarResenasPorUsuario(this.idUsuarioActual).subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.resenas = response.data;
          this.aplicarFiltros();
          this.isLoading = false;
        }
      },
      error: (err) => {
        console.error('Error al cargar mis reseñas:', err);
        this.error = 'No se pudieron cargar tus reseñas';
        this.isLoading = false;
      },
    });
  }

  toggleMisResenas() {
    this.verSoloMias = !this.verSoloMias;
    this.currentPage = 1;

    if (this.verSoloMias) {
      this.cargarMisResenas();
    } else {
      this.cargarResenas();
    }
  }

  filtrarPorCalificacion(calificacion: number | null) {
    this.filtroCalificacion = calificacion;
    this.currentPage = 1;
    this.aplicarFiltros();
  }

  aplicarFiltros() {
    this.resenasFiltradas = this.resenas;

    // Filtrar por calificación si está activo
    if (this.filtroCalificacion !== null) {
      this.resenasFiltradas = this.resenasFiltradas.filter(
        (resena) => resena.calificacion === this.filtroCalificacion,
      );
    }
  }

  // Paginación
  get resenasPaginadas(): ResenaListResponse[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.resenasFiltradas.slice(startIndex, endIndex);
  }

  get totalPages(): number {
    return Math.ceil(this.resenasFiltradas.length / this.itemsPerPage);
  }

  get pages(): number[] {
    const maxPagesToShow = 5;
    const pages: number[] = [];

    if (this.totalPages <= maxPagesToShow) {
      return Array.from({ length: this.totalPages }, (_, i) => i + 1);
    }

    const startPage = Math.max(1, this.currentPage - 2);
    const endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  // Navegación
  irACrearResena() {
    this.router.navigate(['/cliente/crear-resenia']);
  }

  // Helpers
  getStars(calificacion: number): number[] {
    return Array(5)
      .fill(0)
      .map((_, i) => (i < calificacion ? 1 : 0));
  }

  getCalificacionPromedio(): number {
    if (this.resenasFiltradas.length === 0) return 0;
    const suma = this.resenasFiltradas.reduce(
      (acc, r) => acc + r.calificacion,
      0,
    );
    return suma / this.resenasFiltradas.length;
  }

  getTotalPorCalificacion(calificacion: number): number {
    return this.resenas.filter((r) => r.calificacion === calificacion).length;
  }

  onImgError(event: any) {
    event.target.src = '/img/user-default.png';
  }
get maxItem(): number {
  return Math.min(this.currentPage * this.itemsPerPage, this.resenasFiltradas.length);
}

  onImgErrorEntidad(event: any) {
    event.target.src = '/img/no-imagen.jpg';
  }
}
