import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
import { ConsultationRequest, Engagement, ExpertProfile, ExpertService } from '../core/expert/expert.service';

@Component({
  selector: 'app-expert-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <main class="dashboard">
      <header class="topbar">
        <div>
          <div class="brand">NeshTek Expert Connect</div>
          <div class="subtitle">Expert dashboard</div>
        </div>
        <button class="logout" type="button" (click)="logout()">Sign out</button>
      </header>

      <section class="welcome">
        <div class="eyebrow">Expert workspace</div>
        <h1>Welcome back{{ displayName ? ', ' + displayName : '' }}.</h1>
        <p>Manage your expert profile, consultation opportunities and active engagements from one secure workspace.</p>
      </section>

      @if (loading) {
        <section class="state-card">Loading your expert workspace…</section>
      } @else if (errorMessage) {
        <section class="state-card error" role="alert">{{ errorMessage }}</section>
      } @else if (expert) {
        <section class="grid">
          <article class="card profile">
            <div class="card-title">Expert profile</div>
            <div class="profile-name">{{ expert.firstName }} {{ expert.lastName }}</div>
            <div class="muted">{{ expert.email }}</div>
            <dl>
              <div><dt>Expert ID</dt><dd>{{ expert.expertId }}</dd></div>
              <div><dt>Status</dt><dd><span class="status">{{ expert.status }}</span></dd></div>
              <div><dt>Skills</dt><dd>{{ expert.skillCount }}</dd></div>
              <div><dt>Expertise words</dt><dd>{{ expert.expertiseWordCount }}</dd></div>
            </dl>
          </article>

          <article class="card stat">
            <div class="card-title">Consultation requests</div>
            <div class="number">{{ requests.length }}</div>
            <div class="muted">Requests currently visible to your expert account.</div>
          </article>

          <article class="card stat">
            <div class="card-title">Pending</div>
            <div class="number">{{ pendingCount }}</div>
            <div class="muted">Requests waiting for your response.</div>
          </article>
        </section>

        <section class="requests">
          <div class="section-heading">
            <div>
              <div class="eyebrow">Consultations</div>
              <h2>Consultation requests</h2>
            </div>
            <button type="button" class="refresh" (click)="loadRequests()">Refresh</button>
          </div>

          @if (requests.length === 0) {
            <div class="state-card empty">No consultation requests are available yet.</div>
          } @else {
            <div class="request-list">
              @for (request of requests; track request.id) {
                <article class="request-card">
                  <div class="request-main">
                    <div class="request-title">{{ request.requirementTitle }}</div>
                    <div class="muted">Customer #{{ request.customerId }} · Requirement #{{ request.requirementId }}</div>
                    @if (request.message) { <p>{{ request.message }}</p> }
                    <div class="meta">
                      <span>{{ request.estimatedHours }} hrs</span>
                      <span>{{ request.proposedRate }} {{ request.currencyCode }}/hr</span>
                      <span>{{ request.requestedStartDate }}</span>
                    </div>
                  </div>
                  <div class="request-actions">
                    <span class="badge" [class.pending]="request.status === 'PENDING'">{{ request.status }}</span>
                    @if (request.status === 'PENDING') {
                      <div class="buttons">
                        <button type="button" class="accept" [disabled]="actionId === request.id" (click)="accept(request)">Accept</button>
                        <button type="button" class="reject" [disabled]="actionId === request.id" (click)="reject(request)">Reject</button>
                      </div>
                    }
                  </div>
                </article>
              }
            </div>
          }
        </section>

        <section class="engagements">
          <div class="section-heading">
            <div>
              <div class="eyebrow">Work delivery</div>
              <h2>My engagements</h2>
              <p class="section-copy">Accepted consultations become engagements that you can start, complete or cancel.</p>
            </div>
            <button type="button" class="refresh" (click)="loadEngagements()">Refresh</button>
          </div>

          @if (engagements.length === 0) {
            <div class="state-card empty">No engagements are available yet. Accept a consultation request to create one.</div>
          } @else {
            <div class="engagement-list">
              @for (engagement of engagements; track engagement.id) {
                <article class="engagement-card">
                  <div class="engagement-main">
                    <div class="engagement-title">{{ engagement.requirementTitle }}</div>
                    <div class="muted">Customer #{{ engagement.customerId }} · Requirement #{{ engagement.requirementId }}</div>
                    <div class="meta">
                      <span>{{ engagement.estimatedHours ?? '—' }} hrs</span>
                      <span>{{ engagement.agreedRate ?? '—' }} {{ engagement.currencyCode ?? '' }}/hr</span>
                      @if (engagement.requestedStartDate) { <span>Start {{ engagement.requestedStartDate }}</span> }
                    </div>
                    @if (engagement.startedAt) { <div class="timeline">Started {{ engagement.startedAt | date:'medium' }}</div> }
                    @if (engagement.completedAt) { <div class="timeline">Completed {{ engagement.completedAt | date:'medium' }}</div> }
                    @if (engagement.cancelledAt) { <div class="timeline">Cancelled {{ engagement.cancelledAt | date:'medium' }}</div> }
                  </div>
                  <div class="engagement-actions">
                    <span class="badge" [class.ready]="engagement.status === 'READY'" [class.active]="engagement.status === 'ACTIVE'" [class.completed]="engagement.status === 'COMPLETED'" [class.cancelled]="engagement.status === 'CANCELLED'">{{ engagement.status }}</span>
                    @if (engagement.status === 'READY') {
                      <button type="button" class="primary-action" [disabled]="engagementActionId === engagement.id" (click)="startEngagement(engagement)">Start engagement</button>
                      <button type="button" class="cancel-action" [disabled]="engagementActionId === engagement.id" (click)="cancelEngagement(engagement)">Cancel</button>
                    }
                    @if (engagement.status === 'ACTIVE') {
                      <button type="button" class="primary-action" [disabled]="engagementActionId === engagement.id" (click)="completeEngagement(engagement)">Complete engagement</button>
                      <button type="button" class="cancel-action" [disabled]="engagementActionId === engagement.id" (click)="cancelEngagement(engagement)">Cancel</button>
                    }
                  </div>
                </article>
              }
            </div>
          }
        </section>
      }
    </main>
  `,
  styles: [`
    .dashboard{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 56px;box-sizing:border-box}.topbar{max-width:1180px;margin:0 auto;padding:22px 0;display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #e3e8f0}.brand{font-weight:850}.subtitle,.muted{color:#667085;font-size:14px}.subtitle{margin-top:4px}.logout,.refresh{border:1px solid #d5dbe6;background:#fff;color:#172033;border-radius:9px;padding:10px 16px;font-weight:750;cursor:pointer}.welcome,.requests,.engagements{max-width:1180px;margin:0 auto}.welcome{padding:54px 0 28px}.eyebrow{text-transform:uppercase;letter-spacing:.12em;font-size:11px;font-weight:850;color:#315ea8}.welcome h1{font-size:42px;line-height:1.1;margin:9px 0 12px}.welcome p{max-width:760px;color:#667085;line-height:1.6}.grid{max-width:1180px;margin:0 auto;display:grid;grid-template-columns:2fr 1fr 1fr;gap:18px}.card,.state-card,.request-card,.engagement-card{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:24px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.card-title{font-size:13px;font-weight:800;color:#667085;text-transform:uppercase;letter-spacing:.07em}.profile-name{font-size:24px;font-weight:850;margin:18px 0 5px}.profile dl{margin:24px 0 0;display:grid;grid-template-columns:1fr 1fr;gap:16px}.profile dt{font-size:12px;color:#98a2b3;margin-bottom:4px}.profile dd{margin:0;font-weight:700}.status,.badge{display:inline-block;padding:5px 9px;border-radius:999px;background:#eef2f7;font-size:12px;font-weight:800}.number{font-size:46px;font-weight:850;margin:25px 0 5px}.stat{min-height:180px}.requests,.engagements{margin-top:42px}.section-heading{display:flex;justify-content:space-between;align-items:end;margin-bottom:16px}.section-heading h2{font-size:28px;margin:8px 0 0}.section-copy{color:#667085;margin:8px 0 0;font-size:14px}.request-list,.engagement-list{display:grid;gap:14px}.request-card,.engagement-card{display:flex;justify-content:space-between;gap:24px}.request-title,.engagement-title{font-size:20px;font-weight:850;margin-bottom:5px}.request-main p{color:#536078;line-height:1.5;margin:15px 0}.meta{display:flex;gap:16px;flex-wrap:wrap;font-size:13px;font-weight:750;color:#536078;margin-top:14px}.request-actions,.engagement-actions{display:flex;flex-direction:column;align-items:end;justify-content:space-between;gap:12px;min-width:180px}.badge.pending{background:#fff4d6;color:#8a5a00}.badge.ready{background:#eef4ff;color:#315ea8}.badge.active{background:#e9f7ef;color:#087443}.badge.completed{background:#e8f1ff;color:#175cd3}.badge.cancelled{background:#fcebea;color:#b42318}.buttons{display:flex;gap:8px}.buttons button,.primary-action,.cancel-action{border:0;border-radius:9px;padding:9px 13px;font-weight:800;cursor:pointer}.accept,.primary-action{background:#172033;color:#fff}.reject,.cancel-action{background:#fff;color:#b42318;border:1px solid #e4b8b4!important}.buttons button:disabled,.primary-action:disabled,.cancel-action:disabled{opacity:.5;cursor:not-allowed}.state-card{max-width:1180px;margin:0 auto}.error{color:#b42318;background:#fff5f4}.empty{color:#667085}.refresh{margin-bottom:2px}.timeline{font-size:13px;color:#667085;margin-top:10px}.engagement-main{flex:1}.engagement-actions{justify-content:flex-start}.engagement-actions button{width:100%}@media(max-width:900px){.grid{grid-template-columns:1fr}.request-card,.engagement-card{flex-direction:column}.request-actions,.engagement-actions{align-items:start}.engagement-actions button{width:auto}.welcome h1{font-size:34px}.dashboard{padding:0 18px 42px}}
  `]
})
export class ExpertDashboardComponent {
  private readonly auth = inject(AuthService);
  private readonly expertService = inject(ExpertService);
  private readonly router = inject(Router);

  expert: ExpertProfile | null = null;
  requests: ConsultationRequest[] = [];
  engagements: Engagement[] = [];
  loading = true;
  errorMessage = '';
  actionId: number | null = null;
  engagementActionId: number | null = null;

  get displayName(): string {
    return this.expert ? `${this.expert.firstName} ${this.expert.lastName}` : '';
  }

  get pendingCount(): number {
    return this.requests.filter(request => request.status === 'PENDING').length;
  }

  constructor() {
    const user = this.auth.getCurrentUser();
    if (!user) {
      this.router.navigateByUrl('/login');
      return;
    }

    this.expertService.findByEmail(user.email).subscribe({
      next: expert => {
        this.expert = expert;
        this.loading = false;
        this.loadRequests();
        this.loadEngagements();
      },
      error: error => {
        this.loading = false;
        this.errorMessage = error?.error?.error || error?.message || 'Unable to load your expert profile.';
      }
    });
  }

  loadRequests(): void {
    if (!this.expert) return;
    this.expertService.getConsultationRequests(this.expert.expertId).subscribe({
      next: page => this.requests = page.content ?? [],
      error: error => this.errorMessage = error?.error?.error || 'Unable to load consultation requests.'
    });
  }

  loadEngagements(): void {
    if (!this.expert) return;
    this.expertService.getEngagements(this.expert.expertId).subscribe({
      next: page => this.engagements = page.content ?? [],
      error: error => this.errorMessage = error?.error?.error || 'Unable to load engagements.'
    });
  }

  accept(request: ConsultationRequest): void {
    this.actionId = request.id;
    this.expertService.acceptConsultation(request.id).subscribe({
      next: updated => {
        this.replaceRequest(updated);
        this.actionId = null;
        this.loadEngagements();
      },
      error: error => {
        this.actionId = null;
        this.errorMessage = error?.error?.error || 'Unable to accept the consultation request.';
      }
    });
  }

  reject(request: ConsultationRequest): void {
    const reason = window.prompt('Enter a reason for rejecting this consultation request:', 'Not available for the requested schedule.');
    if (!reason?.trim()) return;

    this.actionId = request.id;
    this.expertService.rejectConsultation(request.id, reason.trim()).subscribe({
      next: updated => {
        this.replaceRequest(updated);
        this.actionId = null;
      },
      error: error => {
        this.actionId = null;
        this.errorMessage = error?.error?.error || 'Unable to reject the consultation request.';
      }
    });
  }

  startEngagement(engagement: Engagement): void {
    this.runEngagementAction(engagement.id, () => this.expertService.startEngagement(engagement.id));
  }

  completeEngagement(engagement: Engagement): void {
    this.runEngagementAction(engagement.id, () => this.expertService.completeEngagement(engagement.id));
  }

  cancelEngagement(engagement: Engagement): void {
    if (!window.confirm('Cancel this engagement?')) return;
    this.runEngagementAction(engagement.id, () => this.expertService.cancelEngagement(engagement.id));
  }

  private runEngagementAction(id: number, action: () => ReturnType<ExpertService['startEngagement']>): void {
    this.engagementActionId = id;
    action().subscribe({
      next: updated => {
        this.engagements = this.engagements.map(item => item.id === updated.id ? updated : item);
        this.engagementActionId = null;
      },
      error: error => {
        this.engagementActionId = null;
        this.errorMessage = error?.error?.error || 'Unable to update the engagement.';
      }
    });
  }

  private replaceRequest(updated: ConsultationRequest): void {
    this.requests = this.requests.map(request => request.id === updated.id ? updated : request);
  }

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
