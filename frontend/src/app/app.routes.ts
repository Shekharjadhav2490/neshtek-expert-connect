import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/home.component').then((m) => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./pages/login.component').then((m) => m.LoginComponent) },
  { path: 'customer/dashboard', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-dashboard.component').then((m) => m.CustomerDashboardComponent) },
  { path: 'customer/invoices', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-invoices.component').then((m) => m.CustomerInvoicesComponent) },
  { path: 'customer/invoices/:id', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-invoice-detail.component').then((m) => m.CustomerInvoiceDetailComponent) },
  { path: 'customer/requirements/:requirementId/edit', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-requirement-edit.component').then((m) => m.CustomerRequirementEditComponent) },
  { path: 'customer/requirements/:requirementId/matches', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-matching.component').then((m) => m.CustomerMatchingComponent) },
  { path: 'customer/consultations', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-consultations.component').then((m) => m.CustomerConsultationsComponent) },
  { path: 'customer/work-logs/:engagementId', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-work-logs.component').then((m) => m.CustomerWorkLogsComponent) },
  { path: 'customer/expert-replacement/:engagementId', canActivate: [authGuard, roleGuard(['CUSTOMER', 'ROLE_CUSTOMER'])], loadComponent: () => import('./pages/customer-expert-replacement.component').then((m) => m.CustomerExpertReplacementComponent) },
  { path: 'expert/dashboard', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-dashboard.component').then((m) => m.ExpertDashboardComponent) },
  { path: 'expert/settlement', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-settlement.component').then((m) => m.ExpertSettlementComponent) },
  { path: 'expert/consultations', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-consultation-inbox.component').then((m) => m.ExpertConsultationInboxComponent) },
  { path: 'expert/work-logs/:engagementId', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/expert-work-logs.component').then((m) => m.ExpertWorkLogsComponent) },
  { path: 'expert/engagement-history/:engagementId', canActivate: [authGuard, roleGuard(['EXPERT', 'ROLE_EXPERT'])], loadComponent: () => import('./pages/engagement-history.component').then((m) => m.EngagementHistoryComponent) },
  { path: 'admin/dashboard', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-dashboard.component').then((m) => m.AdminDashboardComponent) },
  { path: 'admin/invoices', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-invoices.component').then((m) => m.AdminInvoicesComponent) },
  { path: 'admin/settlements', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-settlements.component').then((m) => m.AdminSettlementsComponent) },
  { path: 'admin/experts', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-experts.component').then((m) => m.AdminExpertsComponent) },
  { path: 'admin/customers', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-customers.component').then((m) => m.AdminCustomersComponent) },
  { path: 'admin/requirements', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-requirements.component').then((m) => m.AdminRequirementsComponent) },
  { path: 'admin/consultations', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-consultations.component').then((m) => m.AdminConsultationsComponent) },
  { path: 'admin/engagements', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-engagements.component').then((m) => m.AdminEngagementsComponent) },
  { path: 'admin/expert-replacements', canActivate: [authGuard, roleGuard(['ADMIN', 'ROLE_ADMIN'])], loadComponent: () => import('./pages/admin-expert-replacements.component').then((m) => m.AdminExpertReplacementsComponent) },
  { path: '**', redirectTo: '' }
];