import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Etudiant } from '../models/etudiant.model';

@Injectable({ providedIn: 'root' })
export class SelectedEtudiantService {
  private selectedSubject = new BehaviorSubject<Etudiant | null>(null);
  public selected$: Observable<Etudiant | null> = this.selectedSubject.asObservable();

  setSelected(etudiant: Etudiant | null) {
    this.selectedSubject.next(etudiant);
  }

  getSelected(): Etudiant | null {
    return this.selectedSubject.getValue();
  }
}
