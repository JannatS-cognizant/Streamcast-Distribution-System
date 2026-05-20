import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CalendarSchedule, Conflict, CreateSchedule, ScheduleApiResponse } from '../../core/models/schedule';

@Injectable({ providedIn: 'root' })
export class SchedulesService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api`;

  calendar(start: string, end: string): Observable<CalendarSchedule[]> {
    const params = new HttpParams().set('start', start).set('end', end);
    return this.http.get<ScheduleApiResponse<CalendarSchedule[]>>(
      `${this.base}/schedules/calendar`, { params }
    ).pipe(map(r => r.data));
  }

  create(s: CreateSchedule): Observable<CalendarSchedule> {
    return this.http.post<ScheduleApiResponse<CalendarSchedule>>(
      `${this.base}/schedules`, s
    ).pipe(map(r => r.data));
  }

  conflictsFor(schedulesId: number): Observable<Conflict[]> {
    const params = new HttpParams().set('schedulesId', schedulesId);
    return this.http.get<ScheduleApiResponse<Conflict[]>>(
      `${this.base}/conflicts`, { params }
    ).pipe(map(r => r.data));
  }
}
