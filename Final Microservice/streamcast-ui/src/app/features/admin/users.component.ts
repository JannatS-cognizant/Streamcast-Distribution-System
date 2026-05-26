import { CommonModule } from '@angular/common';
import { Component, Inject, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { forkJoin } from 'rxjs';
import { RoleDef, UserCreate, UserDef } from '../../core/models/admin';
import { AdminService } from './admin.service';

@Component({
  selector: 'sc-confirm-dialog-users',
  standalone: true,
  imports: [MatDialogModule, MatIconModule],
  template: `
    <div class="p-6">
      <div class="flex items-center gap-3 mb-4">
        <mat-icon [class]="data.color === 'amber'
          ? '!text-amber-400 !text-[28px] !w-7 !h-7'
          : '!text-red-400 !text-[28px] !w-7 !h-7'">
          {{ data.color === 'amber' ? 'info' : 'warning' }}
        </mat-icon>
        <h2 class="text-ink-100 text-lg font-semibold">{{ data.title }}</h2>
      </div>
      <p class="text-ink-300 text-sm mb-6">{{ data.message }}</p>
      <div class="flex justify-end gap-2">
        <button class="sc-btn-ghost" (click)="ref.close(false)">Cancel</button>
        <button [class]="data.color === 'amber'
          ? 'sc-btn-primary !bg-amber-500 hover:!bg-amber-600 flex items-center gap-1'
          : 'sc-btn-primary !bg-red-500 hover:!bg-red-600 flex items-center gap-1'"
                (click)="ref.close(true)">
          <mat-icon class="!text-[18px] !w-5 !h-5">
            {{ data.color === 'amber' ? 'block' : 'delete' }}
          </mat-icon>
          {{ data.confirmLabel ?? 'Confirm' }}
        </button>
      </div>
    </div>
  `
})
export class UsersConfirmDialog {
  ref  = inject(MatDialogRef<UsersConfirmDialog>);
  data = inject<{ title: string; message: string; confirmLabel?: string; color?: string }>(MAT_DIALOG_DATA);
}

@Component({
  selector: 'sc-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Create user</h2>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-3 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Full name</label>
            <input formControlName="name" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Email</label>
            <input type="email" formControlName="email" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Temporary password</label>
            <input type="text" formControlName="password" class="sc-input" />
          </div>
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Role</label>
            <select formControlName="roleId" class="sc-input">
              <option *ngFor="let r of data.roles" [ngValue]="r.id">{{ r.name }}</option>
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
export class UserFormDialog {
  ref = inject(MatDialogRef<UserFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    name:     ['', Validators.required],
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    roleId:   [1, Validators.required]
  });
  constructor(@Inject(MAT_DIALOG_DATA) public data: { roles: RoleDef[] }) {}
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-users',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatProgressBarModule,
            MatTooltipModule, MatDialogModule, UsersConfirmDialog],
  template: `
    <div class="animate-fade space-y-6">
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Administration</div>
          <div class="sc-page-title mt-1">Users</div>
          <div class="sc-page-sub">Manage who can sign in to the console.</div>
        </div>
        <div class="flex items-center gap-2">
          <button class="sc-btn-ghost" (click)="filter.set(filter()==='PENDING' ? 'ALL' : 'PENDING')">
            <mat-icon class="!text-[18px] !w-5 !h-5">
              {{ filter()==='PENDING' ? 'filter_alt_off' : 'pending_actions' }}
            </mat-icon>
            {{ filter()==='PENDING' ? 'Show all' : 'Pending only' }}
            <span *ngIf="pendingCount() > 0" class="ml-2 sc-chip-brand !text-[10px] !py-0">
              {{ pendingCount() }}
            </span>
          </button>
          <button class="sc-btn-primary" (click)="openCreate()">
            <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> New user
          </button>
        </div>
      </div>

      <div class="sc-card overflow-hidden">
        <mat-progress-bar mode="indeterminate" *ngIf="loading()"></mat-progress-bar>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-[11px] uppercase tracking-wider
                         text-ink-300 bg-surface-3 border-b border-border">
                <th class="px-5 py-3">ID</th>
                <th class="px-5 py-3">Name</th>
                <th class="px-5 py-3">Email</th>
                <th class="px-5 py-3">Role</th>
                <th class="px-5 py-3">Status</th>
                <th class="px-5 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of visibleRows()"
                  class="border-b border-border hover:bg-surface-3 transition">
                <td class="px-5 py-3 font-medium">{{ r.id }}</td>
                <td class="px-5 py-3">{{ r.name }}</td>
                <td class="px-5 py-3 text-ink-300">{{ r.email }}</td>
                <td class="px-5 py-3">
                  <span class="sc-chip-brand">{{ r.role || '—' }}</span>
                </td>
                <td class="px-5 py-3">
                  <span class="sc-chip"
                        [class.sc-chip-success]="!r.pending"
                        [class.sc-chip-danger]="r.pending">
                    {{ r.pending ? 'Pending' : 'Approved' }}
                  </span>
                </td>
                <td class="px-5 py-3 text-right whitespace-nowrap">
                  <button *ngIf="r.pending"
                          class="sc-btn-ghost !h-8 !px-2 !text-emerald-300 hover:!text-emerald-200"
                          (click)="approve(r)" matTooltip="Approve user">
                    <mat-icon class="!text-[16px] !w-4 !h-4">check_circle</mat-icon> Approve
                  </button>
                  <button *ngIf="!r.pending"
                          class="sc-btn-ghost !h-8 !px-2"
                          (click)="reject(r)" matTooltip="Revoke approval">
                    <mat-icon class="!text-[16px] !w-4 !h-4">block</mat-icon> Revoke
                  </button>
                  <button class="sc-icon-btn hover:!text-brand-400"
                          (click)="del(r)" matTooltip="Delete user">
                    <mat-icon class="!text-[18px]">delete_outline</mat-icon>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div *ngIf="!loading() && visibleRows().length === 0"
             class="py-10 text-center text-ink-300">
          {{ filter()==='PENDING' ? 'No users awaiting approval.' : 'No users.' }}
        </div>
      </div>
    </div>
  `
})
export class UsersComponent {
  private admin  = inject(AdminService);
  private dialog = inject(MatDialog);
  private snack  = inject(MatSnackBar);

  rows    = signal<UserDef[]>([]);
  roles   = signal<RoleDef[]>([]);
  loading = signal(false);
  filter  = signal<'ALL' | 'PENDING'>('ALL');

  pendingCount = computed(() => this.rows().filter(u => u.pending).length);
  visibleRows  = computed(() =>
    this.filter() === 'PENDING'
      ? this.rows().filter(u => u.pending)
      : this.rows()
  );

  constructor() { this.refresh(); }

  refresh(): void {
    this.loading.set(true);
    forkJoin({
      users: this.admin.listUsers(),
      roles: this.admin.listRoles()
    }).subscribe({
      next: ({ users, roles }) => {
        this.rows.set(users);
        this.roles.set(roles);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load users', 'OK', { duration: 3000 });
      }
    });
  }

  openCreate(): void {
    this.dialog.open(UserFormDialog, { width: '640px', data: { roles: this.roles() } })
      .afterClosed().subscribe((value: UserCreate | undefined) => {
        if (!value) return;
        this.admin.createUser(value).subscribe({
          next:  () => { this.snack.open('User created', 'OK', { duration: 2500 }); this.refresh(); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }

  del(u: UserDef): void {
    this.dialog.open(UsersConfirmDialog, {
      width: '420px',
      data: {
        title: 'Delete user',
        message: `Permanently delete "${u.email}"? They will lose all access.`,
        confirmLabel: 'Delete',
        color: 'red'
      }
    }).afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      this.admin.deleteUser(u.id).subscribe({
        next:  () => { this.snack.open('User deleted', 'OK', { duration: 2500 }); this.refresh(); },
        error: () => this.snack.open('Delete failed', 'OK', { duration: 3000 })
      });
    });
  }

  approve(u: UserDef): void {
    this.admin.approveUser(u.id).subscribe({
      next:  () => { this.snack.open(`Approved ${u.email}`, 'OK', { duration: 2500 }); this.refresh(); },
      error: err => this.snack.open(err?.error?.message ?? 'Approve failed', 'OK', { duration: 3000 })
    });
  }

  reject(u: UserDef): void {
    this.dialog.open(UsersConfirmDialog, {
      width: '420px',
      data: {
        title: 'Revoke access',
        message: `Revoke access for "${u.email}"? They will not be able to sign in.`,
        confirmLabel: 'Revoke',
        color: 'amber'
      }
    }).afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      this.admin.rejectUser(u.id).subscribe({
        next:  () => { this.snack.open(`Revoked ${u.email}`, 'OK', { duration: 2500 }); this.refresh(); },
        error: err => this.snack.open(err?.error?.message ?? 'Revoke failed', 'OK', { duration: 3000 })
      });
    });
  }
}