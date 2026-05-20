export interface UsageSummary {
  totalViews: number;
  totalRevenue: number;
  totalRecords: number;
}

export interface UsageDetails {
  titleId: number;
  platform: string;
  date: string;
  views: number;
  revenue: number;
}

export interface UsageBreakdown {
  group: unknown;
  views: number;
  revenue: number;
}
