import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
import { ConsultationRequest, ExpertProfile, ExpertService } from '../core/expert/expert.service';

@Component({
  selector: 'app-expert-consultation-inbox',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <main class="page">
      <header class="topbar">
        <div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Expert consultation inbox</div></div>
        <div class="nav"><a routerLink="/expert/dashboard">Dashboard</a><button type="button" (click)="logout()">Sign out</button></div>
      </header>

      <section class="content">
        <div class="eyebrow">Consultation management</div>
        <h1>Consultation inbox</h1>
        <p class="intro">Review customer consultation requests and respond securely from your expert workspace.</p>

        @if (loading) { <section class="state">Loading consultation inbox…</section> }
        @else if (errorMessage) { <section class="state error">{{ errorMessage }}</section> }
        @else {
          <section class="summary">
            <div class="card"><span>Total</span><strong>{{ requests.length }}</strong></div>
            <div class="card"><span>Pending</span><strong>{{ pendingCount }}</strong></div>
            <div class="card"><span>Accepted</span><strong>{{ acceptedCount }}</strong></div>
            <div class="card"><span>Rejected</span><strong>{{ rejectedCount }}</strong></div>
          </section>

          <section class="toolbar">
            <div class="filters">
              @for (value of filters; track value) {
                <button type="button" [class.active]="filter === value" (click)="filter = value">{{ value }}</button>
              }
            </div>
            <button type="button" class="refresh" [disabled]="refreshing" (click)="loadRequests()">{{ refreshing ? 'Refreshing…' : 'Refresh' }}</button>
          </section>

          @if (filteredRequests.length === 0) {
            <section class="state">No {{ filter === 'ALL' ? '' : filter.toLowerCase() + ' ' }}consultation requests found.</section>
          } @else {
            <section class="list">
              @for (request of filteredRequests; track request.id) {
                <article class="request card">
                  <div class="request-header">
                    <div><div class="request-title">{{ request.requirementTitle || 'Consultation request' }}</div><div class="muted">Request #{{ request.id }} · Customer #{{ request.customerId }} · Requirement #{{ request.requirementId }}</div></div>
                    <span class="badge" [class.pending]="request.status === 'PENDING'" [class.accepted]="request.status === 'ACCEPTED'" [class.rejected]="request.status === 'REJECTED'">{{ request.status }}</span>
                  </div>
                  @if (request.message) { <p class="message">{{ request.message }}</p> }
                  <div class="details">
                    <div><span>Requested start</span><b>{{ request.requestedStartDate || 'Not specified' }}</b></div>
                    <div><span>Estimated hours</span><b>{{ request.estimatedHours || '—' }}</b></div>
                    <div><span>Proposed rate</span><b>{{ request.proposedRate || '—' }} {{ request.currencyCode || '' }}</b></div>
                    <div><span>Created</span><b>{{ request.createdAt | date:'medium' }}</b></div>
                  </div>
                  @if (request.status === 'REJECTED' && request.rejectionReason) { <div class="reason"><b>Rejection reason:</b> {{ request.rejectionReason }}</div> }
                  @if (request.status === 'PENDING') {
                    <div class="actions">
                      <button class="accept" type="button" [disabled]="actionId === request.id" (click)="accept(request)">{{ actionId === request.id ? 'Processing…' : 'Accept request' }}</button>
                      <button class="reject" type="button" [disabled]="actionId === request.id" (click)="reject(request)">Reject request</button>
                    </div>
                  }
                </article>
              }
            </section>
          }
        }
      </section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 56px;box-sizing:border-box}.topbar{max-width:1180px;margin:auto;padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,.muted{color:#667085;font-size:14px}.subtitle{margin-top:4px}.nav{display:flex;gap:10px;align-items:center}.nav a,.nav button,.refresh,.filters button{border:1px solid #d5dbe6;background:#fff;color:#172033;border-radius:9px;padding:10px 14px;font-weight:750;text-decoration:none;cursor:pointer}.content{max-width:1180px;margin:auto;padding-top:48px}.eyebrow{text-transform:uppercase;letter-spacing:.1em;font-size:11px;font-weight:850;color:#315ea8}.content h1{font-size:42px;margin:9px 0}.intro{color:#667085;max-width:760px;line-height:1.6}.summary{display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-top:28px}.card{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:20px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.summary span{display:block;color:#667085;font-size:12px;text-transform:uppercase;font-weight:800}.summary strong{display:block;font-size:34px;margin-top:10px}.toolbar{display:flex;justify-content:space-between;align-items:center;margin:32px 0 16px}.filters{display:flex;gap:8px;flex-wrap:wrap}.filters button.active{background:#172033;color:#fff}.refresh:disabled,.actions button:disabled{opacity:.55;cursor:not-allowed}.state{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:22px;margin-top:24px}.error{color:#b42318;background:#fff5f4}.list{display:grid;gap:14px}.request-header{display:flex;justify-content:space-between;gap:20px;align-items:start}.request-title{font-size:20px;font-weight:850}.request{padding:22px}.badge{display:inline-block;border-radius:999px;padding:6px 10px;background:#eef2f7;font-size:12px;font-weight:850}.badge.pending{background:#fff4d6;color:#8a5a00}.badge.accepted{background:#e8f7ee;color:#067647}.badge.rejected{background:#fff0ef;color:#b42318}.message{color:#536078;line-height:1.55}.details{display:grid;grid-template-columns:repeat(4,1fr);gap:10px;margin:18px 0}.details div{background:#f8fafc;border-radius:10px;padding:11px}.details span{display:block;color:#98a2b3;font-size:11px;margin-bottom:4px}.details b{font-size:13px}.reason{color:#b42318;font-size:13px;margin-top:8px}.actions{display:flex;gap:9px;margin-top:16px}.actions button{border:0;border-radius:9px;padding:10px 15px;font-weight:800;cursor:pointer}.accept{background:#172033;color:#fff}.reject{background:#fff;color:#b42318;border:1px solid #e4b8b4!important}@media(max-width:850px){.page{padding:0 18px 40px}.summary{grid-template-columns:1fr 1fr}.details{grid-template-columns:1fr 1fr}.toolbar{align-items:start;gap:12px;flex-direction:column}.request-header{flex-direction:column}.content h1{font-size:34px}}
  `]
})
export class ExpertConsultationInboxComponent {
  private readonly auth = inject(AuthService);
  private readonly expertService = inject(ExpertService);

  expert: ExpertProfile | null = null;
  requests: ConsultationRequest[] = [];
  loading = true;
  refreshing = false;
  errorMessage = '';
  actionId: number | null = null;
  filter = 'ALL';
  readonly filters = ['ALL', 'PENDING', 'ACCEPTED', 'REJECTED'];

  get filteredRequests(): ConsultationRequest[] { return this.filter === 'ALL' ? this.requests : this.requests.filter(r => r.status === this.filter); }
  get pendingCount(): number { return this.requests.filter(r => r.status === 'PENDING').length; }
  get acceptedCount(): number { return this.requests.filter(r => r.status === 'ACCEPTED').length; }
  get rejectedCount(): number { return this.requests.filter(r => r.status === 'REJECTED').length; }

  constructor() {
    const user = this.auth.getCurrentUser();
    if (!user) { window.location.href = '/login'; return; }
    this.expertService.findByEmail(user.email).subscribe({
      next: expert => { this.expert = expert; this.loading = false; this.loadRequests(); },
      error: error => { this.loading = false; this.errorMessage = error?.error?.error || error?.message || 'Unable to load your expert profile.'; }
    });
  }

  loadRequests(): void {
    if (!this.expert) return;
    this.refreshing = true;
    this.expertService.getConsultationRequests(this.expert.expertId).subscribe({
      next: page => { this.requests = page.content ?? []; this.refreshing = false; this.errorMessage = ''; },
      error: error => { this.refreshing = false; this.errorMessage = error?.error?.error || 'Unable to load consultation requests.'; }
    });
  }

  accept(request: ConsultationRequest): void {
    this.actionId = request.id;
    this.expertService.acceptConsultation(request.id).subscribe({
      next: updated => { this.replace(updated); this.actionId = null; },
      error: error => { this.actionId = null; this.errorMessage = error?.error?.error || 'Unable to accept the request.'; }
    });
  }

  reject(request: ConsultationRequest): void {
    const reason = window.prompt('Reason for rejecting this request:', 'Not available for the requested schedule.');
    if (!reason?.trim()) return;
    this.actionId = request.id;
    this.expertService.rejectConsultation(request.id, reason.trim()).subscribe({
      next: updated => { this.replace(updated); this.actionId = null; },
      error: error => { this.actionId = null; this.errorMessage = error?.error?.error || 'Unable to reject the request.'; }
    });
  }

  private replace(updated: ConsultationRequest): void { this.requests = this.requests.map(r => r.id === updated.id ? updated : r); }
  logout(): void { this.auth.logout(); window.location.href = '/login'; }
}
