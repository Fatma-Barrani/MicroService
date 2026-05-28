import { Routes } from '@angular/router';
import { EnseignantTableComponent } from './components/enseignant-table/enseignant-table.component';
import { ExamenListComponent } from './components/client/examen-list/examen-list.component';
import { ExamenEditComponent } from './components/client/examen-edit/examen-edit.component';

export const routes: Routes = [

 
  {
    path: 'enseignants',
    component: EnseignantTableComponent
  },

  {
    path: 'listExamen',
    component: ExamenListComponent
  },
  {
  path: 'examens/edit/:id',
  component: ExamenEditComponent
}

];
