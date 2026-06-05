import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Chart } from 'chart.js/auto';
import { EtudiantService } from '../../../services/etudiant.service';
import { Etudiant } from '../../../models/etudiant.model';

@Component({
  selector: 'app-admin-statistiques',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-statistiques.component.html',
  styleUrls: ['./admin-statistiques.component.css']
})
export class AdminStatistiquesComponent implements OnInit, AfterViewInit {
  totalEtudiants = 0;
  moyenneGenerale = 0;
  totalExamens = 0;
  totalEnseignants = 0;

  private allStudents: Etudiant[] = [];
  filteredStudents: Etudiant[] = [];

  searchTerm = '';
  sortColumn: keyof Etudiant = 'moyenneGenerale';
  sortDirection: 'asc' | 'desc' = 'desc';

  @ViewChild('histogramChart') histogramRef!: ElementRef;
  @ViewChild('donutChart') donutRef!: ElementRef;
  private histChart: Chart | null = null;
  private donutChart: Chart | null = null;

  constructor(
    private etudiantService: EtudiantService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  ngAfterViewInit(): void {}

  loadData(): void {
    this.etudiantService.getStatistiques().subscribe({
      next: (stats: any) => {
        this.totalEtudiants = stats.totalEtudiants || 0;
        this.moyenneGenerale = stats.moyenneGlobale || 0;
        this.totalExamens = stats.totalExamens || 0;
        this.totalEnseignants = stats.totalEnseignants || 0;
      },
      error: err => console.error(err)
    });

    this.etudiantService.getAll().subscribe({
      next: (data: Etudiant[]) => {
        const uniqueMap = new Map<number, Etudiant>();
        data.forEach(e => { if (e.id) uniqueMap.set(e.id, e); });
        this.allStudents = Array.from(uniqueMap.values());
        this.applyFilterAndSort();
        this.buildCharts();
      },
      error: err => console.error(err)
    });
  }

  applyFilterAndSort(): void {
    let filtered = this.allStudents;
    const term = this.searchTerm.toLowerCase().trim();
    if (term) {
      filtered = this.allStudents.filter(e =>
        e.nom.toLowerCase().includes(term) ||
        e.prenom.toLowerCase().includes(term) ||
        e.filiere.toLowerCase().includes(term)
      );
    }
    filtered.sort((a, b) => {
      let valA = a[this.sortColumn];
      let valB = b[this.sortColumn];
      if (valA === undefined) valA = '';
      if (valB === undefined) valB = '';
      if (typeof valA === 'string') valA = valA.toLowerCase();
      if (typeof valB === 'string') valB = valB.toLowerCase();
      if (valA < valB) return this.sortDirection === 'asc' ? -1 : 1;
      if (valA > valB) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
    this.filteredStudents = filtered;
  }

  onSearch(): void {
    this.applyFilterAndSort();
  }

  onSort(column: keyof Etudiant): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = (column === 'nom' || column === 'prenom' || column === 'filiere') ? 'asc' : 'desc';
    }
    this.applyFilterAndSort();
  }

  getSortIcon(column: keyof Etudiant): string {
    if (this.sortColumn !== column) return 'ti ti-arrows-up-down';
    return this.sortDirection === 'asc' ? 'ti ti-arrow-up' : 'ti ti-arrow-down';
  }

  buildCharts(): void {
    if (!this.histogramRef || !this.donutRef || !this.allStudents.length) return;

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
    const ctx1 = this.histogramRef.nativeElement.getContext('2d');
    this.histChart = new Chart(ctx1, {
      type: 'bar',
      data: {
        labels: ['<8', '8-10', '10-12', '12-14', '14+'],
        datasets: [{ label: "Nombre d'étudiants", data: bins, backgroundColor: '#2563eb', borderRadius: 8 }]
      },
      options: { responsive: true, scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } } }
    });

    const filieresMap = new Map<string, number>();
    this.allStudents.forEach(e => {
      const f = e.filiere;
      filieresMap.set(f, (filieresMap.get(f) || 0) + 1);
    });
    if (this.donutChart) this.donutChart.destroy();
    const ctx2 = this.donutRef.nativeElement.getContext('2d');
    this.donutChart = new Chart(ctx2, {
      type: 'doughnut',
      data: {
        labels: Array.from(filieresMap.keys()),
        datasets: [{ data: Array.from(filieresMap.values()), backgroundColor: ['#2563eb','#3b82f6','#60a5fa','#93c5fd'] }]
      },
      options: { responsive: true }
    });
  }

  getBadgeClass(value: number): string {
    if (value >= 14) return 'badge-success';
    if (value >= 10) return 'badge-warning';
    return 'badge-danger';
  }

  goToStudentManagement(): void {
    this.router.navigate(['/admin/etudiants']);
  }
}