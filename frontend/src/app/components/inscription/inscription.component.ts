import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
  ReactiveFormsModule
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';

// ── Custom validator: passwords must match ────────────────────────────────────
function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password');
  const confirm = control.get('confirmPassword');
  if (password && confirm && password.value !== confirm.value) {
    return { passwordMismatch: true };
  }
  return null;
}

@Component({
  selector: 'app-inscription',
  standalone: true,
  templateUrl: './inscription.component.html',
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, RouterModule],
  styleUrls: ['./inscription.component.css']
})
export class InscriptionComponent implements OnInit, OnDestroy {

  signupForm!: FormGroup;
  isLoading = false;
  submitted = false;
  showPassword = false;
  showConfirm = false;
  successMessage = '';
  errorMessage = '';

  passwordStrength = 0;
  strengthLabel = '';

  readonly strengthColors = ['', 'weak', 'fair', 'good', 'strong'];

  private pwSub!: Subscription;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.signupForm = this.fb.group(
      {
        username: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        role: ['ETUDIANT', Validators.required],
        // ✅ AJOUT DES 4 CHAMPS POUR LE MICROSERVICE ÉTUDIANT
        nom: ['', Validators.required],
        prenom: ['', Validators.required],
        filiere: ['', Validators.required],
        anneeInscription: [new Date().getFullYear(), [Validators.required, Validators.min(2000), Validators.max(2030)]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required],
        idEtudiant: [''],
        idEnseignant: [''],
        terms: [false, Validators.requiredTrue],
      },
      { validators: passwordMatchValidator }
    );

    this.pwSub = this.signupForm.get('password')!.valueChanges
      .subscribe(val => this.updateStrength(val));
  }

  ngOnDestroy(): void {
    this.pwSub?.unsubscribe();
  }

  isInvalid(field: string): boolean {
    const c = this.signupForm.get(field);
    return !!(c && c.invalid && (c.touched || this.submitted));
  }

  isValid(field: string): boolean {
    const c = this.signupForm.get(field);
    return !!(c && c.valid && c.touched);
  }

  getError(field: string): string {
    const c = this.signupForm.get(field);
    if (!c || !c.errors) return '';
    if (c.errors['required']) return 'This field is required.';
    if (c.errors['email']) return 'Please enter a valid email address.';
    if (c.errors['minlength']) {
      return `Minimum ${c.errors['minlength'].requiredLength} characters required.`;
    }
    if (c.errors['min'] || c.errors['max']) return 'Enter a valid year (2000-2030).';
    return 'Invalid value.';
  }

  get showMismatchError(): boolean {
    return (
      this.signupForm.hasError('passwordMismatch') &&
      !!this.signupForm.get('confirmPassword')?.touched
    );
  }

  togglePassword(): void { this.showPassword = !this.showPassword; }
  toggleConfirm(): void { this.showConfirm = !this.showConfirm; }

  updateStrength(value: string): void {
    if (!value) { this.passwordStrength = 0; this.strengthLabel = ''; return; }
    let score = 0;
    if (value.length >= 8) score++;
    if (/[A-Z]/.test(value)) score++;
    if (/[0-9]/.test(value)) score++;
    if (/[^A-Za-z0-9]/.test(value)) score++;
    this.passwordStrength = score;
    this.strengthLabel = ['', 'Weak', 'Fair', 'Good', 'Strong'][score];
  }

  barClass(barIndex: number): string {
    const active = this.passwordStrength >= barIndex;
    if (!active) return 'bar';
    return `bar active level-${this.passwordStrength}`;
  }

  onSubmit(): void {
    this.submitted = true;
    this.successMessage = '';
    this.errorMessage = '';
    this.signupForm.markAllAsTouched();

    if (this.signupForm.invalid) {
      this.errorMessage = 'Please fix the errors in the form.';
      return;
    }

    const formValue = this.signupForm.value;

    const payload: any = {
      username: formValue.username,
      email: formValue.email,
      password: formValue.password,
      role: formValue.role,
      // ✅ AJOUT DES 4 CHAMPS DANS LE PAYLOAD
      nom: formValue.nom,
      prenom: formValue.prenom,
      filiere: formValue.filiere,
      anneeInscription: formValue.anneeInscription
    };

    if (formValue.role === 'ETUDIANT' && formValue.idEtudiant) {
      payload.idEtudiant = formValue.idEtudiant;
    }

    if (formValue.role === 'ENSEIGNANT' && formValue.idEnseignant) {
      payload.idEnseignant = formValue.idEnseignant;
    }

    this.isLoading = true;

    // Redirection immédiate après soumission (optimiste)
    if (formValue.role === 'ETUDIANT') {
      this.successMessage = '🎉 Account created successfully! Vous êtes redirigé vers votre espace étudiant.';
      setTimeout(() => {
        this.router.navigate(['/etudiant/dashboard']);
      }, 500);
    } else {
      this.successMessage = '✅ Account created successfully! Redirecting to login...';
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 500);
    }

    // Envoi du formulaire en arrière-plan (fire & forget)
    this.authService.register(payload).subscribe({
      next: (res) => {
        this.isLoading = false;
        // Store user data if needed
        if (res?.token) {
          localStorage.setItem('token', res.token);
        }
      },
      error: (err) => {
        this.isLoading = false;
        // L'utilisateur est déjà redirigé, juste log l'erreur
        console.error('Registration error:', err);
      }
    });
  }
}
