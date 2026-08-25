import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminExpert, AdminPage, AdminService } from '../core/admin/admin.service';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-admin-experts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="page">
      <header class="topbar"><div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Administration / Experts</div></div><div class="actions"><button (click)="back()">Dashboard</button><button (click)="logout()">Sign out</button></div></header>
      <section class="hero"><div><div class="eyebrow">Expert administration</div><h1>Expert management</h1><p>Review, verify and activate expert profiles.</p></div><button class="refresh" (click)="load()" [disabled]="loading">{{loading ? 'Refreshing…' : 'Refresh'}}</button></section>
      <section *ngIf="error" class="error">{{error}}</section>
      <section class="panel">
        <div class="toolbar"><strong>{{total}} expert{{total === 1 ? '' : 's'}}</strong><div class="filters"><input [(ngModel)]="search" placeholder="Search name or email" (input)="apply()"><select [(ngModel)]="statusFilter" (change)="load()"><option value="ALL">All statuses</option><option value="SUBMITTED">Submitted</option><option value="UNDER_REVIEW">Under review</option><option value="VERIFICATION">Verification</option><option value="APPROVED">Approved</option><option value="ACTIVE">Active</option><option value="REJECTED">Rejected</option><option value="SUSPENDED">Suspended</option></select></div></div>
        <div class="table-wrap"><table><thead><tr><th>Expert</th><th>Email</th><th>Skills</th><th>Expertise words</th><th>Status</th><th>Created</th><th>Action</th></tr></thead>
        <tbody><tr *ngFor="let e of filtered"><td><b>{{e.firstName}} {{e.lastName}}</b></td><td>{{e.email}}</td><td>{{e.skillCount}}</td><td>{{e.expertiseWordCount}}</td><td><span class="status" [class.active]="e.status==='ACTIVE'" [class.review]="e.status==='UNDER_REVIEW'||e.status==='VERIFICATION'" [class.rejected]="e.status==='REJECTED'">{{e.status}}</span></td><td>{{e.createdAt | date:'mediumDate'}}</td><td><button class="action" (click)="startReview(e)" *ngIf="e.status==='SUBMITTED'">Review</button><button class="action" (click)="startVerification(e)" *ngIf="e.status==='UNDER_REVIEW'">Verify</button><button class="action" (click)="approve(e)" *ngIf="e.status==='APPROVED'">Activate</button><button class="danger" (click)="reject(e)" *ngIf="e.status==='UNDER_REVIEW'||e.status==='VERIFICATION'">Reject</button><button class="link" (click)="view(e)">View</button></td></tr><tr *ngIf="!loading && filtered.length===0"><td colspan="7" class="empty">No experts found.</td></tr></tbody></table></div>
      </section>
      <section *ngIf="selected" class="panel detail"><div class="eyebrow">Expert profile</div><h2>{{selected.firstName}} {{selected.lastName}}</h2><p>{{selected.email}} · {{selected.mobileNumber}}</p><div class="detail-grid"><div><b>Status</b><span>{{selected.status}}</span></div><div><b>Skills</b><span>{{selected.skillCount}}</span></div><div><b>Expertise words</b><span>{{selected.expertiseWordCount}}</span></div></div></section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 48px}.topbar,.hero,.panel,.error{max-width:1180px;margin:auto}.topbar{padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,td{color:#667085;font-size:14px}.subtitle{margin-top:4px}.actions{display:flex;gap:8px}.topbar button,.refresh,.action,.link,.danger,.toolbar input,.toolbar select{border:1px solid #d5dbe6;background:#fff;border-radius:9px;padding:10px 13px;font-weight:700;cursor:pointer}.hero{padding:46px 0 24px;display:flex;justify-content:space-between;align-items:end;gap:20px}.eyebrow{font-size:11px;text-transform:uppercase;letter-spacing:.12em;font-weight:850;color:#315ea8}.hero h1{font-size:40px;margin:9px 0}.hero p{color:#667085}.error{margin-bottom:18px;padding:12px 14px;border:1px solid #e8b4b4;background:#fff5f5;border-radius:10px;color:#a33}.panel{background:#fff;border:1px solid #e1e6ef;border-radius:16px;box-shadow:0 12px 32px rgba(23,32,51,.05);overflow:hidden}.toolbar{padding:18px 20px;border-bottom:1px solid #e5e9f0;display:flex;justify-content:space-between;gap:15px;align-items:center}.filters{display:flex;gap:8px}.table-wrap{overflow:auto}table{width:100%;border-collapse:collapse;min-width:1100px}th,td{text-align:left;padding:15px 16px;border-bottom:1px solid #eef1f5}th{font-size:11px;text-transform:uppercase;letter-spacing:.06em;color:#667085;background:#fbfcfe}.status{display:inline-block;padding:5px 9px;border-radius:999px;background:#f1f3f6;font-size:11px;font-weight:850}.status.active{background:#eaf7ef;color:#176b3a}.status.review{background:#fff7e8;color:#8a5a00}.status.rejected{background:#fff0f0;color:#a33}.action,.link,.danger{padding:7px 9px;margin-right:5px}.danger{border-color:#efc3c3;color:#a33}.link{background:transparent;border-color:transparent}.empty{text-align:center;padding:36px}.detail{margin-top:24px;padding:24px}.detail h2{margin:8px 0}.detail p{color:#667085}.detail-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:12px}.detail-grid div{border:1px solid #e5e9f0;border-radius:10px;padding:14px}.detail-grid b,.detail-grid span{display:block}.detail-grid span{margin-top:6px;color:#667085}@media(max-width:800px){.page{padding:0 18px 36px}.hero,.toolbar{align-items:start;flex-direction:column}.filters{width:100%;flex-wrap:wrap}.detail-grid{grid-template-columns:1fr}}
  `]
})
export class AdminExpertsComponent {
  private readonly admin = inject(AdminService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  experts: AdminExpert[] = [];
  filtered: AdminExpert[] = [];
  total = 0;
  loading = false;
  error = '';
  search = '';
  statusFilter = 'ALL';
  selected: AdminExpert | null = null;

  constructor(){ const user=this.auth.getCurrentUser(); if(!user || !['ADMIN','ROLE_ADMIN'].includes(user.role)){ this.router.navigateByUrl('/'); return; } this.load(); }
  load(){ this.loading=true; this.error=''; const status=this.statusFilter==='ALL'?undefined:this.statusFilter; this.admin.getExperts(0,100,status).subscribe({next:p=>{this.experts=p.content;this.total=p.totalElements;this.apply();this.loading=false;},error:err=>{console.error(err);this.error='Unable to load experts.';this.loading=false;}}); }
  apply(){ const q=this.search.trim().toLowerCase(); this.filtered=!q?this.experts:this.experts.filter(e=>`${e.firstName} ${e.lastName} ${e.email}`.toLowerCase().includes(q)); }
  startReview(e:AdminExpert){this.admin.startExpertReview(e.expertId).subscribe({next:()=>this.load(),error:err=>this.showError(err)});}
  startVerification(e:AdminExpert){this.admin.startExpertVerification(e.expertId).subscribe({next:()=>this.load(),error:err=>this.showError(err)});}
  approve(e:AdminExpert){this.admin.activateExpert(e.expertId).subscribe({next:()=>this.load(),error:err=>this.showError(err)});}
  reject(e:AdminExpert){const reason=window.prompt('Enter rejection reason:');if(!reason?.trim())return;this.admin.rejectExpert(e.expertId,reason.trim()).subscribe({next:()=>this.load(),error:err=>this.showError(err)});}
  view(e:AdminExpert){this.admin.getExpert(e.expertId).subscribe({next:x=>this.selected=x,error:err=>this.showError(err)});}
  showError(err:any){console.error(err);this.error=err?.error?.message||'Operation failed.';}
  back(){this.router.navigateByUrl('/admin/dashboard');}
  logout(){this.auth.logout();this.router.navigateByUrl('/login');}
}
