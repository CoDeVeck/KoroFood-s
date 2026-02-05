import { Component, OnInit } from '@angular/core';
import { Usuario } from '../../shared/model/usuario.model';
import { AuthService } from '../../auth/service/auth.service';
import { PerfilClienteResponse } from '../../shared/response/perfilCllienteResponse.model';
import { PerfilService } from '../service/perfil.service';
import { UserService } from '../service/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-perfil',
  imports: [],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css',
})
export class PerfilComponent implements OnInit {
  usuario: Usuario | null = null;
  perfilDTO: PerfilClienteResponse[] | null = null;

  constructor(
    private perfilService: PerfilService,
    private userService: UserService,
    private router: Router,
    private socialAuth: AuthService,
  ) {}

  ngOnInit(): void {
    if (!this.socialAuth.isLoggedIn()) {
      console.log('Logeo requerido, Redirigiendo a Login!');
      (this.router.navigate(['/login']),
        {
          queryParams: { message: 'login_required' },
        });
      return;
    }

    const usuario = this.userService.getUser();

    if (!usuario) {
      this.router.navigate(['/login'], {
        queryParams: { message: 'login_required' },
      });
      return;
    }

    const idUsuario = usuario.idUsuario;

    this.perfilService.getPerfilCliente(idUsuario!).subscribe({
      next: (data: PerfilClienteResponse[]) => {
        this.perfilDTO = data;
      },

      error: (err) => {
        console.error('Error al obtener el perfil', err);
        if (err.status === 401) {
          this.socialAuth.logout();
          this.router.navigate(['/login'], {
            queryParams: { message: 'session_expired' },
          });
        }
      },
    });
  }
}
