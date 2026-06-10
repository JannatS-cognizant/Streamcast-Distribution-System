import { CommonModule } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/auth/auth.service';
import { Role } from '../../core/models/user';

interface Tile {
  title: string; description: string; icon: string; link: string;
  accent: 'brand' | 'accent' | 'emerald' | 'amber' | 'violet' | 'slate';
  roles: Role[];
}

const TILES: Tile[] = [
  { title: 'Titles',     description: 'Catalog films & shows with assets',
    icon: 'movie',          link: '/titles',    accent: 'brand',
    roles: ['ADMIN','CONTENT_OWNER','RIGHTS_MANAGER','SCHEDULER'] },
  { title: 'Contracts',  description: 'Rights, exclusivity, territories',
    icon: 'description',    link: '/contracts', accent: 'accent',
    roles: ['ADMIN','RIGHTS_MANAGER','SCHEDULER','COMPLIANCE_OFFICER'] },
  { title: 'Clauses',    description: 'Legal clauses on contracts',
    icon: 'gavel',          link: '/clauses',   accent: 'amber',
    roles: ['ADMIN','RIGHTS_MANAGER','SCHEDULER','COMPLIANCE_OFFICER'] },
  { title: 'Partners',   description: 'Distribution endpoints',
    icon: 'handshake',      link: '/partners',  accent: 'emerald',
    roles: ['ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN','RIGHTS_MANAGER'] },
  { title: 'Manifests',  description: 'Delivery packages & attempts',
    icon: 'local_shipping', link: '/manifests', accent: 'slate',
    roles: ['ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN'] },
  { title: 'Schedules',  description: 'Broadcast windows',
    icon: 'event',          link: '/schedules', accent: 'violet',
    roles: ['ADMIN','SCHEDULER','RIGHTS_MANAGER','COMPLIANCE_OFFICER'] },
  { title: 'Conflicts',  description: 'Overlapping schedule detection',
    icon: 'warning_amber',  link: '/conflicts', accent: 'amber',
    roles: ['ADMIN','SCHEDULER','COMPLIANCE_OFFICER'] },
  { title: 'Usage',      description: 'Views and revenue insights',
    icon: 'insights',       link: '/usage',     accent: 'brand',
    roles: ['ADMIN','COMPLIANCE_OFFICER','SCHEDULER'] },
  { title: 'Compliance', description: 'Schedule-vs-contract checks',
    icon: 'fact_check',     link: '/compliance', accent: 'amber',
    roles: ['ADMIN','COMPLIANCE_OFFICER','LEGAL_OFFICER'] },
  { title: 'Users',      description: 'Manage console access',
    icon: 'group',          link: '/users',     accent: 'accent',
    roles: ['ADMIN'] },
  { title: 'Roles',      description: 'Permission groups',
    icon: 'badge',          link: '/roles',     accent: 'emerald',
    roles: ['ADMIN'] }
];

@Component({
  selector: 'sc-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule],
  template: `
    <div class="space-y-8 animate-fade">

      <!-- Hero -->
      <section class="relative overflow-hidden rounded-3xl border border-border bg-grad-hero">
        <div class="absolute -top-24 -right-24 w-[26rem] h-[26rem] rounded-full bg-brand-600/30 blur-[100px]"></div>
        <div class="absolute -bottom-24 -left-24 w-[22rem] h-[22rem] rounded-full bg-accent-500/20 blur-[100px]"></div>
        <div class="relative p-6 sm:p-10">
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full
                      bg-white/[0.04] border border-white/10 text-xs text-ink-300">
            <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
            Live · {{ now }}
          </div>
          <h1 class="mt-4 text-3xl sm:text-5xl font-semibold tracking-tight">
            Welcome back, <span class="bg-gradient-to-r from-brand-300 via-brand-400 to-accent-300 bg-clip-text text-transparent">{{ name() }}</span>
          </h1>
          <p class="mt-2 text-ink-300 max-w-2xl">
            Signed in as <span class="sc-chip-brand">{{ role() }}</span>.
            Explore the modules available to your role below.
          </p>

          <div class="mt-6 grid grid-cols-2 sm:grid-cols-4 gap-3 max-w-3xl">
            <div *ngFor="let s of stats()" class="sc-glass !rounded-xl p-4">
              <div class="text-2xl font-semibold">{{ s.value }}</div>
              <div class="text-[11px] uppercase tracking-wider text-ink-300 mt-0.5">{{ s.label }}</div>
            </div>
          </div>
        </div>
      </section>

      <!-- Modules -->
      <section>
        <div class="flex items-end justify-between mb-4">
          <div>
            <div class="sc-section-title">Modules</div>
            <div class="sc-page-title mt-1">Your workspace</div>
          </div>
          <div class="hidden sm:block text-xs text-ink-500">{{ tiles().length }} available</div>
        </div>

        <div *ngIf="tiles().length; else empty"
             class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 2xl:grid-cols-4 gap-4">
          <a *ngFor="let t of tiles()" [routerLink]="t.link"
             class="group relative overflow-hidden sc-card sc-card-hover p-5">
            <div class="absolute -top-10 -right-10 w-32 h-32 rounded-full opacity-30 blur-2xl transition group-hover:opacity-50"
                 [class.bg-brand-500]="t.accent==='brand'"
                 [class.bg-accent-500]="t.accent==='accent'"
                 [class.bg-emerald-500]="t.accent==='emerald'"
                 [class.bg-amber-500]="t.accent==='amber'"
                 [class.bg-violet-500]="t.accent==='violet'"
                 [class.bg-slate-500]="t.accent==='slate'"></div>
            <div class="relative flex items-start justify-between">
              <div class="w-11 h-11 rounded-xl flex items-center justify-center
                          bg-white/[0.04] border border-white/10">
                <mat-icon class="!text-[22px]"
                          [class.text-brand-400]="t.accent==='brand'"
                          [class.text-accent-400]="t.accent==='accent'"
                          [class.text-emerald-400]="t.accent==='emerald'"
                          [class.text-amber-400]="t.accent==='amber'"
                          [class.text-violet-400]="t.accent==='violet'"
                          [class.text-slate-300]="t.accent==='slate'">{{ t.icon }}</mat-icon>
              </div>
              <mat-icon class="text-ink-500 group-hover:text-ink-100 group-hover:translate-x-1 transition">
                arrow_forward
              </mat-icon>
            </div>
            <div class="relative mt-5">
              <div class="text-lg font-semibold text-ink-100">{{ t.title }}</div>
              <div class="text-sm text-ink-300 mt-1">{{ t.description }}</div>
            </div>
          </a>
        </div>

        <ng-template #empty>
          <div class="sc-card-padded text-center text-ink-300">
            No modules are currently available to your role.
          </div>
        </ng-template>
      </section>
    </div>
  `
})
export class DashboardComponent {
  private auth = inject(AuthService);

  name = computed(() => {
    const email = this.auth.user()?.email ?? '';
    return email ? email.split('@')[0] : 'there';
  });
  role = computed(() => this.auth.role());

  tiles = computed(() => {
    const r = this.auth.role();
    return r ? TILES.filter(t => t.roles.includes(r)) : [];
  });

  stats = computed(() => [
    { label: 'Services',  value: 10 },
    { label: 'Roles',     value: 8 },
    { label: 'Modules',   value: this.tiles().length },
    { label: 'Status',    value: 'OK' }
  ]);

  now = new Date().toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
}
