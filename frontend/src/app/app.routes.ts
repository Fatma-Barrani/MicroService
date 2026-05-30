import { Routes } from '@angular/router';
import { EnseignantTableComponent } from './components/enseignant-table/enseignant-table.component';
import { ExamenListComponent } from './components/client/examen-list/examen-list.component';
import { ExamenEditComponent } from './components/client/examen-edit/examen-edit.component';
import { ExamenAddComponent } from './components/client/examen-add/examen-add.component';
import { EnseignantLayoutComponent } from './layouts/enseignant-layout/enseignant-layout.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';

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
];