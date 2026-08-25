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
      <header class="topbar"><div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Administration / Consultations</div></div><div class="actions"><button (click)="back()">Dashboard</button><button (click)="logout()">Sign out</button></div></header>
      <section class="hero"><div><div class="eyebrow">Consultation lifecycle</div><h1>Consultation management</h1><p>Monitor request status, participants, commercial details and response timestamps.</p></div><button class="refresh" (click)="load()" [disabled]="loading">{{loading ? 'Refreshing…' : 'Refresh'}}</button></section>
      <section *ngIf="error" class="error">{{error}}</section>
      <section class="panel">
        <div class="toolbar"><strong>{{total}} consultation{{total === 1 ? '' : 's'}}</strong><div class="filters"><input [(ngModel)]="search" placeholder="Search expert or requirement" (keyup.enter)="load()"><button class="search" (click)="load()">Search</button><select [(ngModel)]="statusFilter" (change)="applyFilter()"><option value="ALL">All statuses</option><option value="PENDING">Pending</option><option value="ACCEPTED">Accepted</option><option value="REJECTED">Rejected</option><option value="CANCELLED">Cancelled</option></select></div></div>
        <div class="table-wrap"><table><thead><tr><th>Request</th><th>Customer</th><th>Requirement</th><th>Expert</th><th>Hours</th><th>Rate</th><th>Status</th><th>Created</th><th></th></tr></thead>
        <tbody><tr *ngFor="let item of filtered"><td>#{{item.id}}</td><td>#{{item.customerId}}</td><td>{{item.requirementTitle}}</td><td>{{item.expertName}}</td><td>{{item.estimatedHours || '—'}}</td><td>{{item.proposedRate || '—'}} {{item.currencyCode}}</td><td><span class="status" [class.accepted]="item.status==='ACCEPTED'" [class.rejected]="item.status==='REJECTED'">{{item.status}}</span></td><td>{{item.createdAt | date:'mediumDate'}}</td><td><button class="link" (click)="select(item)">View</button></td></tr><tr *ngIf="!loading && filtered.length===0"><td colspan="9" class="empty">No consultations found.</td></tr></tbody></table></div>
        <div class="pager"><button (click)="previous()" [disabled]="page===0 || loading">Previous</button><span>Page {{page+1}} of {{totalPages || 1}}</span><button (click)="next()" [disabled]="page+1>=totalPages || loading">Next</button></div>
      </section>
      <section *ngIf="selected" class="panel detail"><div class="detail-head"><div><div class="eyebrow">Consultation #{{selected.id}}</div><h2>{{selected.requirementTitle}}</h2><p>{{selected.expertName}} · Customer #{{selected.customerId}}</p></div><button class="close" (click)="selected=null">Close</button></div><div class="detail-grid"><div><b>Status</b><span>{{selected.status}}</span></div><div><b>Requirement</b><span>#{{selected.requirementId}}</span></div><div><b>Expert</b><span>#{{selected.expertId}} · {{selected.expertName}}</span></div><div><b>Estimated hours</b><span>{{selected.estimatedHours || '—'}}</span></div><div><b>Proposed rate</b><span>{{selected.proposedRate || '—'}} {{selected.currencyCode}}</span></div><div><b>Requested start</b><span>{{selected.requestedStartDate | date:'mediumDate'}}</span></div><div><b>Created</b><span>{{selected.createdAt | date:'medium'}}</span></div><div><b>Responded</b><span>{{selected.respondedAt ? (selected.respondedAt | date:'medium') : 'Not responded'}}</span></div><div><b>Updated</b><span>{{selected.updatedAt | date:'medium'}}</span></div></div><div class="message"><b>Message</b><p>{{selected.message || 'No message provided.'}}</p></div><div *ngIf="selected.rejectionReason" class="rejection"><b>Rejection reason</b><p>{{selected.rejectionReason}}</p></div></section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 48px}.topbar,.hero,.panel,.error{max-width:1180px;margin:auto}.topbar{padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,td{color:#667085;font-size:14px}.subtitle{margin-top:4px}.actions,.filters{display:flex;gap:8px}.topbar button,.refresh,.search,.link,.close,.pager button,.filters input,.filters select{border:1px solid #d5dbe6;background:#fff;border-radius:9px;padding:10px 13px;font-weight:700;cursor:pointer}.hero{padding:46px 0 24px;display:flex;justify-content:space-between;align-items:end;gap:20px}.eyebrow{font-size:11px;text-transform:uppercase;letter-spacing:.12em;font-weight:850;color:#315ea8}.hero h1{font-size:40px;margin:9px 0}.hero p{color:#667085}.error{margin-bottom:18px;padding:12px 14px;border:1px solid #e8b4b4;background:#fff5f5;border-radius:10px;color:#a33}.panel{background:#fff;border:1px solid #e1e6ef;border-radius:16px;box-shadow:0 12px 32px rgba(23,32,51,.05);overflow:hidden}.toolbar{padding:18px 20px;border-bottom:1px solid #e5e9f0;display:flex;justify-content:space-between;align-items:center;gap:15px}.table-wrap{overflow:auto}table{width:100%;border-collapse:collapse;min-width:1120px}th,td{text-align:left;padding:15px 16px;border-bottom:1px solid #eef1f5}th{font-size:11px;text-transform:uppercase;letter-spacing:.06em;color:#667085;background:#fbfcfe}.status{display:inline-block;padding:5px 9px;border-radius:999px;background:#f1f3f6;font-size:11px;font-weight:850}.status.accepted{background:#eaf7ef;color:#176b3a}.status.rejected{background:#fff0f0;color:#a33}.link{background:transparent;border-color:transparent;color:#315ea8}.empty{text-align:center;padding:36px}.pager{display:flex;justify-content:center;align-items:center;gap:18px;padding:16px}.detail{margin-top:24px;padding:24px}.detail-head{display:flex;justify-content:space-between;align-items:start}.detail h2{margin:8px 0}.detail p{color:#667085}.detail-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin-top:20px}.detail-grid div{border:1px solid #e5e9f0;border-radius:10px;padding:14px}.detail-grid b,.detail-grid span{display:block}.detail-grid span{margin-top:6px;color:#667085}.message,.rejection{margin-top:16px;padding:16px;border:1px solid #e5e9f0;border-radius:10px}.message p,.rejection p{margin-bottom:0}@media(max-width:800px){.page{padding:0 18px 36px}.hero,.toolbar{align-items:start;flex-direction:column}.filters{width:100%;flex-wrap:wrap}.detail-grid{grid-template-columns:1fr}}
  `]
})
export class AdminConsultationsComponent {
  private readonly admin = inject(AdminService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  consultations: AdminConsultation[] = [];
  filtered: AdminConsultation[] = [];
  selected: AdminConsultation | null = null;
  total = 0; totalPages = 0; page = 0; size = 20; search = ''; statusFilter = 'ALL'; loading = false; error = '';

  constructor(){ const user=this.auth.getCurrentUser(); if(!user || !['ADMIN','ROLE_ADMIN'].includes(user.role)){this.router.navigateByUrl('/');return;} this.load(); }
  load(){ this.loading=true; this.error=''; this.admin.getConsultations(this.page,this.size).subscribe({next:p=>{this.consultations=p.content;this.total=p.totalElements;this.totalPages=p.totalPages;this.applyFilter();this.loading=false;},error:err=>{console.error(err);this.error='Unable to load consultations.';this.loading=false;}}); }
  applyFilter(){ const q=this.search.trim().toLowerCase(); this.filtered=this.consultations.filter(i=>(this.statusFilter==='ALL'||i.status===this.statusFilter)&&(!q||i.expertName?.toLowerCase().includes(q)||i.requirementTitle?.toLowerCase().includes(q))); }
  next(){if(this.page+1<this.totalPages){this.page++;this.load();}}
  previous(){if(this.page>0){this.page--;this.load();}}
  select(item:AdminConsultation){this.selected=item;}
  back(){this.router.navigateByUrl('/admin/dashboard');}
  logout(){this.auth.logout();this.router.navigateByUrl('/login');}
}
