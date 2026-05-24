import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'sc-login',
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

      <!-- Login card -->
      <div class="relative w-full max-w-md mx-4 animate-pop">
        <div class="sc-glass p-8 sm:p-10">
          <!-- Logo / title -->
          <div class="flex flex-col items-center mb-8">
            <div class="relative">
              <div class="absolute inset-0 rounded-2xl bg-grad-brand blur-xl opacity-60"></div>
              <img src="/assets/logo.svg" alt="Streamcast"
                   class="relative h-14 w-14 rounded-2xl shadow-card" />
            </div>
            <h1 class="mt-5 text-2xl font-semibold tracking-tight text-ink-100">Streamcast</h1>
            <p class="mt-1 text-sm text-ink-300">Sign in to your console</p>
          </div>

          <form [formGroup]="form" (ngSubmit)="submit()"
                (submit)="$event.preventDefault()" novalidate class="space-y-4">

            <div class="sc-field">
              <label for="email" class="sc-label">Email or username</label>
              <input id="email" type="email" formControlName="email"
                     autocomplete="username"
                     placeholder="Email or username"
                     class="sc-input" />
            </div>

            <div class="sc-field">
              <label for="password" class="sc-label">Password</label>
              <div class="relative">
                <input id="password" [type]="show() ? 'text' : 'password'"
                       formControlName="password"
                       autocomplete="current-password"
                       placeholder="••••••••"
                       class="sc-input pr-11" />
                <button type="button" tabindex="-1" (click)="show.set(!show())"
                        class="absolute inset-y-0 right-2 my-auto sc-icon-btn">
                  <mat-icon class="!w-5 !h-5 !text-[20px]">{{ show() ? 'visibility_off' : 'visibility' }}</mat-icon>
                </button>
              </div>
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
                <span>Sign in</span>
                <mat-icon class="!text-[18px] !w-5 !h-5">arrow_forward</mat-icon>
              </ng-container>
              <ng-template #spin>
                <mat-spinner diameter="22" color="accent"></mat-spinner>
              </ng-template>
            </button>

            <div class="flex items-center justify-between text-xs text-ink-300 pt-1">
              <a routerLink="/forgot-password" class="hover:text-brand-300 transition">Forgot password?</a>
              <a routerLink="/forgot-username" class="hover:text-brand-300 transition">Forgot username?</a>
            </div>

            <div class="text-center text-sm text-ink-300 pt-2">
              New here?
              <a routerLink="/register" class="text-brand-300 hover:text-brand-200 font-medium">Create an account</a>
            </div>

            <div class="relative pt-4">
              <div class="absolute inset-x-0 top-1/2 border-t border-border"></div>
              <div class="relative flex justify-center">
                <span class="px-3 bg-surface-1/0 text-[11px] uppercase tracking-wider text-ink-500">or</span>
              </div>
            </div>

            <a routerLink="/admin/login"
               class="flex items-center justify-center gap-2 w-full h-11 rounded-lg
                      border border-violet-500/30 bg-violet-500/10 text-violet-200
                      hover:bg-violet-500/15 hover:text-violet-100 transition text-sm font-medium">
              <mat-icon class="!text-[18px] !w-5 !h-5">admin_panel_settings</mat-icon>
              Sign in as administrator
            </a>
          </form>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  show = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required]],
    password: ['', Validators.required]
  });

  submit(): void {
    if (this.form.invalid || this.loading()) return;
    this.loading.set(true);
    this.error.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: err => {
        this.loading.set(false);
        console.error('[login] error:', err);
        const serverMsg = err?.error?.message;

        if (err?.status === 0) {
          this.error.set('Cannot reach the gateway. Make sure API-Gateway is running on :8082.');
        } else if (err?.status === 400 && serverMsg) {
          this.error.set(serverMsg);
        } else if (err?.status === 401 || err?.status === 403) {
          this.error.set('Invalid email or password.');
        } else if (err?.status >= 500) {
          this.error.set(`Server error (${err.status}).`);
        } else if (err?.status) {
          this.error.set(`Request failed (${err.status})${serverMsg ? ': ' + serverMsg : ''}`);
        } else {
          this.error.set('Login failed. Please try again.');
        }
      }
    });
  }
}
