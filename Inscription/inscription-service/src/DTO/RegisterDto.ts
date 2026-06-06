import { Role } from '../auth/enums/role.enum';

export class RegisterDto {
  email!: string;
  password!: string;
  role!: Role;
  username!: string;

  // Champs optionnels pour la création automatique dans MS Étudiant
  nom?: string;
  prenom?: string;
  filiere?: string;
  anneeInscription?: number;
  moyenneGenerale?: number;

  // Optionnels pour liaisons futures
  idEtudiant?: string;

  idEnseignant?: string;
}
