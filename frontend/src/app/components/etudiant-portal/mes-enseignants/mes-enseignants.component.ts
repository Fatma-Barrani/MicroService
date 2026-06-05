import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EnseignantService } from '../../../services/enseignant.service';
import { EtudiantService } from '../../../services/etudiant.service';
import { SelectedEtudiantService } from '../../../services/selected-etudiant.service';

@Component({
  selector: 'app-mes-enseignants',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mes-enseignants.component.html',
  styleUrls: ['./mes-enseignants.component.css']
})
export class MesEnseignantsComponent implements OnInit {
  enseignants: any[] = [];
  selectedEtudiant: any = null;

  constructor(
    private etuService: EtudiantService,
    private ensService: EnseignantService,
    private sel: SelectedEtudiantService
  ) {}

  ngOnInit(): void {
    // Forcer une sélection par défaut si nécessaire
    this.etuService.getAll().subscribe((students: any[]) => {
      if (students.length > 0 && !this.sel.getSelected()) {
        this.sel.setSelected(students[0]);
      }
    });

    // S'abonner à l'étudiant sélectionné pour rafraîchir la liste des enseignants
    this.sel.selected$.subscribe(() => {
      this.loadEnseignants();
    });
  }

  loadEnseignants() {
    // Utiliser le proxy Feign du MS Étudiant pour obtenir la liste des enseignants
    this.etuService.getEnseignants().subscribe({
      next: (data: any[]) => {
        this.enseignants = data || [];
      },
      error: () => {
        // Fallback : appeler directement le service Enseignant
        this.ensService.getAll().subscribe({
          next: (data: any[]) => this.enseignants = data || [],
          error: () => this.enseignants = []
        });
      }
    });
  }
}