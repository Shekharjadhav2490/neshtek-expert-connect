import { Component, Input, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ExpertReplacement, ExpertReplacementService } from '../core/replacement/expert-replacement.service';

@Component({
  selector: 'app-replacement-history-panel', standalone: true, imports: [CommonModule, DatePipe, RouterLink],
  template: `
    <section class="replacement-panel">
      <div class="panel-head">
        <div><div class="eyebrow">Replacement tracking</div><h3>Expert replacement</h3><div class="summary">{{ items.length ? items.length + ' replacement request' + (items.length === 1 ? '' : 's') + ' recorded' : 'Replacement history available on demand' }}</div></div>
        <div class="head-actions">
          @if (allowRequest && canRequest) { <a class="request-button" [routerLink]="['/customer/expert-replacement', engagementId]">Request replacement</a> }
          @if (allowRequest && hasOpenRequest) { <span class="request-status">Replacement in progress</span> }
          <button type="button" class="history-button" (click)="toggle()">{{ expanded ? 'Hide history' : 'View history' }}</button>
        </div>
      </div>
      @if (expanded) {
        @if (loading) { <div class="state">Loading replacement history…</div> }
        @else if (error) { <div class="state error">{{ error }}</div> }
        @else if (!items.length) { <div class="empty">No replacement requests have been recorded for this engagement.</div> }
        @else { <div class="history-list">@for (item of items; track item.id) { <article class="history-item"><div class="history-main"><div class="history-title"><b>Request #{{ item.id }}</b><span class="status" [class.requested]="item.status==='REQUESTED'" [class.approved]="item.status==='APPROVED'" [class.replaced]="item.status==='REPLACED'" [class.rejected]="item.status==='REJECTED'" [class.cancelled]="item.status==='CANCELLED'">{{ item.status }}</span></div><div class="muted">{{ item.reasonCode }} · Requested {{ item.requestedAt | date:'medium' }}</div><p>{{ item.comments || 'No additional comments.' }}</p>@if (item.reviewedAt) { <small>Reviewed {{ item.reviewedAt | date:'medium' }}{{ item.reviewerComment ? ' · ' + item.reviewerComment : '' }}</small> }</div><div class="history-result"><span>Replacement expert</span><b>{{ item.newExpertName || 'Not assigned yet' }}</b>@if (item.newEngagementId) { <small>New engagement #{{ item.newEngagementId }}</small> }</div></article> }</div> }
      }
    </section>
  `,
  styles: [`
    .replacement-panel{margin-top:16px;background:#fff;border:1px solid #dbe3ef;border-radius:14px;padding:16px}.panel-head{display:flex;justify-content:space-between;align-items:center;gap:16px}.eyebrow{font-size:10px;text-transform:uppercase;letter-spacing:.1em;font-weight:850;color:#315ea8}.panel-head h3{margin:4px 0 2px;font-size:17px}.summary{color:#667085;font-size:11px}.head-actions{display:flex;gap:8px;align-items:center}.history-button,.request-button,.request-status{border-radius:8px;padding:8px 11px;font-size:11px;font-weight:800;text-decoration:none;white-space:nowrap}.history-button{cursor:pointer;border:1px solid #315ea8;background:#fff;color:#315ea8}.request-button{border:1px solid #172033;background:#172033;color:#fff}.request-status{border:1px solid #f0d79b;background:#fff8e6;color:#8a5a00}.history-list{display:grid;gap:9px;margin-top:14px}.history-item{display:flex;justify-content:space-between;gap:18px;border-top:1px solid #edf0f5;padding-top:12px}.history-main{flex:1}.history-title{display:flex;gap:8px;align-items:center}.status{padding:5px 9px;border-radius:999px;background:#eef2f7;font-size:10px;font-weight:850}.status.requested{background:#fff4d6;color:#8a5a00}.status.approved{background:#eef4ff;color:#315ea8}.status.replaced{background:#ecfdf3;color:#067647}.status.rejected,.status.cancelled{background:#fff5f4;color:#b42318}.muted,small{color:#667085;font-size:11px}.history-main p{margin:7px 0;font-size:12px;line-height:1.45}.history-result{min-width:170px;background:#f8fafc;border-radius:9px;padding:10px}.history-result span{display:block;color:#98a2b3;text-transform:uppercase;font-size:9px;font-weight:800}.history-result b{display:block;margin-top:4px;font-size:13px}.history-result small{display:block;margin-top:4px}.empty,.state{padding:16px 4px;color:#667085;font-size:12px}.error{color:#b42318}@media(max-width:650px){.panel-head,.history-item{align-items:flex-start;flex-direction:column}.head-actions{flex-wrap:wrap}.history-result{min-width:0;width:100%;box-sizing:border-box}}
  `]
})
export class ReplacementHistoryPanelComponent implements OnChanges {
  @Input({required:true}) engagementId = 0;
  @Input() allowRequest = false;
  @Input() engagementStatus = '';
  private readonly service = inject(ExpertReplacementService);
  items: ExpertReplacement[] = [];
  loading = false; error = ''; expanded = false;

  get hasOpenRequest(): boolean { return this.items.some(item => item.status === 'REQUESTED' || item.status === 'APPROVED'); }
  get canRequest(): boolean { if (!this.allowRequest || !['READY','ACTIVE','PAUSED'].includes(this.engagementStatus)) return false; return !this.hasOpenRequest; }
  ngOnChanges(changes: SimpleChanges): void { if (changes['engagementId'] && this.engagementId) { this.items = []; this.expanded = false; } }
  toggle(): void { this.expanded = !this.expanded; if (this.expanded && !this.items.length && !this.loading) this.load(); }
  load(): void { this.loading=true; this.error=''; this.service.byEngagement(this.engagementId).subscribe({next:items=>{this.items=items??[];this.loading=false;},error:e=>{this.error=e?.error?.message||e?.error?.error||'Unable to load replacement history.';this.loading=false;}}); }
}
