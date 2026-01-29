import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AlertIziToast } from '../../util/iziToastAlert.service';

//servicios a consumir

import { DistritoService } from '../service/distrito.service';
import { AuthService } from '../service/auth.service';

//modelos a utilizar

import { Distrito } from '../../shared/model/distrito.model';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../cliente/service/user.service';
import { Usuario } from '../../shared/model/usuario.model';
import { ResultadoResponse } from '../../shared/response/resultadoResponse.models';

interface SignupData {
  fullName: string;
  email: string;
  password: string;
  confirmPassword: string;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  registerForm!: FormGroup; //para el formulario de registro
  loginForm!: FormGroup; // para el formulario de login
  distritos: Distrito[] = [];
  errorMessage: string = '';
  successMessage: string = '';
  isLoginMode: boolean = true;

  showPassword: boolean = false;
  showConfirmPassword: boolean = false;
  showLoginPassword: boolean = false;

  constructor(
    private distritoService: DistritoService,
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
  ) {
    this.initForm();

    this.loginForm = this.formBuilder.group({
      correo: ['', [Validators.required, Validators.email]],
      clave: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    this.cargarDistritos();
  }

  private initForm(): void {
    this.registerForm = this.formBuilder.group({
      nombres: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      apePaterno: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      apeMaterno: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      correo: [
        '',
        [Validators.required, Validators.email, Validators.maxLength(50)],
      ],
      clave: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(225),
        ],
      ],
      tipoDoc: ['', Validators.required],
      nroDoc: ['', [Validators.required, Validators.pattern(/^[0-9]{8}$/)]],
      direccion: ['', [Validators.required, Validators.maxLength(50)]],
      idDistrito: ['', [Validators.required]],
      telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{9}$/)]],
    });
  }

  cargarDistritos(): void {
    this.distritoService.listarDistritos().subscribe({
      next: (data: Distrito[]) => {
        this.distritos = data;
        console.log('Distritos cargados:', this.distritos);
      },
      error: (error) => {
        console.error('Error al cargar distritos:', error);
      },
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  toggleLoginPassword(): void {
    this.showLoginPassword = !this.showLoginPassword;
  }

  switchToLogin(): void {
    this.isLoginMode = true;
  }

  switchToSignup(): void {
    this.isLoginMode = false;
  }

  //REGISTRO DE UNA CUENTA NUEVA
  onSignUp(): void {
    if (this.registerForm.invalid) {
      this.validateSignupForm();

      this.errorMessage = 'Completar todos los campos correctamente.';
      return;
    }

    this.isLoginMode = true;
    this.errorMessage = '';
    this.successMessage = 'Registro exitoso. Por favor, inicia sesión.';

    const formValue = this.registerForm.value;
    const usuario: Usuario = {
      nombres: formValue.nombres,
      apePaterno: formValue.apePaterno,
      apeMaterno: formValue.apeMaterno,
      correo: formValue.correo,
      clave: formValue.clave,
      tipoDoc: formValue.tipoDoc,
      nroDoc: formValue.nroDoc,
      direccion: formValue.direccion,
      distrito: {
        idDistrito: parseInt(formValue.idDistrito),
      },
      telefono: formValue.telefono,
    };

    console.log('Datos de registro:', usuario);

    this.authService.register(usuario).subscribe({
      next: (resultado: ResultadoResponse) => {
        this.isLoginMode = false;

        if (resultado.valor) {
          AlertIziToast.success(
            'Registro Exitoso!',
            'Por favor, inicia sesión con tus credenciales.',
          );
          this.registerForm.reset();

          setTimeout(() => {
            this.isLoginMode = true;
          }, 1000);
        } else {
          AlertIziToast.error(
            'Error en el Registro',
            resultado.mensaje || 'No se pudo registrar el usuario.',
          );
        }
      },
      error: (error) => {
        this.isLoginMode = false;
        console.error('Error en el registro:', error);

        if (error.status === 400) {
          AlertIziToast.error(
            'Error en el Registro',
            error.error?.mensaje || 'Datos inválidos proporcionados.',
          );
        } else if (error.status === 409) {
          AlertIziToast.error(
            'Error en el registro',
            error.error?.mensaje || 'El correo ya está registrado.',
          );
        } else if (error.status === 500) {
          AlertIziToast.error(
            'Error en el registro',
            'Error del servidor. Intenta nuevamente más tarde.',
          );
        } else {
          AlertIziToast.error(
            'Error en el registro',
            error.error?.mensaje ||
              'Error al registrar usuario. Intenta nuevamente.',
          );
        }
      },
    });
  }

  //LOGIN CON CORREO Y CONTRASEÑA
  onLogin(): void {
    console.log('Formulario login válido?', this.loginForm.valid);
    console.log('Datos enviados:', this.loginForm.value);

    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (response: any) => {
          console.log('Respuesta login:', response);
          const token = response.token;
          this.authService.saveToken(token);
          console.log('Token guardado: ', token);

          this.authService.getUsuario().subscribe({
            next: (usuario) => {
              this.userService.setUser(usuario);
              console.log('Usuario logueado: ', usuario);

              const rolUsuario = this.userService.getRol();
              console.log('Rol del usuario: ', rolUsuario);
              const descripcion = usuario.rol.descripcion;
              console.log('Descripción del rol: ', descripcion);
              AlertIziToast.success(
                'Login Exitoso!',
                `Bienvendido ${usuario.nombres}!`,
              );
              switch (descripcion) {
                case 'A':
                  this.router.navigate(['/admin']);
                  break;
                case 'C':
                  this.router.navigate(['/cliente']);
                  break;
                case 'R':
                  this.router.navigate(['/recepcionista']);
                  break;
                case 'M':
                  this.router.navigate(['/mesero']);
                  break;
                default:
                  this.router.navigate(['/auth/login']);
              }
            },

            error: (error) => {
              console.error('Error al obtener el usuario: ', error);
              AlertIziToast.error(
                'Error al obtener datos del usuario',
                'Error de Autenticación',
              );
            },
          });
        },
        error: (error) => {
          console.error('Error LOGIN:', error);
          console.error('Status:', error.status);
          console.error('Body:', error.error);
          this.errorMessage = 'Correo o contraseña incorrectos.';
          AlertIziToast.error(this.errorMessage, 'Error de Autenticación');
        },
      });
    }
  }

  //AUTENTICACION CON GOOGLE
  onGoogleAuth(): void {
    console.log('Google authentication initiated');

    //autenticacion con google

    alert('Autenticación con Google en proceso...');
  }

  //AUTENTICACION CON GITHUB
  onGithubAuth(): void {
    console.log('GitHub authentication initiated');

    //autenticacion con gituh

    alert('Autenticación con GitHub en proceso...');
  }

  //Implmentar metodo para recuperar contraseña
  onForgotPassword(): void {
    const email = prompt(
      'Por favor ingresa tu email para recuperar tu contraseña:',
    );

    if (email) {
      console.log('Password reset requested for:', email);

      alert('Se ha enviado un correo de recuperación a ' + email);
    }
  }

  private validateSignupForm(): boolean {
    const {
      nombres,
      apePaterno,
      apeMaterno,
      nroDoc,
      direccion,
      telefono,
      idDistrito,
      correo,
      clave,
    } = this.registerForm.value;

    if (!nombres || !correo || !clave) {
      alert('Por favor complete todos los campos');
      return false;
    }

    if (!this.isValidEmail(correo)) {
      alert('Por favor ingrese un email válido');
      return false;
    }

    if (clave.length < 6) {
      alert('La contraseña debe tener al menos 6 caracteres');
      return false;
    }

    return true;
  }

  //VALIDACION DEL FORMULARIO DE LOGIN
  private validateLoginForm(): boolean {
    const correo = this.loginForm.get('correo')?.value;
    const clave = this.loginForm.get('clave')?.value;

    if (!correo || !clave) {
      alert('Por favor complete todos los campos');
      return false;
    }

    if (!this.isValidEmail(correo)) {
      alert('Por favor ingrese un email válido');
      return false;
    }

    return true;
  }

  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  private resetSignupForm(): void {
    this.registerForm.reset({
      nombres: '',
      apePaterno: '',
      apeMaterno: '',
      correo: '',
      clave: '',
      nroDoc: '',
      direccion: '',
      idDistrito: '',
      telefono: '',
    });
    this.showPassword = false;
    this.showConfirmPassword = false;
  }

  private resetLoginForm(): void {
    this.loginForm.reset({
      email: '',
      password: '',
    });
    this.showLoginPassword = false;
  }
}
