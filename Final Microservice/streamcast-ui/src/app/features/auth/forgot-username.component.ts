import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'sc-forgot-username',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="relative min-h-screen w-full flex items-center justify-center overflow-hidden bg-bg">
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
            <h1 class="mt-5 text-2xl font-semibold tracking-tight text-ink-100">Forgot username</h1>
            <p class="mt-1 text-sm text-ink-300 text-center">
              Enter the email associated with your account.
            </p>
          </div>

          <form [formGroup]="form" (ngSubmit)="submit()"
                (submit)="$event.preventDefault()" novalidate class="space-y-4">

            <div class="sc-field">
              <label class="sc-label">Email</label>
              <input type="email" formControlName="email" autocomplete="email"
                     placeholder="you@company.com" class="sc-input" />
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
                <span>Send username</span>
                <mat-icon class="!text-[18px] !w-5 !h-5">mail</mat-icon>
              </ng-container>
              <ng-template #spin>
                <mat-spinner diameter="22" color="accent"></mat-spinner>
              </ng-template>
            </button>

            <div class="text-center text-sm text-ink-300 pt-2">
              <a routerLink="/login" class="text-brand-300 hover:text-brand-200 font-medium">Back to sign in</a>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class ForgotUsernameComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  loading = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]]
  });

  submit(): void {
    if (this.form.invalid || this.loading()) return;
    this.loading.set(true);
    this.error.set(null);
    this.success.set(null);

    this.auth.forgotUsername(this.form.controls.email.value).subscribe({
      next: msg => {
        this.loading.set(false);
        this.success.set(msg || 'Username sent to your email.');
      },
      error: err => {
        this.loading.set(false);
        const m = err?.error?.message;
        if (err?.status === 0) {
          this.error.set('Cannot reach the gateway. Make sure API-Gateway is running on :8082.');
        } else if (err?.status === 404) {
          this.error.set(m ?? 'No account found with that email.');
        } else {
          this.error.set(m ?? `Request failed (${err?.status ?? 'unknown'}).`);
        }
      }
    });
  }
}
