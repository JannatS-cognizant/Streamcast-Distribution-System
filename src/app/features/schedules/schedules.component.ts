import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';
import { CalendarSchedule, CreateSchedule } from '../../core/models/schedule';
import { SchedulesService } from './schedules.service';

// ─── Confirm Dialog ───────────────────────────────────────────────────────────
@Component({
  selector: 'sc-confirm-dialog',
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
export class ConfirmDialog {
  ref  = inject(MatDialogRef<ConfirmDialog>);
  data = inject<{ title: string; message: string; confirmLabel?: string }>(MAT_DIALOG_DATA);
}

// ─── Schedule Form Dialog ─────────────────────────────────────────────────────
@Component({
  selector: 'sc-schedule-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">
      {{ data ? 'Edit schedule' : 'New schedule' }}
    </h2>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-3 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field">
            <label class="sc-label">Title ID</label>
            <input type="number" min="1" formControlName="titleId" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Contract ID</label>
            <input type="number" min="1" formControlName="contractId" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Platform</label>
            <input formControlName="platform" class="sc-input" placeholder="Netflix, Hotstar…" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Window type</label>
            <select formControlName="windowType" class="sc-input">
              <option value="PREMIERE">Premiere</option>
              <option value="REPEAT">Repeat</option>
              <option value="OTT">OTT</option>
              <option value="SYNDICATION">Syndication</option>
            </select>
          </div>
          <div class="sc-field">
            <label class="sc-label">Start</label>
            <input type="datetime-local" formControlName="startDateTime" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">End</label>
            <input type="datetime-local" formControlName="endDateTime" class="sc-input" />
          </div>
        </div>
        <p class="text-xs text-ink-300 mt-3">
          Window type: PREMIERE / REPEAT / OTT / SYNDICATION
        </p>
      </mat-dialog-content>
      <mat-dialog-actions class="!px-6 !pt-5 !pb-6 !justify-end !gap-2">
        <button type="button" class="sc-btn-ghost" (click)="ref.close()">Cancel</button>
        <button type="submit" class="sc-btn-primary" [disabled]="form.invalid">
          <mat-icon class="!text-[18px] !w-5 !h-5">check</mat-icon>
          {{ data ? 'Update' : 'Create' }}
        </button>
      </mat-dialog-actions>
    </form>
  `
})
export class ScheduleFormDialog {
  ref  = inject(MatDialogRef<ScheduleFormDialog>);
  data = inject<CalendarSchedule | null>(MAT_DIALOG_DATA);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    titleId:       [this.data?.titleId    ?? 1,     [Validators.required, Validators.min(1)]],
    contractId:    [this.data?.contractId ?? 1,     [Validators.required, Validators.min(1)]],
    platform:      [this.data?.platform   ?? '',    Validators.required],
    windowType:    [this.data?.windowType ?? 'OTT', Validators.required],
    startDateTime: [this.data ? this.data.startDateTime.slice(0,16) : '', Validators.required],
    endDateTime:   [this.data ? this.data.endDateTime.slice(0,16)   : '', Validators.required],
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

// ─── Schedules Page ───────────────────────────────────────────────────────────
@Component({
  selector: 'sc-schedules',
  standalone: true,
  imports: [CommonModule, DatePipe, ReactiveFormsModule, MatIconModule,
            MatProgressBarModule, MatDialogModule, ConfirmDialog],
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

      <div class="sc-card-padded">
        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-3 items-end">
          <div class="sc-field col-span-2 sm:col-span-1">
            <label class="sc-label">From</label>
            <input type="datetime-local" [formControl]="from" class="sc-input" />
          </div>
          <div class="sc-field col-span-2 sm:col-span-1">
            <label class="sc-label">To</label>
            <input type="datetime-local" [formControl]="to" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Status</label>
            <select [formControl]="filterStatus" class="sc-input">
              <option value="">All</option>
              <option value="ACTIVE">Active</option>
              <option value="EXPIRED">Expired</option>
            </select>
          </div>
          <div class="sc-field">
            <label class="sc-label">Platform</label>
            <input [formControl]="filterPlatform" class="sc-input" placeholder="Netflix…" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Per page</label>
            <select [formControl]="pageSizeCtrl" class="sc-input">
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>
          </div>
          <button class="sc-btn-ghost h-10" (click)="search()">
            <mat-icon class="!text-[18px] !w-5 !h-5">search</mat-icon> Apply
          </button>
        </div>
      </div>

      <div class="sc-card overflow-hidden">
        <mat-progress-bar mode="indeterminate" *ngIf="loading()"></mat-progress-bar>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-4 py-3">ID</th>
                <th class="px-4 py-3">Title</th>
                <th class="px-4 py-3">Contract</th>
                <th class="px-4 py-3">Platform</th>
                <th class="px-4 py-3">Window</th>
                <th class="px-4 py-3">Start</th>
                <th class="px-4 py-3">End</th>
                <th class="px-4 py-3">Status</th>
                <th class="px-4 py-3 text-right" *ngIf="canEdit()">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-4 py-3 font-medium">{{ r.scheduleId }}</td>
                <td class="px-4 py-3 text-ink-300">#{{ r.titleId }}</td>
                <td class="px-4 py-3 text-ink-300">#{{ r.contractId }}</td>
                <td class="px-4 py-3"><span class="sc-chip-brand">{{ r.platform }}</span></td>
                <td class="px-4 py-3"><span class="sc-chip-muted">{{ r.windowType }}</span></td>
                <td class="px-4 py-3 text-ink-300 text-xs">{{ r.startDateTime | date:'dd/MM/yy HH:mm' }}</td>
                <td class="px-4 py-3 text-ink-300 text-xs">{{ r.endDateTime   | date:'dd/MM/yy HH:mm' }}</td>
                <td class="px-4 py-3">
                  <span [class]="r.status === 'ACTIVE' ? 'sc-chip-success' : 'sc-chip-danger'">
                    {{ r.status }}
                  </span>
                </td>
                <td class="px-4 py-3 text-right" *ngIf="canEdit()">
                  <div class="flex items-center justify-end gap-1">
                    <button class="sc-icon-btn" title="Edit" (click)="openEdit(r)">
                      <mat-icon class="!text-[16px]">edit</mat-icon>
                    </button>
                    <button class="sc-icon-btn !text-red-400" title="Delete" (click)="remove(r.scheduleId)">
                      <mat-icon class="!text-[16px]">delete</mat-icon>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-12 text-center text-ink-300">
          No schedules found in this window.
        </div>
        <div class="flex items-center justify-between px-5 py-3 border-t border-border text-sm flex-wrap gap-2">
          <span class="text-ink-300 text-xs">
            Page {{ page() + 1 }} · {{ rows().length }} records shown
            <span *ngIf="rows().length < pageSize() && rows().length > 0"> · last page</span>
          </span>
          <div class="flex items-center gap-2">
            <button class="sc-btn-ghost !py-1 !px-3 text-xs"
              [disabled]="page() === 0 || loading()" (click)="goToPage(0)">
              <mat-icon class="!text-[13px]">first_page</mat-icon>
            </button>
            <button class="sc-btn-ghost !py-1 !px-3 text-xs flex items-center gap-1"
              [disabled]="page() === 0 || loading()" (click)="goToPage(page() - 1)">
              <mat-icon class="!text-[13px]">chevron_left</mat-icon> Prev
            </button>
            <span class="px-3 py-1 rounded border border-border text-xs font-medium">
              {{ page() + 1 }}
            </span>
            <button class="sc-btn-ghost !py-1 !px-3 text-xs flex items-center gap-1"
              [disabled]="rows().length < pageSize() || loading()" (click)="goToPage(page() + 1)">
              Next <mat-icon class="!text-[13px]">chevron_right</mat-icon>
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SchedulesComponent {
  private api    = inject(SchedulesService);
  private auth   = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack  = inject(MatSnackBar);
  private fb     = inject(FormBuilder);

  rows     = signal<CalendarSchedule[]>([]);
  loading  = signal(false);
  page     = signal(0);
  pageSize = signal(10);

  canEdit = computed(() =>
    this.auth.hasAnyRole(['ADMIN','SCHEDULER','RIGHTS_MANAGER','COMPLIANCE_OFFICER'])
  );

  from           = this.fb.nonNullable.control(this.defaultFrom());
  to             = this.fb.nonNullable.control(this.defaultTo());
  filterStatus   = this.fb.nonNullable.control('');
  filterPlatform = this.fb.nonNullable.control('');
  pageSizeCtrl   = this.fb.nonNullable.control('10');

  constructor() { this.load(); }

  search(): void {
    this.pageSize.set(Number(this.pageSizeCtrl.value));
    this.page.set(0);
    this.load();
  }

  goToPage(p: number): void { this.page.set(p); this.load(); }

  load(): void {
    this.loading.set(true);
    this.api.calendar(
      `${this.from.value}:00`, `${this.to.value}:00`,
      this.page(), this.pageSize(),
      this.filterStatus.value   || undefined,
      this.filterPlatform.value || undefined
    ).subscribe({
      next:  data => { this.rows.set(data); this.loading.set(false); },
      error: ()   => { this.loading.set(false); this.snack.open('Failed to load', 'OK', { duration: 3000 }); }
    });
  }

  openCreate(): void {
    this.dialog.open(ScheduleFormDialog, { width: '680px', data: null })
      .afterClosed().subscribe((v: CreateSchedule | undefined) => {
        if (!v) return;
        this.api.create({ ...v, startDateTime: v.startDateTime+':00', endDateTime: v.endDateTime+':00' }).subscribe({
          next:  () => { this.snack.open('Schedule created', 'OK', { duration: 2500 }); this.search(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 4000 })
        });
      });
  }

  openEdit(row: CalendarSchedule): void {
    this.dialog.open(ScheduleFormDialog, { width: '680px', data: row })
      .afterClosed().subscribe((v: CreateSchedule | undefined) => {
        if (!v) return;
        this.api.update(row.scheduleId, { ...v, startDateTime: v.startDateTime+':00', endDateTime: v.endDateTime+':00' }).subscribe({
          next:  () => { this.snack.open('Schedule updated', 'OK', { duration: 2500 }); this.load(); },
          error: err => this.snack.open(err?.error?.message ?? 'Update failed', 'OK', { duration: 4000 })
        });
      });
  }

  remove(id: number): void {
    this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: 'Delete schedule',
        message: `Are you sure you want to delete schedule #${id}? This cannot be undone.`,
        confirmLabel: 'Delete'
      }
    }).afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      this.api.delete(id).subscribe({
        next:  () => { this.snack.open('Schedule deleted', 'OK', { duration: 2500 }); this.load(); },
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

  private defaultFrom(): string { return '2024-01-01T00:00'; }
  private defaultTo():   string { return '2028-12-31T23:59'; }
}