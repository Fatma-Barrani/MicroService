import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Cours } from '../../../models/cours.model';
import { CoursService } from '../../../services/cours.service';

@Component({
  selector: 'app-cours-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cours-edit.component.html',
  styleUrls: ['./cours-edit.component.css']
})
export class CoursEditComponent implements OnInit {

  cours: Cours = { titre: '', categorie: '' };
  isSubmitting = false;
  isLoading = true;
  successMsg = '';
  errorMsg = '';

niveaux = ['Licence 1', 'Licence 2', 'Licence 3', 'Licence', 'Master 1', 'Master 2', 'Master', 'PHD', 'Ingénieurie'];  categories = ['Informatique', 'Mathématiques', 'Physique', 'Langue', 'Gestion', 'Autre'];

  constructor(
    private coursService: CoursService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.coursService.getById(id).subscribe({
      next: (data) => { this.cours = data; this.isLoading = false; },
      error: (err) => { this.errorMsg = 'Cours introuvable.'; this.isLoading = false; console.error(err); }
    });
  }

  isFormValid(): boolean {
    return !!(this.cours.titre && this.cours.categorie && this.cours.niveau);
  }

  onSubmit(): void {
    if (!this.isFormValid() || !this.cours.id) return;
    this.isSubmitting = true;
    this.coursService.update(this.cours.id, this.cours).subscribe({
      next: () => {
        this.successMsg = 'Cours modifié avec succès !';
        setTimeout(() => this.router.navigate(['/cours']), 1200);
      },
      error: (err) => {
        this.errorMsg = 'Erreur lors de la modification.';
        this.isSubmitting = false;
        console.error(err);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/cours']);
  }
}
