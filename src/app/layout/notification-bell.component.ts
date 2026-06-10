import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatBadgeModule } from '@angular/material/badge';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { NotificationsService } from '../core/notifications/notifications.service';

@Component({
  selector: 'sc-notification-bell',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink, MatBadgeModule, MatIconModule, MatMenuModule],
  template: `
    <button class="sc-icon-btn relative"
            [matMenuTriggerFor]="bellMenu"
            (menuOpened)="onOpen()"
            aria-label="Notifications">
      <mat-icon>notifications</mat-icon>
      <span *ngIf="notif.unseenCount() > 0"
            class="absolute -top-1 -right-1 min-w-[18px] h-[18px] px-1 rounded-full
                   bg-brand-500 text-white text-[10px] font-semibold leading-[18px]
                   text-center border-2 border-surface-1">
        {{ notif.unseenCount() > 99 ? '99+' : notif.unseenCount() }}
      </span>
    </button>

    <mat-menu #bellMenu="matMenu" xPosition="before" class="!min-w-[360px]">
      <div class="px-4 pt-3 pb-2 flex items-center justify-between border-b border-border">
        <div>
          <div class="text-sm font-semibold text-ink-100">Notifications</div>
          <div class="text-xs text-ink-300">{{ notif.items().length }} recent</div>
        </div>
        <button *ngIf="notif.items().length > 0"
                mat-menu-item class="!text-xs !text-ink-300 !min-h-[32px] !leading-[32px] !px-2"
                (click)="$event.stopPropagation(); notif.clearAll()">
          <mat-icon class="!text-[14px] !w-4 !h-4">clear_all</mat-icon> Clear all
        </button>
      </div>

      <div class="max-h-[420px] overflow-y-auto">
        <div *ngIf="notif.items().length === 0" class="px-4 py-8 text-center text-sm text-ink-300">
          You're all caught up.
        </div>

        <a *ngFor="let n of notif.items()"
           mat-menu-item
           [routerLink]="n.link"
           class="!h-auto !py-3 !px-4 !flex !items-start !gap-3 !border-b !border-border last:!border-0">
          <span class="w-8 h-8 rounded-lg flex items-center justify-center shrink-0"
                [ngClass]="n.kind==='CONTRACT'
                    ? 'bg-brand-500/15 text-brand-300'
                    : 'bg-violet-500/15 text-violet-300'">
            <mat-icon class="!text-[18px] !w-[18px] !h-[18px]">
              {{ n.kind === 'CONTRACT' ? 'description' : 'event' }}
            </mat-icon>
          </span>
          <span class="flex-1 min-w-0">
            <span class="flex items-center gap-2">
              <span class="text-sm font-medium text-ink-100">{{ n.title }}</span>
              <span *ngIf="!n.seen" class="w-1.5 h-1.5 rounded-full bg-brand-400"></span>
            </span>
            <span class="block text-xs text-ink-300 truncate">{{ n.summary }}</span>
            <span class="block text-[10px] text-ink-500 mt-0.5">{{ n.createdAt | date:'shortTime' }}</span>
          </span>
        </a>
      </div>
    </mat-menu>
  `
})
export class NotificationBellComponent {
  notif = inject(NotificationsService);

  onOpen(): void {
    this.notif.markAllSeen();
  }
}
