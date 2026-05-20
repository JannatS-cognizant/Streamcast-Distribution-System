export interface ScheduleApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export interface CalendarSchedule {
  scheduleId: number;
  platform: string;
  startDateTime: string;
  endDateTime: string;
  status: string;
}

export interface CreateSchedule {
  titleId: number;
  contractId: number;
  platform: string;
  startDateTime: string;
  endDateTime: string;
  windowType: string;
}

export interface Conflict {
  conflictId: number;
  scheduleId1: number;
  scheduleId2: number;
  detectedAt: string;
  resolved: boolean;
}
