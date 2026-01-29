import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminLayoutComponent } from '../layout/admin-layout/admin-layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { EventoListComponent } from './crudEventos/event-list/evento.list.component';
import { EventoFormComponent } from './crudEventos/event-form/evento.form.component';
import { PlatoListComponent } from './crudMenus/menu-list/plato-list.component';
import { PlatoFormComponent } from './crudMenus/menu-form/plato-form.component';

const routes: Routes = [
  {
    path:'',
    component: AdminLayoutComponent,
    children: [
      {path: '', redirectTo: 'dashboard', pathMatch: 'full'},
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: {title: 'Dashboard'},
      },
       // Rutas de Eventos
      { path: '', redirectTo: 'eventos', pathMatch: 'full' },
      { path: 'eventos', component: EventoListComponent },
      { path: 'eventos/nuevo', component: EventoFormComponent },
      { path: 'eventos/editar/:id', component: EventoFormComponent },
      // Rutas de Menús
      { path: 'menus', component: PlatoListComponent },
      { path: 'menus/nuevo', component: PlatoFormComponent },
      { path: 'menus/editar/:id', component: PlatoFormComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
