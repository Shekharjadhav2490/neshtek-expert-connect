import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home.component').then((m) => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'customer/dashboard',
    canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])],
    loadComponent: () => import('./pages/customer-dashboard.component').then((m) => m.CustomerDashboardComponent)
  },
  {
    path: 'customer/requirements/:requirementId/matches',
    canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])],
    loadComponent: () => import('./pages/customer-matching.component').then((m) => m.CustomerMatchingComponent)
  },
  {
    path: 'expert/dashboard',
    canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])],
    loadComponent: () => import('./pages/expert-dashboard.component').then((m) => m.ExpertDashboardComponent)
  },
  { path: '**', redirectTo: '' }
];
