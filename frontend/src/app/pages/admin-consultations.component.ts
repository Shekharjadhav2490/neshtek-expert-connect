import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminConsultation, AdminService } from '../core/admin/admin.service';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-admin-consultations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="page">
      <header class="topbar">
        <div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Administration / Consultations</div></div>
        <div class="actions"><button (click)="back()">Dashboard</button><button (click)="logout()">Sign out</button></div>
      </header>
      <section class="hero">
        <div><div class="eyebrow">Consultation management</div><h1>Consultation inbox</h1><p>Monitor consultation requests across the marketplace.</p></div>
        <button class="refresh" (click)="load()" [disabled]="loading">{{loading ? 'Refreshing…' : 'Refresh'}}</button>
      </section>
      <section *ngIf="error" class="error">{{error}}</section>
      <section class="panel">
        <div class="toolbar"><strong>{{total}} consultation{{total === 1 ? '' : 's'}}</strong><select [(ngModel)]="statusFilter" (change)="applyFilter()"><option value="ALL">All statuses</option><option value="PENDING">Pending</option><option value="ACCEPTED">Accepted</option><option value="REJECTED">Rejected</option><option value="CANCELLED">Cancelled</option></select></div>
        <div class="table-wrap">
          <table><thead><tr><th>Request</th><th>Customer</th><th>Requirement</th><th>Expert</th><th>Hours</th><th>Rate</th><th>Status</th><th>Created</th></tr></thead>
          <tbody><tr *ngFor="let item of filtered"><td>#{{item.id}}</td><td>#{{item.customerId}}</td><td>{{item.requirementTitle}}</td><td>{{item.expertName}}</td><td>{{item.estimatedHours || '—'}}</td><td>{{item.proposedRate || '—'}} {{item.currencyCode}}</td><td><span class="status" [class.accepted]="item.status==='ACCEPTED'" [class.rejected]="item.status==='REJECTED'">{{item.status}}</span></td><td>{{item.createdAt | date:'mediumDate'}}</td></tr><tr *ngIf="!loading && filtered.length===0"><td colspan="8" class="empty">No consultations found.</td></tr></tbody></table>
        </div>
      </section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 48px}.topbar,.hero,.panel,.error{max-width:1180px;margin:auto}.topbar{padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,td{color:#667085;font-size:14px}.subtitle{margin-top:4px}.actions{display:flex;gap:8px}.topbar button,.refresh,.toolbar select{border:1px solid #d5dbe6;background:#fff;border-radius:9px;padding:10px 16px;font-weight:750;cursor:pointer}.hero{padding:46px 0 24px;display:flex;justify-content:space-between;align-items:end;gap:20px}.eyebrow{font-size:11px;text-transform:uppercase;letter-spacing:.12em;font-weight:850;color:#315ea8}.hero h1{font-size:40px;margin:9px 0}.hero p{color:#667085}.error{margin-bottom:18px;padding:12px 14px;border:1px solid #e8b4b4;background:#fff5f5;border-radius:10px;color:#a33}.panel{background:#fff;border:1px solid #e1e6ef;border-radius:16px;box-shadow:0 12px 32px rgba(23,32,51,.05);overflow:hidden}.toolbar{padding:18px 20px;border-bottom:1px solid #e5e9f0;display:flex;justify-content:space-between;align-items:center}.toolbar select{font-weight:600;padding:9px 12px}.table-wrap{overflow:auto}table{width:100%;border-collapse:collapse;min-width:980px}th,td{text-align:left;padding:15px 16px;border-bottom:1px solid #eef1f5}th{font-size:11px;text-transform:uppercase;letter-spacing:.06em;color:#667085;background:#fbfcfe}.status{display:inline-block;padding:5px 9px;border-radius:999px;background:#f1f3f6;font-size:11px;font-weight:850}.status.accepted{background:#eaf7ef;color:#176b3a}.status.rejected{background:#fff0f0;color:#a33}.empty{text-align:center;padding:36px}@media(max-width:700px){.page{padding:0 18px 36px}.hero{align-items:start;flex-direction:column}}
  `]
})
export class AdminConsultationsComponent {
  private readonly admin = inject(AdminService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  consultations: AdminConsultation[] = [];
  filtered: AdminConsultation[] = [];
  total = 0;
  loading = false;
  error = '';
  statusFilter = 'ALL';

  constructor(){
    const user=this.auth.getCurrentUser();
    if(!user || !['ADMIN','ROLE_ADMIN'].includes(user.role)){ this.router.navigateByUrl('/'); return; }
    this.load();
  }

  load(){
    this.loading = true;
    this.error = '';
    this.admin.getConsultations(0, 100).subscribe({
      next: page => { this.consultations = page.content; this.total = page.totalElements; this.applyFilter(); this.loading = false; },
      error: err => { console.error('Admin consultations load failed', err); this.error = 'Unable to load consultations.'; this.loading = false; }
    });
  }

  applyFilter(){ this.filtered = this.statusFilter === 'ALL' ? this.consultations : this.consultations.filter(item => item.status === this.statusFilter); }
  back(){ this.router.navigateByUrl('/admin/dashboard'); }
  logout(){ this.auth.logout(); this.router.navigateByUrl('/login'); }
}
