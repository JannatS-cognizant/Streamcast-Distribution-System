import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Conflict } from '../../core/models/schedule';
import { SchedulesService } from '../schedules/schedules.service';

@Component({
  selector: 'sc-conflicts',
  standalone: true,
  imports: [CommonModule, DatePipe, ReactiveFormsModule, MatIconModule, MatProgressBarModule],
  template: `
    <div class="animate-fade space-y-6">
      <div>
        <div class="sc-section-title">Scheduling</div>
        <div class="sc-page-title mt-1">Conflicts</div>
        <div class="sc-page-sub">Overlapping windows detected by the schedule engine.</div>
      </div>

      <div class="sc-card-padded">
        <form [formGroup]="form" (ngSubmit)="lookup()" (submit)="$event.preventDefault()" novalidate
              class="grid grid-cols-1 sm:grid-cols-3 gap-4 items-end">
          <div class="sc-field"><label class="sc-label">Schedule ID</label>
            <input type="number" min="1" formControlName="schedulesId" class="sc-input" /></div>
          <button type="submit" class="sc-btn-primary" [disabled]="form.invalid">
            <mat-icon class="!text-[18px] !w-5 !h-5">search</mat-icon> Find conflicts
          </button>
        </form>
      </div>

      <div class="sc-card overflow-hidden">
        <mat-progress-bar mode="indeterminate" *ngIf="loading()"></mat-progress-bar>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-5 py-3">ID</th>
                <th class="px-5 py-3">Overlapping schedules</th>
                <th class="px-5 py-3">Detected</th>
                <th class="px-5 py-3">Resolved</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.conflictId }}</td>
                <td class="px-5 py-3 text-ink-300">#{{ r.scheduleId1 }} ↔ #{{ r.scheduleId2 }}</td>
                <td class="px-5 py-3 text-ink-300">{{ r.detectedAt | date:'medium' }}</td>
                <td class="px-5 py-3">
                  <span class="sc-chip" [class.sc-chip-success]="r.resolved" [class.sc-chip-danger]="!r.resolved">
                    {{ r.resolved ? 'Yes' : 'No' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">
          {{ searched() ? 'No conflicts for this schedule.' : 'Enter a schedule ID above to look up conflicts.' }}
        </div>
      </div>
    </div>
  `
})
export class ConflictsComponent {
  private api = inject(SchedulesService);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);

  rows = signal<Conflict[]>([]);
  loading = signal(false);
  searched = signal(false);
  form = this.fb.nonNullable.group({ schedulesId: [1, [Validators.required, Validators.min(1)]] });

  lookup(): void {
    if (this.form.invalid) return;
    this.loading.set(true); this.searched.set(true);
    this.api.conflictsFor(this.form.controls.schedulesId.value).subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load conflicts', 'OK', { duration: 3000 }); }
    });
  }
}
