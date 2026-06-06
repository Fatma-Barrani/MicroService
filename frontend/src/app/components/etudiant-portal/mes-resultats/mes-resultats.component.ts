import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart } from 'chart.js/auto';
import { Etudiant } from '../../../models/etudiant.model';
import { EtudiantService } from '../../../services/etudiant.service';
import { SelectedEtudiantService } from '../../../services/selected-etudiant.service';
import { environment } from '../../../../environments/environment';

interface Participation {
  id: number;
  examenId: number;
  titre: string;
  matiere: string;
  date: string;
  note: number | null;
  coefficient: number;
}

@Component({
  selector: 'app-mes-resultats',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mes-resultats.component.html',
  styleUrls: ['./mes-resultats.component.css']
})
export class MesResultatsComponent implements OnInit, AfterViewInit {
  allStudents: Etudiant[] = [];
  selected: Etudiant | null = null;
  participations: Participation[] = [];
  filteredParticipations: Participation[] = [];
  studentRank = 0;
  generalAverage = 0;
  moyennePersonnelle = 0;

  filterMatiere = '';
  filterNoteMin: number | null = null;
  filterNoteMax: number | null = null;
  matieresList: string[] = [];
  useMock = environment.mockParticipations;

  private mockParticipations: Participation[] = [
    { id: 1, examenId: 1, titre: 'Examen Java', matiere: 'Java', date: '2025-01-15', note: 15.5, coefficient: 2 },
    { id: 2, examenId: 2, titre: 'Examen Spring', matiere: 'Spring', date: '2025-02-10', note: 12.0, coefficient: 1.5 },
    { id: 3, examenId: 3, titre: 'Examen Angular', matiere: 'Angular', date: '2025-03-05', note: null, coefficient: 2 }
  ];

  @ViewChild('histChart') histRef!: ElementRef;
  @ViewChild('donutChart') donutRef!: ElementRef;
  private histChart: Chart | null = null;
  private donutChart: Chart | null = null;

  constructor(
    private etuService: EtudiantService,
    private sel: SelectedEtudiantService
  ) {}

  ngOnInit(): void {
    this.etuService.getAll().subscribe({
      next: (data: Etudiant[]) => {
        const uniqueMap = new Map<number, Etudiant>();
        data.forEach(e => { if (e.id) uniqueMap.set(e.id, e); });
        this.allStudents = Array.from(uniqueMap.values()).sort((a,b) => b.moyenneGenerale - a.moyenneGenerale);
        this.generalAverage = this.computeGeneralAverage(this.allStudents);
        this.updateSelected();
        this.buildCharts();
      }
    });
    this.sel.selected$.subscribe(() => this.updateSelected());
  }

  ngAfterViewInit(): void {
    this.buildCharts();
  }

  updateSelected(): void {
    this.selected = this.sel.getSelected();
    if (this.selected && this.allStudents.length) {
      this.studentRank = this.allStudents.findIndex(e => e.id === this.selected?.id) + 1;
      this.moyennePersonnelle = this.selected.moyenneGenerale;
      this.loadParticipations(this.selected.id!);
    }
  }

  loadParticipations(etudiantId: number): void {
    if (this.useMock) {
      this.participations = [...this.mockParticipations];
      this.matieresList = [...new Set(this.participations.map(p => p.matiere))];
      this.applyFilters();
      return;
    }

    this.etuService.getParticipationsByEtudiant(etudiantId).subscribe({
      next: (data: Participation[]) => {
        this.participations = data;
        this.matieresList = [...new Set(data.map(p => p.matiere))];
        this.applyFilters();
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des participations :', err);
        this.participations = [];
        this.matieresList = [];
        this.applyFilters();
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.participations];
    if (this.filterMatiere) {
      filtered = filtered.filter(p => p.matiere === this.filterMatiere);
    }
    if (this.filterNoteMin !== null) {
      filtered = filtered.filter(p => p.note !== null && p.note >= this.filterNoteMin!);
    }
    if (this.filterNoteMax !== null) {
      filtered = filtered.filter(p => p.note !== null && p.note <= this.filterNoteMax!);
    }
    this.filteredParticipations = filtered;
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  resetFilters(): void {
    this.filterMatiere = '';
    this.filterNoteMin = null;
    this.filterNoteMax = null;
    this.applyFilters();
  }

  computeGeneralAverage(students: Etudiant[]): number {
    if (!students.length) return 0;
    const total = students.reduce((sum, s) => sum + s.moyenneGenerale, 0);
    return Math.round((total / students.length) * 100) / 100;
  }

  buildCharts(): void {
    if (!this.histRef || !this.donutRef || !this.allStudents.length) return;

    const bins = [0,0,0,0,0];
    this.allStudents.forEach(e => {
      const m = e.moyenneGenerale;
      if (m < 8) bins[0]++;
      else if (m < 10) bins[1]++;
      else if (m < 12) bins[2]++;
      else if (m < 14) bins[3]++;
      else bins[4]++;
    });

    if (this.histChart) this.histChart.destroy();
    const ctxHist = this.histRef.nativeElement.getContext('2d');
    this.histChart = new Chart(ctxHist, {
      type: 'bar',
      data: {
        labels: ['<8', '8-10', '10-12', '12-14', '14+'],
        datasets: [{ label: "Nombre d'étudiants", data: bins, backgroundColor: '#2563eb', borderRadius: 8 }]
      },
      options: { responsive: true, maintainAspectRatio: false, scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } } }
    });

    const filieresMap = new Map<string, number>();
    this.allStudents.forEach(e => {
      const f = e.filiere;
      filieresMap.set(f, (filieresMap.get(f) || 0) + 1);
    });
    if (this.donutChart) this.donutChart.destroy();
    const ctxDonut = this.donutRef.nativeElement.getContext('2d');
    this.donutChart = new Chart(ctxDonut, {
      type: 'doughnut',
      data: {
        labels: Array.from(filieresMap.keys()),
        datasets: [{ data: Array.from(filieresMap.values()), backgroundColor: ['#2563eb','#3b82f6','#60a5fa','#93c5fd'] }]
      },
      options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right' } } }
    });
  }

  getBadgeClass(note: number | null): string {
    if (note === null) return 'badge-secondary';
    if (note >= 14) return 'badge-success';
    if (note >= 10) return 'badge-warning';
    return 'badge-danger';
  }

  formatNote(note: number | null): string {
    return note !== null ? note.toFixed(2) : 'Non noté';
  }
}
