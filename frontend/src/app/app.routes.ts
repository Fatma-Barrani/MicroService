import { Routes } from '@angular/router';
import { EnseignantTableComponent } from './components/enseignant-table/enseignant-table.component';
import { ExamenListComponent } from './components/client/examen-list/examen-list.component';
import { ExamenEditComponent } from './components/client/examen-edit/examen-edit.component';
import { ExamenAddComponent } from './components/client/examen-add/examen-add.component';
import { EnseignantLayoutComponent } from './layouts/enseignant-layout/enseignant-layout.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { EtudiantManagementComponent } from './components/admin/etudiant-management/etudiant-management.component';
import { SidebarEtudiantComponent } from './layouts/sidebar-etudiant/sidebar-etudiant.component';
import { EtudiantDashboardComponent } from './components/etudiant-portal/etudiant-dashboard/etudiant-dashboard.component';
import { MonProfilComponent } from './components/etudiant-portal/mon-profil/mon-profil.component';
import { MesResultatsComponent } from './components/etudiant-portal/mes-resultats/mes-resultats.component';
import { InscriptionExamenComponent } from './components/etudiant-portal/inscription-examen/inscription-examen.component';
import { MesEnseignantsComponent } from './components/etudiant-portal/mes-enseignants/mes-enseignants.component';
import { AdminStatistiquesComponent } from './components/admin/admin-statistiques/admin-statistiques.component';

export const routes: Routes = [

  // 👉 enseignants
  {
    path: 'enseignants',
    component: EnseignantTableComponent
  },

  // 👉 admin dashboard
  {
    path: 'dashboard',
    component: AdminDashboardComponent
  },

  // 👉 layout enseignant avec sous-routes examens
  {
    path: '',
    component: EnseignantLayoutComponent,
    children: [
      { path: 'listExamen', component: ExamenListComponent },
      { path: 'examens/edit/:id', component: ExamenEditComponent },
      { path: 'examens/add', component: ExamenAddComponent },
      { path: '', redirectTo: 'listExamen', pathMatch: 'full' }
    ]
  }
  ,
  // admin: gestion etudiants
  {
    path: 'admin/etudiants',
    component: EtudiantManagementComponent
  },
  {
    path: 'admin/statistiques',
    component: AdminStatistiquesComponent
  },

  // etudiant portal (layout + children)
  {
    path: 'etudiant',
    component: SidebarEtudiantComponent,
    children: [
      { path: 'dashboard', component: EtudiantDashboardComponent },
      { path: 'profil', component: MonProfilComponent },
      { path: 'resultats', component: MesResultatsComponent },
      { path: 'inscription', component: InscriptionExamenComponent },
      { path: 'enseignants', component: MesEnseignantsComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];