import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ConsultationRequestService, ConsultationRequest } from '../core/consultation/consultation-request.service';

@Component({
  selector: 'app-customer-consultations',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <main class="page">
      <header class="topbar">
        <div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Customer consultations</div></div>
        <a routerLink="/customer/dashboard" class="back">Customer dashboard</a>
      </header>
      <section class="content">
        <div class="eyebrow">Consultations</div>
        <h1>My consultation requests</h1>
        <p class="intro">Track requests sent to experts and their current response status.</p>
        @if (error) { <section class="state error">{{ error }}</section> }
        @if (loading) { <section class="state">Loading consultations…</section> }
        @else if (!requests.length && !error) { <section class="state">No consultation requests found.</section> }
        @else {
          <section class="list">
            @for (request of requests; track request.id) {
              <article class="card">
                <div class="head">
                  <div><div class="label">Requirement</div><h2>{{ request.requirementTitle || ('Requirement #' + request.requirementId) }}</h2></div>
                  <span class="status" [class.accepted]="request.status === 'ACCEPTED'" [class.rejected]="request.status === 'REJECTED'">{{ request.status }}</span>
                </div>
                <div class="grid">
                  <div><span>Expert</span><b>{{ request.expertName || ('Expert #' + request.expertId) }}</b></div>
                  <div><span>Hours</span><b>{{ request.estimatedHours ?? '—' }}</b></div>
                  <div><span>Rate</span><b>{{ request.proposedRate ?? '—' }} {{ request.currencyCode || '' }}</b></div>
                  <div><span>Start date</span><b>{{ request.requestedStartDate || '—' }}</b></div>
                  <div><span>Requested</span><b>{{ request.createdAt | date:'medium' }}</b></div>
                  <div><span>Responded</span><b>{{ request.respondedAt ? (request.respondedAt | date:'medium') : 'Pending' }}</b></div>
                </div>
                @if (request.message) { <p class="message">{{ request.message }}</p> }
                @if (request.rejectionReason) { <p class="rejection"><b>Reason:</b> {{ request.rejectionReason }}</p> }
              </article>
            }
          </section>
        }
      </section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 56px}.topbar{max-width:1180px;margin:auto;padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,.intro,.message{color:#667085}.subtitle{margin-top:4px;font-size:14px}.back{color:#315ea8;text-decoration:none;font-weight:750;border:1px solid #d5dbe6;background:#fff;padding:10px 14px;border-radius:9px}.content{max-width:1180px;margin:auto;padding-top:48px}.eyebrow,.label{text-transform:uppercase;letter-spacing:.1em;font-size:11px;font-weight:850;color:#315ea8}.content h1{font-size:42px;margin:9px 0}.intro{line-height:1.6}.list{display:grid;gap:14px;margin-top:28px}.card{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:22px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.head{display:flex;justify-content:space-between;gap:20px}.head h2{margin:8px 0}.status{align-self:flex-start;padding:7px 11px;border-radius:999px;background:#eef2f7;font-size:12px;font-weight:850}.status.accepted{background:#ecfdf3;color:#067647}.status.rejected{background:#fff5f4;color:#b42318}.grid{display:grid;grid-template-columns:repeat(3,1fr);gap:10px;margin-top:18px}.grid div{background:#f8fafc;border-radius:10px;padding:11px}.grid span{display:block;color:#98a2b3;font-size:11px;margin-bottom:4px}.grid b{font-size:13px}.message{line-height:1.5;margin:16px 0 0}.rejection{color:#b42318;font-size:13px}.state{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:22px;margin-top:24px}.error{color:#b42318;background:#fff5f4}@media(max-width:800px){.page{padding:0 18px 40px}.grid{grid-template-columns:1fr 1fr}.head{flex-direction:column}}
  `]
})
export class CustomerConsultationsComponent {
  private readonly service = inject(ConsultationRequestService);
  requests: ConsultationRequest[] = [];
  loading = true;
  error = '';
  constructor(){ this.load(); }
  load(): void {
    this.loading = true; this.error = '';
    this.service.listMine().subscribe({ next: r => { this.requests = r.content ?? r; this.loading = false; }, error: e => { this.loading = false; this.error = e?.error?.message || e?.error?.error || 'Unable to load consultation requests.'; } });
  }
}
