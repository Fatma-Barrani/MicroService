import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Cours } from '../../../models/cours.model';
import { CoursService } from '../../../services/cours.service';

@Component({
  selector: 'app-cours-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cours-list.component.html',
  styleUrls: ['./cours-list.component.css']
})
export class CoursListComponent implements OnInit {

  cours: Cours[] = [];
  searchQuery = '';
  activeFilter = 'all';

  constructor(private coursService: CoursService, private router: Router) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.coursService.getAll().subscribe({
      next: (data) => { this.cours = data; },
      error: (err) => { console.error('Erreur chargement cours', err); }
    });
  }

  get filtered(): Cours[] {
    return this.cours.filter(c => {
      const matchFilter = this.activeFilter === 'all' || (c.niveau?.startsWith(this.activeFilter) ?? false);
      const q = this.searchQuery.toLowerCase();
      const matchSearch = !q ||
        c.titre.toLowerCase().includes(q) ||
        c.categorie.toLowerCase().includes(q) ||
        (c.niveau ?? '').toLowerCase().includes(q);
      return matchFilter && matchSearch;
    });
  }

  countByNiveau(niveau: string): number {
    return this.cours.filter(c => c.niveau === niveau).length;
  }

  countByNiveauPrefix(prefix: string): number {
    return this.cours.filter(c => c.niveau?.startsWith(prefix)).length;
  }

  niveaux(): string[] {
    return [...new Set(this.cours.map(c => c.niveau).filter(Boolean))] as string[];
  }

  get totalPlaces(): number {
    return this.cours.reduce((sum, c) => sum + (c.nbPlaces ?? 0), 0);
  }

  add(): void {
    this.router.navigate(['/cours/add']);
  }

  edit(cours: Cours): void {
    this.router.navigate(['/cours/edit', cours.id]);
  }

  delete(id?: number): void {
    if (!id) return;
    if (confirm('Voulez-vous vraiment supprimer ce cours ?')) {
      this.coursService.delete(id).subscribe({
        next: () => { this.cours = this.cours.filter(c => c.id !== id); },
        error: (err) => { console.error('Erreur suppression', err); }
      });
    }
  }
}
