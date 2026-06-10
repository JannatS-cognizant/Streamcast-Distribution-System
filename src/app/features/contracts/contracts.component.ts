import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';
import { Contract } from '../../core/models/contract';
import { ContractsService } from './contracts.service';

@Component({
  selector: 'sc-contract-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Create contract</h2>
    <p class="text-sm text-ink-300 px-6 pt-1 pb-3">Link rights to a catalog title.</p>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-2 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field">
            <label class="sc-label">Title ID</label>
            <input type="number" min="1" formControlName="titleId" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Territories</label>
            <input formControlName="territoryListJson" class="sc-input" placeholder="IN,US,UK" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Start date</label>
            <input type="date" formControlName="startDate" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">End date</label>
            <input type="date" formControlName="endDate" class="sc-input" />
          </div>
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Terms summary</label>
            <textarea rows="3" formControlName="termsSummary" class="sc-input"
                      placeholder="Standard distribution terms…"></textarea>
          </div>
          <div class="sc-field">
            <label class="sc-label">Status</label>
            <select formControlName="status" class="sc-input">
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </select>
          </div>
          <label class="sc-field flex items-center gap-3 cursor-pointer h-11 mt-6">
            <input type="checkbox" formControlName="exclusivityFlag"
                   class="h-5 w-5 rounded border-border bg-surface-1 accent-brand-500" />
            <span class="text-sm text-ink-100">Exclusive deal</span>
          </label>
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
export class ContractFormDialog {
  ref = inject(MatDialogRef<ContractFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    titleId: [1, [Validators.required, Validators.min(1)]],
    territoryListJson: ['', [Validators.required]],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    termsSummary: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
    status: ['ACTIVE' as 'ACTIVE' | 'INACTIVE', Validators.required],
    exclusivityFlag: [false, Validators.required]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-contracts',
  standalone: true,
  imports: [CommonModule, DatePipe, MatIconModule, MatProgressBarModule],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Rights</div>
          <div class="sc-page-title mt-1">Contracts</div>
          <div class="sc-page-sub">Title rights, territories and validity windows.</div>
        </div>
        <button class="sc-btn-primary" *ngIf="canEdit()" (click)="openCreate()">
          <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> New contract
        </button>
      </div>

      <div class="sc-card overflow-hidden">
        <mat-progress-bar mode="indeterminate" *ngIf="loading()"></mat-progress-bar>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-5 py-3">ID</th>
                <th class="px-5 py-3">Title</th>
                <th class="px-5 py-3">Territories</th>
                <th class="px-5 py-3">Window</th>
                <th class="px-5 py-3">Exclusive</th>
                <th class="px-5 py-3">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.contractId }}</td>
                <td class="px-5 py-3"><span class="sc-chip-muted">#{{ r.titleId }}</span></td>
                <td class="px-5 py-3 text-ink-300">{{ r.territoryListJson }}</td>
                <td class="px-5 py-3 text-ink-300">
                  {{ r.startDate | date:'mediumDate' }} <span class="text-ink-500">→</span> {{ r.endDate | date:'mediumDate' }}
                </td>
                <td class="px-5 py-3">
                  <span class="sc-chip" [class.sc-chip-brand]="r.exclusivityFlag" [class.sc-chip-muted]="!r.exclusivityFlag">
                    {{ r.exclusivityFlag ? 'Yes' : 'No' }}
                  </span>
                </td>
                <td class="px-5 py-3">
                  <span class="sc-chip" [class.sc-chip-success]="r.status==='ACTIVE'" [class.sc-chip-muted]="r.status!=='ACTIVE'">{{ r.status }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">No contracts yet.</div>
      </div>
    </div>
  `
})
export class ContractsComponent {
  private api = inject(ContractsService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  rows = signal<Contract[]>([]);
  loading = signal(false);
  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN','RIGHTS_MANAGER']));

  constructor() { this.refresh(); }

  refresh(): void {
    this.loading.set(true);
    this.api.list().subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load contracts', 'OK', { duration: 3000 }); }
    });
  }

  openCreate(): void {
    this.dialog.open(ContractFormDialog, { width: '680px' })
      .afterClosed().subscribe((value: Contract | undefined) => {
        if (!value) return;
        this.api.create(value).subscribe({
          next: () => { this.snack.open('Contract created', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }
}
