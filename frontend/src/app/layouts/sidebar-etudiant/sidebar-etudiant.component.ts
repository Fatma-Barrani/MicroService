import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-sidebar-etudiant',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-etudiant.component.html',
  styleUrls: ['./sidebar-etudiant.component.css']
})
export class SidebarEtudiantComponent {
  prenom = '';
  nom = '';

  constructor(private router: Router) {
    if (typeof localStorage !== 'undefined') {
      const rawUser = localStorage.getItem('user');
      if (rawUser) {
        try {
          const user = JSON.parse(rawUser);
          this.prenom = user?.prenom || user?.firstName || '';
          this.nom = user?.nom || user?.lastName || '';
        } catch {
          this.prenom = '';
          this.nom = '';
        }
      }
    }
  }

  logout(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    this.router.navigate(['/login']);
  }
}