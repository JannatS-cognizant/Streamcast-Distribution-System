import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../core/auth/auth.service';
import { Role } from '../core/models/user';
import { NotificationBellComponent } from './notification-bell.component';

interface NavItem { path: string; label: string; icon: string; roles: Role[]; }
interface NavSection { title: string; items: NavItem[]; }

const ALL: Role[] = ['ADMIN','CONTENT_OWNER','RIGHTS_MANAGER','SCHEDULER',
                     'DISTRIBUTION_OPERATOR','LEGAL_OFFICER','PARTNER_ADMIN','COMPLIANCE_OFFICER'];

const SECTIONS: NavSection[] = [
  { title: 'Overview', items: [
    { path: '/dashboard', label: 'Dashboard', icon: 'dashboard', roles: ALL }
  ]},
  { title: 'Catalog', items: [
    { path: '/titles', label: 'Titles', icon: 'movie',
      roles: ['ADMIN','CONTENT_OWNER','RIGHTS_MANAGER','SCHEDULER'] },
    { path: '/assets', label: 'Assets', icon: 'video_library',
      roles: ['ADMIN','CONTENT_OWNER','RIGHTS_MANAGER','SCHEDULER'] },
    { path: '/metadata', label: 'Metadata', icon: 'sell',
      roles: ['ADMIN','CONTENT_OWNER','RIGHTS_MANAGER'] }
  ]},
  { title: 'Rights', items: [
    { path: '/contracts', label: 'Contracts', icon: 'description',
      roles: ['ADMIN','RIGHTS_MANAGER','SCHEDULER','COMPLIANCE_OFFICER'] },
    { path: '/clauses', label: 'Clauses', icon: 'gavel',
      roles: ['ADMIN','RIGHTS_MANAGER','SCHEDULER','COMPLIANCE_OFFICER'] }
  ]},
  { title: 'Distribution', items: [
    { path: '/partners', label: 'Partners', icon: 'handshake',
      roles: ['ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN','RIGHTS_MANAGER'] },
    { path: '/manifests', label: 'Manifests', icon: 'local_shipping',
      roles: ['ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN'] },
    { path: '/receipts', label: 'Receipts', icon: 'receipt_long',
      roles: ['ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN'] }
  ]},
  { title: 'Scheduling', items: [
    { path: '/schedules', label: 'Schedules', icon: 'event',
      roles: ['ADMIN','SCHEDULER','RIGHTS_MANAGER','COMPLIANCE_OFFICER'] },
    { path: '/conflicts', label: 'Conflicts', icon: 'warning_amber',
      roles: ['ADMIN','SCHEDULER','COMPLIANCE_OFFICER'] }
  ]},
  { title: 'Analytics', items: [
    { path: '/usage', label: 'Usage', icon: 'insights',
      roles: ['ADMIN','COMPLIANCE_OFFICER','SCHEDULER'] }
  ]},
  { title: 'Compliance', items: [
    { path: '/compliance', label: 'Checks', icon: 'fact_check',
      roles: ['ADMIN','COMPLIANCE_OFFICER','LEGAL_OFFICER'] }
  ]},
  { title: 'Administration', items: [
    { path: '/users', label: 'Users', icon: 'group', roles: ['ADMIN'] },
    { path: '/roles', label: 'Roles', icon: 'badge', roles: ['ADMIN'] }
  ]}
];

@Component({
  selector: 'sc-shell',
  standalone: true,
  imports: [
    CommonModule, RouterLink, RouterLinkActive, RouterOutlet,
    MatIconModule, MatMenuModule, MatTooltipModule,
    NotificationBellComponent
  ],
  template: `
    <div class="min-h-screen flex bg-bg text-ink-100">

      <!-- Sidebar -->
      <aside class="hidden md:flex flex-col shrink-0 border-r border-border bg-surface-1"
             [class.w-64]="!collapsed()" [class.w-20]="collapsed()">
        <!-- Brand -->
        <div class="h-16 px-5 flex items-center gap-3 border-b border-border">
          <div class="relative shrink-0">
            <div class="absolute inset-0 rounded-xl bg-grad-brand blur-md opacity-50"></div>
            <img src="/assets/logo.svg" class="relative h-8 w-8 rounded-xl" />
          </div>
          <span class="font-semibold tracking-tight text-ink-100" *ngIf="!collapsed()">Streamcast</span>
        </div>

        <!-- Nav -->
        <nav class="flex-1 px-3 py-4 space-y-5 overflow-y-auto">
          <div *ngFor="let section of visibleSections()">
            <div *ngIf="!collapsed()" class="px-3 pb-1.5 sc-section-title">{{ section.title }}</div>
            <div class="space-y-1">
              <a *ngFor="let item of section.items" [routerLink]="item.path"
                 routerLinkActive="active"
                 class="group relative flex items-center gap-3 px-3 py-2.5 rounded-lg
                        text-ink-300 hover:text-ink-100 hover:bg-surface-3
                        transition"
                 [matTooltip]="collapsed() ? item.label : ''" matTooltipPosition="right">
                <span class="active-rail"></span>
                <mat-icon class="!text-[20px] shrink-0">{{ item.icon }}</mat-icon>
                <span *ngIf="!collapsed()" class="text-sm font-medium">{{ item.label }}</span>
              </a>
            </div>
          </div>
        </nav>

        <button class="mx-3 mb-3 sc-btn-ghost !h-10 !justify-start" (click)="collapsed.set(!collapsed())">
          <mat-icon class="!text-[18px] !w-5 !h-5">{{ collapsed() ? 'chevron_right' : 'chevron_left' }}</mat-icon>
          <span *ngIf="!collapsed()">Collapse</span>
        </button>
      </aside>

      <!-- Main column -->
      <div class="flex-1 flex flex-col min-w-0">
        <!-- Topbar -->
        <header class="sticky top-0 z-30 h-16 px-4 md:px-6 flex items-center gap-3
                       bg-surface-1/80 backdrop-blur border-b border-border">
          <button class="md:hidden sc-icon-btn" (click)="mobileOpen.set(!mobileOpen())">
            <mat-icon>menu</mat-icon>
          </button>
          <div class="text-sm text-ink-300 hidden sm:flex items-center gap-2">
            <span class="text-ink-100 font-semibold">Streamcast</span>
            <span class="text-ink-500">/</span>
            <span>{{ isAdmin() ? 'Admin Console' : 'Console' }}</span>
            <span *ngIf="isAdmin()"
                  class="ml-2 inline-flex items-center gap-1 px-2 py-0.5 rounded-full
                         bg-violet-500/15 border border-violet-500/30 text-violet-300
                         text-[10px] uppercase tracking-wider font-medium">
              <mat-icon class="!text-[12px] !w-3 !h-3">shield</mat-icon>
              Admin
            </span>
          </div>

          <div class="ml-auto flex items-center gap-2">
            <sc-notification-bell *ngIf="canSeeNotifications()"></sc-notification-bell>
            <span class="sc-chip-brand hidden sm:inline-flex">
              <mat-icon class="!text-[14px] !w-3.5 !h-3.5">verified_user</mat-icon>
              {{ role() }}
            </span>
            <button class="sc-icon-btn" [matMenuTriggerFor]="userMenu">
              <mat-icon>account_circle</mat-icon>
            </button>
            <mat-menu #userMenu="matMenu">
              <div class="px-4 py-2 text-sm">
                <div class="font-medium text-ink-100">{{ email() }}</div>
                <div class="text-ink-300 text-xs mt-0.5">{{ role() }}</div>
              </div>
              <button mat-menu-item (click)="logout()" class="!text-ink-100">
                <mat-icon class="!text-brand-400">logout</mat-icon><span>Sign out</span>
              </button>
            </mat-menu>
          </div>
        </header>

        <!-- Mobile nav drawer -->
        <div *ngIf="mobileOpen()"
             class="md:hidden fixed inset-x-0 top-16 bottom-0 z-30
                    bg-surface-1 border-t border-border overflow-y-auto animate-fade">
          <ng-container *ngFor="let section of visibleSections()">
            <div class="px-5 pt-4 pb-1 sc-section-title">{{ section.title }}</div>
            <a *ngFor="let item of section.items"
               [routerLink]="item.path" (click)="mobileOpen.set(false)"
               routerLinkActive="bg-surface-3 !text-ink-100"
               class="flex items-center gap-3 px-5 py-3 text-ink-300 hover:bg-surface-3">
              <mat-icon class="!text-[20px]">{{ item.icon }}</mat-icon>
              <span class="text-sm font-medium">{{ item.label }}</span>
            </a>
          </ng-container>
        </div>

        <main class="flex-1 p-4 md:p-6 lg:p-8 overflow-auto">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
  styles: [`
    a.active {
      background: linear-gradient(180deg, rgba(244,63,94,0.18), rgba(244,63,94,0.06));
      color: #ffffff;
    }
    a.active .active-rail {
      position: absolute;
      left: -12px;
      top: 8px;
      bottom: 8px;
      width: 3px;
      border-radius: 4px;
      background: linear-gradient(180deg, #f43f5e, #be123c);
    }
    a .active-rail { display: none; }
    a.active .active-rail { display: block; }
  `]
})
export class ShellComponent {
  private auth = inject(AuthService);

  collapsed = signal(false);
  mobileOpen = signal(false);

  email = computed(() => this.auth.user()?.email ?? '');
  role  = computed(() => this.auth.role() ?? '');
  isAdmin = computed(() => this.auth.role() === 'ADMIN');
  canSeeNotifications = computed(() => this.auth.hasAnyRole([
    'ADMIN', 'RIGHTS_MANAGER', 'SCHEDULER', 'COMPLIANCE_OFFICER'
  ]));

  visibleSections = computed(() => {
    const r = this.auth.role();
    if (!r) return [];
    return SECTIONS
      .map(s => ({ ...s, items: s.items.filter(i => i.roles.includes(r)) }))
      .filter(s => s.items.length > 0);
  });

  logout(): void { this.auth.logout(); }
}
