import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClienteLayoutComponent } from '../layout/cliente-layout/cliente-layout.component';
import { IndexComponent } from './index/index.component';
import { ResenaComponent } from './resena/resena.component';
import { FormResenaComponent } from './form-resena/form-resena.component';

const routes: Routes = [{
    path: '',
    component: ClienteLayoutComponent,
     children: [
      {
        path: 'inicio', 
        component: IndexComponent,data: { title: 'Inicio' }
      },
      {
        path: 'resenia', 
        component: ResenaComponent,data: { title: 'Reseña' }
      },
      {
        path: 'crear-resenia', 
        component: FormResenaComponent,data: { title: 'Crear Reseña' }
      }
    ],
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClienteRoutingModule { }
