import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Etudiant } from '../../../models/etudiant.model';
import { SelectedEtudiantService } from '../../../services/selected-etudiant.service';
import { EtudiantService } from '../../../services/etudiant.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-etudiant-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './etudiant-management.component.html',
  styleUrls: ['./etudiant-management.component.css']
})
export class EtudiantManagementComponent implements OnInit {
  etudiants: Etudiant[] = [];
  filteredEtudiants: Etudiant[] = [];
  uniqueFilieres: string[] = ['informatique', 'finance', 'business'];
  moyenneGenerale = 0;
  showModal = false;
  isEditMode = false;
  currentEtudiant: Etudiant = this.emptyEtudiant();
  message = '';
  searchTerm = '';
  isLoading = false;
  isSaving = false;   // ✅ protection anti-double-clic sur Ajouter/Modifier
  isDeleting = false; // ✅ protection anti-double-clic sur Supprimer

  constructor(
    private etudiantService: EtudiantService,
    private selectedService: SelectedEtudiantService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.isLoading = true;
    this.etudiantService.getAll().subscribe({
      next: (data: Etudiant[]) => {
        // Déduplication basée sur l'id
        const uniqueMap = new Map<number, Etudiant>();
        data.forEach(e => { if (e.id) uniqueMap.set(e.id, e); });
        this.etudiants = Array.from(uniqueMap.values());
        this.applyFilter();
        this.uniqueFilieres = this.computeUniqueFilieres(this.etudiants);
        this.moyenneGenerale = this.computeGeneralAverage(this.etudiants);
        this.isLoading = false;
      },
      error: (err) => {
        this.message = 'Erreur chargement étudiants';
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  applyFilter() {
    if (!this.searchTerm.trim()) {
      this.filteredEtudiants = [...this.etudiants];
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredEtudiants = this.etudiants.filter(e =>
        e.nom.toLowerCase().includes(term) ||
        e.prenom.toLowerCase().includes(term) ||
        e.email.toLowerCase().includes(term) ||
        e.filiere.toLowerCase().includes(term)
      );
    }
  }

  onSearch() {
    this.applyFilter();
  }

  private computeUniqueFilieres(students: Etudiant[]): string[] {
    const base: string[] = ['informatique', 'finance', 'business'];
    const filieres = students.map(e => e.filiere).filter(f => !!f);
    return Array.from(new Set([...base, ...filieres]));
  }

  private computeGeneralAverage(students: Etudiant[]): number {
    if (!students.length) return 0;
    const total = students.reduce((sum, s) => sum + (s.moyenneGenerale || 0), 0);
    return Math.round((total / students.length) * 100) / 100;
  }

  openAddModal() {
    this.isEditMode = false;
    this.currentEtudiant = this.emptyEtudiant();
    this.showModal = true;
    this.message = '';
  }

  openEditModal(etudiant: Etudiant) {
    this.isEditMode = true;
    this.currentEtudiant = { ...etudiant };
    this.showModal = true;
    this.message = '';
  }

  closeModal() {
    this.showModal = false;
    this.message = '';
    this.isSaving = false;
  }

  saveEtudiant() {
    if (!this.currentEtudiant.nom || !this.currentEtudiant.prenom || !this.currentEtudiant.email) {
      this.message = 'Veuillez remplir Nom, Prénom et Email';
      return;
    }

    // ✅ bloquer les doubles clics
    if (this.isSaving) return;
    this.isSaving = true;

    if (this.isEditMode && this.currentEtudiant.id) {
      this.etudiantService.update(this.currentEtudiant.id, this.currentEtudiant).subscribe({
        next: () => {
          this.closeModal();
          this.load();
          this.message = '✅ Étudiant mis à jour';
          setTimeout(() => this.message = '', 3000);
        },
        error: (err) => {
          this.message = '❌ Erreur mise à jour';
          this.isSaving = false;
          console.error(err);
        }
      });
    } else {
      this.etudiantService.create(this.currentEtudiant).subscribe({
        next: () => {
          this.closeModal();  // ✅ fermer le modal AVANT load() pour éviter double soumission
          this.load();
          this.message = '✅ Étudiant créé';
          setTimeout(() => this.message = '', 3000);
        },
        error: (err) => {
          this.message = '❌ Erreur création';
          this.isSaving = false;
          console.error(err);
        }
      });
    }
  }

  remove(e: Etudiant) {
    if (!e.id) return;
    if (this.isDeleting) return; // ✅ anti-double-clic
    if (!confirm(`Supprimer ${e.nom} ${e.prenom} ?`)) return;

    this.isDeleting = true;
    this.etudiantService.delete(e.id).subscribe({
      next: () => {
        this.isDeleting = false;
        this.load();
        this.message = '✅ Étudiant supprimé';
        setTimeout(() => this.message = '', 3000);
      },
      error: (err) => {
        this.isDeleting = false;
        this.message = '❌ Erreur suppression';
        console.error('Détail erreur suppression :', err);
      }
    });
  }

  inscrire(e: Etudiant) {
    const examenIdStr = prompt('Entrez l\'ID de l\'examen pour inscription:');
    if (!examenIdStr) return;
    const examenId = Number(examenIdStr);
    this.etudiantService.inscrireExamen(e.id!, examenId).subscribe({
      next: () => alert('Inscription réussie'),
      error: () => alert('Échec inscription')
    });
  }

  selectAsCurrent(e: Etudiant) {
    this.selectedService.setSelected(e);
    this.message = `Étudiant ${e.nom} sélectionné pour le portail étudiant`;
    setTimeout(() => this.message = '', 3000);
  }

  getMoyenneBadgeClass(value: number | undefined): string {
    if (value === undefined || value === null) return 'badge badge-secondary';
    if (value >= 14) return 'badge badge-success';
    if (value >= 10) return 'badge badge-warning';
    return 'badge badge-danger';
  }

  private emptyEtudiant(): Etudiant {
    return {
      nom: '',
      prenom: '',
      email: '',
      filiere: 'informatique',
      anneeInscription: new Date().getFullYear(),
      moyenneGenerale: 0
    };
  }

  goToStudentManagement(id?: number) {
    if (id) {
      window.location.href = `/admin/etudiants`;
    }
  }
}