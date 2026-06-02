import { Component, OnInit } from '@angular/core';
import { ExamenService } from '../../../services/examen.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar-enseignant',
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-enseignant.component.html',
  styleUrl: './sidebar-enseignant.component.css'
})
export class SidebarEnseignantComponent implements OnInit {

 
  isCollapsed = false;

  enseignant = {
    nom: 'Amrani',
    prenom: 'Sara',
  };

  counts = {
    examens: 0,
    classes: 3,
    etudiants: 42,
  };

  get initiales(): string {
    return (this.enseignant.prenom[0] + this.enseignant.nom[0]).toUpperCase();
  }

  constructor(private examenService: ExamenService) {}

  ngOnInit(): void {
    this.examenService.getExamens().subscribe({
      next: (data) => { this.counts.examens = data.length; },
      error: (err) => { console.error(err); }
    });
  }

  toggle(): void {
    this.isCollapsed = !this.isCollapsed;
  }

}
