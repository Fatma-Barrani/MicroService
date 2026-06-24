import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  constructor(private router: Router, private authService: AuthService) { }

  isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  isEnseignant(): boolean {
    return this.authService.hasRole('ENSEIGNANT');
  }

  logout(): void {
    // Clear auth data (adapt to your auth strategy)
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    sessionStorage.clear();

    // Redirect to login
    this.router.navigate(['/login']);
  }
}
