import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Asset, Metadata } from '../../core/models/catalog';

@Injectable({ providedIn: 'root' })
export class AssetsService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api`;

  listAssets(titleId: number): Observable<Asset[]> {
    return this.http
      .get<Asset[]>(`${this.base}/titles/${titleId}/assets`)
      .pipe(catchError(() => of([])));
  }
  createAsset(titleId: number, a: Omit<Asset, 'id' | 'titleId' | 'titleName'>): Observable<Asset> {
    return this.http.post<Asset>(`${this.base}/assets/${titleId}`, a);
  }

  listMetadata(titleId: number): Observable<Metadata[]> {
    return this.http
      .get<Metadata[]>(`${this.base}/titles/${titleId}/metadata`)
      .pipe(catchError(() => of([])));
  }
  createMetadata(titleId: number, m: { key: string; value: string }): Observable<Metadata> {
    return this.http.post<Metadata>(`${this.base}/titles/${titleId}/metadata`, m);
  }
}
