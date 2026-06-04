import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cours } from '../models/cours.model';

@Injectable({ providedIn: 'root' })
export class CoursService {
  private apiUrl = 'http://localhost:8082/api/cours';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Cours[]> {
    return this.http.get<Cours[]>(`${this.apiUrl}/all`);
  }

  getById(id: number): Observable<Cours> {
    return this.http.get<Cours>(`${this.apiUrl}/${id}`);
  }

  create(cours: Cours): Observable<Cours> {
    return this.http.post<Cours>(`${this.apiUrl}/create`, cours);
  }

  update(id: number, cours: Cours): Observable<Cours> {
    return this.http.put<Cours>(`${this.apiUrl}/update/${id}`, cours);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }

  searchByCategorie(categorie: string): Observable<Cours[]> {
    return this.http.get<Cours[]>(`${this.apiUrl}/search/categorie`, { params: { categorie } });
  }
}
