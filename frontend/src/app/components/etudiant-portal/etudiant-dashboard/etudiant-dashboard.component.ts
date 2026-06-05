import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart } from 'chart.js/auto';
import { Etudiant } from '../../../models/etudiant.model';
import { EtudiantService } from '../../../services/etudiant.service';
import { ExamenService } from '../../../services/examen.service';
import { SelectedEtudiantService } from '../../../services/selected-etudiant.service';
import { Examen } from '../../../models/examen';

interface ExamenAvecNote extends Examen {
  note?: number;
}

@Component({
  selector: 'app-etudiant-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './etudiant-dashboard.component.html',
  styleUrls: ['./etudiant-dashboard.component.css']
})
export class EtudiantDashboardComponent implements OnInit, AfterViewInit {
  selected: Etudiant | null = null;
  generalAverage = 0;
  allStudents: Etudiant[] = [];
  examens: ExamenAvecNote[] = [];
  private viewReady = false;

  @ViewChild('chart') chartRef!: ElementRef<HTMLCanvasElement>;
  private chart: Chart | null = null;

  constructor(
    private etuService: EtudiantService,
    private examenService: ExamenService,
    private sel: SelectedEtudiantService
  ) {}

  ngOnInit(): void {
    // Charger les examens
    this.examenService.getExamens().subscribe({
      next: (data: Examen[]) => {
        this.examens = data || [];
      },
      error: () => this.examens = []
    });

    // Charger les étudiants
    this.etuService.getAll().subscribe({
      next: (data: Etudiant[]) => {
        // Dédoublonnage par id
        const uniqueMap = new Map<number, Etudiant>();
        data.forEach(e => { if (e.id) uniqueMap.set(e.id, e); });
        this.allStudents = Array.from(uniqueMap.values());
        this.generalAverage = this.computeGeneralAverage(this.allStudents);

        // Sélectionner le premier étudiant si aucun n'est sélectionné
        if (this.allStudents.length > 0 && !this.sel.getSelected()) {
          this.sel.setSelected(this.allStudents[0]);
        }

        this.buildChart();
      },
      error: err => console.error(err)
    });

    // S'abonner aux changements de sélection
    this.sel.selected$.subscribe((s: Etudiant | null) => {
      this.selected = s;
      this.buildChart();
    });
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.buildChart();
  }

  get studentAverage(): number {
    return this.selected?.moyenneGenerale ?? 0;
  }

  get rank(): number {
    if (!this.selected) return 0;
    const sorted = [...this.allStudents].sort((a,b) => b.moyenneGenerale - a.moyenneGenerale);
    return sorted.findIndex(e => e.id === this.selected?.id) + 1;
  }

  private computeGeneralAverage(students: Etudiant[]): number {
    if (!students.length) return 0;
    const total = students.reduce((sum, s) => sum + (s.moyenneGenerale || 0), 0);
    return Math.round((total / students.length) * 100) / 100;
  }

  private buildChart(): void {
    if (!this.viewReady || !this.selected || !this.chartRef || !this.allStudents.length) return;

    const ctx = this.chartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.chart) this.chart.destroy();

    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Moyenne personnelle', 'Moyenne promo'],
        datasets: [{
          label: 'Moyenne',
          data: [this.studentAverage, this.generalAverage],
          backgroundColor: ['#1976d2', '#42a5f5'],
          borderRadius: 8
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        scales: {
          y: { beginAtZero: true, max: 20, title: { display: true, text: 'Moyenne /20' } }
        },
        plugins: { tooltip: { callbacks: { label: (ctx) => `${ctx.raw} / 20` } } }
      }
    });
  }

  inscrire(examenId: number) {
    if (!this.selected?.id) return;
    this.etuService.inscrireExamen(this.selected.id, examenId).subscribe({
      next: () => alert('✅ Inscription réussie !'),
      error: () => alert('❌ Erreur lors de l\'inscription')
    });
  }
}