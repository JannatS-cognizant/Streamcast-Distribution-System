import { CommonModule, DatePipe } from '@angular/common';
import { Component, Input, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { AuthService } from '../../core/auth/auth.service';
import { Asset, Metadata, Title } from '../../core/models/catalog';
import { AssetsService } from './assets.service';
import { TitlesService } from './titles.service';

@Component({
  selector: 'sc-asset-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Add asset</h2>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-3 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field">
            <label class="sc-label">Asset type</label>
            <select formControlName="assetType" class="sc-input">
              <option value="VIDEO">Video</option>
              <option value="AUDIO">Audio</option>
              <option value="SUBTITLE">Subtitle</option>
              <option value="POSTER">Poster</option>
            </select>
          </div>
          <div class="sc-field">
            <label class="sc-label">Duration (s)</label>
            <input type="number" min="1" formControlName="duration" class="sc-input" />
          </div>
          <div class="sc-field sm:col-span-2">
            <label class="sc-label">File URI</label>
            <input formControlName="fileURI" class="sc-input" placeholder="https://…" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Checksum</label>
            <input formControlName="checksum" class="sc-input" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Status</label>
            <select formControlName="status" class="sc-input">
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </select>
          </div>
        </div>
      </mat-dialog-content>
      <mat-dialog-actions class="!px-6 !pt-5 !pb-6 !justify-end !gap-2">
        <button type="button" class="sc-btn-ghost" (click)="ref.close()">Cancel</button>
        <button type="submit" class="sc-btn-primary" [disabled]="form.invalid">Save</button>
      </mat-dialog-actions>
    </form>
  `
})
export class AssetFormDialog {
  ref = inject(MatDialogRef<AssetFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    assetType: ['VIDEO', Validators.required],
    fileURI: ['', Validators.required],
    checksum: ['', Validators.required],
    duration: [60, [Validators.required, Validators.min(1)]],
    status: ['ACTIVE', Validators.required]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-metadata-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title class="!text-ink-100 !text-lg !font-semibold !px-6 !pt-6 !pb-0">Add metadata</h2>
    <form [formGroup]="form" (ngSubmit)="save()" (submit)="$event.preventDefault()" novalidate>
      <mat-dialog-content class="!px-6 !pt-3 !pb-0">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="sc-field">
            <label class="sc-label">Key</label>
            <input formControlName="key" class="sc-input" placeholder="director" />
          </div>
          <div class="sc-field">
            <label class="sc-label">Value</label>
            <input formControlName="value" class="sc-input" placeholder="John Doe" />
          </div>
        </div>
      </mat-dialog-content>
      <mat-dialog-actions class="!px-6 !pt-5 !pb-6 !justify-end !gap-2">
        <button type="button" class="sc-btn-ghost" (click)="ref.close()">Cancel</button>
        <button type="submit" class="sc-btn-primary" [disabled]="form.invalid">Save</button>
      </mat-dialog-actions>
    </form>
  `
})
export class MetadataFormDialog {
  ref = inject(MatDialogRef<MetadataFormDialog>);
  private fb = inject(FormBuilder);
  form = this.fb.nonNullable.group({
    key: ['', Validators.required],
    value: ['', Validators.required]
  });
  save(): void { if (this.form.valid) this.ref.close(this.form.getRawValue()); }
}

@Component({
  selector: 'sc-title-detail',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink, MatTabsModule, MatIconModule, MatProgressBarModule],
  template: `
    <div class="animate-fade space-y-6">
      <!-- Breadcrumb -->
      <div class="flex items-center gap-2 text-sm text-ink-300">
        <a routerLink="/titles" class="hover:text-brand-400 inline-flex items-center gap-1 transition">
          <mat-icon class="!text-[16px] !w-4 !h-4">arrow_back</mat-icon> Titles
        </a>
        <span class="text-ink-500">/</span>
        <span class="text-ink-100 font-medium">{{ title()?.name ?? ('#' + id) }}</span>
      </div>

      <!-- Hero -->
      <section class="relative overflow-hidden rounded-3xl border border-border">
        <div class="absolute inset-0" [style.background]="banner()"></div>
        <div class="absolute inset-0 bg-gradient-to-t from-surface-1 via-surface-1/60 to-transparent"></div>
        <div class="relative p-6 sm:p-10 grid grid-cols-1 md:grid-cols-[180px_1fr] gap-6 items-end">
          <div class="aspect-[2/3] w-[180px] rounded-xl shadow-elev border border-border overflow-hidden hidden md:block"
               [style.background]="banner()"></div>
          <div>
            <ng-container *ngIf="title() as t; else loadingT">
              <div class="text-xs uppercase tracking-widest text-ink-300">{{ t.genre }} · {{ t.language }}</div>
              <h1 class="mt-1 text-3xl sm:text-5xl font-semibold tracking-tight">{{ t.name }}</h1>
              <div class="mt-3 flex items-center gap-3 flex-wrap">
                <span class="sc-chip-info">
                  <mat-icon class="!text-[14px] !w-3.5 !h-3.5">event</mat-icon>
                  {{ t.releaseDate | date:'mediumDate' }}
                </span>
                <span class="sc-chip"
                      [class.sc-chip-success]="t.status === 'AVAILABLE'"
                      [class.sc-chip-warn]="t.status === 'DRAFT'"
                      [class.sc-chip-muted]="t.status === 'RETIRED'">{{ t.status }}</span>
              </div>
            </ng-container>
            <ng-template #loadingT>
              <mat-progress-bar mode="indeterminate"></mat-progress-bar>
            </ng-template>
          </div>
        </div>
      </section>

      <!-- Tabs -->
      <mat-tab-group animationDuration="200ms" class="sc-card">
        <mat-tab label="Assets">
          <div class="p-4 flex justify-end" *ngIf="canEdit()">
            <button class="sc-btn-primary" (click)="addAsset()">
              <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> Add asset
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-y border-border">
                  <th class="px-5 py-3">ID</th>
                  <th class="px-5 py-3">Type</th>
                  <th class="px-5 py-3">File</th>
                  <th class="px-5 py-3">Duration</th>
                  <th class="px-5 py-3">Status</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let a of assets()"
                    class="border-b border-border hover:bg-surface-3 transition">
                  <td class="px-5 py-3 font-medium">{{ a.id }}</td>
                  <td class="px-5 py-3"><span class="sc-chip-info">{{ a.assetType }}</span></td>
                  <td class="px-5 py-3 max-w-xs truncate text-ink-300">{{ a.fileURI }}</td>
                  <td class="px-5 py-3 text-ink-300">{{ a.duration }}s</td>
                  <td class="px-5 py-3">
                    <span class="sc-chip" [class.sc-chip-success]="a.status==='ACTIVE'" [class.sc-chip-muted]="a.status!=='ACTIVE'">{{ a.status }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div *ngIf="assets().length === 0" class="py-10 text-center text-ink-300">No assets yet.</div>
        </mat-tab>

        <mat-tab label="Metadata">
          <div class="p-4 flex justify-end" *ngIf="canEdit()">
            <button class="sc-btn-primary" (click)="addMetadata()">
              <mat-icon class="!text-[18px] !w-5 !h-5">add</mat-icon> Add metadata
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr class="text-left text-[11px] uppercase tracking-wider text-ink-300 bg-surface-3 border-y border-border">
                  <th class="px-5 py-3">ID</th>
                  <th class="px-5 py-3">Key</th>
                  <th class="px-5 py-3">Value</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let m of metadata()"
                    class="border-b border-border hover:bg-surface-3 transition">
                  <td class="px-5 py-3">{{ m.id }}</td>
                  <td class="px-5 py-3 font-medium">{{ m.key }}</td>
                  <td class="px-5 py-3 text-ink-300">{{ m.value }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div *ngIf="metadata().length === 0" class="py-10 text-center text-ink-300">No metadata yet.</div>
        </mat-tab>
      </mat-tab-group>
    </div>
  `
})
export class TitleDetailComponent implements OnInit {
  @Input({ required: true }) id!: number;

  private titles = inject(TitlesService);
  private assetsApi = inject(AssetsService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  title = signal<Title | null>(null);
  assets = signal<Asset[]>([]);
  metadata = signal<Metadata[]>([]);

  canEdit = computed(() => this.auth.hasAnyRole(['ADMIN','CONTENT_OWNER']));
  banner = computed(() => {
    const t = this.title();
    if (!t) return 'linear-gradient(140deg, #1c1e29, #0f1015)';
    const palettes = [
      ['#7f1d3a','#1c1e29'], ['#0c4a6e','#0f1015'], ['#581c87','#0f1015'],
      ['#065f46','#0f1015'], ['#9a3412','#0f1015'], ['#1e1b4b','#15161e']
    ];
    const seed = (t.id ?? t.name?.length ?? 0) % palettes.length;
    return `linear-gradient(140deg, ${palettes[seed][0]} 0%, ${palettes[seed][1]} 100%)`;
  });

  ngOnInit(): void {
    const tid = Number(this.id);
    this.titles.get(tid).subscribe(t => this.title.set(t));
    this.assetsApi.listAssets(tid).subscribe(a => this.assets.set(a));
    this.assetsApi.listMetadata(tid).subscribe(m => this.metadata.set(m));
  }

  addAsset(): void {
    this.dialog.open(AssetFormDialog, { width: '640px', panelClass: 'sc-dialog' })
      .afterClosed().subscribe(v => {
        if (!v) return;
        this.assetsApi.createAsset(Number(this.id), v).subscribe({
          next: () => { this.snack.open('Asset added', 'OK', { duration: 2500 });
                        this.assetsApi.listAssets(Number(this.id)).subscribe(a => this.assets.set(a)); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }

  addMetadata(): void {
    this.dialog.open(MetadataFormDialog, { width: '520px', panelClass: 'sc-dialog' })
      .afterClosed().subscribe(v => {
        if (!v) return;
        this.assetsApi.createMetadata(Number(this.id), v).subscribe({
          next: () => { this.snack.open('Metadata added', 'OK', { duration: 2500 });
                        this.assetsApi.listMetadata(Number(this.id)).subscribe(m => this.metadata.set(m)); },
          error: err => this.snack.open(err?.error?.message ?? 'Create failed', 'OK', { duration: 3000 })
        });
      });
  }
}
