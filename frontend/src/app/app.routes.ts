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
    component: LoginComponent},


  {
    path: '',
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

  }
];