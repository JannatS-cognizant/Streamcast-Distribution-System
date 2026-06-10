import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, Contract } from '../../core/models/contract';

@Injectable({ providedIn: 'root' })
export class ContractsService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/contracts`;

  list(): Observable<Contract[]> {
    return this.http.get<ApiResponse<Contract[]>>(this.base).pipe(map(r => r.data));
  }
  get(id: number): Observable<Contract> {
    return this.http.get<ApiResponse<Contract>>(`${this.base}/${id}`).pipe(map(r => r.data));
  }
  create(c: Contract): Observable<Contract> {
    return this.http.post<ApiResponse<Contract>>(this.base, c).pipe(map(r => r.data));
  }
  update(id: number, c: Contract): Observable<Contract> {
    return this.http.put<ApiResponse<Contract>>(`${this.base}/${id}`, c).pipe(map(r => r.data));
  }
  delete(id: number): Observable<unknown> {
    return this.http.delete(`${this.base}/${id}`);
  }
}
