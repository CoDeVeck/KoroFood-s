import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'cliente',
    loadChildren: () =>
      import('./cliente/cliente.module').then((m) => m.ClienteModule),
  },
  {
    path: 'admin',
    loadChildren: () =>
      import('./admin/admin.module').then((m) => m.AdminModule),
  },
  {
    path: 'mesero',
    loadChildren: () =>
      import('./mesero/mesero.module').then((m) => m.MeseroModule),
  },
  {
    path: 'recepcionista',
    loadChildren: () =>
      import('./recepcionista/recepcionista.module').then(
        (m) => m.RecepcionistaModule,
      ),
  },
  { path: '', redirectTo: 'cliente/index', pathMatch: 'full' },
  { path: '**', redirectTo: 'auth/login' },
];
