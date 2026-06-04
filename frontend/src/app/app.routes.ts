import { Routes } from '@angular/router';
import { EnseignantTableComponent } from './components/enseignant-table/enseignant-table.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { InscriptionComponent } from './components/inscription/inscription.component';
import { LoginComponent } from './components/login/login.component';

export const routes: Routes = [

 
      {
    path: 'enseignants',
    component: EnseignantTableComponent
  },

    {
    path: 'dashboard',
    component: AdminDashboardComponent
  },

    {
    path: 'inscription',
    component: InscriptionComponent
  },

     {
    path: 'login',
    component: LoginComponent
  }
];
