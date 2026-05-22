import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../../core/auth/auth.service';
import { Manifest, Receipt, ReceiptRecord } from '../../core/models/manifest';
import { ManifestsService } from '../manifests/manifests.service';
import { ReceiptFormDialog } from '../manifests/manifests.component';

@Component({
  selector: 'sc-receipts',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, MatIconModule, MatProgressBarModule, MatTooltipModule],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Distribution</div>
          <div class="sc-page-title mt-1">Receipts</div>
          <div class="sc-page-sub">Partner acknowledgements for delivered manifests.</div>
        </div>
      </div>

      <div class="sc-card p-4 flex items-end gap-3 flex-wrap">
        <div class="sc-field" style="min-width:280px">
          <label class="sc-label">Manifest</label>
          <select [(ngModel)]="manifestIdInput" class="sc-input">
            <option [ngValue]="null" disabled>Choose a manifest…</option>
            <option *ngFor="let m of manifests()" [ngValue]="m.id">
              #{{ m.id }} &middot; title {{ m.titleId }} &middot; partner {{ m.partnerId }} &middot; {{ m.status }}
            </option>
          </select>
        </div>
        <button class="sc-btn-primary !h-11" (click)="load()" [disabled]="!manifestIdInput">
          <mat-icon class="!text-[18px] !w-5 !h-5">search</mat-icon> Load
        </button>
        <button class="sc-btn-primary !h-11 ml-auto" *ngIf="canEdit() && manifestId()" (click)="record()">
          <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> Record receipt
        </button>
      </div>

      <div class="sc-card overflow-hidden" *ngIf="manifestId()">
        <mat-progress-bar mode="indeterminate" *ngIf="loading()"></mat-progress-bar>
        <div class="px-5 py-3 text-sm text-ink-300 border-b border-border">
          Receipts for manifest <span class="text-ink-100 font-medium">#{{ manifestId() }}</span>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-5 py-3">ID</th>
                <th class="px-5 py-3">Received at</th>
                <th class="px-5 py-3">Received by</th>
                <th class="px-5 py-3">Receipt URI</th>
                <th class="px-5 py-3">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.receiptId }}</td>
                <td class="px-5 py-3 text-ink-300">{{ r.receivedAt | date:'medium' }}</td>
                <td class="px-5 py-3">{{ r.receivedBy }}</td>
                <td class="px-5 py-3 text-ink-300 max-w-xs truncate">{{ r.receiptURI }}</td>
                <td class="px-5 py-3">
                  <span class="sc-chip"
                        [class.sc-chip-success]="r.status==='RECEIVED' || r.status==='COMPLETED'"
                        [class.sc-chip-muted]="!['RECEIVED','COMPLETED'].includes(r.status ?? '')">
                    {{ r.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">
          No receipts recorded for this manifest yet.
        </div>
      </div>

      <div *ngIf="!manifestId()" class="sc-card p-10 text-center text-ink-300">
        Select a manifest above to view its receipts.
      </div>
    </div>
  `
})
export class ReceiptsComponent {
  private api = inject(ManifestsService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  manifestIdInput: number | null = null;
  manifestId = signal<number | null>(null);
  manifests = signal<Manifest[]>([]);
  rows = signal<ReceiptRecord[]>([]);
  loading = signal(false);

  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN', 'DISTRIBUTION_OPERATOR', 'PARTNER_ADMIN']));

  constructor() {
    this.api.list().subscribe({
      next: data => this.manifests.set(data),
      error: () => this.snack.open('Failed to load manifests', 'OK', { duration: 3000 })
    });
  }

  load(): void {
    if (!this.manifestIdInput) return;
    this.manifestId.set(Number(this.manifestIdInput));
    this.refresh();
  }

  private refresh(): void {
    const mid = this.manifestId();
    if (!mid) return;
    this.loading.set(true);
    this.api.listReceipts(mid).subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load receipts', 'OK', { duration: 3000 }); }
    });
  }

  record(): void {
    const mid = this.manifestId();
    if (!mid) return;
    this.dialog.open(ReceiptFormDialog, { width: '560px' })
      .afterClosed().subscribe((value: Receipt | undefined) => {
        if (!value) return;
        this.api.recordReceipt(mid, value).subscribe({
          next: () => { this.snack.open('Receipt recorded', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Failed to record receipt', 'OK', { duration: 3000 })
        });
      });
  }
}
