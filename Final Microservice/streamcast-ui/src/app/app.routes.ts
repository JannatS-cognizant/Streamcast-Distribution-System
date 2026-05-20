import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'forbidden',
    loadComponent: () =>
      import('./features/misc/forbidden.component').then(m => m.ForbiddenComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./layout/shell.component').then(m => m.ShellComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'titles',
        canActivate: [roleGuard(['ADMIN', 'CONTENT_OWNER', 'RIGHTS_MANAGER', 'SCHEDULER'])],
        loadComponent: () =>
          import('./features/catalog/titles.component').then(m => m.TitlesComponent)
      },
      {
        path: 'titles/:id',
        canActivate: [roleGuard(['ADMIN', 'CONTENT_OWNER', 'RIGHTS_MANAGER', 'SCHEDULER'])],
        loadComponent: () =>
          import('./features/catalog/title-detail.component').then(m => m.TitleDetailComponent)
      },
      {
        path: 'contracts',
        canActivate: [roleGuard(['ADMIN', 'RIGHTS_MANAGER', 'SCHEDULER', 'COMPLIANCE_OFFICER'])],
        loadComponent: () =>
          import('./features/contracts/contracts.component').then(m => m.ContractsComponent)
      },
      {
        path: 'clauses',
        canActivate: [roleGuard(['ADMIN', 'RIGHTS_MANAGER', 'SCHEDULER', 'COMPLIANCE_OFFICER'])],
        loadComponent: () =>
          import('./features/clauses/clauses.component').then(m => m.ClausesComponent)
      },
      {
        path: 'partners',
        canActivate: [roleGuard(['ADMIN', 'DISTRIBUTION_OPERATOR', 'PARTNER_ADMIN', 'RIGHTS_MANAGER'])],
        loadComponent: () =>
          import('./features/partners/partners.component').then(m => m.PartnersComponent)
      },
      {
        path: 'manifests',
        canActivate: [roleGuard(['ADMIN', 'DISTRIBUTION_OPERATOR', 'PARTNER_ADMIN'])],
        loadComponent: () =>
          import('./features/manifests/manifests.component').then(m => m.ManifestsComponent)
      },
      {
        path: 'schedules',
        canActivate: [roleGuard(['ADMIN', 'SCHEDULER', 'RIGHTS_MANAGER', 'COMPLIANCE_OFFICER'])],
        loadComponent: () =>
          import('./features/schedules/schedules.component').then(m => m.SchedulesComponent)
      },
      {
        path: 'conflicts',
        canActivate: [roleGuard(['ADMIN', 'SCHEDULER', 'COMPLIANCE_OFFICER'])],
        loadComponent: () =>
          import('./features/conflicts/conflicts.component').then(m => m.ConflictsComponent)
      },
      {
        path: 'usage',
        canActivate: [roleGuard(['ADMIN', 'COMPLIANCE_OFFICER', 'SCHEDULER'])],
        loadComponent: () =>
          import('./features/usage/usage.component').then(m => m.UsageComponent)
      },
      {
        path: 'users',
        canActivate: [roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/admin/users.component').then(m => m.UsersComponent)
      },
      {
        path: 'roles',
        canActivate: [roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/admin/roles.component').then(m => m.RolesComponent)
      }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
