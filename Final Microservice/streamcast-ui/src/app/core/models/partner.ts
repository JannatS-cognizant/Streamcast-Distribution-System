export interface Partner {
  partnerId?: number;
  name: string;
  contactInfo: string;
  endpointDetailsNote: string;
  status: 'ACTIVE' | 'INACTIVE';
  titleId: number;
}
