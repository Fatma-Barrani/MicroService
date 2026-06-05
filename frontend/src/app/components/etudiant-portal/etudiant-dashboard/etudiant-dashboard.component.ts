import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EtudiantService } from '../../../services/etudiant.service';
import { Cours } from '../../../models/cours.model';

@Component({
  selector: 'app-etudiant-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './etudiant-dashboard.component.html',
  styleUrls: ['./etudiant-dashboard.component.css']
})
export class EtudiantDashboardComponent implements OnInit {
  coursList: Cours[] = [];
  filteredCours: Cours[] = [];
  searchTerm: string = '';
  selectedCategorie: string = '';
  categories: string[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(private etudiantService: EtudiantService) {}

  ngOnInit(): void {
    this.loadCours();
  }

  loadCours(): void {
    this.isLoading = true;
    this.etudiantService.getAllCours().subscribe({
      next: (data) => {
        this.coursList = data;
        this.filteredCours = data;
        this.categories = [...new Set(data.map(c => c.categorie).filter(c => c))];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur chargement cours', err);
        this.errorMessage = 'Impossible de charger les cours. Veuillez réessayer plus tard.';
        this.isLoading = false;
      }
    });
  }

  filterCours(): void {
    let temp = this.coursList;
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      temp = temp.filter(c =>
        c.titre.toLowerCase().includes(term) ||
        (c.description && c.description.toLowerCase().includes(term))
      );
    }
    if (this.selectedCategorie) {
      temp = temp.filter(c => c.categorie === this.selectedCategorie);
    }
    this.filteredCours = temp;
  }
}
