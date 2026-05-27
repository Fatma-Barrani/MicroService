export interface Enseignant {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  specialite: string;
  examensIds?: number[];
}