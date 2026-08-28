import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/home.component').then((m) => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./pages/login.component').then((m) => m.LoginComponent) },
  { path: 'customer/dashboard', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-dashboard.component').then((m) => m.CustomerDashboardComponent) },
  { path: 'customer/requirements/:requirementId/matches', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-matching.component').then((m) => m.CustomerMatchingComponent) },
  { path: 'customer/consultations', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-consultations.component').then((m) => m.CustomerConsultationsComponent) },
  { path: 'customer/work-logs/:engagementId', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-work-logs.component').then((m) => m.CustomerWorkLogsComponent) },
  { path: 'expert/dashboard', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-dashboard.component').then((m) => m.ExpertDashboardComponent) },
  { path: 'expert/settlement', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-settlement.component').then((m) => m.ExpertSettlementComponent) },
  { path: 'expert/consultations', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-consultation-inbox.component').then((m) => m.ExpertConsultationInboxComponent) },
  { path: 'expert/work-logs/:engagementId', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-work-logs.component').then((m) => m.ExpertWorkLogsComponent) },
  { path: 'admin/dashboard', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-dashboard.component').then((m) => m.AdminDashboardComponent) },
  { path: 'admin/experts', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-experts.component').then((m) => m.AdminExpertsComponent) },
  { path: 'admin/customers', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-customers.component').then((m) => m.AdminCustomersComponent) },
  { path: 'admin/requirements', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-requirements.component').then((m) => m.AdminRequirementsComponent) },
  { path: 'admin/consultations', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-consultations.component').then((m) => m.AdminConsultationsComponent) },
  { path: 'admin/engagements', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-engagements.component').then((m) => m.AdminEngagementsComponent) },
  { path: '**', redirectTo: '' }
];
