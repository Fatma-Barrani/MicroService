import {
  Body,
  Controller,
  Post,
  Get,
  UseGuards,
  Request,
} from '@nestjs/common';

import { AuthService } from './auth.service';
import { RegisterDto } from '../DTO/RegisterDto';
import { LoginDto } from '../DTO/LoginDto';

import { JwtAuthGuard } from './guards/jwt-auth/jwt-auth.guard';

import { RolesGuard } from './guards/roles.guard';
import { Roles } from './decorators/roles.decorator';
import { Role } from './enums/role.enum';

@Controller('auth')
export class AuthController {
  constructor(private authService: AuthService) {}

  // =========================
  // REGISTER
  // =========================
  @Post('register')
  register(@Body() dto: RegisterDto) {
    return this.authService.register(dto);
  }

  // =========================
  // LOGIN
  // =========================
  @Post('login')
  login(@Body() dto: LoginDto) {
    return this.authService.login(dto);
  }

  // =========================
  // PROFILE (JWT REQUIRED)
  // =========================
  @UseGuards(JwtAuthGuard)
  @Get('profile')
  getProfile(@Request() req) {
    return req.user;
  }

  // =========================
  // ADMIN ONLY
  // =========================
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles(Role.ADMIN)
  @Get('admin')
  adminRoute() {
    return { message: 'Bienvenue ADMIN' };
  }

  // =========================
  // ETUDIANT ONLY
  // =========================
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles(Role.ETUDIANT)
  @Get('etudiant')
  etudiantRoute() {
    return { message: 'Bienvenue ETUDIANT' };
  }

  // =========================
  // ENSEIGNANT ONLY
  // =========================
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles(Role.ENSEIGNANT)
  @Get('enseignant')
  enseignantRoute() {
    return { message: 'Bienvenue ENSEIGNANT' };
  }
}
