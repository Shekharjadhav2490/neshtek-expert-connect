import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <main class="page">
      <header class="topbar">
        <div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Administration</div></div>
        <button (click)="logout()">Sign out</button>
      </header>
      <section class="hero"><div class="eyebrow">Admin workspace</div><h1>Platform overview</h1><p>Manage the Expert Connect marketplace from one place.</p></section>
      <section class="grid">
        <article class="card"><span>Customers</span><strong>—</strong><small>Customer accounts</small></article>
        <article class="card"><span>Experts</span><strong>—</strong><small>Expert profiles</small></article>
        <article class="card"><span>Requirements</span><strong>—</strong><small>Customer requirements</small></article>
        <article class="card"><span>Consultations</span><strong>—</strong><small>Consultation requests</small></article>
      </section>
      <section class="panel"><div class="eyebrow">Administration</div><h2>Management areas</h2><div class="areas"><div><b>Experts</b><p>Review and manage expert profiles and verification.</p></div><div><b>Customers</b><p>Review customer accounts and company profiles.</p></div><div><b>Requirements</b><p>Monitor marketplace requirements.</p></div><div><b>Consultations</b><p>Monitor consultation request lifecycle.</p></div></div></section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 48px}.topbar,.hero,.grid,.panel{max-width:1180px;margin:auto}.topbar{padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,small{color:#667085;font-size:14px}.subtitle{margin-top:4px}.topbar button{border:1px solid #d5dbe6;background:#fff;border-radius:9px;padding:10px 16px;font-weight:750;cursor:pointer}.hero{padding:52px 0 28px}.eyebrow{font-size:11px;text-transform:uppercase;letter-spacing:.12em;font-weight:850;color:#315ea8}.hero h1{font-size:42px;margin:9px 0}.hero p{color:#667085}.grid{display:grid;grid-template-columns:repeat(4,1fr);gap:16px}.card,.panel{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:22px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.card span{display:block;color:#667085;font-size:13px;font-weight:800;text-transform:uppercase}.card strong{display:block;font-size:40px;margin:18px 0 4px}.panel{margin-top:24px}.panel h2{margin:8px 0 20px}.areas{display:grid;grid-template-columns:1fr 1fr;gap:14px}.areas div{border:1px solid #e5e9f0;border-radius:12px;padding:18px}.areas p{color:#667085;line-height:1.5;margin-bottom:0}@media(max-width:800px){.grid{grid-template-columns:1fr 1fr}.areas{grid-template-columns:1fr}.page{padding:0 18px 36px}}@media(max-width:520px){.grid{grid-template-columns:1fr}.hero h1{font-size:34px}}
  `]
})
export class AdminDashboardComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  constructor(){ const user=this.auth.getCurrentUser(); if(!user || user.role !== 'ADMIN'){ this.router.navigateByUrl('/'); } }
  logout(){ this.auth.logout(); this.router.navigateByUrl('/login'); }
}
