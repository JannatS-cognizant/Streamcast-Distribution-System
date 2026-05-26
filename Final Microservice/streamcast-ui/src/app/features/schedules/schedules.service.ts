import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CalendarSchedule, Conflict, CreateSchedule, ScheduleApiResponse } from '../../core/models/schedule';

@Injectable({ providedIn: 'root' })
export class SchedulesService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api`;

  calendar(
    start: string, end: string,
    page = 0, size = 10,
    status?: string, platform?: string
  ): Observable<CalendarSchedule[]> {
    let params = new HttpParams()
      .set('start', start).set('end', end)
      .set('page', page).set('size', size);
    if (status)   params = params.set('status', status);
    if (platform) params = params.set('platform', platform);
    return this.http
      .get<ScheduleApiResponse<CalendarSchedule[]>>(`${this.base}/schedules/calendar`, { params })
      .pipe(map(r => r.data));
  }

  getById(id: number): Observable<CalendarSchedule> {
    return this.http
      .get<ScheduleApiResponse<CalendarSchedule>>(`${this.base}/schedules/${id}`)
      .pipe(map(r => r.data));
  }

  create(s: CreateSchedule): Observable<CalendarSchedule> {
    return this.http
      .post<ScheduleApiResponse<CalendarSchedule>>(`${this.base}/schedules`, s)
      .pipe(map(r => r.data));
  }

  update(id: number, s: CreateSchedule): Observable<CalendarSchedule> {
    return this.http
      .put<ScheduleApiResponse<CalendarSchedule>>(`${this.base}/schedules/${id}`, s)
      .pipe(map(r => r.data));
  }

  patch(id: number, fields: Record<string, unknown>): Observable<CalendarSchedule> {
    return this.http
      .patch<ScheduleApiResponse<CalendarSchedule>>(`${this.base}/schedules/${id}`, fields)
      .pipe(map(r => r.data));
  }

  delete(id: number): Observable<void> {
    return this.http
      .delete<ScheduleApiResponse<string>>(`${this.base}/schedules/${id}`)
      .pipe(map(() => void 0));
  }

  detectConflicts(id: number): Observable<string> {
    return this.http
      .post<ScheduleApiResponse<string>>(`${this.base}/schedules/${id}/detect-conflicts`, {})
      .pipe(map(r => r.message));
  }

  conflictsFor(schedulesId: number): Observable<Conflict[]> {
    const params = new HttpParams().set('schedulesId', schedulesId);
    return this.http
      .get<ScheduleApiResponse<Conflict[]>>(`${this.base}/conflicts`, { params })
      .pipe(map(r => r.data));
  }

  conflictById(id: number): Observable<Conflict> {
    return this.http
      .get<ScheduleApiResponse<Conflict>>(`${this.base}/conflicts/${id}`)
      .pipe(map(r => r.data));
  }

  resolveConflict(id: number): Observable<Conflict> {
    return this.http
      .put<ScheduleApiResponse<Conflict>>(`${this.base}/conflicts/${id}/resolve`, {})
      .pipe(map(r => r.data));
  }
}