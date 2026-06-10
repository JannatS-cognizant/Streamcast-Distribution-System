import { Injectable, computed, effect, inject, signal } from '@angular/core';
import { Subscription, interval, startWith, switchMap } from 'rxjs';
import { ContractsService } from '../../features/contracts/contracts.service';
import { SchedulesService } from '../../features/schedules/schedules.service';
import { Contract } from '../models/contract';
import { CalendarSchedule } from '../models/schedule';
import { AuthService } from '../auth/auth.service';
import { Notification } from './notification.model';

const POLL_MS = 5_000;   // 5 seconds for demo — change to 30_000 for production
const STORAGE_LAST_SEEN = 'sc.notifications.lastSeen';
const STORAGE_DISMISSED = 'sc.notifications.dismissed';

interface LastSeen { contract: number; schedule: number; }

const CONTRACT_ROLES = ['ADMIN', 'RIGHTS_MANAGER', 'SCHEDULER', 'COMPLIANCE_OFFICER'];
const SCHEDULE_ROLES = ['ADMIN', 'SCHEDULER', 'RIGHTS_MANAGER', 'COMPLIANCE_OFFICER'];

@Injectable({ providedIn: 'root' })
export class NotificationsService {
  private contracts = inject(ContractsService);
  private schedules = inject(SchedulesService);
  private auth      = inject(AuthService);

  private _items    = signal<Notification[]>([]);
  readonly items        = this._items.asReadonly();
  readonly unseenCount  = computed(() => this._items().filter(n => !n.seen).length);

  private contractsSub?: Subscription;
  private schedulesSub?: Subscription;
  private initialised = false;

  constructor() {
    effect(() => {
      const authed = this.auth.isAuthenticated();
      if (authed && !this.initialised) {
        this.start();
        this.initialised = true;
      } else if (!authed && this.initialised) {
        this.stop();
        this._items.set([]);
        this.initialised = false;
      }
    });
  }

  private start(): void {
    const role = this.auth.role();
    if (!role) return;

    if (CONTRACT_ROLES.includes(role)) {
      this.contractsSub = interval(POLL_MS).pipe(
        startWith(0),
        switchMap(() => this.contracts.list())
      ).subscribe({
        next: list => this.mergeContracts(list),
        error: () => {}
      });
    }

    if (SCHEDULE_ROLES.includes(role)) {
      const start = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString();
      const end   = new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString();
      this.schedulesSub = interval(POLL_MS).pipe(
        startWith(0),
        switchMap(() => this.schedules.calendar(start, end))
      ).subscribe({
        next: list => this.mergeSchedules(list),
        error: () => {}
      });
    }
  }

  private stop(): void {
    this.contractsSub?.unsubscribe();
    this.schedulesSub?.unsubscribe();
    this.contractsSub = undefined;
    this.schedulesSub = undefined;
  }

  private mergeContracts(list: Contract[]): void {
    const last      = this.readLastSeen();
    const dismissed = this.readDismissed();

    // Handle both contractId and id field from backend
    const valid = list
      .map(c => ({ ...c, contractId: c.contractId ?? (c as any).id ?? 0 }))
      .filter(c => (c.contractId as number) > 0) as (Contract & { contractId: number })[];

    const maxId = valid.reduce((m, c) => Math.max(m, c.contractId), 0);

    if (last.contract === 0) {
      // First boot — set to 0 so all existing contracts show as notifications
      this.writeLastSeen({ ...last, contract: 0 });
    }

    const fresh = valid
      .filter(c => c.contractId > last.contract)
      .map<Notification>(c => ({
        id:        `contract-${c.contractId}`,
        kind:      'CONTRACT',
        refId:     c.contractId,
        title:     'New contract',
        summary:   `Contract #${c.contractId} · title #${c.titleId} · ${c.territoryListJson}`,
        link:      '/app/contracts',
        createdAt: new Date().toISOString(),
        seen:      false
      }))
      .filter(n => !dismissed.includes(n.id));

    if (fresh.length) this.pushNotifications(fresh);
    if (maxId > last.contract) this.writeLastSeen({ ...last, contract: maxId });
  }

  private mergeSchedules(list: CalendarSchedule[]): void {
    const last      = this.readLastSeen();
    const dismissed = this.readDismissed();

    const maxId = list.reduce((m, s) => Math.max(m, s.scheduleId), 0);

    if (last.schedule === 0) {
      // First boot — set to 0 so all existing schedules show as notifications
      this.writeLastSeen({ ...last, schedule: 0 });
    }

    const fresh = list
      .filter(s => s.scheduleId > last.schedule)
      .map<Notification>(s => ({
        id:        `schedule-${s.scheduleId}`,
        kind:      'SCHEDULE',
        refId:     s.scheduleId,
        title:     'New schedule',
        summary:   `${s.platform} · ${new Date(s.startDateTime).toLocaleString()} → ${new Date(s.endDateTime).toLocaleString()}`,
        link:      '/app/schedules',
        createdAt: new Date().toISOString(),
        seen:      false
      }))
      .filter(n => !dismissed.includes(n.id));

    if (fresh.length) this.pushNotifications(fresh);
    if (maxId > last.schedule) this.writeLastSeen({ ...last, schedule: maxId });
  }

  private pushNotifications(fresh: Notification[]): void {
    const existingIds = new Set(this._items().map(n => n.id));
    const additions   = fresh.filter(n => !existingIds.has(n.id));
    if (!additions.length) return;
    this._items.update(curr => [...additions, ...curr].slice(0, 50));
  }

  markAllSeen(): void {
    this._items.update(curr => curr.map(n => ({ ...n, seen: true })));
  }

  dismiss(id: string): void {
    const dismissed = this.readDismissed();
    if (!dismissed.includes(id)) {
      dismissed.push(id);
      localStorage.setItem(STORAGE_DISMISSED, JSON.stringify(dismissed.slice(-200)));
    }
    this._items.update(curr => curr.filter(n => n.id !== id));
  }

  clearAll(): void {
    const ids       = this._items().map(n => n.id);
    const dismissed = this.readDismissed();
    localStorage.setItem(
      STORAGE_DISMISSED,
      JSON.stringify([...dismissed, ...ids].slice(-200))
    );
    this._items.set([]);
  }

  private readLastSeen(): LastSeen {
    try {
      const raw = localStorage.getItem(STORAGE_LAST_SEEN);
      if (raw) return JSON.parse(raw) as LastSeen;
    } catch {}
    return { contract: 0, schedule: 0 };
  }

  private writeLastSeen(v: LastSeen): void {
    localStorage.setItem(STORAGE_LAST_SEEN, JSON.stringify(v));
  }

  private readDismissed(): string[] {
    try {
      const raw = localStorage.getItem(STORAGE_DISMISSED);
      if (raw) return JSON.parse(raw) as string[];
    } catch {}
    return [];
  }
}