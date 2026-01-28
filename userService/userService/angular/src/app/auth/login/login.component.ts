import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
interface SignupData {
  fullName: string;
  email: string;
  password: string;
  confirmPassword: string;
}

interface LoginData {
  email: string;
  password: string;
  rememberMe: boolean;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
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

  loginData: LoginData = {
    email: '',
    password: '',
    rememberMe: false,
  };

  constructor() {}

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

  onLogin(): void {
    if (this.validateLoginForm()) {
      console.log('Login submitted:', {
        email: this.loginData.email,
        rememberMe: this.loginData.rememberMe,
      });

      // aurtenticacion de login

      alert('¡Inicio de sesión exitoso! Bienvenido de vuelta');

      this.resetLoginForm();
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
    const { email, password } = this.loginData;

    if (!email || !password) {
      alert('Por favor complete todos los campos');
      return false;
    }

    if (!this.isValidEmail(email)) {
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
    this.loginData = {
      email: '',
      password: '',
      rememberMe: false,
    };
    this.showLoginPassword = false;
  }
}
