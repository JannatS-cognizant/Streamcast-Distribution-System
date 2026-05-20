export interface RoleDef {
  id: number;
  name: string;
}

export interface UserDef {
  id: number;
  name: string;
  email: string;
  role: RoleDef | null;
  emailVerified?: boolean;
}

export interface UserCreate {
  name: string;
  email: string;
  password: string;
  roleId: number;
}
