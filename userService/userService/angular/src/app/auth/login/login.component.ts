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

  isLoginMode: boolean = false;

  showPassword: boolean = false;
  showConfirmPassword: boolean = false;
  showLoginPassword: boolean = false;

  signupData: SignupData = {
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
  };

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

  onSignUp(): void {
    if (this.validateSignupForm()) {
      console.log('Sign up submitted:', {
        fullName: this.signupData.fullName,
        email: this.signupData.email,
      });

      // autenticacion de registro

      alert("¡Registro exitoso! Bienvenido a Koro-Food's Restaurant");

      this.resetSignupForm();
    }
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
                  this.router.navigate(['/admin/dashboard']);
                  break;
                case 'C':
                  this.router.navigate(['/cliente']);
                  break;
                case 'R':
                  this.router.navigate(['/recepcionista/index']);
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

  onGoogleAuth(): void {
    console.log('Google authentication initiated');

    //autenticacion con google

    alert('Autenticación con Google en proceso...');
  }

  onGithubAuth(): void {
    console.log('GitHub authentication initiated');

    //autenticacion con gituh

    alert('Autenticación con GitHub en proceso...');
  }

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
    const { fullName, email, password, confirmPassword } = this.signupData;

    if (!fullName || !email || !password || !confirmPassword) {
      alert('Por favor complete todos los campos');
      return false;
    }

    if (!this.isValidEmail(email)) {
      alert('Por favor ingrese un email válido');
      return false;
    }

    if (password.length < 6) {
      alert('La contraseña debe tener al menos 6 caracteres');
      return false;
    }

    if (password !== confirmPassword) {
      alert('Las contraseñas no coinciden');
      return false;
    }

    return true;
  }

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
    this.signupData = {
      fullName: '',
      email: '',
      password: '',
      confirmPassword: '',
    };
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
