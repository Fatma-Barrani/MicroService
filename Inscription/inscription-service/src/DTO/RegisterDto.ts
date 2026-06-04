import { Role } from "../auth/enums/role.enum";

export class RegisterDto {

  email!: string;

  password!: string;

  role!: Role;

  idEtudiant?: string;

  idEnseignant?: string;
}