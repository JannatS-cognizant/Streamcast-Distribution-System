export interface RoleDef {
  id: number;
  name: string;
}

export interface UserDef {
  id: number;
  name: string;
  email: string;
  role: string | null;
  pending: boolean;        // ← ADD this line
}

export interface UserCreate {
  name: string;
  email: string;
  password: string;
  roleId: number;
}
