export type ComplianceResult = 'PASS' | 'FAIL' | 'PENDING';

export interface ComplianceCheck {
  checkId?: number;
  contractId: number;
  scheduleId: number;
  result: ComplianceResult;
  notes: string;
  checkedAt?: string;
}

export interface ComplianceUpdate {
  result: ComplianceResult;
  notes: string;
}
