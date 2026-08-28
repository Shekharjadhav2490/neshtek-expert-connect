import { Component, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
import { AdminEngagement, AdminService } from '../core/admin/admin.service';

@Component({
  selector: 'app-admin-engagements',
  standalone: true,
  imports: [CommonModule, DatePipe],
  template: `
    <main class="page">
      <header class="topbar">
        <div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Administration · Engagements</div></div>
        <div class="top-actions"><button type="button" (click)="back()">Dashboard</button><button type="button" (click)="logout()">Sign out</button></div>
      </header>

      <section class="hero">
        <div class="hero-row">
          <div><div class="eyebrow">Engagement management</div><h1>Engagements</h1><p>Monitor every customer-to-expert engagement and its lifecycle.</p></div>
          <button class="refresh" type="button" (click)="load()" [disabled]="loading">{{ loading ? 'Refreshing…' : 'Refresh' }}</button>
        </div>
        @if (error) { <div class="error">{{ error }}</div> }
      </section>

      <section class="stats">
        <article><span>Total</span><strong>{{ total }}</strong></article>
        <article><span>Ready</span><strong>{{ count('READY') }}</strong></article>
        <article><span>Active</span><strong>{{ count('ACTIVE') }}</strong></article>
        <article><span>Completed</span><strong>{{ count('COMPLETED') }}</strong></article>
        <article><span>Cancelled</span><strong>{{ count('CANCELLED') }}</strong></article>
      </section>

      <section class="panel">
        <div class="panel-head"><div><div class="eyebrow">Lifecycle</div><h2>All engagements</h2></div><select [value]="statusFilter" (change)="statusFilter = $any($event.target).value"><option value="ALL">All statuses</option><option value="READY">Ready</option><option value="ACTIVE">Active</option><option value="COMPLETED">Completed</option><option value="CANCELLED">Cancelled</option></select></div>
        @if (loading) { <div class="state">Loading engagements…</div> }
        @else if (!filtered.length) { <div class="state">No engagements found.</div> }
        @else {
          <div class="table-wrap"><table><thead><tr><th>ID</th><th>Customer</th><th>Requirement</th><th>Expert</th><th>Commercials</th><th>Start date</th><th>Status</th><th>Lifecycle</th></tr></thead>
          <tbody>@for (e of filtered; track e.id) { <tr><td>#{{ e.id }}</td><td><b>{{ e.companyName }}</b><small>Customer #{{ e.customerId }}</small></td><td><b>{{ e.requirementTitle }}</b><small>Requirement #{{ e.requirementId }}</small></td><td><b>{{ e.expertName }}</b><small>Expert #{{ e.expertId }}</small></td><td>{{ e.agreedRate ?? '—' }} {{ e.currencyCode || '' }}<small>{{ e.estimatedHours ?? '—' }} hours</small></td><td>{{ e.requestedStartDate || '—' }}</td><td><span class="status" [class.ready]="e.status==='READY'" [class.active]="e.status==='ACTIVE'" [class.completed]="e.status==='COMPLETED'" [class.cancelled]="e.status==='CANCELLED'">{{ e.status }}</span></td><td><small>Created {{ e.createdAt | date:'mediumDate' }}</small><small>Started {{ e.startedAt | date:'medium' || '—' }}</small><small>Completed {{ e.completedAt | date:'medium' || '—' }}</small><small>Cancelled {{ e.cancelledAt | date:'medium' || '—' }}</small></td></tr> }</tbody></table></div>
        }
      </section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 48px}.topbar,.hero,.stats,.panel{max-width:1240px;margin:auto}.topbar{padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,small{color:#667085;font-size:13px}.subtitle{margin-top:4px}.top-actions{display:flex;gap:8px}.topbar button,.refresh,select{border:1px solid #d5dbe6;background:#fff;color:#172033;border-radius:9px;padding:10px 14px;font-weight:750;cursor:pointer}.hero{padding:50px 0 26px}.hero-row{display:flex;justify-content:space-between;align-items:end;gap:20px}.eyebrow{font-size:11px;text-transform:uppercase;letter-spacing:.12em;font-weight:850;color:#315ea8}.hero h1{font-size:42px;margin:9px 0}.hero p{color:#667085;margin:0}.error{margin-top:18px;padding:12px 14px;border:1px solid #e8b4b4;background:#fff5f5;border-radius:10px;color:#a33}.stats{display:grid;grid-template-columns:repeat(5,1fr);gap:12px}.stats article,.panel{background:#fff;border:1px solid #e1e6ef;border-radius:15px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.stats article{padding:18px}.stats span{display:block;color:#667085;font-size:12px;font-weight:800;text-transform:uppercase}.stats strong{display:block;font-size:30px;margin-top:12px}.panel{margin-top:22px;padding:22px}.panel-head{display:flex;justify-content:space-between;align-items:end;margin-bottom:16px}.panel h2{margin:8px 0 0}.state{text-align:center;padding:48px;color:#667085}.table-wrap{overflow:auto}table{width:100%;border-collapse:collapse;min-width:1080px}th{text-align:left;background:#f7f9fc;color:#667085;font-size:11px;text-transform:uppercase;letter-spacing:.06em;padding:12px;border-bottom:1px solid #e1e6ef}td{padding:14px 12px;border-bottom:1px solid #edf0f5;vertical-align:top;font-size:13px}td b{display:block}td small{display:block;margin-top:4px}.status{display:inline-block;padding:6px 10px;border-radius:999px;background:#eef2f7;font-size:11px;font-weight:850}.status.ready{background:#fff4d6;color:#8a5a00}.status.active{background:#eaf2ff;color:#315ea8}.status.completed{background:#ecfdf3;color:#067647}.status.cancelled{background:#fff5f4;color:#b42318}@media(max-width:800px){.page{padding:0 18px 36px}.hero-row,.panel-head{align-items:start;flex-direction:column}.stats{grid-template-columns:1fr 1fr}.hero h1{font-size:34px}}@media(max-width:520px){.stats{grid-template-columns:1fr}.topbar{align-items:start;gap:12px;flex-direction:column}}
  `]
})
export class AdminEngagementsComponent {
  private readonly auth = inject(AuthService);
  private readonly admin = inject(AdminService);
  private readonly router = inject(Router);
  engagements: AdminEngagement[] = [];
  loading = false;
  error = '';
  statusFilter = 'ALL';

  constructor(){
    const user=this.auth.getCurrentUser();
    if(!user || !['ADMIN','ROLE_ADMIN'].includes(user.role)){ this.router.navigateByUrl('/'); return; }
    this.load();
  }

  get total(): number { return this.engagements.length; }
  get filtered(): AdminEngagement[] { return this.statusFilter === 'ALL' ? this.engagements : this.engagements.filter(e => e.status === this.statusFilter); }
  count(status:string): number { return this.engagements.filter(e => e.status === status).length; }
  load(){ this.loading=true; this.error=''; this.admin.getEngagements(0,100).subscribe({next:r=>{this.engagements=r.content ?? [];this.loading=false;},error:e=>{this.error=e?.error?.message||e?.error?.error||'Unable to load engagements.';this.loading=false;}}); }
  back(){ this.router.navigateByUrl('/admin/dashboard'); }
  logout(){ this.auth.logout(); this.router.navigateByUrl('/login'); }
}
