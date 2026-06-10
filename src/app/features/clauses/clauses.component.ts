import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';
import { Clause } from '../../core/models/clause';
import { ClausesService } from './clauses.service';

@Component({
  selector: 'sc-clause-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Create clause</h2>
    <p class="text-sm text-ink-300 px-6 pt-1 pb-3">Attach a legal clause to a contract.</p>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-2 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field">
            <label class="sc-label">Contract ID</label>
            <input type="number" min="1" formControlName="contractId" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Clause type</label>
            <select formControlName="clauseType" class="sc-input">
              <option value="EXCLUSIVITY">Exclusivity</option>
              <option value="TERRITORY">Territory</option>
              <option value="REVENUE_SHARE">Revenue share</option>
              <option value="TERMINATION">Termination</option>
              <option value="OTHER">Other</option>
            </select>
          </div>
          <div class="sc-field">
            <label class="sc-label">Effective from</label>
            <input type="date" formControlName="effectiveFrom" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Effective to</label>
            <input type="date" formControlName="effectiveTo" class="sc-input" />
          </div>
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Details (JSON)</label>
            <textarea rows="4" formControlName="detailsJSON" class="sc-input"
                      placeholder='{"note": "..."}'></textarea>
          </div>
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
export class ClauseFormDialog {
  ref = inject(MatDialogRef<ClauseFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    contractId: [1, [Validators.required, Validators.min(1)]],
    clauseType: ['EXCLUSIVITY', Validators.required],
    effectiveFrom: ['', Validators.required],
    effectiveTo: ['', Validators.required],
    detailsJSON: ['', Validators.required]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-clauses',
  standalone: true,
  imports: [CommonModule, DatePipe, MatIconModule, MatProgressBarModule],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Rights</div>
          <div class="sc-page-title mt-1">Clauses</div>
          <div class="sc-page-sub">Contract clauses across all active deals.</div>
        </div>
        <button class="sc-btn-primary" *ngIf="canEdit()" (click)="openCreate()">
          <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> New clause
        </button>
      </div>

      <div class="sc-card overflow-hidden">
        <mat-progress-bar mode="indeterminate" *ngIf="loading()"></mat-progress-bar>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-5 py-3">ID</th>
                <th class="px-5 py-3">Contract</th>
                <th class="px-5 py-3">Type</th>
                <th class="px-5 py-3">From</th>
                <th class="px-5 py-3">To</th>
                <th class="px-5 py-3">Details</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.clauseId }}</td>
                <td class="px-5 py-3 text-ink-300">#{{ r.contractId }}</td>
                <td class="px-5 py-3"><span class="sc-chip-brand">{{ r.clauseType }}</span></td>
                <td class="px-5 py-3 text-ink-300">{{ r.effectiveFrom | date:'mediumDate' }}</td>
                <td class="px-5 py-3 text-ink-300">{{ r.effectiveTo | date:'mediumDate' }}</td>
                <td class="px-5 py-3 text-ink-300 max-w-xs truncate">{{ r.detailsJSON }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">No clauses yet.</div>
      </div>
    </div>
  `
})
export class ClausesComponent {
  private api = inject(ClausesService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  rows = signal<Clause[]>([]);
  loading = signal(false);
  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN','RIGHTS_MANAGER']));

  constructor() { this.refresh(); }

  refresh(): void {
    this.loading.set(true);
    this.api.list().subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load clauses', 'OK', { duration: 3000 }); }
    });
  }

  openCreate(): void {
    this.dialog.open(ClauseFormDialog, { width: '680px' })
      .afterClosed().subscribe((value: Clause | undefined) => {
        if (!value) return;
        this.api.create(value).subscribe({
          next: () => { this.snack.open('Clause created', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }
}
