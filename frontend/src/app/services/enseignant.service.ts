import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Enseignant } from '../models/enseignant.model';

@Injectable({
  providedIn: 'root'
})
export class EnseignantService {

  // ✅ USE API GATEWAY PORT HERE
  private apiUrl = 'http://localhost:8956/api/enseignants';

  constructor(private http: HttpClient) { }

  // GET ALL
  getAll(): Observable<Enseignant[]> {
    return this.http.get<Enseignant[]>(
      `${this.apiUrl}/ListEnseignant`
    );
  }

  // GET BY ID
  getById(id: number): Observable<Enseignant> {
    return this.http.get<Enseignant>(
      `${this.apiUrl}/getEnseignant/${id}`
    );
  }

  // CREATE
  create(data: Enseignant): Observable<Enseignant> {
    return this.http.post<Enseignant>(
      `${this.apiUrl}/addEnseignant`,
      data
    );
  }

  // UPDATE
  update(id: number, data: Enseignant): Observable<Enseignant> {
    return this.http.put<Enseignant>(
      `${this.apiUrl}/update/${id}`,
      data
    );
  }

  // DELETE
  delete(id: number): Observable<any> {
    return this.http.delete(
      `${this.apiUrl}/delete/${id}`
    );
  }

  // SYNC assign examen
  assignExamen(enseignantId: number, examenId: number) {
    return this.http.put(
      `${this.apiUrl}/${enseignantId}/assignExamen/${examenId}`,
      {}
    );
  }

  // ASYNC assign examen (RabbitMQ)
  assignExamenAsync(enseignantId: number, examenId: number) {
    return this.http.put(
      `${this.apiUrl}/${enseignantId}/assignExamenAsync/${examenId}`,
      {}
    );
  }

  // COUNT enseignants
  getCount(): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/countEnseignant`
    );
  }
}