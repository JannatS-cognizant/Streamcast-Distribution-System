import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'sc-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="relative min-h-screen w-full flex items-center justify-center overflow-hidden bg-bg">
      <!-- Cinematic OTT-style background -->
      <div class="absolute inset-0">
        <div class="absolute inset-0 bg-grad-hero"></div>
        <div class="absolute -top-32 -left-32 w-[42rem] h-[42rem] rounded-full bg-brand-600/25 blur-[120px]"></div>
        <div class="absolute -bottom-40 -right-32 w-[42rem] h-[42rem] rounded-full bg-accent-500/20 blur-[120px]"></div>
        <div class="absolute inset-0 opacity-[0.08]"
             style="background-image: linear-gradient(to right, #ffffff 1px, transparent 1px),
                                       linear-gradient(to bottom, #ffffff 1px, transparent 1px);
                    background-size: 56px 56px;"></div>
        <div class="absolute inset-0 bg-gradient-to-t from-bg via-transparent to-bg/40"></div>
      </div>

      <div class="relative w-full max-w-md mx-4 animate-pop">
        <div class="sc-glass p-8 sm:p-10">
          <div class="flex flex-col items-center mb-8">
            <div class="relative">
              <div class="absolute inset-0 rounded-2xl bg-grad-brand blur-xl opacity-60"></div>
              <img src="/assets/logo.svg" alt="Streamcast"
                   class="relative h-14 w-14 rounded-2xl shadow-card" />
            </div>
            <h1 class="mt-5 text-2xl font-semibold tracking-tight text-ink-100">Forgot your password?</h1>
            <p class="mt-2 text-sm text-ink-300 text-center">
              Enter your email and we'll send a password reset link.
            </p>
          </div>

          <form *ngIf="!sent(); else doneTpl"
                [formGroup]="form" (ngSubmit)="submit()"
                (submit)="$event.preventDefault()" novalidate class="space-y-4">

            <div class="sc-field">
              <label for="email" class="sc-label">Email</label>
              <input id="email" type="email" formControlName="email"
                     autocomplete="username"
                     placeholder="you@company.com"
                     class="sc-input" />
            </div>

            <div *ngIf="error()"
                 class="flex items-start gap-2 px-3 py-2.5 rounded-lg
                        bg-brand-500/10 border border-brand-500/30 text-brand-300 text-sm">
              <mat-icon class="!text-base !w-5 !h-5 mt-0.5 shrink-0">error_outline</mat-icon>
              <div>{{ error() }}</div>
            </div>

            <button type="submit" class="sc-btn-primary !w-full !h-12"
                    [disabled]="form.invalid || loading()">
              <ng-container *ngIf="!loading(); else spin">
                <span>Send reset link</span>
                <mat-icon class="!text-[18px] !w-5 !h-5">send</mat-icon>
              </ng-container>
              <ng-template #spin>
                <mat-spinner diameter="22" color="accent"></mat-spinner>
              </ng-template>
            </button>

            <div class="pt-2 text-center text-sm text-ink-300">
              Remembered it?
              <a routerLink="/login"
                 class="text-brand-300 hover:text-brand-200 font-medium">Back to sign in</a>
            </div>
          </form>

          <ng-template #doneTpl>
            <div class="space-y-5">
              <div class="flex items-start gap-2 px-3 py-3 rounded-lg
                          bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-sm">
                <mat-icon class="!text-base !w-5 !h-5 mt-0.5 shrink-0">mark_email_read</mat-icon>
                <div>
                  If an account exists for <b>{{ form.controls.email.value }}</b>,
                  a password reset link has been sent. Please check your inbox.
                </div>
              </div>

              <a routerLink="/login" class="sc-btn-primary !w-full !h-12 !no-underline">
                <mat-icon class="!text-[18px] !w-5 !h-5">arrow_back</mat-icon>
                <span>Back to sign in</span>
              </a>
            </div>
          </ng-template>
        </div>
      </div>
    </div>
  `
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  sent = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]]
  });

  submit(): void {
    if (this.form.invalid || this.loading()) return;
    this.loading.set(true);
    this.error.set(null);

    this.auth.forgotPassword(this.form.getRawValue().email).subscribe({
      next: () => {
        this.loading.set(false);
        this.sent.set(true);
      },
      error: err => {
        this.loading.set(false);
        console.error('[forgot-password] error:', err);
        const serverMsg = err?.error?.message ?? err?.error;

        if (err?.status === 0) {
          this.error.set('Cannot reach the gateway. Make sure API-Gateway is running on :8082.');
        } else if (err?.status === 404) {
          // Per spec we still want to be friendly — pretend it worked.
          this.sent.set(true);
        } else if (err?.status === 400 && serverMsg) {
          this.error.set(typeof serverMsg === 'string' ? serverMsg : 'Request failed.');
        } else if (err?.status >= 500) {
          this.error.set(`Server error (${err.status}).`);
        } else if (err?.status) {
          this.error.set(`Request failed (${err.status})${serverMsg ? ': ' + serverMsg : ''}`);
        } else {
          this.error.set('Could not send the reset link. Please try again.');
        }
      }
    });
  }
}
