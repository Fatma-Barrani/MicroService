import { AuthService } from './auth.service';
import { RegisterDto } from '../DTO/RegisterDto';
import { LoginDto } from '../DTO/LoginDto';
import { Role } from './enums/role.enum';
export declare class AuthController {
    private authService;
    constructor(authService: AuthService);
    register(dto: RegisterDto): Promise<{
        message: string;
        user: {
            id: import("mongoose").Types.ObjectId;
            email: string;
            role: Role;
            idEtudiant: string | undefined;
            idEnseignant: string | undefined;
            username: string;
        };
    }>;
    login(dto: LoginDto): Promise<{
        keycloak_token: null;
        user: {
            email: string;
            role: Role;
            idEtudiant: string | undefined;
            idEnseignant: string | undefined;
            username: string;
        };
    }>;
    getProfile(req: any): any;
    adminRoute(): {
        message: string;
    };
    etudiantRoute(): {
        message: string;
    };
    enseignantRoute(): {
        message: string;
    };
}
