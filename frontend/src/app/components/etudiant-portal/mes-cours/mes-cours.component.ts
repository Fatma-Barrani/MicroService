import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EtudiantService } from '../../../services/etudiant.service';

@Component({
  selector: 'app-mes-cours',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mes-cours.component.html',
  styleUrls: ['./mes-cours.component.css']
})
export class MesCoursComponent implements OnInit {
  cours: any[] = [];
  filteredCours: any[] = [];
  categories: string[] = [];
  search = '';
  categorie = '';
  loading = true;
  error = '';

  constructor(private etudiantService: EtudiantService) {}

  ngOnInit(): void {
    this.loadCours();
  }

  loadCours(): void {
    this.loading = true;
    this.etudiantService.getAllCours().subscribe({
      next: (data) => {
        this.cours = data || [];
        this.categories = [...new Set(this.cours.map(item => item.categorie).filter(Boolean))];
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement des cours', err);
        this.error = 'Impossible de charger les cours pour le moment.';
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    let items = [...this.cours];
    const searchValue = this.search.trim().toLowerCase();
    if (searchValue) {
      items = items.filter(c =>
        `${c.titre || ''} ${c.description || ''}`.toLowerCase().includes(searchValue)
      );
    }
    if (this.categorie) {
      items = items.filter(c => c.categorie === this.categorie);
    }
    this.filteredCours = items;
  }
}
