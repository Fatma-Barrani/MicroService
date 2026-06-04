import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
  ReactiveFormsModule
} from '@angular/forms';
import { Subscription } from 'rxjs';

// ── Custom validator: passwords must match ────────────────────────────────────
function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password');
  const confirm  = control.get('confirmPassword');
  if (password && confirm && password.value !== confirm.value) {
    return { passwordMismatch: true };
  }
  return null;
}

@Component({
  selector: 'app-inscription',
  templateUrl: './inscription.component.html',
  imports: [CommonModule, ReactiveFormsModule],
  // FIX: was referencing .css — must match actual file extension (.scss)
  styleUrls: ['./inscription.component.css']
})
export class InscriptionComponent implements OnInit, OnDestroy {

  signupForm!: FormGroup;
  isLoading  = false;
  submitted  = false;
  showPassword = false;
  showConfirm  = false;

  passwordStrength = 0;
  strengthLabel    = '';

  // FIX: strength bar colours exposed as a simple array — used in template
  readonly strengthColors = ['', 'weak', 'fair', 'good', 'strong'];

  private pwSub!: Subscription;

  constructor(private fb: FormBuilder) {}

  // ── Lifecycle ───────────────────────────────────────────────────────────────

  ngOnInit(): void {
    this.signupForm = this.fb.group(
      {
        firstName:       ['', [Validators.required, Validators.minLength(2)]],
        lastName:        ['', [Validators.required, Validators.minLength(2)]],
        email:           ['', [Validators.required, Validators.email]],
        role:            ['', Validators.required],
        password:        ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required],
        terms:           [false, Validators.requiredTrue],
      },
      { validators: passwordMatchValidator }
    );

    this.pwSub = this.signupForm.get('password')!.valueChanges
      .subscribe(val => this.updateStrength(val));
  }

  ngOnDestroy(): void {
    this.pwSub?.unsubscribe();
  }

  // ── Helpers ─────────────────────────────────────────────────────────────────

  isInvalid(field: string): boolean {
    const c = this.signupForm.get(field);
    return !!(c && c.invalid && c.touched);
  }

  isValid(field: string): boolean {
    const c = this.signupForm.get(field);
    return !!(c && c.valid && c.touched);
  }

  getError(field: string): string {
    const c = this.signupForm.get(field);
    if (!c || !c.errors) return '';
    if (c.errors['required'])  return 'This field is required.';
    if (c.errors['email'])     return 'Please enter a valid email address.';
    if (c.errors['minlength']) {
      return `Minimum ${c.errors['minlength'].requiredLength} characters required.`;
    }
    return 'Invalid value.';
  }

  // FIX: show mismatch error only when confirmPassword is touched
  get showMismatchError(): boolean {
    return (
      this.signupForm.hasError('passwordMismatch') &&
      !!this.signupForm.get('confirmPassword')?.touched
    );
  }

  togglePassword(): void { this.showPassword = !this.showPassword; }
  toggleConfirm():  void { this.showConfirm  = !this.showConfirm;  }

  // ── Password strength ────────────────────────────────────────────────────────

  updateStrength(value: string): void {
    if (!value) { this.passwordStrength = 0; this.strengthLabel = ''; return; }
    let score = 0;
    if (value.length >= 8)           score++;
    if (/[A-Z]/.test(value))         score++;
    if (/[0-9]/.test(value))         score++;
    if (/[^A-Za-z0-9]/.test(value))  score++;
    this.passwordStrength = score;
    this.strengthLabel    = ['', 'Weak', 'Fair', 'Good', 'Strong'][score];
  }

  // FIX: helper used in template to compute bar CSS class cleanly
  barClass(barIndex: number): string {
    const active = this.passwordStrength >= barIndex;
    if (!active) return 'bar';
    return `bar active level-${this.passwordStrength}`;
  }

  // ── Submit ───────────────────────────────────────────────────────────────────

  onSubmit(): void {
    this.signupForm.markAllAsTouched();
    if (this.signupForm.invalid) return;

    this.isLoading = true;

    // Simulate API call — replace with real AuthService call
    setTimeout(() => {
      this.isLoading = false;
      this.submitted = true;
      console.log('Signup payload:', this.signupForm.value);
      // Example:
      // this.authService.signup(this.signupForm.value).subscribe({
      //   next: () => { this.submitted = true; this.isLoading = false; },
      //   error: err => { this.isLoading = false; /* handle */ }
      // });
    }, 2000);
  }
}
