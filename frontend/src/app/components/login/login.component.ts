import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
 
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
  isLoading    = false;
  submitted    = false;
  loginError   = '';
 
  constructor(private fb: FormBuilder) {
    this.loginForm = this.fb.group({
      email:      ['', [Validators.required, Validators.email]],
      password:   ['', [Validators.required, Validators.minLength(8)]],
      rememberMe: [false]
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
    if (ctrl.errors['required'])  return field === 'email' ? 'Email is required.' : 'Password is required.';
    if (ctrl.errors['email'])     return 'Enter a valid email address.';
    if (ctrl.errors['minlength']) return 'Password must be at least 8 characters.';
    return '';
  }
 
  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
 
  onSubmit(): void {
    this.loginForm.markAllAsTouched();
    this.loginError = '';
 
    if (this.loginForm.invalid) return;
 
    this.isLoading = true;
 
    /* Simulate API call — replace with your real auth service */
    setTimeout(() => {
      this.isLoading = false;
      this.submitted = true;
      // this.authService.login(this.loginForm.value).subscribe(...)
    }, 1800);
  }
}