import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminCustomer, AdminPage, AdminService } from '../core/admin/admin.service';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-admin-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="page">
      <header class="topbar"><div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Administration / Customers</div></div><div class="actions"><button (click)="back()">Dashboard</button><button (click)="logout()">Sign out</button></div></header>
      <section class="hero"><div><div class="eyebrow">Customer administration</div><h1>Customer management</h1><p>Search and review registered customer accounts.</p></div><button class="refresh" (click)="load()" [disabled]="loading">{{loading ? 'Refreshing…' : 'Refresh'}}</button></section>
      <section *ngIf="error" class="error">{{error}}</section>
      <section class="panel">
        <div class="toolbar"><strong>{{total}} customer{{total === 1 ? '' : 's'}}</strong><div class="filters"><input [(ngModel)]="search" placeholder="Search company, contact or email" (keyup.enter)="load()"><button class="search" (click)="load()">Search</button></div></div>
        <div class="table-wrap"><table><thead><tr><th>Company</th><th>Contact</th><th>Email</th><th>Location</th><th>Industry</th><th>Status</th><th>Created</th><th>Action</th></tr></thead>
        <tbody><tr *ngFor="let c of customers"><td><b>{{c.companyName}}</b></td><td>{{c.contactName}}</td><td>{{c.email}}</td><td>{{c.city || '—'}}<span *ngIf="c.country">, {{c.country}}</span></td><td>{{c.industry || '—'}}</td><td><span class="status" [class.active]="c.status==='ACTIVE'">{{c.status}}</span></td><td>{{c.createdAt | date:'mediumDate'}}</td><td><button class="link" (click)="view(c)">View</button></td></tr><tr *ngIf="!loading && customers.length===0"><td colspan="8" class="empty">No customers found.</td></tr></tbody></table></div>
        <div class="pager"><button (click)="previous()" [disabled]="page===0 || loading">Previous</button><span>Page {{page + 1}} of {{totalPages || 1}}</span><button (click)="next()" [disabled]="page + 1 >= totalPages || loading">Next</button></div>
      </section>
      <section *ngIf="selected" class="panel detail"><div class="detail-head"><div><div class="eyebrow">Customer profile</div><h2>{{selected.companyName}}</h2><p>{{selected.contactName}} · {{selected.email}}</p></div><button class="close" (click)="selected=null">Close</button></div><div class="detail-grid"><div><b>Phone</b><span>{{selected.phone || '—'}}</span></div><div><b>Country</b><span>{{selected.country || '—'}}</span></div><div><b>City</b><span>{{selected.city || '—'}}</span></div><div><b>Timezone</b><span>{{selected.timezone || '—'}}</span></div><div><b>Industry</b><span>{{selected.industry || '—'}}</span></div><div><b>Company size</b><span>{{selected.companySize || '—'}}</span></div><div><b>Status</b><span>{{selected.status}}</span></div><div><b>Created</b><span>{{selected.createdAt | date:'medium'}}</span></div><div><b>Updated</b><span>{{selected.updatedAt | date:'medium'}}</span></div></div></section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 48px}.topbar,.hero,.panel,.error{max-width:1180px;margin:auto}.topbar{padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,td{color:#667085;font-size:14px}.subtitle{margin-top:4px}.actions,.filters{display:flex;gap:8px}.topbar button,.refresh,.search,.link,.close,.pager button,.filters input{border:1px solid #d5dbe6;background:#fff;border-radius:9px;padding:10px 13px;font-weight:700;cursor:pointer}.hero{padding:46px 0 24px;display:flex;justify-content:space-between;align-items:end;gap:20px}.eyebrow{font-size:11px;text-transform:uppercase;letter-spacing:.12em;font-weight:850;color:#315ea8}.hero h1{font-size:40px;margin:9px 0}.hero p{color:#667085}.error{margin-bottom:18px;padding:12px 14px;border:1px solid #e8b4b4;background:#fff5f5;border-radius:10px;color:#a33}.panel{background:#fff;border:1px solid #e1e6ef;border-radius:16px;box-shadow:0 12px 32px rgba(23,32,51,.05);overflow:hidden}.toolbar{padding:18px 20px;border-bottom:1px solid #e5e9f0;display:flex;justify-content:space-between;gap:15px;align-items:center}.table-wrap{overflow:auto}table{width:100%;border-collapse:collapse;min-width:1200px}th,td{text-align:left;padding:15px 16px;border-bottom:1px solid #eef1f5}th{font-size:11px;text-transform:uppercase;letter-spacing:.06em;color:#667085;background:#fbfcfe}.status{display:inline-block;padding:5px 9px;border-radius:999px;background:#f1f3f6;font-size:11px;font-weight:850}.status.active{background:#eaf7ef;color:#176b3a}.link{background:transparent;border-color:transparent;color:#315ea8}.empty{text-align:center;padding:36px}.pager{display:flex;justify-content:center;align-items:center;gap:18px;padding:16px}.detail{margin-top:24px;padding:24px}.detail-head{display:flex;justify-content:space-between;align-items:start}.detail h2{margin:8px 0}.detail p{color:#667085}.detail-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin-top:20px}.detail-grid div{border:1px solid #e5e9f0;border-radius:10px;padding:14px}.detail-grid b,.detail-grid span{display:block}.detail-grid span{margin-top:6px;color:#667085}@media(max-width:800px){.page{padding:0 18px 36px}.hero,.toolbar{align-items:start;flex-direction:column}.filters{width:100%;flex-wrap:wrap}.filters input{flex:1}.detail-grid{grid-template-columns:1fr}}
  `]
})
export class AdminCustomersComponent {
  private readonly admin = inject(AdminService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  customers: (AdminCustomer & { phone?: string; country?: string; city?: string; timezone?: string; industry?: string; companySize?: string; createdAt?: string; updatedAt?: string })[] = [];
  selected: any = null;
  page = 0; size = 20; total = 0; totalPages = 0; search = ''; loading = false; error = '';

  constructor(){ const user=this.auth.getCurrentUser(); if(!user || !['ADMIN','ROLE_ADMIN'].includes(user.role)){ this.router.navigateByUrl('/'); return; } this.load(); }
  load(){ this.loading=true; this.error=''; this.admin.getCustomers(this.page,this.size,this.search).subscribe({next:p=>{this.customers=p.content as any;this.total=p.totalElements;this.totalPages=p.totalPages;this.loading=false;},error:err=>{console.error(err);this.error=err?.error?.message||'Unable to load customers.';this.loading=false;}}); }
  next(){if(this.page+1<this.totalPages){this.page++;this.load();}}
  previous(){if(this.page>0){this.page--;this.load();}}
  view(c:any){this.admin.getCustomer(c.customerId).subscribe({next:x=>this.selected=x,error:err=>{console.error(err);this.error=err?.error?.message||'Unable to load customer details.';}});}
  back(){this.router.navigateByUrl('/admin/dashboard');}
  logout(){this.auth.logout();this.router.navigateByUrl('/login');}
}
