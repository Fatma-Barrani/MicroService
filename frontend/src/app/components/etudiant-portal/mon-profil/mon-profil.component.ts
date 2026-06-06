import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SelectedEtudiantService } from '../../../services/selected-etudiant.service';
import { EtudiantService } from '../../../services/etudiant.service';
import { Etudiant } from '../../../models/etudiant.model';

@Component({
  selector: 'app-mon-profil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mon-profil.component.html',
  styleUrls: ['./mon-profil.component.css']
})
export class MonProfilComponent implements OnInit {
  etudiant: Etudiant | null = null;
  isEditing = false;
  editModel: Etudiant = this.emptyEtudiant();
  message = '';
  isLoading = false;

  constructor(
    private sel: SelectedEtudiantService,
    private etu: EtudiantService
  ) {}

  ngOnInit(): void {
    this.etu.getAll().subscribe((students: Etudiant[]) => {
      if (students.length > 0 && !this.sel.getSelected()) {
        this.sel.setSelected(students[0]);
      }
    });

    this.sel.selected$.subscribe((s: Etudiant | null) => {
      this.etudiant = s;
      if (s) {
        this.editModel = { ...s };
      }
      this.isEditing = false;
      this.message = '';
    });
  }

  startEdit() {
    if (this.etudiant) {
      this.editModel = { ...this.etudiant };
      this.isEditing = true;
    }
  }

  cancelEdit() {
    this.isEditing = false;
    this.message = '';
  }

  save() {
    if (!this.editModel.id) {
      this.message = 'ID manquant – impossible de sauvegarder.';
      return;
    }

    this.isLoading = true;
    const payload: Etudiant = {
      ...this.editModel,
      moyenneGenerale: this.etudiant!.moyenneGenerale
    };

    this.etu.update(payload.id!, payload).subscribe({
      next: (updated: Etudiant) => {
        this.sel.setSelected(updated);
        this.message = '✅ Profil mis à jour avec succès !';
        this.isEditing = false;
        this.isLoading = false;
        setTimeout(() => this.message = '', 3000);
      },
      error: () => {
        this.message = '❌ Erreur lors de la mise à jour.';
        this.isLoading = false;
      }
    });
  }

  private emptyEtudiant(): Etudiant {
    return { nom: '', prenom: '', email: '', filiere: '', anneeInscription: 0, moyenneGenerale: 0 };
  }
}
