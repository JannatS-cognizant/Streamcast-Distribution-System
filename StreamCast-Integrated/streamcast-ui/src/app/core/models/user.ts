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
  email: string;
  role: Role;
  token: string;
  expiresAt: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}
