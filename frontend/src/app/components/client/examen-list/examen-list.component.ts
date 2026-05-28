import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Examen } from '../../../models/examen';
import { ExamenService } from '../../../services/examen.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-examen-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './examen-list.component.html',
  styleUrls: ['./examen-list.component.css']
})
export class ExamenListComponent implements OnInit{
constructor(private examenService: ExamenService,
   private router: Router
) {}
  // LISTE
  examens: Examen[] = [];
 
  ngOnInit(): void {
  this.loadExamens();
}

loadExamens(): void {
  this.examenService.getExamens().subscribe({
    next: (data) => {
      this.examens = data;
    },
    error: (err) => {
      console.error('Erreur chargement examens', err);
    }
  });
}
  // MODAL
  isModalOpen = false;
  isEditMode = false;

  // FORM OBJECT
  currentExamen: Examen = this.initEmptyExamen();

  // STATS
  get totalExamens() {
    return this.examens.length;
  }

  // OPEN ADD
  openAddModal() {
    this.isEditMode = false;
    this.currentExamen = this.initEmptyExamen();
    this.isModalOpen = true;
  }

  // OPEN EDIT
  openEditModal(examen: Examen) {
    this.isEditMode = true;
    this.currentExamen = { ...examen };
    this.isModalOpen = true;
  }

  // CLOSE MODAL
  closeModal() {
    this.isModalOpen = false;
  }

  // SAVE
  saveExamen() {

    if (!this.currentExamen.titre) return;

    if (this.isEditMode) {

      const index = this.examens.findIndex(e => e.id === this.currentExamen.id);

      if (index !== -1) {
        this.examens[index] = { ...this.currentExamen };
      }

    } else {

      this.currentExamen.id = Date.now();
      this.examens.push({ ...this.currentExamen });

    }

    this.closeModal();
  }

  // DELETE
deleteExamen(id?: number) {
  if (!id) return;

  if (confirm('Voulez-vous vraiment supprimer cet examen ?')) {

    this.examenService.deleteExamen(id).subscribe({
      next: () => {
        // supprimer aussi côté UI
        this.examens = this.examens.filter(e => e.id !== id);
      },
      error: (err) => {
        console.error('Erreur suppression', err);
      }
    });

  }
}
editExamen(examen: Examen) {
  this.router.navigate(['/examens/edit', examen.id]);
}

  // INIT EMPTY OBJECT
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