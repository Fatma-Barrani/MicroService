import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

import { Role } from 'src/auth/enums/role.enum';

export type UserDocument = User & Document;

@Schema()
export class User {

  @Prop({ unique: true })
  email!: string;

  @Prop()
  password!: string;

  @Prop({ enum: Role, default: Role.ETUDIANT })
  role!: Role;

  @Prop()
  idEtudiant?: string;

  @Prop()
  idEnseignant?: string;
}

export const UserSchema =
  SchemaFactory.createForClass(User);