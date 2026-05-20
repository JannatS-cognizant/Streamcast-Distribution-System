import { CommonModule, CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { UsageBreakdown, UsageDetails, UsageSummary } from '../../core/models/usage';
import { UsageService } from './usage.service';

@Component({
  selector: 'sc-usage',
  standalone: true,
  imports: [
    CommonModule, DatePipe, DecimalPipe, CurrencyPipe, ReactiveFormsModule,
    MatIconModule, MatProgressBarModule, MatTabsModule
  ],
  template: `
    <div class="animate-fade space-y-6">
      <div>
        <div class="sc-section-title">Analytics</div>
        <div class="sc-page-title mt-1">Usage reports</div>
        <div class="sc-page-sub">Views and revenue across the platform.</div>
      </div>

      <!-- Filter -->
      <div class="sc-card-padded">
        <div class="grid grid-cols-1 sm:grid-cols-4 gap-4 items-end">
          <div class="sc-field"><label class="sc-label">From</label>
            <input type="date" [formControl]="from" class="sc-input" /></div>
          <div class="sc-field"><label class="sc-label">To</label>
            <input type="date" [formControl]="to" class="sc-input" /></div>
          <div class="sc-field"><label class="sc-label">Group by</label>
            <select [formControl]="groupBy" class="sc-input">
              <option value="PLATFORM">Platform</option>
              <option value="TITLE">Title</option>
              <option value="DATE">Date</option>
            </select></div>
          <button class="sc-btn-primary" (click)="load()">
            <mat-icon class="!text-[18px] !w-5 !h-5">refresh</mat-icon> Apply
          </button>
        </div>
      </div>

      <!-- KPI cards -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div class="sc-card-padded relative overflow-hidden">
          <div class="absolute -top-10 -right-10 w-40 h-40 rounded-full bg-brand-500/20 blur-3xl"></div>
          <div class="relative">
            <div class="text-[11px] uppercase tracking-wider text-ink-300">Total views</div>
            <div class="text-3xl font-semibold mt-1">{{ summary()?.totalViews ?? 0 | number }}</div>
            <mat-icon class="absolute right-0 top-1 !text-3xl text-brand-400/60">visibility</mat-icon>
          </div>
        </div>
        <div class="sc-card-padded relative overflow-hidden">
          <div class="absolute -top-10 -right-10 w-40 h-40 rounded-full bg-emerald-500/20 blur-3xl"></div>
          <div class="relative">
            <div class="text-[11px] uppercase tracking-wider text-ink-300">Total revenue</div>
            <div class="text-3xl font-semibold mt-1">{{ summary()?.totalRevenue ?? 0 | currency:'USD':'symbol':'1.0-2' }}</div>
            <mat-icon class="absolute right-0 top-1 !text-3xl text-emerald-400/60">payments</mat-icon>
          </div>
        </div>
        <div class="sc-card-padded relative overflow-hidden">
          <div class="absolute -top-10 -right-10 w-40 h-40 rounded-full bg-accent-500/20 blur-3xl"></div>
          <div class="relative">
            <div class="text-[11px] uppercase tracking-wider text-ink-300">Records</div>
            <div class="text-3xl font-semibold mt-1">{{ summary()?.totalRecords ?? 0 | number }}</div>
            <mat-icon class="absolute right-0 top-1 !text-3xl text-accent-400/60">analytics</mat-icon>
          </div>
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
                <div class="h-full bg-gradient-to-r from-brand-500 to-accent-400 rounded-full transition-all duration-500"
                     [style.width.%]="barWidth(b)"></div>
              </div>
            </div>
          </div>
          <ng-template #noBreak><div class="py-10 text-center text-ink-300">No breakdown data.</div></ng-template>
        </mat-tab>

        <mat-tab label="Details">
          <mat-progress-bar mode="indeterminate" *ngIf="loadingDetails()"></mat-progress-bar>
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                  <th class="px-5 py-3">Date</th>
                  <th class="px-5 py-3">Title</th>
                  <th class="px-5 py-3">Platform</th>
                  <th class="px-5 py-3">Views</th>
                  <th class="px-5 py-3">Revenue</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let r of details()" class="border-b border-border hover:bg-surface-3 transition">
                  <td class="px-5 py-3 text-ink-300">{{ r.date | date:'mediumDate' }}</td>
                  <td class="px-5 py-3">#{{ r.titleId }}</td>
                  <td class="px-5 py-3"><span class="sc-chip-info">{{ r.platform }}</span></td>
                  <td class="px-5 py-3">{{ r.views | number }}</td>
                  <td class="px-5 py-3">{{ r.revenue | currency }}</td>
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
  private api = inject(UsageService);
  private fb = inject(FormBuilder);

  from = this.fb.nonNullable.control(this.defaultFrom());
  to   = this.fb.nonNullable.control(this.defaultTo());
  groupBy = this.fb.nonNullable.control('PLATFORM');

  summary = signal<UsageSummary | null>(null);
  details = signal<UsageDetails[]>([]);
  breakdown = signal<UsageBreakdown[]>([]);
  loadingDetails = signal(false);
  loadingBreakdown = signal(false);

  maxBreakdownViews = computed(() => Math.max(1, ...this.breakdown().map(b => b.views || 0)));

  constructor() { this.load(); }

  load(): void {
    const s = this.from.value, e = this.to.value;
    this.api.summary(s, e).subscribe(d => this.summary.set(d));
    this.loadingDetails.set(true);
    this.api.details(s, e).subscribe({
      next: d => { this.details.set(d); this.loadingDetails.set(false); },
      error: () => this.loadingDetails.set(false)
    });
    this.loadingBreakdown.set(true);
    this.api.breakdown(s, e, this.groupBy.value).subscribe({
      next: d => { this.breakdown.set(d); this.loadingBreakdown.set(false); },
      error: () => this.loadingBreakdown.set(false)
    });
  }

  barWidth(b: UsageBreakdown): number {
    return Math.round(((b.views || 0) / this.maxBreakdownViews()) * 100);
  }

  private defaultFrom(): string { const d = new Date(); d.setMonth(d.getMonth()-1); return d.toISOString().slice(0,10); }
  private defaultTo():   string { return new Date().toISOString().slice(0,10); }
}
