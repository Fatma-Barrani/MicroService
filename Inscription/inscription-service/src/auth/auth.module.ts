import { Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { HttpModule } from '@nestjs/axios';  // ⬅️ AJOUTER CET IMPORT

import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';

import { UsersModule } from '../users/users.module';

@Module({
  imports: [

    UsersModule, // ⭐ IMPORTANT
      HttpModule,
    JwtModule.register({
      secret: 'SECRET_KEY',
      signOptions: { expiresIn: '1d' },
    }),
  ],

  controllers: [AuthController],
  providers: [AuthService],
})
export class AuthModule {}