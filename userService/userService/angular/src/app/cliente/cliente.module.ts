import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClienteRoutingModule } from './cliente-routing.module';
import { RouterModule } from '@angular/router';
import { PerfilComponent } from './perfil/perfil.component';

@NgModule({
  declarations: [],
  imports: [CommonModule, RouterModule, ClienteRoutingModule, PerfilComponent],
})
export class ClienteModule {}
