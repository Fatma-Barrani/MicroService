import { BadRequestException, Injectable, UnauthorizedException } from '@nestjs/common';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';
import { UsersService } from '../users/users.service';


@Injectable()
export class AuthService {

  

  constructor(
    private usersService: UsersService,
    private jwtService: JwtService
  ) {}

 async register(dto: any) {
  try {
    const hashedPassword = await bcrypt.hash(dto.password, 10);

    const user = await this.usersService.create({
      ...dto,
      password: hashedPassword,
    });

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

  const access_token =
    this.jwtService.sign(payload);

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