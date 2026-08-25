import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CustomerService, CustomerProfile, CustomerRequirement } from '../core/customer/customer.service';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <main class="dashboard">
      <header class="topbar">
        <div>
          <div class="brand">NeshTek Expert Connect</div>
          <div class="subtitle">Customer dashboard</div>
        </div>
        <button class="logout" type="button" (click)="logout()">Sign out</button>
      </header>

      <section class="welcome">
        <div>
          <div class="eyebrow">Customer workspace</div>
          <h1>Welcome back{{ displayName ? ', ' + displayName : '' }}.</h1>
          <p>Manage your company profile and continue finding the right experts for your requirements.</p>
        </div>
      </section>

      @if (loading) {
        <section class="state-card">Loading your customer profile…</section>
      } @else if (errorMessage) {
        <section class="state-card error" role="alert">{{ errorMessage }}</section>
      } @else if (customer) {
        <section class="grid">
          <article class="card profile">
            <div class="card-title">Company profile</div>
            <div class="profile-name">{{ customer.companyName }}</div>
            <div class="muted">{{ customer.contactName }} · {{ customer.email }}</div>
            <dl>
              <div><dt>Location</dt><dd>{{ customer.city }}, {{ customer.country }}</dd></div>
              <div><dt>Industry</dt><dd>{{ customer.industry || 'Not specified' }}</dd></div>
              <div><dt>Company size</dt><dd>{{ customer.companySize || 'Not specified' }}</dd></div>
              <div><dt>Status</dt><dd>{{ customer.status }}</dd></div>
            </dl>
          </article>

          <article class="card stat">
            <div class="card-title">Requirements</div>
            <div class="number">{{ requirements.length }}</div>
            <div class="muted">Requirements currently available to your account.</div>
          </article>

          <article class="card next">
            <div class="card-title">Next step</div>
            <h2>Find expert matches</h2>
            <p>Select a requirement below to compare the best available experts.</p>
            @if (requirements[0]) {
              <a class="primary-link" [routerLink]="['/customer/requirements', requirements[0].id, 'matches']">Open latest requirement</a>
            }
          </article>
        </section>

        <section class="requirements-section">
          <div class="section-head">
            <div>
              <div class="eyebrow">Your requirements</div>
              <h2>Requirements & expert matching</h2>
            </div>
          </div>
          @if (requirementsLoading) {
            <section class="state-card">Loading requirements…</section>
          } @else if (!requirements.length) {
            <section class="state-card">No requirements are available yet.</section>
          } @else {
            <div class="requirements-list">
              @for (requirement of requirements; track requirement.id) {
                <article class="requirement-card">
                  <div>
                    <div class="requirement-title">{{ requirement.title }}</div>
                    <div class="muted">{{ requirement.technology }} · {{ requirement.requiredExperienceYears }} years+ · {{ requirement.priority }}</div>
                    <div class="skill-list">
                      @for (skill of requirement.skills; track skill.id) {
                        <span>{{ skill.skillName }}</span>
                      }
                    </div>
                  </div>
                  <a class="match-link" [routerLink]="['/customer/requirements', requirement.id, 'matches']">Find expert matches →</a>
                </article>
              }
            </div>
          }
        </section>
      }
    </main>
  `,
  styles: [`
    .dashboard{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 48px;box-sizing:border-box}.topbar{max-width:1180px;margin:0 auto;padding:22px 0;display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #e3e8f0}.brand{font-weight:850;letter-spacing:.01em}.subtitle,.muted{color:#667085;font-size:14px}.subtitle{margin-top:4px}.logout{border:1px solid #d5dbe6;background:#fff;color:#172033;border-radius:9px;padding:10px 16px;font-weight:750;cursor:pointer}.welcome{max-width:1180px;margin:0 auto;padding:56px 0 28px}.eyebrow{text-transform:uppercase;letter-spacing:.12em;font-size:11px;font-weight:850;color:#315ea8}.welcome h1{font-size:42px;line-height:1.1;margin:9px 0 12px}.welcome p{max-width:720px;color:#667085;line-height:1.6;margin:0}.grid{max-width:1180px;margin:0 auto;display:grid;grid-template-columns:2fr 1fr 1fr;gap:18px}.card,.state-card{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:24px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.card-title{font-size:13px;font-weight:800;color:#667085;text-transform:uppercase;letter-spacing:.07em}.profile-name{font-size:24px;font-weight:850;margin:18px 0 5px}.profile dl{margin:24px 0 0;display:grid;grid-template-columns:1fr 1fr;gap:16px}.profile dt{font-size:12px;color:#98a2b3;margin-bottom:4px}.profile dd{margin:0;font-weight:700}.number{font-size:46px;font-weight:850;margin:25px 0 5px}.next h2{margin:18px 0 8px;font-size:22px}.next p{color:#667085;line-height:1.55}.primary-link,.match-link{display:inline-block;margin-top:12px;color:#315ea8;font-weight:800;text-decoration:none}.state-card{max-width:1180px;margin:0 auto}.error{color:#b42318;background:#fff5f4}.stat,.next{min-height:180px}.requirements-section{max-width:1180px;margin:42px auto 0}.section-head h2{margin:8px 0 18px}.requirements-list{display:grid;gap:12px}.requirement-card{background:#fff;border:1px solid #e1e6ef;border-radius:14px;padding:20px;display:flex;align-items:center;justify-content:space-between;gap:20px}.requirement-title{font-size:18px;font-weight:850;margin-bottom:5px}.skill-list{display:flex;flex-wrap:wrap;gap:7px;margin-top:12px}.skill-list span{background:#eef2f7;border-radius:999px;padding:5px 9px;font-size:11px;font-weight:750}.match-link{white-space:nowrap}@media(max-width:900px){.grid{grid-template-columns:1fr}.welcome h1{font-size:34px}.dashboard{padding:0 18px 36px}.requirement-card{align-items:flex-start;flex-direction:column}.match-link{white-space:normal}}
  `]
})
export class CustomerDashboardComponent {
  private readonly auth = inject(AuthService);
  private readonly customerService = inject(CustomerService);
  private readonly router = inject(Router);

  customer: CustomerProfile | null = null;
  requirements: CustomerRequirement[] = [];
  loading = true;
  requirementsLoading = true;
  errorMessage = '';

  get displayName(): string {
    return this.customer?.contactName ?? '';
  }

  constructor() {
    const user = this.auth.getCurrentUser();
    if (!user) {
      this.router.navigateByUrl('/login');
      return;
    }

    this.customerService.getCustomer(user.userId).subscribe({
      next: customer => {
        this.customer = customer;
        this.loading = false;
        this.customerService.getRequirements(customer.customerId).subscribe({
          next: page => {
            this.requirements = page.content ?? [];
            this.requirementsLoading = false;
          },
          error: () => {
            this.requirements = [];
            this.requirementsLoading = false;
          }
        });
      },
      error: error => {
        this.loading = false;
        this.errorMessage = error?.error?.error || 'Unable to load your customer profile.';
      }
    });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
