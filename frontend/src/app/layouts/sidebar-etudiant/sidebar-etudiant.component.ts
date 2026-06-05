import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar-etudiant',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-etudiant.component.html',
  styleUrls: ['./sidebar-etudiant.component.css']
})
export class SidebarEtudiantComponent {}