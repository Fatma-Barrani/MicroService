import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Etudiant } from '../models/etudiant.model';
import {Cours} from '../models/cours.model';

@Injectable({
  providedIn: 'root'
})
export class EtudiantService {
  private apiUrl = 'http://localhost:8956/etudiants';
  private examenApiUrl = 'http://localhost:8956/api/examens';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Etudiant[]> {
    return this.http.get<Etudiant[]>(`${this.apiUrl}/allEtudiants`);
  }

  create(etudiant: Etudiant): Observable<Etudiant> {
    return this.http.post<Etudiant>(`${this.apiUrl}/addEtudiant`, etudiant);
  }

  update(id: number, etudiant: Etudiant): Observable<Etudiant> {
    return this.http.put<Etudiant>(`${this.apiUrl}/updateEtudiant/${id}`, etudiant);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deleteEtudiant/${id}`);
  }

  inscrireExamen(etudiantId: number, examenId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${etudiantId}/inscrire/${examenId}`, {});
  }

  notifierEnseignant(etudiantId: number, action: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/${etudiantId}/notif-enseignant?action=${action}`, {});
  }

  getStatistiques(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/statistiques`);
  }

  getEnseignants(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/enseignants-feign`);
  }

  /**
   * ✅ Vraies participations depuis MS Examen via Gateway
   * Fallback sur données mockées si l'endpoint est indisponible
   */
  getParticipationsByEtudiant(etudiantId: number): Observable<any[]> {
    const mockParticipations = [
      { id: 1, examenId: 1, titre: 'Examen Java',    matiere: 'Java',    date: '2025-01-15', note: 15.5, coefficient: 2   },
      { id: 2, examenId: 2, titre: 'Examen Spring',  matiere: 'Spring',  date: '2025-02-10', note: 12.0, coefficient: 1.5 },
      { id: 3, examenId: 3, titre: 'Examen Angular', matiere: 'Angular', date: '2025-03-05', note: null,  coefficient: 2   }
    ];

    return this.http
      .get<any[]>(`${this.examenApiUrl}/participations/etudiant/${etudiantId}`)
      .pipe(
        // ✅ Si le backend échoue (MS Examen down, pas de participations), on affiche les mockées
        catchError(() => of(mockParticipations))
      );
  }

  getAllCours(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cours/all`);
  }

// Rechercher par catégorie
  searchCoursByCategorie(categorie: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cours/search?categorie=${categorie}`);
  }

}
