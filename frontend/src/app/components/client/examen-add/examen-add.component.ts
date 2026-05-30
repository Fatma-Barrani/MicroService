import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ExamenService } from '../../../services/examen.service';
import { Examen } from '../../../models/examen';

@Component({
  selector: 'app-examen-add',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './examen-add.component.html',
  styleUrls: ['./examen-add.component.css']
})
export class ExamenAddComponent {

  examen: Examen = {
    titre: '',
    description: '',
    dateExamen: '',
    duree: 60,
    coefficient: 1,
    niveau: '',
    matiere: '',
    statut: 'En attente',
    enseignantId: 1
  };

  niveaux = ['L1', 'L2', 'L3', 'M1', 'M2'];
  statuts = ['En attente', 'Planifié', 'En cours', 'Terminé'];

  isSubmitting = false;
  successMsg = '';
  errorMsg = '';

  constructor(
    private examenService: ExamenService,
    private router: Router
  ) {}

  onSubmit() {
    if (!this.isFormValid()) return;

    this.isSubmitting = true;
    this.successMsg = '';
    this.errorMsg = '';

    this.examenService.createExamen(this.examen).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMsg = 'Examen créé avec succès !';
        setTimeout(() => this.router.navigate(['/listExamen']), 1500);
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMsg = 'Une erreur est survenue. Veuillez réessayer.';
      }
    });
  }

  isFormValid(): boolean {
    return !!(
      this.examen.titre &&
      this.examen.matiere &&
      this.examen.niveau &&
      this.examen.dateExamen &&
      this.examen.duree > 0 &&
      this.examen.coefficient > 0
    );
  }

  goBack() {
    this.router.navigate(['/listExamen']);
  }
}