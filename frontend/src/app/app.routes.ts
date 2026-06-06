import { Routes } from '@angular/router';
import { EnseignantTableComponent } from './components/enseignant-table/enseignant-table.component';
import { ExamenListComponent } from './components/client/examen-list/examen-list.component';
import { ExamenEditComponent } from './components/client/examen-edit/examen-edit.component';
import { ExamenAddComponent } from './components/client/examen-add/examen-add.component';
import { EnseignantLayoutComponent } from './layouts/enseignant-layout/enseignant-layout.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';

import { InscriptionComponent } from './components/inscription/inscription.component';
import { LoginComponent } from './components/login/login.component';

import { CoursListComponent } from './components/cours/cours-list/cours-list.component';
import { CoursAddComponent } from './components/cours/cours-add/cours-add.component';
import { CoursEditComponent } from './components/cours/cours-edit/cours-edit.component';

// ✅ LAYOUTS
import { SidebarComponent } from './layouts/sidebar/sidebar.component';
import { SidebarEtudiantComponent } from './layouts/sidebar-etudiant/sidebar-etudiant.component';

// ✅ COMPOSANTS ÉTUDIANTS
import { EtudiantDashboardComponent } from './components/etudiant-portal/etudiant-dashboard/etudiant-dashboard.component';
import { MesCoursComponent } from './components/etudiant-portal/mes-cours/mes-cours.component';
import { MesResultatsComponent } from './components/etudiant-portal/mes-resultats/mes-resultats.component';
import { MesEnseignantsComponent } from './components/etudiant-portal/mes-enseignants/mes-enseignants.component';
import { MonProfilComponent } from './components/etudiant-portal/mon-profil/mon-profil.component';
import { InscriptionExamenComponent } from './components/etudiant-portal/inscription-examen/inscription-examen.component';
import { EtudiantManagementComponent } from './components/admin/etudiant-management/etudiant-management.component';
import { AdminStatistiquesComponent } from './components/admin/admin-statistiques/admin-statistiques.component';

export const routes: Routes = [

  // ========== AUTHENTIFICATION ==========
  { path: 'login', component: LoginComponent },
  { path: 'inscription', component: InscriptionComponent },

  // ========== ROUTES ENSEIGNANT (pour examen) ==========
  {
    path: 'enseignant',
    component: EnseignantLayoutComponent,
    children: [
      { path: 'listExamen', component: ExamenListComponent },
      { path: 'examens/edit/:id', component: ExamenEditComponent },
      { path: 'examens/add', component: ExamenAddComponent },
      { path: 'cours', component: CoursListComponent },
      { path: 'cours/add', component: CoursAddComponent },
      { path: 'cours/edit/:id', component: CoursEditComponent },
      { path: '', redirectTo: 'listExamen', pathMatch: 'full' }
    ]
  },

  // ========== ROUTES ADMIN / ENSEIGNANT ==========
  {
    path: '',
    component: SidebarComponent,
    children: [
      { path: 'enseignants', component: EnseignantTableComponent },
      { path: 'admin/etudiants', component: EtudiantManagementComponent },
      { path: 'admin/statistiques', component: AdminStatistiquesComponent },
      { path: 'cours', component: CoursListComponent },
      { path: 'cours/add', component: CoursAddComponent },
      { path: 'cours/edit/:id', component: CoursEditComponent },
      { path: 'examens', component: ExamenListComponent },
      { path: 'examens/add', component: ExamenAddComponent },
      { path: 'examens/edit/:id', component: ExamenEditComponent },
      { path: 'dashboard', component: AdminDashboardComponent },
      { path: '', redirectTo: 'enseignants', pathMatch: 'full' }
    ]
  },

  // ========== ROUTES ÉTUDIANT ==========
  {
    path: 'etudiant',
    component: SidebarEtudiantComponent,
    children: [
      { path: 'dashboard', component: EtudiantDashboardComponent },
      { path: 'cours', component: MesCoursComponent },
      { path: 'resultats', component: MesResultatsComponent },
      { path: 'enseignants', component: MesEnseignantsComponent },
      { path: 'profil', component: MonProfilComponent },
      { path: 'inscription', component: InscriptionExamenComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  // Redirection par défaut
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
