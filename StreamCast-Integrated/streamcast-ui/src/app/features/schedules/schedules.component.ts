import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';
import { CalendarSchedule, CreateSchedule } from '../../core/models/schedule';
import { SchedulesService } from './schedules.service';

@Component({
  selector: 'sc-schedule-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Schedule a broadcast</h2>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-3 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field"><label class="sc-label">Title ID</label>
            <input type="number" min="1" formControlName="titleId" class="sc-input" /></div>
          <div class="sc-field"><label class="sc-label">Contract ID</label>
            <input type="number" min="1" formControlName="contractId" class="sc-input" /></div>
          <div class="sc-field"><label class="sc-label">Platform</label>
            <input formControlName="platform" class="sc-input" placeholder="OTT, TV, MOBILE" /></div>
          <div class="sc-field"><label class="sc-label">Window type</label>
            <select formControlName="windowType" class="sc-input">
              <option value="PREMIERE">Premiere</option>
              <option value="REGULAR">Regular</option>
              <option value="RERUN">Rerun</option>
            </select></div>
          <div class="sc-field"><label class="sc-label">Start</label>
            <input type="datetime-local" formControlName="startDateTime" class="sc-input" /></div>
          <div class="sc-field"><label class="sc-label">End</label>
            <input type="datetime-local" formControlName="endDateTime" class="sc-input" /></div>
        </div>
      </mat-dialog-content>
      <mat-dialog-actions class="!px-6 !pt-5 !pb-6 !justify-end !gap-2">
        <button type="button" class="sc-btn-ghost" (click)="ref.close()">Cancel</button>
        <button type="submit" class="sc-btn-primary" [disabled]="form.invalid">
          <mat-icon class="!text-[18px] !w-5 !h-5">check</mat-icon> Save
        </button>
      </mat-dialog-actions>
    </form>
  `
})
export class ScheduleFormDialog {
  ref = inject(MatDialogRef<ScheduleFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    titleId: [1, [Validators.required, Validators.min(1)]],
    contractId: [1, [Validators.required, Validators.min(1)]],
    platform: ['OTT', Validators.required],
    windowType: ['REGULAR', Validators.required],
    startDateTime: ['', Validators.required],
    endDateTime: ['', Validators.required]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-schedules',
  standalone: true,
  imports: [CommonModule, DatePipe, ReactiveFormsModule, MatIconModule, MatProgressBarModule],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Scheduling</div>
          <div class="sc-page-title mt-1">Schedules</div>
          <div class="sc-page-sub">Broadcast plan across platforms.</div>
        </div>
        <button class="sc-btn-primary" *ngIf="canEdit()" (click)="openCreate()">
          <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> New schedule
        </button>
      </div>

      <!-- Filters -->
      <div class="sc-card-padded">
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 items-end">
          <div class="sc-field"><label class="sc-label">From</label>
            <input type="datetime-local" [formControl]="from" class="sc-input" /></div>
          <div class="sc-field"><label class="sc-label">To</label>
            <input type="datetime-local" [formControl]="to" class="sc-input" /></div>
          <button class="sc-btn-ghost" (click)="refresh()">
            <mat-icon class="!text-[18px] !w-5 !h-5">refresh</mat-icon> Apply
          </button>
        </div>
      </div>

      <div class="sc-card overflow-hidden">
        <mat-progress-bar mode="indeterminate" *ngIf="loading()"></mat-progress-bar>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-5 py-3">ID</th>
                <th class="px-5 py-3">Platform</th>
                <th class="px-5 py-3">Start</th>
                <th class="px-5 py-3">End</th>
                <th class="px-5 py-3">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.scheduleId }}</td>
                <td class="px-5 py-3"><span class="sc-chip-brand">{{ r.platform }}</span></td>
                <td class="px-5 py-3 text-ink-300">{{ r.startDateTime | date:'medium' }}</td>
                <td class="px-5 py-3 text-ink-300">{{ r.endDateTime | date:'medium' }}</td>
                <td class="px-5 py-3"><span class="sc-chip-muted">{{ r.status }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">
          No schedules in this window.
        </div>
      </div>
    </div>
  `
})
export class SchedulesComponent {
  private api = inject(SchedulesService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);

  rows = signal<CalendarSchedule[]>([]);
  loading = signal(false);
  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN','SCHEDULER']));

  from = this.fb.nonNullable.control(this.defaultFrom());
  to   = this.fb.nonNullable.control(this.defaultTo());

  constructor() { this.refresh(); }

  refresh(): void {
    this.loading.set(true);
    this.api.calendar(`${this.from.value}:00`, `${this.to.value}:00`).subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load schedules', 'OK', { duration: 3000 }); }
    });
  }

  openCreate(): void {
    this.dialog.open(ScheduleFormDialog, { width: '680px' })
      .afterClosed().subscribe((v: CreateSchedule | undefined) => {
        if (!v) return;
        this.api.create(v).subscribe({
          next: () => { this.snack.open('Schedule created', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }

  private defaultFrom(): string { const d = new Date(); d.setHours(0,0,0,0); return d.toISOString().slice(0,16); }
  private defaultTo():   string { const d = new Date(); d.setMonth(d.getMonth()+1); d.setHours(0,0,0,0); return d.toISOString().slice(0,16); }
}
