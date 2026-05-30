import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Examen } from '../../../models/examen';
import { ExamenService } from '../../../services/examen.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-examen-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './examen-list.component.html',
  styleUrls: ['./examen-list.component.css']
})
export class ExamenListComponent implements OnInit {

  constructor(
    private examenService: ExamenService,
    private router: Router
  ) {}

  examens: Examen[] = [];
  searchQuery = '';
  activeFilter = 'all';
  isModalOpen = false;
  isEditMode = false;
  nbEnCours = 0;
nbEnAttente = 0;
nbCorrige = 0;
  currentExamen: Examen = this.initEmptyExamen();

  ngOnInit(): void {
    this.loadExamens();
   this.loadStats();
  }

  loadExamens(): void {
    this.examenService.getExamens().subscribe({
      next: (data) => { this.examens = data; },
      error: (err) => { console.error('Erreur chargement examens', err); }
    });
  }

  get filteredExamens() {
    return this.examens.filter(e => {
      const matchFilter = this.activeFilter === 'all' || e.statut === this.activeFilter;
      const q = this.searchQuery.toLowerCase();
      const matchSearch = !q ||
        e.titre.toLowerCase().includes(q) ||
        e.matiere.toLowerCase().includes(q) ||
        e.niveau.toLowerCase().includes(q);
      return matchFilter && matchSearch;
    });
  }

  countByStatus(statut: string): number {
    return this.examens.filter(e => e.statut === statut).length;
  }

  addExamen() {
    this.router.navigate(['/examens/add']);
    this.isEditMode = false;
    this.currentExamen = this.initEmptyExamen();
    this.isModalOpen = true;
  }

  openEditModal(examen: Examen) {
    this.isEditMode = true;
    this.currentExamen = { ...examen };
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  saveExamen() {
    if (!this.currentExamen.titre) return;
    if (this.isEditMode) {
      const index = this.examens.findIndex(e => e.id === this.currentExamen.id);
      if (index !== -1) this.examens[index] = { ...this.currentExamen };
    } else {
      this.currentExamen.id = Date.now();
      this.examens.push({ ...this.currentExamen });
    }
    this.closeModal();
  }

  deleteExamen(id?: number) {
    if (!id) return;
    if (confirm('Voulez-vous vraiment supprimer cet examen ?')) {
      this.examenService.deleteExamen(id).subscribe({
        next: () => { this.examens = this.examens.filter(e => e.id !== id); },
        error: (err) => { console.error('Erreur suppression', err); }
      });
    }
  }

  editExamen(examen: Examen) {
    this.router.navigate(['/examens/edit', examen.id]);
  }
loadStats(): void {

  this.examenService.getCountEnCours().subscribe({
    next: (data) => {
      this.nbEnCours = data;
    },
    error: (err) => {
      console.error('Erreur stats en cours', err);
    }
  });

  this.examenService.getCountEnAttente().subscribe({
    next: (data) => {
      this.nbEnAttente = data;
    },
    error: (err) => {
      console.error('Erreur stats en attente', err);
    }
  });

  this.examenService.getCountCorrige().subscribe({
    next: (data) => {
      this.nbCorrige = data;
    },
    error: (err) => {
      console.error('Erreur stats corrigé', err);
    }
  });

}
  private initEmptyExamen(): Examen {
    return {
      id: undefined,
      titre: '',
      description: '',
      dateExamen: '',
      duree: 0,
      coefficient: 1,
      niveau: '',
      matiere: '',
      statut: 'En attente',
      enseignantId: 1
    };
  }
}