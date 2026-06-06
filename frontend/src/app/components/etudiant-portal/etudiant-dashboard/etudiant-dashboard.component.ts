import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart } from 'chart.js/auto';
import { EtudiantService } from '../../../services/etudiant.service';
import { ExamenService } from '../../../services/examen.service';
import { SelectedEtudiantService } from '../../../services/selected-etudiant.service';
import { Etudiant } from '../../../models/etudiant.model';
import { Cours } from '../../../models/cours.model';

@Component({
  selector: 'app-etudiant-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './etudiant-dashboard.component.html',
  styleUrls: ['./etudiant-dashboard.component.css']
})
export class EtudiantDashboardComponent implements OnInit, AfterViewInit {
  // ========== COURS ==========
  coursList: Cours[] = [];
  filteredCours: Cours[] = [];
  searchTerm: string = '';
  selectedCategorie: string = '';
  categories: string[] = [];
  isLoadingCours = true;
  errorMessageCours = '';

  // ========== STATS & EXAMENS ==========
  selected: Etudiant | null = null;
  allStudents: Etudiant[] = [];
  examens: any[] = [];
  rank: number = 0;
  generalAverage: number = 0;
  stats: any = {};

  @ViewChild('chart') chartRef!: ElementRef<HTMLCanvasElement>;
  private chart: Chart | null = null;

  constructor(
    private etudiantService: EtudiantService,
    private examenService: ExamenService,
    private selectedService: SelectedEtudiantService
  ) {}

  ngOnInit(): void {
    this.loadCours();
    this.loadExamens();
    this.loadStudents();
    this.loadStatistics();
    this.subscribeToSelectedStudent();
  }

  loadStatistics(): void {
    this.etudiantService.getStatistiques().subscribe({
      next: (data) => {
        this.stats = data || {};
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des statistiques', err);
      }
    });
  }

  ngAfterViewInit(): void {
    this.buildChart();
  }

  // ========== COURS ==========
  loadCours(): void {
    this.isLoadingCours = true;
    this.etudiantService.getAllCours().subscribe({
      next: (data) => {
        this.coursList = data;
        this.filteredCours = data;
        this.categories = [...new Set(data.map((c: any) => c.categorie).filter((c: string) => c))];
        this.isLoadingCours = false;
      },
      error: (err) => {
        console.error('Erreur chargement cours', err);
        this.errorMessageCours = 'Impossible de charger les cours.';
        this.isLoadingCours = false;
      }
    });
  }

  filterCours(): void {
    let temp = this.coursList;
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      temp = temp.filter(c =>
        c.titre.toLowerCase().includes(term) ||
        (c.description && c.description.toLowerCase().includes(term))
      );
    }
    if (this.selectedCategorie) {
      temp = temp.filter(c => c.categorie === this.selectedCategorie);
    }
    this.filteredCours = temp;
  }

  // ========== EXAMENS ==========
  loadExamens(): void {
    this.examenService.getExamens().subscribe({
      next: (data) => this.examens = data || [],
      error: () => this.examens = []
    });
  }

  inscrire(examenId: number): void {
    if (!this.selected?.id) {
      alert('Aucun étudiant sélectionné');
      return;
    }
    this.etudiantService.inscrireExamen(this.selected.id, examenId).subscribe({
      next: () => alert('✅ Inscription réussie !'),
      error: () => alert('❌ Erreur lors de l\'inscription')
    });
  }

  // ========== STATISTIQUES ==========
  loadStudents(): void {
    this.etudiantService.getAll().subscribe({
      next: (data: Etudiant[]) => {
        const uniqueMap = new Map<number, Etudiant>();
        data.forEach(e => { if (e.id) uniqueMap.set(e.id, e); });
        this.allStudents = Array.from(uniqueMap.values());
        this.generalAverage = this.computeGeneralAverage(this.allStudents);
        if (this.allStudents.length > 0 && !this.selectedService.getSelected()) {
          this.selectedService.setSelected(this.allStudents[0]);
        }
      },
      error: err => console.error(err)
    });
  }

  subscribeToSelectedStudent(): void {
    this.selectedService.selected$.subscribe((student: Etudiant | null) => {
      this.selected = student;
      if (student && this.allStudents.length) {
        this.rank = this.allStudents.findIndex(e => e.id === student.id) + 1;
      }
      this.buildChart();
    });
  }

  computeGeneralAverage(students: Etudiant[]): number {
    if (!students.length) return 0;
    const total = students.reduce((sum, s) => sum + (s.moyenneGenerale || 0), 0);
    return Math.round((total / students.length) * 100) / 100;
  }

  buildChart(): void {
    if (!this.chartRef || !this.selected || !this.allStudents.length) return;

    if (this.chart) this.chart.destroy();

    this.chart = new Chart(this.chartRef.nativeElement, {
      type: 'bar',
      data: {
        labels: ['Ma moyenne', 'Moyenne promo'],
        datasets: [{
          label: 'Moyenne /20',
          data: [this.selected.moyenneGenerale, this.generalAverage],
          backgroundColor: ['#2563eb', '#94a3b8'],
          borderRadius: 8
        }]
      },
      options: {
        responsive: true,
        scales: { y: { beginAtZero: true, max: 20 } }
      }
    });
  }
}
