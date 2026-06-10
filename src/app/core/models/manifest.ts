export interface Manifest {
  id?: number;
  titleId: string;
  assetIdsJSON: string;
  destination: string;
  createdBy: string;
  createdAt?: string;
  status: string;
  partnerId: number;
}

export interface Attempt {
  result: 'SUCCESS' | 'FAILED' | string;
  details: string;
}

export interface AttemptRecord extends Attempt {
  attemptId?: number;
  manifestId?: number;
  attemptedAt?: string;
  methodNote?: string;
  retries?: number;
}

export interface Receipt {
  receivedBy: string;
  receiptURI: string;
}

export interface ReceiptRecord extends Receipt {
  receiptId?: number;
  manifestId?: number;
  receivedAt?: string;
  status?: string;
}
