import { Routes } from '@angular/router';
import { EnseignantTableComponent } from './components/enseignant-table/enseignant-table.component';
import { ExamenListComponent } from './components/client/examen-list/examen-list.component';
import { ExamenEditComponent } from './components/client/examen-edit/examen-edit.component';
import { EnseignantLayoutComponent } from './layouts/enseignant-layout/enseignant-layout.component';
import { ExamenAddComponent } from './components/client/examen-add/examen-add.component';

export const routes: Routes = [

  {
    path: 'enseignants',
    component: EnseignantTableComponent
  },

  // ✅ Toutes les routes enseignant sous le layout avec sidebar
  {
    path: '',
    component: EnseignantLayoutComponent,
    children: [
      { path: 'listExamen',       component: ExamenListComponent },
      { path: 'examens/edit/:id', component: ExamenEditComponent },
      { path: '',                 redirectTo: 'listExamen', pathMatch: 'full' },
      { path: 'examens/add', component: ExamenAddComponent },
    ]
  }

];