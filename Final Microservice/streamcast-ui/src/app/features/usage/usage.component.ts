import { CommonModule, CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CreateUsage, UsageBreakdown, UsageDetails, UsageSummary } from '../../core/models/usage';
import { UsageService } from './usage.service';

// ─── Confirm Dialog ───────────────────────────────────────────────────────────
@Component({
  selector: 'sc-confirm-dialog-usage',
  standalone: true,
  imports: [MatDialogModule, MatIconModule],
  template: `
    <div class="p-6">
      <div class="flex items-center gap-3 mb-4">
        <mat-icon class="!text-red-400 !text-[28px] !w-7 !h-7">warning</mat-icon>
        <h2 class="text-ink-100 text-lg font-semibold">{{ data.title }}</h2>
      </div>
      <p class="text-ink-300 text-sm mb-6">{{ data.message }}</p>
      <div class="flex justify-end gap-2">
        <button class="sc-btn-ghost" (click)="ref.close(false)">Cancel</button>
        <button class="sc-btn-primary !bg-red-500 hover:!bg-red-600 flex items-center gap-1"
                (click)="ref.close(true)">
          <mat-icon class="!text-[18px] !w-5 !h-5">delete</mat-icon>
          {{ data.confirmLabel ?? 'Confirm' }}
        </button>
      </div>
    </div>
  `
})
export class UsageConfirmDialog {
  ref  = inject(MatDialogRef<UsageConfirmDialog>);
  data = inject<{ title: string; message: string; confirmLabel?: string }>(MAT_DIALOG_DATA);
}

// ─── Usage Form Dialog ────────────────────────────────────────────────────────
@Component({
  selector: 'sc-usage-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">
      {{ data ? 'Edit usage record' : 'Add usage record' }}
    </h2>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-3 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field">
            <label class="sc-label">Title ID</label>
            <input type="number" min="1" formControlName="titleId" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Platform</label>
            <input formControlName="platform" class="sc-input" placeholder="Netflix, Hotstar…" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Date</label>
            <input type="date" formControlName="date" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Views</label>
            <input type="number" min="0" formControlName="views" class="sc-input" />
          </div>
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Revenue (USD)</label>
            <input type="number" min="0" step="0.01" formControlName="revenue" class="sc-input" />
          </div>
        </div>
        <p class="text-xs text-ink-300 mt-2">Date must not be in the future. Revenue must be 0 if views is 0.</p>
      </mat-dialog-content>
      <mat-dialog-actions class="!px-6 !pt-5 !pb-6 !justify-end !gap-2">
        <button type="button" class="sc-btn-ghost" (click)="ref.close()">Cancel</button>
        <button type="submit" class="sc-btn-primary" [disabled]="form.invalid">
          <mat-icon class="!text-[18px] !w-5 !h-5">check</mat-icon> {{ data ? 'Update' : 'Save' }}
        </button>
      </mat-dialog-actions>
    </form>
  `
})
export class UsageFormDialog {
  ref  = inject(MatDialogRef<UsageFormDialog>);
  data = inject<UsageDetails | null>(MAT_DIALOG_DATA);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    titleId:  [this.data?.titleId  ?? 1,  [Validators.required, Validators.min(1)]],
    platform: [this.data?.platform ?? '', Validators.required],
    date:     [this.data?.date     ?? '', Validators.required],
    views:    [this.data?.views    ?? 0,  [Validators.required, Validators.min(0)]],
    revenue:  [this.data?.revenue  ?? 0,  [Validators.required, Validators.min(0)]],
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

// ─── Usage Page ───────────────────────────────────────────────────────────────
@Component({
  selector: 'sc-usage',
  standalone: true,
  imports: [
    CommonModule, DatePipe, DecimalPipe, CurrencyPipe, ReactiveFormsModule,
    MatIconModule, MatProgressBarModule, MatTabsModule, MatDialogModule,
    UsageConfirmDialog
  ],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Analytics</div>
          <div class="sc-page-title mt-1">Usage reports</div>
          <div class="sc-page-sub">Views and revenue across the platform.</div>
        </div>
        <button class="sc-btn-primary" (click)="openCreate()">
          <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> Add record
        </button>
      </div>

      <div class="sc-card-padded">
        <div class="grid grid-cols-1 sm:grid-cols-4 gap-4 items-end">
          <div class="sc-field">
            <label class="sc-label">From</label>
            <input type="date" [formControl]="from" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">To</label>
            <input type="date" [formControl]="to" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Group by</label>
            <select [formControl]="groupBy" class="sc-input">
              <option value="platform">Platform</option>
              <option value="title">Title</option>
              <option value="date">Date</option>
            </select>
          </div>
          <button class="sc-btn-primary" (click)="load()">
            <mat-icon class="!text-[18px] !w-5 !h-5">refresh</mat-icon> Apply
          </button>
        </div>
      </div>

      <div class="sc-card-padded">
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 items-end">
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Lookup by Title ID</label>
            <input type="number" min="1" [formControl]="titleIdSearch" class="sc-input" placeholder="Enter title ID" />
          </div>
          <button class="sc-btn-ghost" (click)="loadByTitle()" [disabled]="titleIdSearch.invalid">
            <mat-icon class="!text-[18px] !w-5 !h-5">search</mat-icon> Find
          </button>
        </div>
        <div *ngIf="byTitle().length > 0" class="mt-4 overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-4 py-2">ID</th><th class="px-4 py-2">Date</th>
                <th class="px-4 py-2">Platform</th><th class="px-4 py-2">Views</th>
                <th class="px-4 py-2">Revenue</th><th class="px-4 py-2 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of byTitle()" class="border-b border-border hover:bg-surface-3">
                <td class="px-4 py-2 font-medium">{{ r.usageId }}</td>
                <td class="px-4 py-2 text-ink-300">{{ r.date | date:'mediumDate' }}</td>
                <td class="px-4 py-2"><span class="sc-chip-info">{{ r.platform }}</span></td>
                <td class="px-4 py-2">{{ r.views | number }}</td>
                <td class="px-4 py-2">{{ r.revenue | currency }}</td>
                <td class="px-4 py-2 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <button class="sc-icon-btn" (click)="openEdit(r)">
                      <mat-icon class="!text-[15px]">edit</mat-icon>
                    </button>
                    <button class="sc-icon-btn !text-red-400" (click)="remove(r.usageId)">
                      <mat-icon class="!text-[15px]">delete</mat-icon>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div class="sc-card-padded">
          <div class="text-[11px] uppercase tracking-wider text-ink-300">Total views</div>
          <div class="text-3xl font-semibold mt-1">{{ summary()?.totalViews ?? 0 | number }}</div>
        </div>
        <div class="sc-card-padded">
          <div class="text-[11px] uppercase tracking-wider text-ink-300">Total revenue</div>
          <div class="text-3xl font-semibold mt-1">{{ summary()?.totalRevenue ?? 0 | currency:'USD':'symbol':'1.0-2' }}</div>
        </div>
        <div class="sc-card-padded">
          <div class="text-[11px] uppercase tracking-wider text-ink-300">Records</div>
          <div class="text-3xl font-semibold mt-1">{{ summary()?.totalRecords ?? 0 | number }}</div>
        </div>
      </div>

      <mat-tab-group animationDuration="200ms" class="sc-card">
        <mat-tab label="Breakdown">
          <mat-progress-bar mode="indeterminate" *ngIf="loadingBreakdown()"></mat-progress-bar>
          <div class="p-6" *ngIf="breakdown().length; else noBreak">
            <div *ngFor="let b of breakdown()" class="py-2">
              <div class="flex items-center justify-between mb-1 text-sm">
                <span class="font-medium text-ink-100">{{ b.group }}</span>
                <span class="text-ink-300">{{ b.views | number }} views · {{ b.revenue | currency }}</span>
              </div>
              <div class="h-2 bg-surface-3 rounded-full overflow-hidden">
                <div class="h-full bg-brand-500 rounded-full transition-all duration-500"
                     [style.width.%]="barWidth(b)"></div>
              </div>
            </div>
          </div>
          <ng-template #noBreak>
            <div class="py-10 text-center text-ink-300">No breakdown data.</div>
          </ng-template>
        </mat-tab>

        <mat-tab label="Details">
          <mat-progress-bar mode="indeterminate" *ngIf="loadingDetails()"></mat-progress-bar>
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                  <th class="px-5 py-3">ID</th><th class="px-5 py-3">Date</th>
                  <th class="px-5 py-3">Title</th><th class="px-5 py-3">Platform</th>
                  <th class="px-5 py-3">Views</th><th class="px-5 py-3">Revenue</th>
                  <th class="px-5 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let r of details()" class="border-b border-border hover:bg-surface-3 transition">
                  <td class="px-5 py-3 font-medium">{{ r.usageId }}</td>
                  <td class="px-5 py-3 text-ink-300">{{ r.date | date:'mediumDate' }}</td>
                  <td class="px-5 py-3">#{{ r.titleId }}</td>
                  <td class="px-5 py-3"><span class="sc-chip-info">{{ r.platform }}</span></td>
                  <td class="px-5 py-3">{{ r.views | number }}</td>
                  <td class="px-5 py-3">{{ r.revenue | currency }}</td>
                  <td class="px-5 py-3 text-right">
                    <div class="flex items-center justify-end gap-1">
                      <button class="sc-icon-btn" (click)="openEdit(r)">
                        <mat-icon class="!text-[15px]">edit</mat-icon>
                      </button>
                      <button class="sc-icon-btn !text-red-400" (click)="remove(r.usageId)">
                        <mat-icon class="!text-[15px]">delete</mat-icon>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div *ngIf="!loadingDetails() && details().length === 0" class="py-10 text-center text-ink-300">
            No usage in this window.
          </div>
        </mat-tab>
      </mat-tab-group>
    </div>
  `
})
export class UsageComponent {
  private api    = inject(UsageService);
  private dialog = inject(MatDialog);
  private snack  = inject(MatSnackBar);
  private fb     = inject(FormBuilder);

  from          = this.fb.nonNullable.control(this.defaultFrom());
  to            = this.fb.nonNullable.control(this.defaultTo());
  groupBy       = this.fb.nonNullable.control('platform');
  titleIdSearch = this.fb.nonNullable.control(1, [Validators.required, Validators.min(1)]);

  summary          = signal<UsageSummary | null>(null);
  details          = signal<UsageDetails[]>([]);
  breakdown        = signal<UsageBreakdown[]>([]);
  byTitle          = signal<UsageDetails[]>([]);
  loadingDetails   = signal(false);
  loadingBreakdown = signal(false);

  maxBreakdownViews = computed(() => Math.max(1, ...this.breakdown().map(b => Number(b.views) || 0)));

  constructor() { this.load(); }

  load(): void {
    const s = this.from.value, e = this.to.value;
    this.api.summary(s, e).subscribe(d => this.summary.set(d));
    this.loadingDetails.set(true);
    this.api.details(s, e).subscribe({
      next:  d => { this.details.set(d); this.loadingDetails.set(false); },
      error: () => this.loadingDetails.set(false)
    });
    this.loadingBreakdown.set(true);
    this.api.breakdown(s, e, this.groupBy.value).subscribe({
      next:  d => { this.breakdown.set(d); this.loadingBreakdown.set(false); },
      error: () => this.loadingBreakdown.set(false)
    });
  }

  loadByTitle(): void {
    this.api.getByTitleId(this.titleIdSearch.value).subscribe({
      next:  d => this.byTitle.set(d),
      error: err => this.snack.open(err?.error?.message ?? 'Not found', 'OK', { duration: 3000 })
    });
  }

  openCreate(): void {
    this.dialog.open(UsageFormDialog, { width: '520px', data: null })
      .afterClosed().subscribe((v: CreateUsage | undefined) => {
        if (!v) return;
        this.api.create(v).subscribe({
          next:  () => { this.snack.open('Record created', 'OK', { duration: 2500 }); this.load(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 4000 })
        });
      });
  }

  openEdit(row: UsageDetails): void {
    this.dialog.open(UsageFormDialog, { width: '520px', data: row })
      .afterClosed().subscribe((v: CreateUsage | undefined) => {
        if (!v) return;
        this.api.update(row.usageId, v).subscribe({
          next:  () => { this.snack.open('Record updated', 'OK', { duration: 2500 }); this.load(); },
          error: err => this.snack.open(err?.error?.message ?? 'Update failed', 'OK', { duration: 4000 })
        });
      });
  }

  remove(id: number): void {
    this.dialog.open(UsageConfirmDialog, {
      width: '420px',
      data: {
        title: 'Delete usage record',
        message: `Are you sure you want to delete usage record #${id}? This cannot be undone.`,
        confirmLabel: 'Delete'
      }
    }).afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      this.api.delete(id).subscribe({
        next:  () => { this.snack.open('Deleted', 'OK', { duration: 2500 }); this.load(); },
        error: err => this.snack.open(err?.error?.message ?? 'Delete failed', 'OK', { duration: 4000 })
      });
    });
  }

  private toast(msg: string, type: 'toast-success'|'toast-error'|'toast-warning'|'toast-info' = 'toast-success'): void {
    this.snack.open(msg, '✕', {
      duration: 3000,
      panelClass: type,
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }

  barWidth(b: UsageBreakdown): number {
    return Math.round((Number(b.views) / this.maxBreakdownViews()) * 100);
  }

  private defaultFrom(): string { const d = new Date(); d.setMonth(d.getMonth()-1); return d.toISOString().slice(0,10); }
  private defaultTo():   string { return new Date().toISOString().slice(0,10); }
}