import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AuthService } from '../../core/auth/auth.service';
import { Title } from '../../core/models/catalog';
import { TitlesService } from './titles.service';

@Component({
  selector: 'sc-title-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Create title</h2>
    <p class="text-sm text-ink-300 px-6 pt-1 pb-3">Add a new entry to the catalog.</p>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-2 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">Name</label>
            <input formControlName="name" class="sc-input" placeholder="Sample movie" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Release date</label>
            <input type="date" formControlName="releaseDate" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Language</label>
            <input formControlName="language" class="sc-input" placeholder="English" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Genre</label>
            <input formControlName="genre" class="sc-input" placeholder="Action" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Status</label>
            <select formControlName="status" class="sc-input">
              <option value="active">Active</option>
              <option value="draft">Draft</option>
              <option value="inactive">Inactive</option>
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
export class TitleFormDialog {
  ref = inject(MatDialogRef<TitleFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    releaseDate: ['', Validators.required],
    genre: ['', Validators.required],
    language: ['', Validators.required],
    status: ['active', Validators.required]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-titles',
  standalone: true,
  imports: [CommonModule, DatePipe, MatIconModule, MatProgressBarModule],
  template: `
    <div class="animate-fade space-y-6">
      <!-- Header -->
      <div class="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <div class="sc-section-title">Catalog</div>
          <div class="sc-page-title mt-1">Titles</div>
          <div class="sc-page-sub">Films and shows available in Streamcast.</div>
        </div>
        <div class="flex items-center gap-2">
          <div class="relative">
            <mat-icon class="!absolute left-3 top-1/2 -translate-y-1/2 !text-ink-500 !w-5 !h-5 !text-[20px]">search</mat-icon>
            <input class="sc-input pl-10 !h-11 !w-64" placeholder="Search titles…"
                   [value]="query()" (input)="query.set(toStr($event))" />
          </div>
          <button class="sc-btn-primary" *ngIf="canEdit()" (click)="openCreate()">
            <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> New title
          </button>
        </div>
      </div>

      <mat-progress-bar mode="indeterminate" *ngIf="loading()" class="rounded"></mat-progress-bar>

      <!-- Grid of movie cards -->
      <div *ngIf="filtered().length; else emptyState"
           class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 2xl:grid-cols-6 gap-4">
        <button *ngFor="let t of filtered(); trackBy: trackId" (click)="open(t)"
                class="group text-left sc-card sc-card-hover overflow-hidden">
          <!-- Poster -->
          <div class="relative aspect-[2/3] overflow-hidden bg-surface-3">
            <div class="absolute inset-0 transition-transform duration-500 ease-out group-hover:scale-110"
                 [style.background]="poster(t)"></div>
            <div class="absolute inset-x-0 bottom-0 h-1/2 bg-gradient-to-t from-surface-1/95 to-transparent"></div>
            <span class="absolute top-2 right-2 sc-chip"
                  [class.sc-chip-success]="isActive(t.status)"
                  [class.sc-chip-warn]="isDraft(t.status)"
                  [class.sc-chip-muted]="isInactive(t.status)">
              {{ t.status | uppercase }}
            </span>
            <div class="absolute inset-x-0 bottom-0 p-3">
              <div class="text-sm font-semibold text-white truncate">{{ t.name }}</div>
              <div class="text-[11px] text-ink-300 mt-0.5 truncate">
                {{ t.genre }} · {{ t.language }}
              </div>
            </div>
          </div>
          <!-- Footer -->
          <div class="flex items-center justify-between px-3 py-2.5">
            <div class="flex flex-col">
              <span class="text-xs text-ink-300">{{ t.releaseDate | date:'mediumDate' }}</span>
              <span class="text-[11px] text-ink-500 font-mono">ID: {{ t.id }}</span>
            </div>
            <mat-icon class="!text-[18px] text-ink-500 group-hover:text-brand-400 transition">play_circle</mat-icon>
          </div>
        </button>
      </div>

      <ng-template #emptyState>
        <div *ngIf="!loading()" class="sc-card-padded text-center text-ink-300">
          {{ rows().length ? 'No titles match your search.' : 'No titles yet.' }}
        </div>
      </ng-template>
    </div>
  `
})
export class TitlesComponent {
  private api = inject(TitlesService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private router = inject(Router);

  rows = signal<Title[]>([]);
  loading = signal(false);
  query = signal('');
  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN', 'CONTENT_OWNER']));

  filtered = computed(() => {
    const q = this.query().trim().toLowerCase();
    if (!q) return this.rows();
    return this.rows().filter(t =>
      `${t.name} ${t.genre} ${t.language} ${t.status}`.toLowerCase().includes(q)
    );
  });

  constructor() { this.refresh(); }

  refresh(): void {
    this.loading.set(true);
    this.api.list().subscribe({
      next: data => { this.rows.set(data); this.loading.set(false); },
      error: () => { this.loading.set(false); this.snack.open('Failed to load titles', 'OK', { duration: 3000 }); }
    });
  }

  openCreate(): void {
    this.dialog.open(TitleFormDialog, { width: '600px' })
      .afterClosed().subscribe((value: Title | undefined) => {
        if (!value) return;
        this.api.create(value).subscribe({
          next: () => { this.snack.open('Title created', 'OK', { duration: 2500 }); this.refresh(); },
          error: () => this.snack.open('Create failed', 'OK', { duration: 3000 })
        });
      });
  }

  open(t: Title): void {
    if (t.id != null) this.router.navigate(['/titles', t.id]);
  }

  trackId = (_: number, t: Title) => t.id ?? t.name;
  toStr = (e: Event) => (e.target as HTMLInputElement).value;

  // Status helpers — handles both lowercase (db) and uppercase (legacy)
  isActive   = (s?: string) => s?.toLowerCase() === 'active'   || s === 'AVAILABLE';
  isDraft    = (s?: string) => s?.toLowerCase() === 'draft'    || s === 'DRAFT';
  isInactive = (s?: string) => s?.toLowerCase() === 'inactive' || s === 'RETIRED';

  poster(t: Title): string {
    const palettes = [
      ['#7f1d3a', '#1c1e29'],
      ['#0c4a6e', '#0f1015'],
      ['#581c87', '#0f1015'],
      ['#065f46', '#0f1015'],
      ['#9a3412', '#0f1015'],
      ['#1e1b4b', '#15161e']
    ];
    const seed = (t.id ?? t.name?.length ?? 0) % palettes.length;
    const [a, b] = palettes[seed];
    return `linear-gradient(140deg, ${a} 0%, ${b} 100%)`;
  }
}
