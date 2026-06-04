import { Injectable, UnauthorizedException } from '@nestjs/common';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';
import { UsersService } from '../users/users.service';
import axios from 'axios';

@Injectable()
export class AuthService {

  private keycloakUrl = 'http://localhost:8080/realms/examen-realm/protocol/openid-connect/token';
  private clientId = 'examen-client';
  private clientSecret = 'D98AbjVetOcslqrQVVAgmXukybpdGEqZ';

  constructor(
    private usersService: UsersService,
    private jwtService: JwtService
  ) {}

  async register(dto: any) {
    const hashedPassword = await bcrypt.hash(dto.password, 10);
    const user = await this.usersService.create({
      ...dto,
      password: hashedPassword,
    });
    return {
      message: 'User created successfully',
      user: {
        id: user._id,
        email: user.email,
        role: user.role,
        idEtudiant: user.idEtudiant,
        idEnseignant: user.idEnseignant,
        username: user.username,
      }
    };
  }

  async login(dto: any) {

  const user = await this.usersService.findByUsername(dto.username);
  if (!user) throw new UnauthorizedException('Username incorrect');

  const isMatch = await bcrypt.compare(dto.password, user.password);
  if (!isMatch) throw new UnauthorizedException('Password incorrect');

  let keycloakToken = null;

  try {
    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('client_id', this.clientId);
    params.append('client_secret', this.clientSecret);
    params.append('username', user.username || dto.username);
    params.append('password', dto.password);

    const response = await axios.post(this.keycloakUrl, params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    });

    keycloakToken = response.data.access_token;

  } catch (error) {
    throw new UnauthorizedException('Keycloak login failed');
  }

  return {
    keycloak_token: keycloakToken,
    user: {
      email: user.email,
      role: user.role,
      idEtudiant: user.idEtudiant,
      idEnseignant: user.idEnseignant,
      username: user.username,
    }
  };
}
}