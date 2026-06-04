import { Injectable, UnauthorizedException } from '@nestjs/common';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';
import { UsersService } from '../users/users.service';

@Injectable()
export class AuthService {

  constructor(
    private usersService: UsersService,
    private jwtService: JwtService
  ) {}

  // =========================
  // REGISTER
  // =========================
  async register(dto: any) {

    const hashedPassword =
      await bcrypt.hash(dto.password, 10);

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
  }
    };
  }

  // =========================
  // LOGIN
  // =========================
  async login(dto: any) {

    const user = await this.usersService.findByEmail(dto.email);

    if (!user) {
      throw new UnauthorizedException('Email incorrect');
    }

    const isMatch = await bcrypt.compare(
      dto.password,
      user.password
    );

    if (!isMatch) {
      throw new UnauthorizedException('Password incorrect');
    }

    const payload = {
      email: user.email,
      role: user.role,
      idEtudiant: user.idEtudiant,
      idEnseignant: user.idEnseignant,
    };

    return {
      access_token: this.jwtService.sign(payload),
    };
  }
}