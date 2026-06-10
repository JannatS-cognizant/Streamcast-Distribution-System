import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Title } from '../../core/models/catalog';

@Injectable({ providedIn: 'root' })
export class TitlesService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/titles`;

  list(): Observable<Title[]>            { return this.http.get<Title[]>(this.base); }
  get(id: number): Observable<Title>     { return this.http.get<Title>(`${this.base}/${id}`); }
  create(t: Title): Observable<Title>    { return this.http.post<Title>(this.base, t); }
  update(id: number, t: Title): Observable<Title> { return this.http.put<Title>(`${this.base}/${id}`, t); }
  delete(id: number): Observable<unknown> { return this.http.delete(`${this.base}/${id}`); }
}
