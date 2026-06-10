import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';
import { ComplianceCheck, ComplianceResult, ComplianceUpdate } from '../../core/models/compliance';
import { ComplianceService } from './compliance.service';

@Component({
  selector: 'sc-compliance-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Run compliance check</h2>
    <p class="text-sm text-ink-300 px-6 pt-1 pb-3">Evaluate a schedule against its contract.</p>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-2 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field">
            <label class="sc-label">Contract ID</label>
            <input type="number" min="1" formControlName="contractId" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Schedule ID</label>
            <input type="number" min="1" formControlName="scheduleId" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Result</label>
            <select formControlName="result" class="sc-input">
              <option value="PASS">Pass</option>
              <option value="FAIL">Fail</option>
              <option value="PENDING">Pending</option>
            </select>
          </div>
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Notes</label>
            <textarea rows="3" formControlName="notes" class="sc-input"
                      placeholder="Observations from the compliance review…"></textarea>
          </div>
        </div>
      </mat-dialog-content>
      <mat-dialog-actions class="!px-6 !pt-5 !pb-6 !justify-end !gap-2">
        <button type="button" class="sc-btn-ghost" (click)="ref.close()">Cancel</button>
        <button type="submit" class="sc-btn-primary" [disabled]="form.invalid">
          <mat-icon class="!text-[18px] !w-5 !h-5">fact_check</mat-icon> Run check
        </button>
      </mat-dialog-actions>
    </form>
  `
})
export class ComplianceFormDialog {
  ref = inject(MatDialogRef<ComplianceFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    contractId: [1, [Validators.required, Validators.min(1)]],
    scheduleId: [1, [Validators.required, Validators.min(1)]],
    result: ['PENDING' as ComplianceResult, Validators.required],
    notes: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(1000)]]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-compliance-update',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Update check #{{ data.checkId }}</h2>
    <p class="text-sm text-ink-300 px-6 pt-1 pb-3">Override the result and notes after manual review.</p>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-2 !pb-0">
        <div class="grid grid-cols-1 gap-4">
          <div class="sc-field">
            <label class="sc-label">Result</label>
            <select formControlName="result" class="sc-input">
              <option value="PASS">Pass</option>
              <option value="FAIL">Fail</option>
              <option value="PENDING">Pending</option>
            </select>
          </div>
          <div class="sc-field">
            <label class="sc-label">Notes</label>
            <textarea rows="4" formControlName="notes" class="sc-input"></textarea>
          </div>
        </div>
      </mat-dialog-content>
      <mat-dialog-actions class="!px-6 !pt-5 !pb-6 !justify-end !gap-2">
        <button type="button" class="sc-btn-ghost" (click)="ref.close()">Cancel</button>
        <button type="submit" class="sc-btn-primary" [disabled]="form.invalid">
          <mat-icon class="!text-[18px] !w-5 !h-5">save</mat-icon> Save
        </button>
      </mat-dialog-actions>
    </form>
  `
})
export class ComplianceUpdateDialog {
  ref = inject(MatDialogRef<ComplianceUpdateDialog>);
  data = inject<ComplianceCheck>(MAT_DIALOG_DATA);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    result: [this.data.result as ComplianceResult, Validators.required],
    notes: [this.data.notes, [Validators.required, Validators.minLength(3), Validators.maxLength(1000)]]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-compliance',
  standalone: true,
  imports: [CommonModule, DatePipe, MatIconModule, MatProgressBarModule],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Compliance</div>
          <div class="sc-page-title mt-1">Compliance checks</div>
          <div class="sc-page-sub">Schedule-vs-contract reviews and their outcomes.</div>
        </div>
        <button class="sc-btn-primary" *ngIf="canCreate()" (click)="openCreate()">
          <mat-icon class="!text-[18px] !w-5 !h-5">fact_check</mat-icon> Run check
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
                <th class="px-5 py-3">Schedule</th>
                <th class="px-5 py-3">Result</th>
                <th class="px-5 py-3">Checked</th>
                <th class="px-5 py-3">Notes</th>
                <th class="px-5 py-3" *ngIf="canEdit()"></th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.checkId }}</td>
                <td class="px-5 py-3"><span class="sc-chip-muted">#{{ r.contractId }}</span></td>
                <td class="px-5 py-3"><span class="sc-chip-muted">#{{ r.scheduleId }}</span></td>
                <td class="px-5 py-3">
                  <span class="sc-chip"
                        [class.sc-chip-success]="r.result==='PASS'"
                        [class.sc-chip-danger]="r.result==='FAIL'"
                        [class.sc-chip-muted]="r.result==='PENDING'">{{ r.result }}</span>
                </td>
                <td class="px-5 py-3 text-ink-300">{{ r.checkedAt | date:'medium' }}</td>
                <td class="px-5 py-3 text-ink-300 max-w-xs truncate" [title]="r.notes">{{ r.notes }}</td>
                <td class="px-5 py-3" *ngIf="canEdit()">
                  <button class="sc-btn-ghost !h-8 !px-2" (click)="openEdit(r)">
                    <mat-icon class="!text-[16px] !w-4 !h-4">edit</mat-icon>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">No compliance checks yet.</div>
      </div>
    </div>
  `
})
export class ComplianceComponent {
  private api = inject(ComplianceService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  rows = signal<ComplianceCheck[]>([]);
  loading = signal(false);
  canCreate = computed(() => this.auth.hasAnyRole(['ADMIN', 'COMPLIANCE_OFFICER']));
  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN', 'COMPLIANCE_OFFICER', 'LEGAL_OFFICER']));

  constructor() { this.refresh(); }

  refresh(): void {
    this.loading.set(true);
    this.api.list().subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load compliance checks', 'OK', { duration: 3000 }); }
    });
  }

  openCreate(): void {
    this.dialog.open(ComplianceFormDialog, { width: '680px' })
      .afterClosed().subscribe((value: ComplianceCheck | undefined) => {
        if (!value) return;
        this.api.runCheck(value).subscribe({
          next: () => { this.snack.open('Compliance check completed', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Check failed', 'OK', { duration: 3000 })
        });
      });
  }

  openEdit(row: ComplianceCheck): void {
    this.dialog.open(ComplianceUpdateDialog, { width: '560px', data: row })
      .afterClosed().subscribe((value: ComplianceUpdate | undefined) => {
        if (!value || row.checkId == null) return;
        this.api.update(row.checkId, value).subscribe({
          next: () => { this.snack.open('Compliance check updated', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Update failed', 'OK', { duration: 3000 })
        });
      });
  }
}
