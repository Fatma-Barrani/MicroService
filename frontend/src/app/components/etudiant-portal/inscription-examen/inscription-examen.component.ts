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
    // Charger les examens
    this.examenService.getExamens().subscribe((data: any[]) => this.examens = data || []);

    // Charger les étudiants et forcer une sélection par défaut si besoin
    this.etuService.getAll().subscribe((students: Etudiant[]) => {
      if (students.length > 0 && !this.sel.getSelected()) {
        this.sel.setSelected(students[0]);
      }
    });

    // S'abonner aux changements de sélection
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
      next: (_: any) => {
        this.message = 'Inscription réussie !';
        setTimeout(() => this.message = '', 3000);
      },
      error: (err: any) => {
        this.message = 'Erreur lors de l\'inscription.';
        console.error(err);
      }
    });
  }
}