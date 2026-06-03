import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Cours } from '../../../models/cours.model';
import { CoursService } from '../../../services/cours.service';

@Component({
  selector: 'app-cours-add',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cours-add.component.html',
  styleUrls: ['./cours-add.component.css']
})
export class CoursAddComponent {

  cours: Cours = { titre: '', categorie: '', niveau: '', description: '', dureeHeures: 0, nbPlaces: 0 };
  isSubmitting = false;
  successMsg = '';
  errorMsg = '';

  niveaux = ['Licence 1', 'Licence 2', 'Licence 3', 'Licence', 'Master 1', 'Master 2', 'Master', 'PHD', 'Ingénieurie'];
  categories = ['Informatique', 'Mathématiques', 'Physique', 'Langue', 'Gestion', 'Autre'];

  constructor(private coursService: CoursService, private router: Router) {}

  isFormValid(): boolean {
    return !!(this.cours.titre && this.cours.categorie && this.cours.niveau);
  }

  onSubmit(): void {
    if (!this.isFormValid()) return;
    this.isSubmitting = true;
    this.coursService.create(this.cours).subscribe({
      next: () => {
        this.successMsg = 'Cours créé avec succès !';
        setTimeout(() => this.router.navigate(['/cours']), 1200);
      },
      error: (err) => {
        this.errorMsg = 'Erreur lors de la création du cours.';
        this.isSubmitting = false;
        console.error(err);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/cours']);
  }
}
