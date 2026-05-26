import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Conflict } from '../../core/models/schedule';
import { SchedulesService } from '../schedules/schedules.service';

// ─── Confirm Dialog ───────────────────────────────────────────────────────────
@Component({
  selector: 'sc-confirm-dialog-conflicts',
  standalone: true,
  imports: [MatDialogModule, MatIconModule],
  template: `
    <div class="p-6">
      <div class="flex items-center gap-3 mb-4">
        <mat-icon class="!text-amber-400 !text-[28px] !w-7 !h-7">warning_amber</mat-icon>
        <h2 class="text-ink-100 text-lg font-semibold">{{ data.title }}</h2>
      </div>
      <p class="text-ink-300 text-sm mb-6">{{ data.message }}</p>
      <div class="flex justify-end gap-2">
        <button class="sc-btn-ghost" (click)="ref.close(false)">Cancel</button>
        <button class="sc-btn-primary flex items-center gap-1" (click)="ref.close(true)">
          <mat-icon class="!text-[18px] !w-5 !h-5">search</mat-icon>
          {{ data.confirmLabel ?? 'Confirm' }}
        </button>
      </div>
    </div>
  `
})
export class ConflictsConfirmDialog {
  ref  = inject(MatDialogRef<ConflictsConfirmDialog>);
  data = inject<{ title: string; message: string; confirmLabel?: string }>(MAT_DIALOG_DATA);
}

// ─── Conflicts Page ───────────────────────────────────────────────────────────
@Component({
  selector: 'sc-conflicts',
  standalone: true,
  imports: [CommonModule, DatePipe, ReactiveFormsModule,
            MatIconModule, MatProgressBarModule, MatDialogModule, ConflictsConfirmDialog],
  template: `
    <div class="animate-fade space-y-6">
      <div>
        <div class="sc-section-title">Scheduling</div>
        <div class="sc-page-title mt-1">Conflicts</div>
        <div class="sc-page-sub">Overlapping windows detected by the schedule engine.</div>
      </div>
      <div class="sc-card-padded">
        <div class="grid grid-cols-1 sm:grid-cols-4 gap-4 items-end">
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Schedule ID</label>
            <input type="number" min="1" [formControl]="scheduleIdCtrl" class="sc-input" />
          </div>
          <button class="sc-btn-ghost" [disabled]="scheduleIdCtrl.invalid" (click)="lookup()">
            <mat-icon class="!text-[18px] !w-5 !h-5">search</mat-icon> Find conflicts
          </button>
          <button class="sc-btn-primary" [disabled]="scheduleIdCtrl.invalid" (click)="detect()">
            <mat-icon class="!text-[18px] !w-5 !h-5">warning_amber</mat-icon> Detect
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
                <th class="px-5 py-3">Schedule 1</th>
                <th class="px-5 py-3">Schedule 2</th>
                <th class="px-5 py-3">Detected</th>
                <th class="px-5 py-3">Resolved</th>
                <th class="px-5 py-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.conflictId }}</td>
                <td class="px-5 py-3 text-ink-300">#{{ r.scheduleId1 }}</td>
                <td class="px-5 py-3 text-ink-300">#{{ r.scheduleId2 }}</td>
                <td class="px-5 py-3 text-ink-300 text-xs">{{ r.detectedAt | date:'medium' }}</td>
                <td class="px-5 py-3">
                  <span [class]="r.resolved ? 'sc-chip-success' : 'sc-chip-danger'">
                    {{ r.resolved ? 'Yes' : 'No' }}
                  </span>
                </td>
                <td class="px-5 py-3 text-right">
                  <button *ngIf="!r.resolved"
                          class="sc-btn-ghost !py-1 !px-3 text-xs"
                          (click)="resolve(r.conflictId)">
                    <mat-icon class="!text-[14px]">check_circle</mat-icon> Resolve
                  </button>
                  <span *ngIf="r.resolved" class="text-xs text-ink-300">Done</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">
          {{ searched() ? 'No conflicts for this schedule.' : 'Enter a schedule ID to look up conflicts.' }}
        </div>
      </div>
    </div>
  `
})
export class ConflictsComponent {
  private api    = inject(SchedulesService);
  private dialog = inject(MatDialog);
  private snack  = inject(MatSnackBar);
  private fb     = inject(FormBuilder);

  rows     = signal<Conflict[]>([]);
  loading  = signal(false);
  searched = signal(false);
  scheduleIdCtrl = this.fb.nonNullable.control(1, [Validators.required, Validators.min(1)]);

  lookup(): void {
    if (this.scheduleIdCtrl.invalid) return;
    this.loading.set(true); this.searched.set(true);
    this.api.conflictsFor(this.scheduleIdCtrl.value).subscribe({
      next:  data => { this.rows.set(data); this.loading.set(false); },
      error: ()   => { this.loading.set(false); this.snack.open('Failed to load', 'OK', { duration: 3000 }); }
    });
  }

  detect(): void {
    if (this.scheduleIdCtrl.invalid) return;
    this.dialog.open(ConflictsConfirmDialog, {
      width: '420px',
      data: {
        title: 'Detect conflicts',
        message: `Run conflict detection for schedule #${this.scheduleIdCtrl.value}? This will scan for overlapping windows and save any conflicts found.`,
        confirmLabel: 'Detect'
      }
    }).afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      this.api.detectConflicts(this.scheduleIdCtrl.value).subscribe({
        next:  msg => { this.snack.open(msg ?? 'Detection complete', 'OK', { duration: 3000 }); this.lookup(); },
        error: ()  => this.snack.open('Detection failed', 'OK', { duration: 3000 })
      });
    });
  }

  resolve(id: number): void {
    this.api.resolveConflict(id).subscribe({
      next:  updated => {
        this.rows.update(list => list.map(c => c.conflictId === id ? updated : c));
        this.snack.open('Conflict resolved', 'OK', { duration: 2500 });
      },
      error: () => this.snack.open('Resolve failed', 'OK', { duration: 3000 })
    });
  }
}