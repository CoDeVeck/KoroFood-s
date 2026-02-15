import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminLayoutComponent } from '../layout/admin-layout/admin-layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { EventoListComponent } from './crudEventos/event-list/evento.list.component';
import { EventoFormComponent } from './crudEventos/event-form/evento.form.component';

import { CrudEmpleadosComponent } from './crud-empleados/crud-empleados.component';


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

      {
        path:'empleado', component:CrudEmpleadosComponent
      }

    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
