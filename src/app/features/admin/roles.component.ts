import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RoleDef } from '../../core/models/admin';
import { AdminService } from './admin.service';

@Component({
  selector: 'sc-role-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Create role</h2>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-3 !pb-0">
        <div class="sc-field">
          <label class="sc-label">Role name</label>
          <input formControlName="name" class="sc-input" placeholder="e.g. COMPLIANCE_OFFICER" />
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
export class RoleFormDialog {
  ref = inject(MatDialogRef<RoleFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.controls.name.value); }
}

@Component({
  selector: 'sc-roles',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatProgressBarModule, MatTooltipModule],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Administration</div>
          <div class="sc-page-title mt-1">Roles</div>
          <div class="sc-page-sub">Permission groups assignable to users.</div>
        </div>
        <button class="sc-btn-primary" (click)="openCreate()">
          <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> New role
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
                <th class="px-5 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rows()" class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.id }}</td>
                <td class="px-5 py-3"><span class="sc-chip-brand">{{ r.name }}</span></td>
                <td class="px-5 py-3 text-right">
                  <button class="sc-icon-btn hover:!text-brand-400" (click)="del(r)" matTooltip="Delete role">
                    <mat-icon class="!text-[18px]">delete_outline</mat-icon>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && rows().length === 0" class="py-10 text-center text-ink-300">No roles.</div>
      </div>
    </div>
  `
})
export class RolesComponent {
  private admin = inject(AdminService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  rows = signal<RoleDef[]>([]);
  loading = signal(false);

  constructor() { this.refresh(); }

  refresh(): void {
    this.loading.set(true);
    this.admin.listRoles().subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load roles', 'OK', { duration: 3000 }); }
    });
  }

  openCreate(): void {
    this.dialog.open(RoleFormDialog, { width: '480px' })
      .afterClosed().subscribe((name: string | undefined) => {
        if (!name) return;
        this.admin.createRole(name).subscribe({
          next: () => { this.snack.open('Role created', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }

  del(r: RoleDef): void {
    if (!confirm(`Delete role "${r.name}"?`)) return;
    this.admin.deleteRole(r.id).subscribe({
      next: () => { this.snack.open('Role deleted', 'OK', { duration: 2500 }); this.refresh(); },
      error: () => this.snack.open('Delete failed', 'OK', { duration: 3000 })
    });
  }
}
