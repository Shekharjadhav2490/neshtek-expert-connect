import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/home.component').then((m) => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./pages/login.component').then((m) => m.LoginComponent) },
  { path: 'customer/dashboard', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-dashboard.component').then((m) => m.CustomerDashboardComponent) },
  { path: 'customer/requirements/:requirementId/matches', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-matching.component').then((m) => m.CustomerMatchingComponent) },
  { path: 'expert/dashboard', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-dashboard.component').then((m) => m.ExpertDashboardComponent) },
  { path: 'expert/consultations', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-consultation-inbox.component').then((m) => m.ExpertConsultationInboxComponent) },
  { path: 'admin/dashboard', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-dashboard.component').then((m) => m.AdminDashboardComponent) },
  { path: 'admin/experts', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-experts.component').then((m) => m.AdminExpertsComponent) },
  { path: 'admin/consultations', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-consultations.component').then((m) => m.AdminConsultationsComponent) },
  { path: '**', redirectTo: '' }
];
