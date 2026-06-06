import { BadRequestException, Injectable, UnauthorizedException } from '@nestjs/common';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';
import { UsersService } from '../users/users.service';
import { HttpService } from '@nestjs/axios';
import { firstValueFrom } from 'rxjs';

@Injectable()
export class AuthService {

  constructor(
    private usersService: UsersService,
    private jwtService: JwtService,
    private httpService: HttpService,
  ) {}

  async register(dto: any) {
    try {
      const hashedPassword = await bcrypt.hash(dto.password, 10);

      const user = await this.usersService.create({
        ...dto,
        password: hashedPassword,
      });

      // ==================================================
      // CRÉATION AUTOMATIQUE DE L'ÉTUDIANT DANS LE MICROSERVICE ÉTUDIANT
      // ==================================================
      if (user.role === 'ETUDIANT') {
        try {
          const etudiantData = {
            nom: dto.nom,
            prenom: dto.prenom,
            email: dto.email,
            filiere: dto.filiere || 'Non définie',
            anneeInscription: dto.anneeInscription || new Date().getFullYear(),
            moyenneGenerale: 0.0
          };

          const response = await firstValueFrom(
            this.httpService.post('http://localhost:8084/etudiants/addEtudiant', etudiantData)
          );

          if (response.data && response.data.id) {
            user.idEtudiant = response.data.id;
            await user.save();
          }

          console.log('✅ Étudiant créé dans le MS Étudiant, id=', response.data.id);
        } catch (error) {
          console.error('⚠️ Erreur appel MS Étudiant:', error.message);
          // Ne pas bloquer l'inscription
        }
      }

      return {
        success: true,
        message: 'User registered successfully',
        data: {
          id: user._id,
          email: user.email,
          role: user.role,
          idEtudiant: user.idEtudiant,
          idEnseignant: user.idEnseignant,
          username: user.username,
        }
      };

    } catch (error: any) {
      const message =
        error?.response?.data?.message ||
        error?.message ||
        'Registration failed';

      throw new BadRequestException(message);
    }
  }

  async login(dto: any) {
    const user = await this.usersService.findByUsername(dto.username);

    if (!user) {
      throw new UnauthorizedException('Username incorrect');
    }

    const isMatch = await bcrypt.compare(
      dto.password,
      user.password
    );

    if (!isMatch) {
      throw new UnauthorizedException('Password incorrect');
    }

    const payload = {
      sub: user._id,
      username: user.username,
      role: user.role,
    };

    const access_token = this.jwtService.sign(payload);

    return {
      access_token,
      user: {
        id: user._id,
        username: user.username,
        email: user.email,
        role: user.role,
        idEtudiant: user.idEtudiant,
        idEnseignant: user.idEnseignant,
      },
    };
  }
}