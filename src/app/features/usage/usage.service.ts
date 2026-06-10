import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../core/models/contract';
import { CreateUsage, UsageBreakdown, UsageDetails, UsageSummary } from '../../core/models/usage';

@Injectable({ providedIn: 'root' })
export class UsageService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/usage`;

  create(dto: CreateUsage): Observable<UsageDetails> {
    return this.http.post<ApiResponse<UsageDetails>>(`${this.base}`, dto).pipe(map(r => r.data));
  }
  update(id: number, dto: CreateUsage): Observable<UsageDetails> {
    return this.http.put<ApiResponse<UsageDetails>>(`${this.base}/${id}`, dto).pipe(map(r => r.data));
  }
  delete(id: number): Observable<void> {
    return this.http.delete<ApiResponse<string>>(`${this.base}/${id}`).pipe(map(() => void 0));
  }
  getByTitleId(titleId: number): Observable<UsageDetails[]> {
    return this.http.get<ApiResponse<UsageDetails[]>>(`${this.base}/title/${titleId}`).pipe(map(r => r.data));
  }
  summary(start: string, end: string): Observable<UsageSummary> {
    const params = new HttpParams().set('start', start).set('end', end);
    return this.http.get<ApiResponse<UsageSummary>>(`${this.base}/summary`, { params }).pipe(map(r => r.data));
  }
  details(start: string, end: string): Observable<UsageDetails[]> {
    const params = new HttpParams().set('start', start).set('end', end);
    return this.http.get<ApiResponse<UsageDetails[]>>(`${this.base}/details`, { params }).pipe(map(r => r.data));
  }
  breakdown(start: string, end: string, groupBy: string): Observable<UsageBreakdown[]> {
    const params = new HttpParams().set('start', start).set('end', end).set('groupBy', groupBy);
    return this.http.get<ApiResponse<UsageBreakdown[]>>(`${this.base}/breakdown`, { params }).pipe(map(r => r.data));
  }
}