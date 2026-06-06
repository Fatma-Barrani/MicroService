// frontend/src/app/models/cours.model.ts
export interface Cours {
  id?: number;
  titre: string;
  description?: string;
  categorie: string;
  dureeHeures?: number;
  nbPlaces?: number;
  enseignantId?: number;
  niveau?: string;
  enseignantNom?: string;
  enseignantPrenom?: string;
}
