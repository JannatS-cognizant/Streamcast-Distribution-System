import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';
import { RoleDef } from '../../core/models/admin';

@Component({
  selector: 'sc-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="relative min-h-screen w-full flex items-center justify-center overflow-hidden bg-bg py-10">
      <div class="absolute inset-0">
        <div class="absolute inset-0 bg-grad-hero"></div>
        <div class="absolute -top-32 -left-32 w-[42rem] h-[42rem] rounded-full bg-brand-600/25 blur-[120px]"></div>
        <div class="absolute -bottom-40 -right-32 w-[42rem] h-[42rem] rounded-full bg-accent-500/20 blur-[120px]"></div>
      </div>

      <div class="relative w-full max-w-md mx-4 animate-pop">
        <div class="sc-glass p-8 sm:p-10">
          <div class="flex flex-col items-center mb-8">
            <div class="relative">
              <div class="absolute inset-0 rounded-2xl bg-grad-brand blur-xl opacity-60"></div>
              <img src="/assets/logo.svg" alt="Streamcast"
                   class="relative h-14 w-14 rounded-2xl shadow-card" />
            </div>
            <h1 class="mt-5 text-2xl font-semibold tracking-tight text-ink-100">Create account</h1>
            <p class="mt-1 text-sm text-ink-300">Register a new Streamcast user</p>
          </div>

          <form [formGroup]="form" (ngSubmit)="submit()"
                (submit)="$event.preventDefault()" novalidate class="space-y-4">

            <div class="sc-field">
              <label class="sc-label">Full name</label>
              <input type="text" formControlName="name"
                     placeholder="Jane Doe" class="sc-input" />
            </div>
            
            <div class="sc-field">
              <label class="sc-label">Username</label>
              <input type="text" formControlName="username" placeholder="Choose a username" class="sc-input" />
            </div>

            <div class="sc-field">
              <label class="sc-label">Email</label>
              <input type="email" formControlName="email"
                     autocomplete="email"
                     placeholder="you@company.com" class="sc-input" />
            </div>

            <div class="sc-field">
              <label class="sc-label">Password</label>
              <div class="relative">
                <input [type]="show() ? 'text' : 'password'" formControlName="password"
                       autocomplete="new-password"
                       placeholder="At least 6 characters" class="sc-input pr-11" />
                <button type="button" tabindex="-1" (click)="show.set(!show())"
                        class="absolute inset-y-0 right-2 my-auto sc-icon-btn">
                  <mat-icon class="!w-5 !h-5 !text-[20px]">{{ show() ? 'visibility_off' : 'visibility' }}</mat-icon>
                </button>
              </div>
            </div>

            <div class="sc-field">
              <label class="sc-label">Role</label>
              <select formControlName="requestedRoleId" class="sc-input">
                <option [ngValue]="null" disabled>Select a role…</option>
                <option *ngFor="let r of roles()" [ngValue]="r.id">{{ r.name }}</option>
              </select>
              <p *ngIf="rolesError()" class="text-xs text-brand-300 mt-1">{{ rolesError() }}</p>
            </div>

            <div *ngIf="error()"
                 class="flex items-start gap-2 px-3 py-2.5 rounded-lg
                        bg-brand-500/10 border border-brand-500/30 text-brand-300 text-sm">
              <mat-icon class="!text-base !w-5 !h-5 mt-0.5 shrink-0">error_outline</mat-icon>
              <div>{{ error() }}</div>
            </div>

            <div *ngIf="success()"
                 class="flex items-start gap-2 px-3 py-2.5 rounded-lg
                        bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-sm">
              <mat-icon class="!text-base !w-5 !h-5 mt-0.5 shrink-0">check_circle</mat-icon>
              <div>{{ success() }}</div>
            </div>

            <button type="submit" class="sc-btn-primary !w-full !h-12"
                    [disabled]="form.invalid || loading()">
              <ng-container *ngIf="!loading(); else spin">
                <span>Create account</span>
                <mat-icon class="!text-[18px] !w-5 !h-5">person_add</mat-icon>
              </ng-container>
              <ng-template #spin>
                <mat-spinner diameter="22" color="accent"></mat-spinner>
              </ng-template>
            </button>

            <div class="text-center text-sm text-ink-300 pt-2">
              Already have an account?
              <a routerLink="/login" class="text-brand-300 hover:text-brand-200 font-medium">Sign in</a>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private http = inject(HttpClient);
  private router = inject(Router);

  loading = signal(false);
  show = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);
  roles = signal<RoleDef[]>([]);
  rolesError = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    username: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(6)]],
    requestedRoleId: [null as number | null, Validators.required]
  });

  constructor() {
    this.loadRoles();
  }

  private loadRoles(): void {
    // Public read-only role list scoped for the registration form. /roles (admin-only)
    // would 401 here because the user isn't logged in yet.
    this.http.get<RoleDef[]>(`${environment.apiBase}/roles`).subscribe({
      next: list => this.roles.set(list),
      error: () => {
        this.rolesError.set('Could not load roles from server — using defaults.');
        this.roles.set([
          { id: 1, name: 'ADMIN' },
          { id: 2, name: 'CONTENT_OWNER' },
          { id: 3, name: 'RIGHTS_MANAGER' },
          { id: 4, name: 'SCHEDULER' },
          { id: 5, name: 'DISTRIBUTION_OPERATOR' },
          { id: 6, name: 'LEGAL_OFFICER' },
          { id: 7, name: 'PARTNER_ADMIN' },
          { id: 8, name: 'COMPLIANCE_OFFICER' }
        ]);
      }
    });
  }

  submit(): void {
    if (this.form.invalid || this.loading()) return;
    this.loading.set(true);
    this.error.set(null);
    this.success.set(null);

    const v = this.form.getRawValue();
    this.auth.register({
      name: v.name,
      email: v.email,
      username: v.username,
      password: v.password,
      requestedRoleId: v.requestedRoleId as number
    }).subscribe({
      next: () => {
        this.loading.set(false);
        this.success.set(
          'Account created. Verify your email, then wait for an administrator to approve your account before signing in.'
        );
        setTimeout(() => this.router.navigate(['/login']), 4000);
      },
      error: err => {
        this.loading.set(false);
        const msg = err?.error?.message;
        if (err?.status === 0) {
          this.error.set('Cannot reach the gateway. Make sure API-Gateway is running on :8082.');
        } else if (err?.status === 400) {
          this.error.set(msg ?? 'Registration failed — check your details.');
        } else if (err?.status === 404) {
          this.error.set(msg ?? 'Selected role was not found.');
        } else {
          this.error.set(msg ?? `Registration failed (${err?.status ?? 'unknown'}).`);
        }
      }
    });
  }
}
