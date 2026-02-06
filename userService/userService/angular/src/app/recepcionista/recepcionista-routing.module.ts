import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RecepcionistaLayoutComponent } from '../layout/recepcionista-layout/recepcionista-layout.component';
import { ChatContainerRecComponent } from './chat/chat-container-rec.component';

const routes: Routes = [
  {
    path: '',
    component: RecepcionistaLayoutComponent,
    children: [
      {
        path: 'chat',
        component: ChatContainerRecComponent,
        data: { title: 'Ordenes' },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RecepcionistaRoutingModule {}
