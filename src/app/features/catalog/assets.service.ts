import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Asset, Metadata } from '../../core/models/catalog';

export type AssetWrite = Omit<Asset, 'id' | 'titleId' | 'titleName'>;
export type MetadataWrite = { key: string; value: string };

@Injectable({ providedIn: 'root' })
export class AssetsService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api`;

  listAssets(titleId: number): Observable<Asset[]> {
    return this.http
      .get<Asset[]>(`${this.base}/titles/${titleId}/assets`)
      .pipe(catchError(() => of([])));
  }
  createAsset(titleId: number, a: AssetWrite): Observable<Asset> {
    return this.http.post<Asset>(`${this.base}/assets/${titleId}`, a);
  }
  updateAsset(id: number, a: AssetWrite): Observable<Asset> {
    return this.http.put<Asset>(`${this.base}/assets/${id}`, a);
  }
  deleteAsset(id: number): Observable<string> {
    return this.http.delete(`${this.base}/assets/${id}`, { responseType: 'text' });
  }

  listMetadata(titleId: number): Observable<Metadata[]> {
    return this.http
      .get<Metadata[]>(`${this.base}/titles/${titleId}/metadata`)
      .pipe(catchError(() => of([])));
  }
  createMetadata(titleId: number, m: MetadataWrite): Observable<Metadata> {
    return this.http.post<Metadata>(`${this.base}/titles/${titleId}/metadata`, m);
  }
  updateMetadata(id: number, m: MetadataWrite): Observable<Metadata> {
    return this.http.put<Metadata>(`${this.base}/metadata/${id}`, m);
  }
  deleteMetadata(id: number): Observable<string> {
    return this.http.delete(`${this.base}/metadata/${id}`, { responseType: 'text' });
  }
}
