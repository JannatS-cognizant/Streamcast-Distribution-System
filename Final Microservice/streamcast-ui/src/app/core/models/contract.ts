export interface Contract {
  contractId?: number;
  titleId: number;
  territoryListJson: string;
  startDate: string;
  endDate: string;
  exclusivityFlag: boolean;
  termsSummary: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface ApiResponse<T> {
  message: string;
  data: T;
  success: boolean;
}
