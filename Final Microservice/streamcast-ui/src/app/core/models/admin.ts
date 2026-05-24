export interface RoleDef {
  id: number;
  name: string;
}

export interface UserDef {
  id: number;
  name: string;
  email: string;
  username:string;
  role: string | null;
  requestedRole: string | null;
  emailVerified?: boolean;
  pending: boolean;
}

export interface UserCreate {
  name: string;
  email: string;
  username:string;
  password: string;
  roleId: number;
}
