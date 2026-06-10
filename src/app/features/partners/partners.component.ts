import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';
import { Partner } from '../../core/models/partner';
import { PartnersService } from './partners.service';

@Component({
  selector: 'sc-partner-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Create partner</h2>
    <p class="text-sm text-ink-300 px-6 pt-1 pb-3">Register a distribution partner.</p>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-2 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Name</label>
            <input formControlName="name" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Contact (10-digit phone)</label>
            <input formControlName="contactInfo" maxlength="10" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Linked Title ID</label>
            <input type="number" min="1" formControlName="titleId" class="sc-input" />
          </div>
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Endpoint details</label>
            <textarea rows="3" formControlName="endpointDetailsNote" class="sc-input"></textarea>
          </div>
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Status</label>
            <select formControlName="status" class="sc-input">
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </select>
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
export class PartnerFormDialog {
  ref = inject(MatDialogRef<PartnerFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    contactInfo: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
    endpointDetailsNote: ['', Validators.required],
    titleId: [1, [Validators.required, Validators.min(1)]],
    status: ['ACTIVE' as 'ACTIVE' | 'INACTIVE', Validators.required]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-partners',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatProgressBarModule],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Distribution</div>
          <div class="sc-page-title mt-1">Partners</div>
          <div class="sc-page-sub">Distribution partners and their endpoints.</div>
        </div>
        <button class="sc-btn-primary" *ngIf="canEdit()" (click)="openCreate()">
          <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> New partner
        </button>
      </div>

      <div class="sc-card overflow-hidden">
        <mat-progress-bar mode="indeterminate" *ngIf="loading()"></mat-progress-bar>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-5 py-3">ID</th>
                <th class="px-5 py-3">Name</th>
                <th class="px-5 py-3">Contact</th>
                <th class="px-5 py-3">Title</th>
                <th class="px-5 py-3">Endpoint</th>
                <th class="px-5 py-3">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.partnerId }}</td>
                <td class="px-5 py-3">{{ r.name }}</td>
                <td class="px-5 py-3 text-ink-300">{{ r.contactInfo }}</td>
                <td class="px-5 py-3 text-ink-300">#{{ r.titleId }}</td>
                <td class="px-5 py-3 text-ink-300 max-w-xs truncate">{{ r.endpointDetailsNote }}</td>
                <td class="px-5 py-3">
                  <span class="sc-chip" [class.sc-chip-success]="r.status==='ACTIVE'" [class.sc-chip-muted]="r.status!=='ACTIVE'">{{ r.status }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">No partners yet.</div>
      </div>
    </div>
  `
})
export class PartnersComponent {
  private api = inject(PartnersService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  rows = signal<Partner[]>([]);
  loading = signal(false);
  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN','DISTRIBUTION_OPERATOR']));

  constructor() { this.refresh(); }

  refresh(): void {
    this.loading.set(true);
    this.api.list().subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load partners', 'OK', { duration: 3000 }); }
    });
  }

  openCreate(): void {
    this.dialog.open(PartnerFormDialog, { width: '680px' })
      .afterClosed().subscribe((value: Partner | undefined) => {
        if (!value) return;
        this.api.create(value).subscribe({
          next: () => { this.snack.open('Partner created', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }
}
