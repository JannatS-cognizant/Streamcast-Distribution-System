import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/auth/auth.service';

function matchPasswords(group: AbstractControl): ValidationErrors | null {
  const pw = group.get('newPassword')?.value;
  const confirm = group.get('confirmPassword')?.value;
  return pw && confirm && pw !== confirm ? { mismatch: true } : null;
}

@Component({
  selector: 'sc-reset-password',
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
            <h1 class="mt-5 text-2xl font-semibold tracking-tight text-ink-100">Reset your password</h1>
            <p class="mt-2 text-sm text-ink-300 text-center">
              Choose a new password for your StreamCast account.
            </p>
          </div>

          <ng-container *ngIf="!success(); else doneTpl">
            <div *ngIf="!token()"
                 class="flex items-start gap-2 px-3 py-3 rounded-lg
                        bg-brand-500/10 border border-brand-500/30 text-brand-300 text-sm mb-4">
              <mat-icon class="!text-base !w-5 !h-5 mt-0.5 shrink-0">error_outline</mat-icon>
              <div>
                Missing reset token. Please request a new
                <a routerLink="/forgot-password" class="underline text-brand-200">password reset link</a>.
              </div>
            </div>

            <form *ngIf="token()"
                  [formGroup]="form" (ngSubmit)="submit()"
                  (submit)="$event.preventDefault()" novalidate class="space-y-4">

              <div class="sc-field">
                <label for="newPassword" class="sc-label">New password</label>
                <div class="relative">
                  <input id="newPassword"
                         [type]="showNew() ? 'text' : 'password'"
                         formControlName="newPassword"
                         autocomplete="new-password"
                         placeholder="At least 8 characters"
                         class="sc-input pr-11" />
                  <button type="button" tabindex="-1" (click)="showNew.set(!showNew())"
                          class="absolute inset-y-0 right-2 my-auto sc-icon-btn">
                    <mat-icon class="!w-5 !h-5 !text-[20px]">{{ showNew() ? 'visibility_off' : 'visibility' }}</mat-icon>
                  </button>
                </div>
                <small *ngIf="form.controls.newPassword.touched && form.controls.newPassword.errors?.['minlength']"
                       class="text-brand-300">Password must be at least 8 characters.</small>
              </div>

              <div class="sc-field">
                <label for="confirmPassword" class="sc-label">Confirm new password</label>
                <div class="relative">
                  <input id="confirmPassword"
                         [type]="showConfirm() ? 'text' : 'password'"
                         formControlName="confirmPassword"
                         autocomplete="new-password"
                         placeholder="Re-enter password"
                         class="sc-input pr-11" />
                  <button type="button" tabindex="-1" (click)="showConfirm.set(!showConfirm())"
                          class="absolute inset-y-0 right-2 my-auto sc-icon-btn">
                    <mat-icon class="!w-5 !h-5 !text-[20px]">{{ showConfirm() ? 'visibility_off' : 'visibility' }}</mat-icon>
                  </button>
                </div>
                <small *ngIf="form.controls.confirmPassword.touched && form.errors?.['mismatch']"
                       class="text-brand-300">Passwords do not match.</small>
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
                  <span>Update password</span>
                  <mat-icon class="!text-[18px] !w-5 !h-5">lock_reset</mat-icon>
                </ng-container>
                <ng-template #spin>
                  <mat-spinner diameter="22" color="accent"></mat-spinner>
                </ng-template>
              </button>

              <div class="pt-2 text-center text-sm text-ink-300">
                <a routerLink="/login"
                   class="text-brand-300 hover:text-brand-200 font-medium">Back to sign in</a>
              </div>
            </form>
          </ng-container>

          <ng-template #doneTpl>
            <div class="space-y-5">
              <div class="flex items-start gap-2 px-3 py-3 rounded-lg
                          bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-sm">
                <mat-icon class="!text-base !w-5 !h-5 mt-0.5 shrink-0">check_circle</mat-icon>
                <div>Your password has been updated successfully. You can now sign in.</div>
              </div>

              <a routerLink="/login" class="sc-btn-primary !w-full !h-12 !no-underline">
                <span>Continue to sign in</span>
                <mat-icon class="!text-[18px] !w-5 !h-5">arrow_forward</mat-icon>
              </a>
            </div>
          </ng-template>
        </div>
      </div>
    </div>
  `
})
export class ResetPasswordComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  loading = signal(false);
  success = signal(false);
  showNew = signal(false);
  showConfirm = signal(false);
  error = signal<string | null>(null);

  token = signal<string>(this.route.snapshot.queryParamMap.get('token') ?? '');

  form = this.fb.nonNullable.group(
    {
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    },
    { validators: matchPasswords }
  );

  submit(): void {
    if (this.form.invalid || this.loading() || !this.token()) return;
    this.loading.set(true);
    this.error.set(null);

    const { newPassword } = this.form.getRawValue();

    this.auth.resetPassword(this.token(), newPassword).subscribe({
      next: () => {
        this.loading.set(false);
        this.success.set(true);
      },
      error: err => {
        this.loading.set(false);
        console.error('[reset-password] error:', err);
        const serverMsg = err?.error?.message ?? err?.error;

        if (err?.status === 0) {
          this.error.set('Cannot reach the gateway. Make sure API-Gateway is running on :8082.');
        } else if (err?.status === 400) {
          this.error.set(typeof serverMsg === 'string' && serverMsg
            ? serverMsg
            : 'Invalid or expired reset link.');
        } else if (err?.status >= 500) {
          this.error.set(`Server error (${err.status}).`);
        } else if (err?.status) {
          this.error.set(`Request failed (${err.status})${serverMsg ? ': ' + serverMsg : ''}`);
        } else {
          this.error.set('Could not reset password. Please try again.');
        }
      }
    });
  }
}
