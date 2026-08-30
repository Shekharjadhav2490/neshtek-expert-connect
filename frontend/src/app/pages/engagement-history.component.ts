import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ExpertService, Engagement, EngagementHistory } from '../core/expert/expert.service';

@Component({
  selector: 'app-engagement-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <main class="page">
      <header class="topbar">
        <div><b>NeshTek Expert Connect</b><div class="sub">Engagement history</div></div>
        <a routerLink="/expert/dashboard">Expert dashboard</a>
      </header>

      <section class="content">
        <div class="eyebrow">Engagement lifecycle</div>
        <h1>Engagement history</h1>
        @if (engagement) {
          <p class="intro">{{ engagement.requirementTitle }} · Engagement #{{ engagement.id }}</p>
        }

        @if (error) { <div class="state error">{{ error }}</div> }
        @if (loading) { <div class="state">Loading history…</div> }

        @if (!loading && !history.length && !error) {
          <div class="state">No lifecycle history has been recorded yet.</div>
        }

        @if (history.length) {
          <section class="timeline">
            @for (item of history; track item.id) {
              <article class="event">
                <div class="dot"></div>
                <div class="event-body">
                  <div class="event-head">
                    <div>
                      <h2>{{ formatAction(item.action) }}</h2>
                      <div class="muted">{{ item.occurredAt | date:'medium' }}</div>
                    </div>
                    @if (item.toStatus) {
                      <span class="status">{{ item.toStatus }}</span>
                    }
                  </div>
                  @if (item.fromStatus && item.toStatus) {
                    <div class="transition">{{ item.fromStatus }} <span>→</span> {{ item.toStatus }}</div>
                  }
                  @if (item.actorName) {
                    <div class="meta"><b>Performed by:</b> {{ item.actorName }} <span>({{ item.actorRole }})</span></div>
                  }
                  @if (item.reason) {
                    <div class="reason"><b>Reason:</b> {{ item.reason }}</div>
                  }
                </div>
              </article>
            }
          </section>
        }
      </section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 56px}.topbar,.content{max-width:1000px;margin:auto}.topbar{padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.topbar a{border:1px solid #d5dbe6;background:#fff;border-radius:9px;padding:10px 14px;text-decoration:none;color:#315ea8;font-weight:750}.sub,.muted,.intro{color:#667085;font-size:14px}.sub{margin-top:4px}.content{padding-top:48px}.eyebrow{text-transform:uppercase;letter-spacing:.1em;font-size:11px;font-weight:850;color:#315ea8}.content h1{font-size:42px;margin:9px 0}.intro{line-height:1.6}.state{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:22px;margin-top:24px}.error{color:#b42318;background:#fff5f4}.timeline{position:relative;margin-top:30px;padding-left:34px}.timeline:before{content:'';position:absolute;left:8px;top:4px;bottom:4px;width:2px;background:#dbe3ef}.event{position:relative;margin-bottom:22px}.dot{position:absolute;left:-32px;top:7px;width:14px;height:14px;border-radius:50%;background:#315ea8;border:3px solid #f7f9fc;box-shadow:0 0 0 1px #315ea8}.event-body{background:#fff;border:1px solid #e1e6ef;border-radius:14px;padding:18px;box-shadow:0 8px 24px rgba(23,32,51,.04)}.event-head{display:flex;justify-content:space-between;gap:18px;align-items:flex-start}.event h2{margin:0 0 5px;font-size:18px}.status{background:#eef4ff;color:#315ea8;padding:6px 10px;border-radius:999px;font-size:11px;font-weight:850}.transition{display:inline-block;margin-top:12px;padding:7px 10px;background:#f8fafc;border-radius:8px;font-weight:750;font-size:13px}.transition span{padding:0 7px;color:#667085}.meta{margin-top:12px;font-size:13px;color:#536078}.meta span{color:#98a2b3}.reason{margin-top:10px;padding:10px 12px;background:#fff7e6;border:1px solid #f0d59c;border-radius:9px;color:#8a5a00;font-size:13px}@media(max-width:700px){.page{padding:0 18px 40px}.content h1{font-size:34px}.event-head{display:block}.status{display:inline-block;margin-top:10px}}
  `]
})
export class EngagementHistoryComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly expertService = inject(ExpertService);
  engagementId = 0;
  engagement: Engagement | null = null;
  history: EngagementHistory[] = [];
  loading = false;
  error = '';

  constructor() {
    this.engagementId = Number(this.route.snapshot.paramMap.get('engagementId'));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.expertService.getEngagement(this.engagementId).subscribe({
      next: e => this.engagement = e,
      error: e => this.error = e?.error?.error || 'Unable to load engagement.'
    });
    this.expertService.getEngagementHistory(this.engagementId).subscribe({
      next: items => { this.history = items ?? []; this.loading = false; },
      error: e => { this.error = e?.error?.error || 'Unable to load engagement history.'; this.loading = false; }
    });
  }

  formatAction(action: string): string {
    return action.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }
}
