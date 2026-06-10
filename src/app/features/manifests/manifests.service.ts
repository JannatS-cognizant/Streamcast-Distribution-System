import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Attempt, AttemptRecord, Manifest, Receipt, ReceiptRecord } from '../../core/models/manifest';

@Injectable({ providedIn: 'root' })
export class ManifestsService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/manifests`;

  list(): Observable<Manifest[]> { return this.http.get<Manifest[]>(this.base); }
  get(id: number): Observable<Manifest> { return this.http.get<Manifest>(`${this.base}/${id}`); }
  create(m: Manifest): Observable<Manifest> { return this.http.post<Manifest>(this.base, m); }
  update(id: number, m: Manifest): Observable<Manifest> {
    return this.http.put<Manifest>(`${this.base}/${id}`, m);
  }
  listByPartner(partnerId: number): Observable<Manifest[]> {
    return this.http.get<Manifest[]>(`${this.base}/partner/${partnerId}`);
  }

  recordAttempt(id: number, attempt: Attempt): Observable<string> {
    return this.http.post(`${this.base}/${id}/attempts`, attempt, { responseType: 'text' });
  }
  listAttempts(manifestId: number): Observable<AttemptRecord[]> {
    return this.http.get<AttemptRecord[]>(`${this.base}/${manifestId}/attempts`);
  }

  recordReceipt(id: number, receipt: Receipt): Observable<string> {
    return this.http.post(`${this.base}/${id}/receipt`, receipt, { responseType: 'text' });
  }
  listReceipts(manifestId: number): Observable<ReceiptRecord[]> {
    return this.http.get<ReceiptRecord[]>(`${this.base}/${manifestId}/receipts`);
  }
}
