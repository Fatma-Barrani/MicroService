import { JwtService } from '@nestjs/jwt';
import { UsersService } from '../users/users.service';
export declare class AuthService {
    private usersService;
    private jwtService;
    private keycloakUrl;
    private clientId;
    private clientSecret;
    constructor(usersService: UsersService, jwtService: JwtService);
    register(dto: any): Promise<{
        message: string;
        user: {
            id: import("mongoose").Types.ObjectId;
            email: string;
            role: import("./enums/role.enum").Role;
            idEtudiant: string | undefined;
            idEnseignant: string | undefined;
            username: string;
        };
    }>;
    login(dto: any): Promise<{
        keycloak_token: null;
        user: {
            email: string;
            role: import("./enums/role.enum").Role;
            idEtudiant: string | undefined;
            idEnseignant: string | undefined;
            username: string;
        };
    }>;
}
