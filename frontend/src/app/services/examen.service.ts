import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Examen } from '../models/examen';

@Injectable({
  providedIn: 'root'
})
export class ExamenService {

 
  private apiUrl = 'http://api-gateway:8956/api/examens';

  constructor(private http: HttpClient) { }

getExamens(): Observable<Examen[]> {
  return this.http.get<Examen[]>(`${this.apiUrl}/GetAllExamens`);
}
getExamensByEnseignant(enseignantId: number): Observable<Examen[]> {
  return this.http.get<Examen[]>(`${this.apiUrl}/enseignant/${enseignantId}`);
}
deleteExamen(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/DeleteExamen/${id}`);
}
 getExamenById(id: number): Observable<Examen> {
    return this.http.get<Examen>(`${this.apiUrl}/ExamenById/${id}`);
  }
  createExamen(examen: Examen): Observable<any> {
    return this.http.post(`${this.apiUrl}/createExamen`, examen);
  }
    updateExamen(id: number, examen: Examen): Observable<any> {
    return this.http.put(`${this.apiUrl}/UpdateExamenBYId/${id}`, examen);
  }
  getCountEnCours(): Observable<number> {
  return this.http.get<number>(`${this.apiUrl}/count/en-cours`);
}

getCountEnAttente(): Observable<number> {
  return this.http.get<number>(`${this.apiUrl}/count/en-attente`);
}

getCountCorrige(): Observable<number> {
  return this.http.get<number>(`${this.apiUrl}/count/terminee`);
}
getTotalExamens(): Observable<number> {
  return this.http.get<number>(`${this.apiUrl}/count/total`);
}
}
