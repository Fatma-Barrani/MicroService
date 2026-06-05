import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { SidebarComponent } from "../../layouts/sidebar/sidebar.component";
import { EnseignantService } from '../../services/enseignant.service';
import { Router } from '@angular/router';
import { ExamenService } from '../../services/examen.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent], // ✅ IMPORTANT
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent {

  constructor(private enseignantService: EnseignantService,
       private examenService: ExamenService,
          private router: Router) { }
  // STATIC DATA
  totalEnseignants = 0;

  totalEtudiants = 320;

  totalClasses = 18;

  totalExamens = 0;

  totalCours = 45;

  ngOnInit(): void {
    this.loadEnseignantCount();
    this.loadExamenCount();
  }

  loadEnseignantCount(): void {
  this.enseignantService.getCount().subscribe({
    next: (count) => {
      this.totalEnseignants = count;
    },
    error: (err) => {
      console.error('Error loading enseignants count', err);
      this.totalEnseignants = 0;
    }
  });
}
loadExamenCount(): void {
    this.examenService.getTotalExamens().subscribe({
      next:  (count) => this.totalExamens = count,
      error: (err)   => {
        console.error('Erreur chargement examens :', err);
        this.totalExamens = 0;
      }
    });
  }

goToAddExamen(): void {
  this.router.navigate(['/examens/add']);
}
goToAddEnseignant(): void {
  this.router.navigate(['/enseignants']);
}
  // ✅ ADD THIS
  recentActivity = [
    {
      text: 'Nouvel enseignant ajouté',
      time: 'Il y a 5 min',
      dotClass: 'dot-blue'
    },
    {
      text: 'Examen Java programmé',
      time: 'Il y a 20 min',
      dotClass: 'dot-green'
    },
    {
      text: 'Classe GL2 créée',
      time: 'Il y a 1 heure',
      dotClass: 'dot-purple'
    },
    {
      text: 'Cours Spring ajouté',
      time: 'Aujourd’hui',
      dotClass: 'dot-orange'
    }
  ];

}