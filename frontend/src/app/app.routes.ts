import { Routes } from '@angular/router';
import { EnseignantTableComponent } from './components/enseignant-table/enseignant-table.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';

export const routes: Routes = [

 
      {
    path: 'enseignants',
    component: EnseignantTableComponent
  },

    {
    path: 'dashboard',
    component: AdminDashboardComponent
  }
];
