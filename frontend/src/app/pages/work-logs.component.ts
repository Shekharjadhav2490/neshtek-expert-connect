import { Component, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
import { CustomerService } from '../core/customer/customer.service';
import { ExpertService, WorkLogRequest } from '../core/expert/expert.service';

@Component({
  selector: 'app-work-logs', standalone: true, imports: [CommonModule, DatePipe, FormsModule, RouterLink],
  template: `
    <main class="page">
      <header class="topbar"><div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Engagement work log</div></div><a class="back" [routerLink]="backPath">Back to dashboard</a></header>
      <section class="hero"><div class="eyebrow">Work delivery</div><h1>Work logs</h1><p>Engagement #{{ engagementId }} · Track time delivered against this engagement.</p></section>
      @if (errorMessage) { <section class="state error" role="alert">{{ errorMessage }}</section> }
      <section class="summary"><div class="summary-card"><span>Total logged</span><strong>{{ totalHours | number:'1.0-2' }} hrs</strong></div><div class="summary-card"><span>Entries</span><strong>{{ logs.length }}</strong></div></section>
      @if (isExpert) {
        <section class="panel"><div class="eyebrow">Add entry</div><h2>Log completed work</h2>
          <form (ngSubmit)="addLog()"><div class="form-grid"><label>Work date<input type="date" [(ngModel)]="form.workDate" name="workDate" required></label><label>Hours<input type="number" [(ngModel)]="form.hours" name="hours" min="0.25" max="24" step="0.25" required></label></div>
          <label>Description<textarea [(ngModel)]="form.description" name="description" rows="4" maxlength="2000" placeholder="Describe the work completed..." required></textarea></label><div class="form-actions"><button class="primary" type="submit" [disabled]="saving">{{ saving ? 'Saving…' : 'Add work log' }}</button></div></form>
        </section>
      }
      <section class="panel"><div class="section-heading"><div><div class="eyebrow">History</div><h2>Logged work</h2></div><button class="refresh" type="button" (click)="load()" [disabled]="loading">{{ loading ? 'Refreshing…' : 'Refresh' }}</button></div>
        @if (loading) { <div class="state">Loading work logs…</div> } @else if (logs.length === 0) { <div class="state">No work has been logged for this engagement yet.</div> } @else { <div class="logs">@for (log of logs; track log.id) { <article class="log"><div class="date">{{ log.workDate | date:'mediumDate' }}</div><div class="hours">{{ log.hours | number:'1.0-2' }} hrs</div><div class="description">{{ log.description }}</div><div class="created">Added {{ log.createdAt | date:'medium' }}</div></article> }</div> }
      </section>
    </main>`,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 56px;box-sizing:border-box}.topbar,.hero,.summary,.panel,.state{max-width:1180px;margin-left:auto;margin-right:auto}.topbar{padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,.state,.created{color:#667085;font-size:14px}.subtitle{margin-top:4px}.back{color:#315ea8;text-decoration:none;font-weight:800;font-size:13px}.hero{padding:52px 0 28px}.eyebrow{text-transform:uppercase;letter-spacing:.12em;font-size:11px;font-weight:850;color:#315ea8}.hero h1{font-size:42px;margin:9px 0}.hero p{color:#667085}.summary{display:grid;grid-template-columns:1fr 1fr;gap:16px}.summary-card,.panel,.log{background:#fff;border:1px solid #e1e6ef;border-radius:16px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.summary-card{padding:22px}.summary-card span{color:#667085;font-size:13px;font-weight:800;text-transform:uppercase}.summary-card strong{display:block;font-size:34px;margin-top:12px}.panel{margin-top:22px;padding:24px}.panel h2{margin:8px 0 20px}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:16px}label{display:block;font-size:13px;font-weight:800;color:#344054;margin-bottom:16px}input,textarea{box-sizing:border-box;width:100%;margin-top:7px;border:1px solid #d5dbe6;border-radius:9px;padding:11px 12px;font:inherit;color:#172033;background:#fff}textarea{resize:vertical}.form-actions{display:flex;justify-content:flex-end}.primary,.refresh{border:1px solid #d5dbe6;border-radius:9px;padding:10px 16px;font-weight:800;cursor:pointer}.primary{background:#172033;color:#fff;border-color:#172033}.refresh{background:#fff;color:#172033}.primary:disabled,.refresh:disabled{opacity:.5;cursor:not-allowed}.section-heading{display:flex;justify-content:space-between;align-items:end;margin-bottom:16px}.logs{display:grid;gap:12px}.log{padding:18px;display:grid;grid-template-columns:160px 100px 1fr;column-gap:18px;align-items:start}.date{font-weight:800}.hours{font-weight:850}.description{line-height:1.5;color:#344054}.created{grid-column:3;margin-top:8px}.state{padding:16px 0}.error{padding:14px 16px;background:#fff5f4;border:1px solid #f1c5c1;border-radius:10px;color:#b42318}.error+.summary{margin-top:20px}@media(max-width:700px){.page{padding:0 18px 42px}.summary,.form-grid{grid-template-columns:1fr}.log{grid-template-columns:1fr;gap:7px}.created{grid-column:auto}.hero h1{font-size:34px}.section-heading{align-items:start;gap:12px;flex-direction:column}}
  `]
})
export class WorkLogsComponent {
  private readonly route = inject(ActivatedRoute); private readonly auth = inject(AuthService); private readonly customerService = inject(CustomerService); private readonly expertService = inject(ExpertService);
  engagementId = Number(this.route.snapshot.paramMap.get('engagementId')); logs: any[] = []; totalHours = 0; loading = true; saving = false; errorMessage = '';
  isExpert = this.auth.getCurrentUser()?.role === 'EXPERT' || this.auth.getCurrentUser()?.role === 'ROLE_EXPERT';
  backPath = this.isExpert ? '/expert/dashboard' : '/customer/dashboard';
  form: WorkLogRequest = { workDate: new Date().toISOString().slice(0, 10), hours: 1, description: '' };
  constructor() { if (!this.engagementId) { this.errorMessage = 'Invalid engagement.'; this.loading = false; return; } this.load(); }
  load(): void { this.loading = true; this.errorMessage = ''; const logs$ = this.isExpert ? this.expertService.getWorkLogs(this.engagementId) : this.customerService.getWorkLogs(this.engagementId); const total$ = this.isExpert ? this.expertService.getTotalWorkHours(this.engagementId) : this.customerService.getTotalWorkHours(this.engagementId); logs$.subscribe({next: page => {this.logs = page.content ?? []; this.loading = false;}, error: error => {this.loading = false; this.errorMessage = error?.error?.error || 'Unable to load work logs.';}}); total$.subscribe({next: total => this.totalHours = Number(total ?? 0), error: () => this.totalHours = 0}); }
  addLog(): void { if (!this.form.workDate || !this.form.hours || !this.form.description.trim()) return; this.saving = true; this.errorMessage = ''; this.expertService.addWorkLog(this.engagementId, {...this.form, description: this.form.description.trim()}).subscribe({next: () => {this.saving = false; this.form = {workDate: new Date().toISOString().slice(0, 10), hours: 1, description: ''}; this.load();}, error: error => {this.saving = false; this.errorMessage = error?.error?.error || 'Unable to save the work log.';}}); }
}
