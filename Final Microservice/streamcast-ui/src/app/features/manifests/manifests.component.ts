import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';
import { Manifest } from '../../core/models/manifest';
import { ManifestsService } from './manifests.service';

@Component({
  selector: 'sc-manifest-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Create manifest</h2>
    <p class="text-sm text-ink-300 px-6 pt-1 pb-3">Package assets for delivery to a partner.</p>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-2 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field"><label class="sc-label">Title ID</label>
            <input formControlName="titleId" class="sc-input" /></div>
          <div class="sc-field"><label class="sc-label">Partner ID</label>
            <input type="number" min="1" formControlName="partnerId" class="sc-input" /></div>
          <div class="sc-field sm:col-span-2"><label class="sc-label">Asset IDs (JSON array)</label>
            <input formControlName="assetIdsJSON" class="sc-input" placeholder="[1,2,3]" /></div>
          <div class="sc-field sm:col-span-2"><label class="sc-label">Destination</label>
            <input formControlName="destination" class="sc-input" placeholder="s3://bucket/path" /></div>
          <div class="sc-field"><label class="sc-label">Created by</label>
            <input formControlName="createdBy" class="sc-input" /></div>
          <div class="sc-field"><label class="sc-label">Status</label>
            <select formControlName="status" class="sc-input">
              <option value="QUEUED">Queued</option>
              <option value="IN_PROGRESS">In progress</option>
              <option value="DELIVERED">Delivered</option>
              <option value="FAILED">Failed</option>
            </select></div>
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
export class ManifestFormDialog {
  ref = inject(MatDialogRef<ManifestFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    titleId: ['', Validators.required],
    assetIdsJSON: ['[]', Validators.required],
    destination: ['', Validators.required],
    createdBy: ['system', Validators.required],
    status: ['QUEUED', Validators.required],
    partnerId: [1, [Validators.required, Validators.min(1)]]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-manifests',
  standalone: true,
  imports: [CommonModule, DatePipe, MatIconModule, MatProgressBarModule],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Distribution</div>
          <div class="sc-page-title mt-1">Manifests</div>
          <div class="sc-page-sub">Delivery packages routed to partners.</div>
        </div>
        <button class="sc-btn-primary" *ngIf="canEdit()" (click)="openCreate()">
          <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> New manifest
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
                <th class="px-5 py-3">Partner</th>
                <th class="px-5 py-3">Destination</th>
                <th class="px-5 py-3">Created</th>
                <th class="px-5 py-3">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.id }}</td>
                <td class="px-5 py-3 text-ink-300">#{{ r.titleId }}</td>
                <td class="px-5 py-3 text-ink-300">#{{ r.partnerId }}</td>
                <td class="px-5 py-3 text-ink-300 max-w-xs truncate">{{ r.destination }}</td>
                <td class="px-5 py-3 text-ink-300">{{ r.createdAt | date:'medium' }}</td>
                <td class="px-5 py-3">
                  <span class="sc-chip"
                        [class.sc-chip-success]="r.status==='DELIVERED'"
                        [class.sc-chip-warn]="r.status==='IN_PROGRESS' || r.status==='QUEUED'"
                        [class.sc-chip-danger]="r.status==='FAILED'"
                        [class.sc-chip-muted]="!['DELIVERED','IN_PROGRESS','QUEUED','FAILED'].includes(r.status)">
                    {{ r.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">No manifests yet.</div>
      </div>
    </div>
  `
})
export class ManifestsComponent {
  private api = inject(ManifestsService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  rows = signal<Manifest[]>([]);
  loading = signal(false);
  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN','DISTRIBUTION_OPERATOR']));

  constructor() { this.refresh(); }

  refresh(): void {
    this.loading.set(true);
    this.api.list().subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load manifests', 'OK', { duration: 3000 }); }
    });
  }

  openCreate(): void {
    this.dialog.open(ManifestFormDialog, { width: '680px' })
      .afterClosed().subscribe((value: Manifest | undefined) => {
        if (!value) return;
        this.api.create(value).subscribe({
          next: () => { this.snack.open('Manifest created', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }
}
