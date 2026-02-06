import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  NavigationEnd,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-mesero-layout',
  imports: [RouterOutlet, CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './mesero-layout.component.html',
  styleUrl: './mesero-layout.component.css',
})
export class MeseroLayoutComponent {
  sidebarCollapsed = false;
  currentTime = new Date();
  userName = 'Carlos Mendoza';

  pageTitles: { [key: string]: string } = {
    '/mesero/ordenes': 'Órdenes',
    '/mesero/nueva-orden': 'Nueva Orden',
    '/dashboard/mesas': 'Mesas',
    '/dashboard/menu': 'Menú',
    '/dashboard/reservas': 'Reservas',
  };

  currentRoute = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    setInterval(() => {
      this.currentTime = new Date();
    }, 60000);

    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.currentRoute = event.url;
      });
  }

  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  getUserInitials(): string {
    return this.userName
      .split(' ')
      .map((n) => n[0])
      .join('')
      .toUpperCase();
  }

  getPageTitle(): string {
    return this.pageTitles[this.currentRoute] || 'Ordenes';
  }

  logout(): void {
    // Implementar lógica de logout
    console.log('Cerrando sesión...');
    this.router.navigate(['/login']);
  }
}
