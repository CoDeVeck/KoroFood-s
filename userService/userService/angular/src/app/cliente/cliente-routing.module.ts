import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClienteLayoutComponent } from '../layout/cliente-layout/cliente-layout.component';
import { IndexComponent } from './index/index.component';
import { ResenaComponent } from './resena/resena.component';
import { FormResenaComponent } from './form-resena/form-resena.component';
import { MenuComponent } from './menu/menu.component';
import { ContactoComponent } from './contacto/contacto.component';
import { ReservaComponent } from './reserva/reserva.component';
import { PerfilComponent } from './perfil/perfil.component';
import { ChatContainerComponent } from './chat/chat-container.component';

const routes: Routes = [
  //Ruta para el chat sin layout
  {
    path: 'chat',
    component: ChatContainerComponent,
    data: { title: 'Chat' },
  },

  //Rutas con el layout presente
  {
    path: '',
    component: ClienteLayoutComponent,
    children: [
      {
        path: 'inicio',
        component: IndexComponent,
        data: { title: 'Inicio' },
      },
      {
        path: 'resenia',
        component: ResenaComponent,
        data: { title: 'Reseña' },
      },
      {
        path: 'crear-resenia',
        component: FormResenaComponent,
        data: { title: 'Crear Reseña' },
      },
      {
        path: 'menu',
        component: MenuComponent,
        data: { title: 'Menú' },
      },
      {
        path: 'contacto',
        component: ContactoComponent,
        data: { title: 'Contacto' },
      },
      {
        path: 'reserva',
        component: ReservaComponent,
        data: { title: 'Reserva' },
      },
      {
        path: 'perfil',
        component: PerfilComponent,
        data: { title: 'Perfil' },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ClienteRoutingModule {}
