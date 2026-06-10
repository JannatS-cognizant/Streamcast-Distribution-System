export type Role =
  | 'ADMIN'
  | 'CONTENT_OWNER'
  | 'RIGHTS_MANAGER'
  | 'SCHEDULER'
  | 'DISTRIBUTION_OPERATOR'
  | 'LEGAL_OFFICER'
  | 'PARTNER_ADMIN'
  | 'COMPLIANCE_OFFICER';

export interface CurrentUser {
  id?: number;
  name?: string;
  email: string;
  role: Role;
  emailVerified?: boolean;
  token: string;
  expiresAt: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  requestedRoleId: number;   // ← ADD this line (or change roleId to this)
}

export interface AuthUserDTO {
  id: number;
  name: string;
  email: string;
  role: string;
  emailVerified: boolean;
}

export interface LoginResponse {
  token: string;
  user: AuthUserDTO;
}
