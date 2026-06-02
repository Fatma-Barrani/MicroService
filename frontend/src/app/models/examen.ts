export interface Examen {

  id?: number;

  titre: string;

  description: string;

  dateExamen: string;

  duree: number;

  coefficient: number;

  niveau: string;

  matiere: string;

  statut: string;

  enseignantId: number;

}