import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CustomerService, CustomerRequirement } from '../core/customer/customer.service';
import { ExpertMatchingService, ExpertMatch } from '../core/matching/expert-matching.service';

@Component({
  selector: 'app-customer-matching',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <main class="page">
      <header class="topbar">
        <div>
          <div class="brand">NeshTek Expert Connect</div>
          <div class="subtitle">Expert matching workspace</div>
        </div>
        <a routerLink="/customer/dashboard" class="back">Customer dashboard</a>
      </header>

      <section class="content">
        <div class="eyebrow">Smart matching</div>
        <h1>Find the right expert</h1>
        <p class="intro">Review your requirement and compare verified expert matches using skills, experience, availability and technology fit.</p>

        @if (loadingRequirement) {
          <section class="state">Loading requirement…</section>
        } @else if (requirementError) {
          <section class="state error">{{ requirementError }}</section>
        } @else if (requirement) {
          <section class="requirement card">
            <div>
              <div class="card-label">Requirement</div>
              <h2>{{ requirement.title }}</h2>
              <p>{{ requirement.description }}</p>
              <div class="chips">
                <span>{{ requirement.technology }}</span>
                <span>{{ requirement.requiredExperienceYears }} years+</span>
                <span>{{ requirement.priority }}</span>
                <span>{{ requirement.status }}</span>
              </div>
              @if (requirement.skills?.length) {
                <div class="skills">
                  @for (skill of requirement.skills; track skill.id) {
                    <span [class.mandatory]="skill.mandatory">{{ skill.skillName }}</span>
                  }
                </div>
              }
            </div>
            <button type="button" (click)="loadMatches()" [disabled]="loadingMatches">
              {{ loadingMatches ? 'Finding matches…' : 'Refresh matches' }}
            </button>
          </section>

          @if (loadingMatches) {
            <section class="state">Finding the best expert matches…</section>
          } @else if (matchError) {
            <section class="state error">{{ matchError }}</section>
          } @else {
            <div class="results-head">
              <h2>{{ matches.length }} expert match{{ matches.length === 1 ? '' : 'es' }}</h2>
              <span>Ranked by match score</span>
            </div>

            @if (!matches.length) {
              <section class="state">No matching experts are available for this requirement yet.</section>
            } @else {
              <section class="matches">
                @for (match of matches; track match.expertId) {
                  <article class="match card">
                    <div class="identity">
                      <div class="avatar">{{ match.firstName.charAt(0) }}{{ match.lastName.charAt(0) }}</div>
                      <div>
                        <h3>{{ match.firstName }} {{ match.lastName }}</h3>
                        <div class="muted">{{ match.city }} · {{ match.totalExperienceYears }} years experience</div>
                      </div>
                    </div>
                    <div class="score">
                      <strong>{{ match.matchScore | number:'1.0-0' }}%</strong>
                      <span>{{ match.matchLevel }}</span>
                    </div>
                    <div class="metrics">
                      <div><span>Skills</span><b>{{ match.matchedSkills }}/{{ match.requiredSkills }}</b></div>
                      <div><span>Experience</span><b>{{ match.experienceMatch ? 'Match' : 'Gap' }}</b></div>
                      <div><span>Availability</span><b>{{ match.availabilityMatch ? 'Match' : 'Gap' }}</b></div>
                      <div><span>Technology</span><b>{{ match.technologyMatch ? 'Match' : 'Gap' }}</b></div>
                    </div>
                    @if (match.matchedSkillNames?.length) {
                      <div class="matched"><b>Matched skills:</b> {{ match.matchedSkillNames.join(', ') }}</div>
                    }
                    @if (match.missingMandatorySkills?.length) {
                      <div class="missing"><b>Missing mandatory:</b> {{ match.missingMandatorySkills.join(', ') }}</div>
                    }
                    <p class="recommendation">{{ match.recommendation }}</p>
                    <button type="button" class="request" [disabled]="!match.mandatorySkillsSatisfied">Request consultation</button>
                  </article>
                }
              </section>
            }
          }
        }
      </section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 56px;box-sizing:border-box}.topbar{max-width:1180px;margin:0 auto;padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle,.muted{color:#667085;font-size:14px}.subtitle{margin-top:4px}.back{color:#315ea8;text-decoration:none;font-weight:750;border:1px solid #d5dbe6;background:#fff;padding:10px 14px;border-radius:9px}.content{max-width:1180px;margin:0 auto;padding-top:48px}.eyebrow,.card-label{text-transform:uppercase;letter-spacing:.1em;font-size:11px;font-weight:850;color:#315ea8}.content h1{font-size:42px;margin:9px 0}.intro{color:#667085;max-width:780px;line-height:1.6}.card,.state{background:#fff;border:1px solid #e1e6ef;border-radius:16px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.requirement{margin-top:28px;padding:24px;display:flex;justify-content:space-between;gap:24px}.requirement h2{margin:10px 0 7px}.requirement p{color:#667085;line-height:1.5;max-width:780px}.requirement button,.request{border:0;border-radius:9px;background:#172033;color:#fff;padding:11px 16px;font-weight:800;height:max-content;cursor:pointer}.requirement button:disabled{opacity:.55}.chips,.skills{display:flex;flex-wrap:wrap;gap:8px}.chips span,.skills span{background:#eef2f7;border-radius:999px;padding:6px 10px;font-size:12px;font-weight:750}.skills{margin-top:12px}.skills .mandatory{background:#e8eef9;color:#315ea8}.results-head{display:flex;justify-content:space-between;align-items:end;margin:34px 0 14px}.results-head h2{margin:0}.results-head span{color:#667085;font-size:13px}.matches{display:grid;gap:14px}.match{padding:22px}.identity{display:flex;gap:13px;align-items:center}.avatar{width:46px;height:46px;border-radius:50%;display:grid;place-items:center;background:#e8eef9;color:#315ea8;font-weight:850}.identity h3{margin:0 0 3px}.score{float:right;text-align:right;margin-top:-44px}.score strong{display:block;font-size:28px}.score span{font-size:12px;color:#315ea8;font-weight:800}.metrics{display:grid;grid-template-columns:repeat(4,1fr);gap:10px;margin:22px 0}.metrics div{background:#f8fafc;border-radius:10px;padding:11px}.metrics span{display:block;color:#98a2b3;font-size:11px;margin-bottom:4px}.metrics b{font-size:13px}.matched,.missing,.recommendation{font-size:13px;line-height:1.5;margin-top:10px}.missing{color:#b42318}.recommendation{color:#667085}.request{margin-top:8px}.request:disabled{opacity:.45;cursor:not-allowed}.state{padding:22px;margin-top:24px}.error{color:#b42318;background:#fff5f4}@media(max-width:800px){.page{padding:0 18px 40px}.requirement{flex-direction:column}.metrics{grid-template-columns:1fr 1fr}.score{float:none;text-align:left;margin:18px 0 0}}
  `]
})
export class CustomerMatchingComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly customerService = inject(CustomerService);
  private readonly matchingService = inject(ExpertMatchingService);

  requirement: CustomerRequirement | null = null;
  matches: ExpertMatch[] = [];
  loadingRequirement = true;
  loadingMatches = false;
  requirementError = '';
  matchError = '';

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('requirementId'));
    if (!Number.isFinite(id) || id <= 0) {
      this.loadingRequirement = false;
      this.requirementError = 'A valid requirement ID is required.';
      return;
    }

    this.customerService.getRequirement(id).subscribe({
      next: requirement => {
        this.requirement = requirement;
        this.loadingRequirement = false;
        this.loadMatches();
      },
      error: error => {
        this.loadingRequirement = false;
        this.requirementError = error?.error?.error || 'Unable to load the requirement.';
      }
    });
  }

  loadMatches(): void {
    if (!this.requirement) return;
    this.loadingMatches = true;
    this.matchError = '';
    this.matchingService.findMatches(this.requirement.id, 10).subscribe({
      next: matches => {
        this.matches = matches;
        this.loadingMatches = false;
      },
      error: error => {
        this.loadingMatches = false;
        this.matchError = error?.error?.error || 'Unable to load expert matches.';
      }
    });
  }
}
