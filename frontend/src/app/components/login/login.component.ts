import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginForm: FormGroup;
  showPassword = false;
  isLoading = false;
  submitted = false;
  loginError = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8)]],
      rememberMe: [false],

    });
  }

  /* ── Helpers ── */
  isInvalid(field: string): boolean {
    const ctrl = this.loginForm.get(field);
    return !!(ctrl && ctrl.invalid && (ctrl.dirty || ctrl.touched));
  }

  isValid(field: string): boolean {
    const ctrl = this.loginForm.get(field);
    return !!(ctrl && ctrl.valid && (ctrl.dirty || ctrl.touched));
  }

  getError(field: string): string {
    const ctrl = this.loginForm.get(field);
    if (!ctrl || !ctrl.errors) return '';
    if (ctrl.errors['required'])
      return field === 'username'
        ? 'Username is required.'
        : 'Password is required.';
    if (ctrl.errors['email'])
      return 'Enter a valid email address.';
    if (ctrl.errors['minlength']) return 'Password must be at least 8 characters.';
    return '';
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

onSubmit(): void {

  this.loginForm.markAllAsTouched();

  if (this.loginForm.invalid) {
    return;
  }

  this.isLoading = true;
  this.loginError = '';
  this.submitted = false;

  this.authService.login(this.loginForm.value)
    .subscribe({

      next: (response) => {

        this.isLoading = false;
        this.submitted = true;

        // ✅ Save token
        localStorage.setItem('token', response.access_token);

        // ✅ Save user
        localStorage.setItem('user', JSON.stringify(response.user));

        console.log('Login successful', response);

        // ─────────────────────────────
        // ✅ ROLE-BASED REDIRECTION
        // ─────────────────────────────

        const role = response?.user?.role;

        if (role === 'ADMIN') {
          this.router.navigate(['/dashboard']);
        }

        else if (role === 'ENSEIGNANT') {
          this.router.navigate(['/listExamen']);
        }

        else {
          // default (ETUDIANT or others)
          this.router.navigate(['/listExamen']);
        }
      },
      

      error: (err) => {

        this.isLoading = false;
        this.submitted = false;

        this.loginError =
          err?.error?.message ||
          'Invalid username or password';
      }
    });
}
}