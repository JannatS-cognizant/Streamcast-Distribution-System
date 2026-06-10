import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../../core/auth/auth.service';
import { Metadata } from '../../core/models/catalog';
import { MetadataFormDialog } from './title-detail.component';
import { AssetsService, MetadataWrite } from './assets.service';

@Component({
  selector: 'sc-metadata',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule, MatProgressBarModule, MatTooltipModule],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Catalog</div>
          <div class="sc-page-title mt-1">Title Metadata</div>
          <div class="sc-page-sub">Key/value attributes attached to each title.</div>
        </div>
      </div>

      <div class="sc-card p-4 flex items-end gap-3 flex-wrap">
        <div class="sc-field" style="min-width:180px">
          <label class="sc-label">Title ID</label>
          <input
            type="number"
            min="1"
            [(ngModel)]="titleIdInput"
            class="sc-input"
            [class.!border-red-500]="titleIdInput !== null && titleIdInput < 1"
            (keydown.enter)="load()"
            placeholder="e.g. 1"
          />
          <span *ngIf="titleIdInput !== null && titleIdInput < 1"
                class="text-xs text-red-400 mt-1 block">
            Title ID must be a positive number.
          </span>
        </div>
        <button class="sc-btn-primary !h-11" (click)="load()"
                [disabled]="!titleIdInput || titleIdInput < 1">
          <mat-icon class="!text-[18px] !w-5 !h-5">search</mat-icon> Load
        </button>
        <button class="sc-btn-primary !h-11 ml-auto"
                *ngIf="canEdit() && titleId()" (click)="add()">
          <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> Add metadata
        </button>
      </div>

      <div class="sc-card overflow-hidden" *ngIf="titleId()">
        <mat-progress-bar mode="indeterminate" *ngIf="loading()"></mat-progress-bar>
        <div class="px-5 py-3 text-sm text-ink-300 border-b border-border">
          Showing metadata for title <span class="text-ink-100 font-medium">#{{ titleId() }}</span>
          <span *ngIf="rows().length && rows()[0].titleDTO?.name"> &middot; {{ rows()[0].titleDTO?.name }}</span>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-5 py-3">ID</th>
                <th class="px-5 py-3">Key</th>
                <th class="px-5 py-3">Value</th>
                <th class="px-5 py-3 text-right" *ngIf="canEdit() || canDelete()">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let m of rows()"
                  class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3">{{ m.id }}</td>
                <td class="px-5 py-3 font-medium">{{ m.key }}</td>
                <td class="px-5 py-3 text-ink-300">{{ m.value }}</td>
                <td class="px-5 py-3 text-right whitespace-nowrap" *ngIf="canEdit() || canDelete()">
                  <button *ngIf="canEdit()" class="sc-icon-btn" (click)="edit(m)" matTooltip="Edit metadata">
                    <mat-icon class="!text-[18px]">edit</mat-icon>
                  </button>
                  <button *ngIf="canDelete()" class="sc-icon-btn hover:!text-brand-400" (click)="del(m)" matTooltip="Delete metadata">
                    <mat-icon class="!text-[18px]">delete_outline</mat-icon>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0"
             class="py-10 text-center text-ink-300">No metadata for this title.</div>
      </div>

      <div *ngIf="!titleId()" class="sc-card p-10 text-center text-ink-300">
        Enter a title ID above to load its metadata.
      </div>
    </div>
  `
})
export class MetadataComponent {
  private api = inject(AssetsService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  titleIdInput: number | null = null;
  titleId = signal<number | null>(null);
  rows = signal<Metadata[]>([]);
  loading = signal(false);

  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN', 'CONTENT_OWNER']));
  canDelete = computed(() => this.auth.hasAnyRole(['ADMIN']));

  load(): void {
    if (!this.titleIdInput || this.titleIdInput < 1) return;
    const tid = Number(this.titleIdInput);
    this.titleId.set(tid);
    this.refresh();
  }

  private refresh(): void {
    const tid = this.titleId();
    if (!tid) return;
    this.loading.set(true);
    this.api.listMetadata(tid).subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load metadata', 'OK', { duration: 3000 }); }
    });
  }

  add(): void {
    const tid = this.titleId();
    if (!tid) return;
    this.dialog.open(MetadataFormDialog, { width: '520px', panelClass: 'sc-dialog', data: {} })
      .afterClosed().subscribe((v: MetadataWrite | undefined) => {
        if (!v) return;
        this.api.createMetadata(tid, v).subscribe({
          next: () => { this.snack.open('Metadata added', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }

  edit(m: Metadata): void {
    if (!m.id) return;
    this.dialog.open(MetadataFormDialog, { width: '520px', panelClass: 'sc-dialog', data: { metadata: m } })
      .afterClosed().subscribe((v: MetadataWrite | undefined) => {
        if (!v) return;
        this.api.updateMetadata(m.id!, v).subscribe({
          next: () => { this.snack.open('Metadata updated', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Update failed', 'OK', { duration: 3000 })
        });
      });
  }

  del(m: Metadata): void {
    if (!m.id) return;
    if (!confirm(`Delete metadata "${m.key}"?`)) return;
    this.api.deleteMetadata(m.id).subscribe({
      next: () => { this.snack.open('Metadata deleted', 'OK', { duration: 2500 }); this.refresh(); },
      error: err => this.snack.open(err?.error?.message ?? 'Delete failed', 'OK', { duration: 3000 })
    });
  }
}
