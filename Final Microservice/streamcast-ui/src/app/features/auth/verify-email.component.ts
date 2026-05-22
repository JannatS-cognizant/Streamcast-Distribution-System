import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'sc-verify-email',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="relative min-h-screen w-full flex items-center justify-center overflow-hidden bg-bg">
      <div class="absolute inset-0">
        <div class="absolute inset-0 bg-grad-hero"></div>
        <div class="absolute -top-32 -left-32 w-[42rem] h-[42rem] rounded-full bg-brand-600/25 blur-[120px]"></div>
        <div class="absolute -bottom-40 -right-32 w-[42rem] h-[42rem] rounded-full bg-accent-500/20 blur-[120px]"></div>
      </div>

      <div class="relative w-full max-w-md mx-4 animate-pop">
        <div class="sc-glass p-8 sm:p-10 text-center">
          <div class="flex flex-col items-center mb-6">
            <div class="relative">
              <div class="absolute inset-0 rounded-2xl bg-grad-brand blur-xl opacity-60"></div>
              <img src="/assets/logo.svg" alt="Streamcast"
                   class="relative h-14 w-14 rounded-2xl shadow-card" />
            </div>
            <h1 class="mt-5 text-2xl font-semibold tracking-tight text-ink-100">Verify email</h1>
          </div>

          <div *ngIf="loading()" class="flex flex-col items-center gap-3 py-4">
            <mat-spinner diameter="32" color="accent"></mat-spinner>
            <p class="text-sm text-ink-300">Verifying your email…</p>
          </div>

          <div *ngIf="!loading() && success()"
               class="flex flex-col items-center gap-3 py-2">
            <mat-icon class="!text-4xl !w-9 !h-9 text-emerald-400">check_circle</mat-icon>
            <p class="text-emerald-300">{{ success() }}</p>
            <a routerLink="/login" class="sc-btn-primary mt-3">
              <mat-icon class="!text-[18px] !w-5 !h-5">login</mat-icon>
              Go to sign in
            </a>
          </div>

          <div *ngIf="!loading() && error()"
               class="flex flex-col items-center gap-3 py-2">
            <mat-icon class="!text-4xl !w-9 !h-9 text-brand-400">error_outline</mat-icon>
            <p class="text-brand-300">{{ error() }}</p>
            <a routerLink="/login" class="sc-btn-ghost mt-3">
              <mat-icon class="!text-[18px] !w-5 !h-5">arrow_back</mat-icon>
              Back to sign in
            </a>
          </div>
        </div>
      </div>
    </div>
  `
})
export class VerifyEmailComponent {
  private auth = inject(AuthService);
  private route = inject(ActivatedRoute);

  loading = signal(true);
  success = signal<string | null>(null);
  error = signal<string | null>(null);

  constructor() {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.loading.set(false);
      this.error.set('Missing verification token.');
      return;
    }
    this.auth.verifyEmail(token).subscribe({
      next: msg => {
        this.loading.set(false);
        this.success.set(msg || 'Email verified successfully.');
      },
      error: err => {
        this.loading.set(false);
        this.error.set(err?.error?.message ?? 'Verification failed. The link may have expired.');
      }
    });
  }
}
