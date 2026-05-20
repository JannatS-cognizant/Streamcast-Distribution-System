import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Manifest } from '../../core/models/manifest';

@Injectable({ providedIn: 'root' })
export class ManifestsService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/manifests`;

  list(): Observable<Manifest[]>          { return this.http.get<Manifest[]>(this.base); }
  get(id: number): Observable<Manifest>   { return this.http.get<Manifest>(`${this.base}/${id}`); }
  create(m: Manifest): Observable<Manifest> { return this.http.post<Manifest>(this.base, m); }
  recordAttempt(id: number, status: string): Observable<string> {
    return this.http.post(`${this.base}/${id}/attempts`, { status }, { responseType: 'text' });
  }
}
