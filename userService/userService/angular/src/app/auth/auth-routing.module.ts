import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { ClienteRoutingModule } from '../cliente/cliente-routing.module';
import { AdminRoutingModule } from '../admin/admin-routing.module';
import { MeseroRoutingModule } from '../mesero/mesero-routing.module';
import { RecepcionistaRoutingModule } from '../recepcionista/recepcionista-routing.module';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    data: { title: 'Iniciar Sesión' },
  },
  {
    path: 'register',
    component: RegisterComponent,
    data: { title: 'Registrarse ' },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AuthRoutingModule {}
