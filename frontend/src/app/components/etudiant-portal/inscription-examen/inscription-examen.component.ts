import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExamenService } from '../../../services/examen.service';
import { EtudiantService } from '../../../services/etudiant.service';
import { SelectedEtudiantService } from '../../../services/selected-etudiant.service';
import { Etudiant } from '../../../models/etudiant.model';

@Component({
  selector: 'app-inscription-examen',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inscription-examen.component.html',
  styleUrls: ['./inscription-examen.component.css']
})
export class InscriptionExamenComponent implements OnInit {
  examens: any[] = [];
  selectedEtudiant: Etudiant | null = null;
  message = '';

  constructor(
    private examenService: ExamenService,
    private etuService: EtudiantService,
    private sel: SelectedEtudiantService
  ) {}

  ngOnInit(): void {
    this.examenService.getExamens().subscribe((data: any[]) => this.examens = data || []);
    this.etuService.getAll().subscribe((students: Etudiant[]) => {
      if (students.length > 0 && !this.sel.getSelected()) {
        this.sel.setSelected(students[0]);
      }
    });
    this.sel.selected$.subscribe((s: Etudiant | null) => {
      this.selectedEtudiant = s;
    });
  }

  inscrire(examenId: number) {
    if (!this.selectedEtudiant?.id) {
      alert('Aucun étudiant sélectionné. Veuillez d’abord sélectionner un étudiant dans l’espace admin.');
      return;
    }
    this.etuService.inscrireExamen(this.selectedEtudiant.id, examenId).subscribe({
      next: () => {
        this.message = 'Inscription réussie !';
        setTimeout(() => this.message = '', 3000);
      },
      error: () => {
        this.message = 'Erreur lors de l\'inscription.';
        setTimeout(() => this.message = '', 3000);
      }
    });
  }
}
