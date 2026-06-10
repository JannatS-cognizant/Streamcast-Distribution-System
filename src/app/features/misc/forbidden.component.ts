import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'sc-forbidden',
  standalone: true,
  imports: [RouterLink, MatIconModule],
  template: `
    <div class="min-h-screen flex items-center justify-center p-6 bg-bg">
      <div class="sc-card-padded max-w-md text-center animate-pop">
        <div class="mx-auto w-14 h-14 rounded-2xl bg-brand-500/10 flex items-center justify-center mb-3">
          <mat-icon class="!text-3xl !w-8 !h-8 text-brand-400">block</mat-icon>
        </div>
        <div class="sc-page-title">Access denied</div>
        <p class="sc-page-sub">Your role doesn't have permission to view this page.</p>
        <a routerLink="/dashboard" class="sc-btn-primary mt-5 inline-flex">
          <mat-icon class="!text-[18px] !w-5 !h-5">arrow_back</mat-icon>
          Back to dashboard
        </a>
      </div>
    </div>
  `
})
export class ForbiddenComponent {}
