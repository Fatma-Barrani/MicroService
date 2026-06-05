import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Examen } from '../../models/examen';
import { ExamenService } from '../../services/examen.service';
import { SidebarComponent } from '../../layouts/sidebar/sidebar.component';
import { ExamenEditComponent } from '../client/examen-edit/examen-edit.component';

@Component({
  selector: 'app-list-exam-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, ExamenEditComponent],
  templateUrl: './list-exam-admin.component.html',
  styleUrl: './list-exam-admin.component.css'
})
export class ListExamAdminComponent implements OnInit {

  examens: Examen[] = [];
  showEditModal = false;
  selectedExamen: Examen | null = null;

  constructor(private examenService: ExamenService) {}

  ngOnInit(): void {
    this.loadExamens();
  }

  loadExamens(): void {
    this.examenService.getExamens().subscribe(data => {
      this.examens = data;
    });
  }

  openEditModal(examen: Examen): void {
    this.selectedExamen = { ...examen };
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedExamen = null;
  }

  onExamenUpdated(examenMisAJour: Examen): void {
    const index = this.examens.findIndex(e => e.id === examenMisAJour.id);
    if (index !== -1) {
      this.examens[index] = examenMisAJour;
    }
    this.closeEditModal();
  }

  // ✅ Suppression
  deleteExamen(id: number): void {
    if (!confirm('Confirmer la suppression de cet examen ?')) return;

    this.examenService.deleteExamen(id).subscribe({
      next: () => {
        this.examens = this.examens.filter(e => e.id !== id);
      },
      error: (err) => {
        console.error('Erreur suppression :', err);
        alert('Erreur lors de la suppression.');
      }
    });
  }
}