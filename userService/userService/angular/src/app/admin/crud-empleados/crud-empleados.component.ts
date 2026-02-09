import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UsuarioSoap } from '../../shared/model/usuarioSoap,model';
import { UsuarioSoapService } from '../service/usuarioSoap.service';
import { CommonModule } from '@angular/common';
import { Distrito } from '../../shared/model/distrito.model';
import { DistritoService } from '../../auth/service/distrito.service';
import { AlertService } from '../../util/alert.service';

@Component({
  selector: 'app-crud-usuarios',
  imports: [ReactiveFormsModule, CommonModule],
    templateUrl: './crud-empleados.component.html',
  styleUrl: './crud-empleados.component.css'
})
export class CrudEmpleadosComponent implements OnInit {
  usuarios: UsuarioSoap[] = [];
  usuarioForm!: FormGroup;
  mostrarModal = false;
  modoEdicion = false;
  usuarioSeleccionado: UsuarioSoap | null = null;
  cargando = false;
  idDistrito = null
distritos: Distrito[] = [];
  roles = [
    { id: 2, nombre: 'Recepcionista' },
    { id: 3, nombre: 'Mesero' }
  ];

  tiposDocumento = ['DNI', 'PAS', 'CDX', 'CMP'];

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioSoapService,
    private distritoService: DistritoService
  ) {
    this.inicializarFormulario();
  }

  ngOnInit(): void {
    this.cargarUsuarios();
    this.cargarDistritos();
  }
cargarDistritos() {
  this.distritoService.listarDistritos().subscribe({
    next: (resp) => this.distritos = resp,
    error: (e) => console.error("Error al cargar distritos", e)
  });
}
  inicializarFormulario(): void {
    this.usuarioForm = this.fb.group({
      nombres: ['', [Validators.required, Validators.minLength(2)]],
      apePaterno: ['', [Validators.required, Validators.minLength(2)]],
      apeMaterno: ['', [Validators.required, Validators.minLength(2)]],
      correo: ['', [Validators.required, Validators.email]],
      clave: ['', [Validators.required, Validators.minLength(6)]],
      tipoDoc: ['DNI', Validators.required],
      nroDoc: ['', [Validators.required, Validators.pattern(/^\d{8,12}$/)]],
      direccion: [''],
      idDistrito: [null, Validators.required],
      telefono: ['', Validators.pattern(/^\d{9}$/)],
      idRol: [2, Validators.required],
      activo: [true]
    });
  }

  cargarUsuarios(): void {
    this.cargando = true;
    this.usuarioService.listarUsuarios().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar usuarios:', error);
        this.cargando = false;
        alert('Error al cargar la lista de usuarios');
      }
    });
  }

  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.usuarioSeleccionado = null;
    this.usuarioForm.reset({
      tipoDoc: 'DNI',
      idRol: 2,
      activo: true
    });
    this.usuarioForm.get('clave')?.setValidators([Validators.required, Validators.minLength(6)]);
    this.mostrarModal = true;
  }

  abrirModalEditar(usuario: UsuarioSoap): void {
    this.modoEdicion = true;
    this.usuarioSeleccionado = usuario;
    
    this.usuarioForm.patchValue({
      nombres: usuario.nombres,
      apePaterno: usuario.apePaterno,
      apeMaterno: usuario.apeMaterno,
      correo: usuario.correo,
      tipoDoc: usuario.tipoDoc,
      nroDoc: usuario.nroDoc,
      direccion: usuario.direccion,
      idDistrito: usuario.idDistrito,
      telefono: usuario.telefono,
      idRol: usuario.idRol,
      activo: usuario.activo
    });
    
    // Clave opcional en edición
    this.usuarioForm.get('clave')?.clearValidators();
    this.usuarioForm.get('clave')?.updateValueAndValidity();
    
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.usuarioForm.reset();
    this.usuarioSeleccionado = null;
  }

  guardarUsuario(): void {
    if (this.usuarioForm.invalid) {
      Object.keys(this.usuarioForm.controls).forEach(key => {
        this.usuarioForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.cargando = true;
    const formData = this.usuarioForm.value;
    if (this.modoEdicion && this.usuarioSeleccionado) {
      this.usuarioService.actualizarUsuario(this.usuarioSeleccionado.idUsuario!, formData)
        .subscribe({
          next: (response) => {
            if (response.exitoso) {
              AlertService.success(response.mensaje);
              this.cargarUsuarios();
              this.cerrarModal();
            } else {
              AlertService.error(response.mensaje);
            }
            this.cargando = false;
          },
          error: (error) => {
            console.error('Error al actualizar:', error);
            alert('Error al actualizar el usuario');
            this.cargando = false;
          }
        });
    } else {
      this.usuarioService.crearUsuario(formData).subscribe({
        next: (response) => {
          if (response.exitoso) {
            AlertService.success(response.mensaje);
            this.cargarUsuarios();
            this.cerrarModal();
          } else {
            AlertService.error(response.mensaje);
          }
          this.cargando = false;
        },
        error: (error) => {
          console.error('Error al crear:', error);
          this.cargando = false;
        }
      });
    }
  }

  cambiarEstado(usuario: UsuarioSoap): void {
    const nuevoEstado = !usuario.activo;
    const mensaje = nuevoEstado ? '¿Activar este usuario?' : '¿Desactivar este usuario?';
    
    if (confirm(mensaje)) {
      this.usuarioService.cambiarEstadoUsuario(usuario.idUsuario!, nuevoEstado)
        .subscribe({
          next: (response) => {
            if (response.exitoso) {
              AlertService.success(response.mensaje);
              console.log('respondi: '+response);
              this.cargarUsuarios();
            } else {
              AlertService.error(response.mensaje);
              console.log('RESPONSE COMPLETO:', response);
              console.log('JSON:', JSON.stringify(response));

            }
          },
          error: (error) => {
            console.error('Error:', error);
            alert('Error al cambiar el estado');
          }
        });
    }
  }

  obtenerNombreRol(idRol: number): string {
    const rol = this.roles.find(r => r.id === idRol);
    return rol ? rol.nombre : 'Desconocido';
  }

  getNombreCompleto(usuario: UsuarioSoap): string {
    return `${usuario.nombres} ${usuario.apePaterno} ${usuario.apeMaterno}`;
  }
}