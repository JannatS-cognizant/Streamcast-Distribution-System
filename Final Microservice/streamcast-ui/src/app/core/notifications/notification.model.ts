export type NotificationKind = 'CONTRACT' | 'SCHEDULE';

export interface Notification {
  id: string;
  kind: NotificationKind;
  refId: number;
  title: string;
  summary: string;
  link: string;
  createdAt: string;
  seen: boolean;
}
