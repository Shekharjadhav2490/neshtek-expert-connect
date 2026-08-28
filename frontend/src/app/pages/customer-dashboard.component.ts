import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService, CustomerProfile, CustomerRequirement, CustomerRequirementRequest, CustomerEngagement } from '../core/customer/customer.service';
import { ConsultationRequest, ConsultationRequestService } from '../core/consultation/consultation-request.service';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <main class="dashboard">
      <header class="topbar"><div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Customer dashboard</div></div><button class="logout" type="button" (click)="logout()">Sign out</button></header>
      <section class="welcome"><div><div class="eyebrow">Customer workspace</div><h1>Welcome back{{ displayName ? ', ' + displayName : '' }}.</h1><p>Manage your company profile, requirements, expert consultations and active engagements from one workspace.</p></div></section>

      @if (loading) { <section class="state-card">Loading your customer profile…</section> }
      @else if (errorMessage) { <section class="state-card error">{{ errorMessage }}</section> }
      @else if (customer) {
        <section class="grid">
          <article class="card profile"><div class="card-title">Company profile</div><div class="profile-name">{{ customer.companyName }}</div><div class="muted">{{ customer.contactName }} · {{ customer.email }}</div><dl><div><dt>Location</dt><dd>{{ customer.city }}, {{ customer.country }}</dd></div><div><dt>Industry</dt><dd>{{ customer.industry || 'Not specified' }}</dd></div><div><dt>Company size</dt><dd>{{ customer.companySize || 'Not specified' }}</dd></div><div><dt>Status</dt><dd>{{ customer.status }}</dd></div></dl></article>
          <article class="card stat"><div class="card-title">Requirements</div><div class="number">{{ requirements.length }}</div><div class="muted">Requirements currently available to your account.</div></article>
          <article class="card next"><div class="card-title">Next step</div><h2>Find expert matches</h2><p>Select a requirement below to compare the best available experts.</p>@if (requirements[0]) { <a class="primary-link" [routerLink]="['/customer/requirements', requirements[0].id, 'matches']">Open latest requirement</a> }</article>
        </section>

        <section class="consultation-section">
          <div class="section-head consultation-head">
            <div><div class="eyebrow">Consultation requests</div><h2>Expert response status</h2><p class="section-description">Track requests sent to experts and see when they are accepted, rejected or still pending.</p></div>
            <a class="secondary-link" routerLink="/customer/consultations">View all consultations →</a>
          </div>
          @if (consultationsLoading) { <section class="state-card compact">Loading consultation requests…</section> }
          @else if (consultationError) { <section class="state-card error compact">{{ consultationError }}</section> }
          @else if (!consultations.length) { <section class="state-card compact empty">No consultation requests have been sent yet.</section> }
          @else {
            <div class="consultation-summary">
              <div class="summary-card"><span>All requests</span><b>{{ consultations.length }}</b></div>
              <div class="summary-card accepted-summary"><span>Accepted</span><b>{{ acceptedConsultations }}</b></div>
              <div class="summary-card pending-summary"><span>Pending</span><b>{{ pendingConsultations }}</b></div>
              <div class="summary-card rejected-summary"><span>Rejected</span><b>{{ rejectedConsultations }}</b></div>
            </div>
            <div class="consultation-list">
              @for (request of recentConsultations; track request.id) {
                <article class="consultation-card"><div class="consultation-main"><div class="label">Requirement</div><h3>{{ request.requirementTitle || ('Requirement #' + request.requirementId) }}</h3><div class="consultation-meta"><span>Expert: <b>{{ request.expertName || ('Expert #' + request.expertId) }}</b></span><span>Requested: <b>{{ request.createdAt ? (request.createdAt | date:'medium') : '—' }}</b></span></div></div><div class="consultation-status-wrap"><span class="status" [class.accepted]="request.status === 'ACCEPTED'" [class.rejected]="request.status === 'REJECTED'" [class.pending]="request.status === 'PENDING'">{{ request.status }}</span>@if (request.respondedAt) { <small>Responded {{ request.respondedAt | date:'mediumDate' }}</small> }</div></article>
              }
            </div>
          }
        </section>

        <section class="engagement-section">
          <div class="section-head engagement-head">
            <div><div class="eyebrow">Engagements</div><h2>My engagements</h2><p class="section-description">Track confirmed expert work from ready to active and completed.</p></div>
            <button type="button" class="refresh" (click)="loadEngagements()">Refresh</button>
          </div>
          @if (engagementsLoading) { <section class="state-card compact">Loading engagements…</section> }
          @else if (engagementError) { <section class="state-card error compact">{{ engagementError }}</section> }
          @else if (!engagements.length) { <section class="state-card compact empty">No engagements have been created yet. An engagement will appear here after an expert accepts your consultation request.</section> }
          @else {
            <div class="engagement-summary">
              <div class="summary-card"><span>All</span><b>{{ engagements.length }}</b></div>
              <div class="summary-card ready-summary"><span>Ready</span><b>{{ readyEngagements }}</b></div>
              <div class="summary-card active-summary"><span>Active</span><b>{{ activeEngagements }}</b></div>
              <div class="summary-card completed-summary"><span>Completed</span><b>{{ completedEngagements }}</b></div>
            </div>
            <div class="engagement-list">
              @for (engagement of engagements; track engagement.id) {
                <article class="engagement-card">
                  <div class="engagement-main">
                    <div class="label">Engagement #{{ engagement.id }}</div>
                    <h3>{{ engagement.requirementTitle || ('Requirement #' + engagement.requirementId) }}</h3>
                    <div class="engagement-meta"><span>Expert: <b>{{ engagement.expertName || ('Expert #' + engagement.expertId) }}</b></span><span>{{ engagement.estimatedHours }} hrs</span><span>{{ engagement.agreedRate }} {{ engagement.currencyCode }}/hr</span><span>Start: {{ engagement.requestedStartDate || '—' }}</span></div>
                    @if (engagement.startedAt) { <div class="timeline-text">Started {{ engagement.startedAt | date:'medium' }}</div> }
                    @if (engagement.completedAt) { <div class="timeline-text">Completed {{ engagement.completedAt | date:'medium' }}</div> }
                    @if (engagement.cancelledAt) { <div class="timeline-text cancelled-text">Cancelled {{ engagement.cancelledAt | date:'medium' }}</div> }
                  </div>
                  <div class="engagement-status-wrap">
                    <span class="engagement-status" [class.ready]="engagement.status === 'READY'" [class.active]="engagement.status === 'ACTIVE'" [class.completed]="engagement.status === 'COMPLETED'" [class.cancelled]="engagement.status === 'CANCELLED'">{{ engagement.status }}</span>
                    <a class="work-log-link" [routerLink]="['/customer/work-logs', engagement.id]">{{ engagement.status === 'ACTIVE' ? 'Log / View work →' : 'View work logs →' }}</a>
                  </div>
                </article>
              }
            </div>
          }
        </section>

        <section class="requirements-section">
          <div class="section-head"><div><div class="eyebrow">Your requirements</div><h2>Requirements & expert matching</h2></div><button class="primary" type="button" (click)="openCreate()">+ Create requirement</button></div>
          @if (successMessage) { <div class="notice success">{{ successMessage }}</div> }
          @if (requirementsLoading) { <section class="state-card">Loading requirements…</section> }
          @else if (!requirements.length) { <section class="state-card empty">No requirements are available yet. Create your first requirement.</section> }
          @else {
            <div class="requirements-list">@for (requirement of requirements; track requirement.id) {
              <article class="requirement-card"><div><div class="requirement-title">{{ requirement.title }}</div><div class="muted">{{ requirement.technology || 'Technology not specified' }} · {{ requirement.requiredExperienceYears || 0 }} years+ · {{ requirement.priority }} · {{ requirement.status }}</div><div class="skill-list">@for (skill of requirement.skills; track skill.id) { <span>{{ skill.skillName }}</span> }</div></div><div class="row-actions"><a class="match-link" [routerLink]="['/customer/requirements', requirement.id, 'matches']">Find expert matches →</a>@if (canDelete(requirement)) { <button class="delete" type="button" (click)="deleteRequirement(requirement)">Delete</button> }</div></article>
            }</div>
          }
        </section>
      }

      @if (showCreate && customer) {
        <div class="overlay"><section class="modal"><div class="modal-head"><div><div class="eyebrow">New requirement</div><h2>Create a requirement</h2><p>Tell us what expertise you need and we'll match verified experts.</p></div><button class="icon" type="button" (click)="closeCreate()">×</button></div>
          <form (ngSubmit)="createRequirement()"><div class="form-grid"><label>Title *<input name="title" [(ngModel)]="form.title" required maxlength="250" placeholder="e.g. Oracle WebCenter Content Expert"></label><label>Technology<input name="technology" [(ngModel)]="form.technology" placeholder="e.g. Oracle WebCenter Content"></label><label class="wide">Description *<textarea name="description" [(ngModel)]="form.description" required rows="4" placeholder="Describe the implementation, support or project requirement"></textarea></label><label>Experience (years)<input type="number" name="experience" min="0" step="0.5" [(ngModel)]="form.requiredExperienceYears"></label><label>Estimated hours<input type="number" name="hours" min="0.1" step="0.5" [(ngModel)]="form.estimatedHours"></label><label>Preferred start date<input type="date" name="startDate" [(ngModel)]="form.preferredStartDate"></label><label>Priority<select name="priority" [(ngModel)]="form.priority"><option>LOW</option><option selected>MEDIUM</option><option>HIGH</option><option>URGENT</option></select></label><label>Budget<input type="number" name="budget" min="0" step="0.01" [(ngModel)]="form.budget"></label><label>Currency<select name="currency" [(ngModel)]="form.currencyCode"><option>USD</option><option>INR</option><option>EUR</option><option>GBP</option><option>AED</option></select></label><label class="wide">Skills <input name="skills" [(ngModel)]="skillsText" placeholder="Oracle WebCenter Content, Oracle Database, Java"></label></div>@if (formError) { <div class="notice error-box">{{ formError }}</div> }<div class="modal-actions"><button type="button" class="secondary" (click)="closeCreate()">Cancel</button><button type="submit" class="primary" [disabled]="saving">{{ saving ? 'Creating…' : 'Create requirement' }}</button></div></form>
        </section></div>
      }
    </main>
  `,
  styles: [`
    .dashboard{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 48px;box-sizing:border-box}.topbar{max-width:1180px;margin:0 auto;padding:22px 0;display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #e3e8f0}.brand{font-weight:850}.subtitle,.muted{color:#667085;font-size:14px}.subtitle{margin-top:4px}.logout,.secondary,.icon,.refresh{border:1px solid #d5dbe6;background:#fff;color:#172033;border-radius:9px;padding:10px 16px;font-weight:750;cursor:pointer}.welcome{max-width:1180px;margin:0 auto;padding:56px 0 28px}.eyebrow{text-transform:uppercase;letter-spacing:.12em;font-size:11px;font-weight:850;color:#315ea8}.welcome h1{font-size:42px;line-height:1.1;margin:9px 0 12px}.welcome p{max-width:760px;color:#667085;line-height:1.6;margin:0}.grid{max-width:1180px;margin:0 auto;display:grid;grid-template-columns:2fr 1fr 1fr;gap:18px}.card,.state-card{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:24px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.card-title{font-size:13px;font-weight:800;color:#667085;text-transform:uppercase;letter-spacing:.07em}.profile-name{font-size:24px;font-weight:850;margin:18px 0 5px}.profile dl{margin:24px 0 0;display:grid;grid-template-columns:1fr 1fr;gap:16px}.profile dt{font-size:12px;color:#98a2b3;margin-bottom:4px}.profile dd{margin:0;font-weight:700}.number{font-size:46px;font-weight:850;margin:25px 0 5px}.next h2{margin:18px 0 8px;font-size:22px}.next p{color:#667085;line-height:1.55}.primary-link,.match-link{display:inline-block;margin-top:12px;color:#315ea8;font-weight:800;text-decoration:none}.state-card{max-width:1180px;margin:0 auto}.state-card.compact{margin:0}.error{color:#b42318;background:#fff5f4}.stat,.next{min-height:180px}.section-head{display:flex;justify-content:space-between;align-items:end;gap:20px}.section-head h2{margin:8px 0 0}.section-description{color:#667085;margin:8px 0 0;line-height:1.5}.consultation-section,.engagement-section,.requirements-section{max-width:1180px;margin:38px auto 0}.consultation-head,.engagement-head{align-items:flex-end;margin-bottom:16px}.secondary-link{color:#315ea8;font-weight:800;text-decoration:none;white-space:nowrap}.consultation-summary,.engagement-summary{display:grid;grid-template-columns:repeat(4,1fr);gap:12px;margin-bottom:12px}.summary-card{background:#fff;border:1px solid #e1e6ef;border-radius:12px;padding:15px 18px;display:flex;align-items:center;justify-content:space-between}.summary-card span{font-size:12px;color:#667085;font-weight:750}.summary-card b{font-size:24px}.accepted-summary b,.active-summary b{color:#067647}.pending-summary b,.ready-summary b{color:#8a5a00}.rejected-summary b,.cancelled-summary b{color:#b42318}.consultation-list,.engagement-list{display:grid;gap:10px}.consultation-card,.engagement-card{background:#fff;border:1px solid #e1e6ef;border-radius:13px;padding:16px 18px;display:flex;justify-content:space-between;align-items:center;gap:20px}.label{font-size:10px;text-transform:uppercase;letter-spacing:.1em;font-weight:850;color:#315ea8}.consultation-card h3,.engagement-card h3{font-size:17px;margin:6px 0}.consultation-meta,.engagement-meta{display:flex;flex-wrap:wrap;gap:18px;color:#667085;font-size:12px}.consultation-meta b,.engagement-meta b{color:#172033}.consultation-status-wrap,.engagement-status-wrap{display:flex;flex-direction:column;align-items:flex-end;gap:5px}.consultation-status-wrap small{color:#98a2b3;font-size:11px}.status{padding:7px 11px;border-radius:999px;background:#eef2f7;font-size:11px;font-weight:850}.status.accepted{background:#ecfdf3;color:#067647}.status.rejected{background:#fff5f4;color:#b42318}.status.pending{background:#fff4d6;color:#8a5a00}.engagement-status{padding:8px 12px;border-radius:999px;background:#eef2f7;font-size:11px;font-weight:850;white-space:nowrap}.engagement-status.ready{background:#fff4d6;color:#8a5a00}.engagement-status.active{background:#ecfdf3;color:#067647}.engagement-status.completed{background:#eaf2ff;color:#315ea8}.engagement-status.cancelled{background:#fff5f4;color:#b42318}.work-log-link{color:#315ea8;font-size:12px;font-weight:800;text-decoration:none;white-space:nowrap}.work-log-link:hover{text-decoration:underline}.timeline-text{margin-top:9px;font-size:11px;color:#667085}.cancelled-text{color:#b42318}.refresh{margin-bottom:2px}.requirements-section{margin-top:42px}.section-head h2{margin:8px 0 18px}.primary{border:0;background:#17233a;color:white;border-radius:9px;padding:11px 15px;font-weight:800;cursor:pointer}.primary:disabled{opacity:.55;cursor:not-allowed}.requirements-list{display:grid;gap:12px}.requirement-card{background:#fff;border:1px solid #e1e6ef;border-radius:14px;padding:20px;display:flex;align-items:center;justify-content:space-between;gap:20px}.requirement-title{font-size:18px;font-weight:850;margin-bottom:5px}.skill-list{display:flex;flex-wrap:wrap;gap:7px;margin-top:12px}.skill-list span{background:#eef2f7;border-radius:999px;padding:5px 9px;font-size:11px;font-weight:750}.row-actions{display:flex;align-items:center;gap:14px}.match-link{white-space:nowrap}.delete{border:0;background:none;color:#b42318;font-weight:800;cursor:pointer}.notice{padding:12px 14px;border-radius:9px;margin:0 0 14px}.success{background:#ecfdf3;color:#176b3a;border:1px solid #b7ebca}.empty{text-align:center}.overlay{position:fixed;inset:0;background:rgba(15,23,42,.42);display:flex;align-items:center;justify-content:center;padding:20px;z-index:20}.modal{width:min(780px,100%);max-height:90vh;overflow:auto;background:white;border-radius:16px;padding:24px;box-shadow:0 24px 70px rgba(0,0,0,.25)}.modal-head{display:flex;justify-content:space-between;gap:20px}.modal-head h2{font-size:28px;margin:8px 0}.modal-head p{color:#667085;margin:0 0 20px}.icon{font-size:24px;width:42px;height:42px;padding:0}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:14px}.form-grid label{font-size:12px;font-weight:800;color:#475467}.form-grid .wide{grid-column:1/-1}.form-grid input,.form-grid textarea,.form-grid select{display:block;width:100%;box-sizing:border-box;margin-top:6px;border:1px solid #d5dbe6;border-radius:8px;padding:10px 11px;font:inherit;color:#172033;background:#fff}.form-grid textarea{resize:vertical}.modal-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:20px}.error-box{background:#fff5f4;color:#b42318;border:1px solid #f3b7b2}@media(max-width:900px){.grid{grid-template-columns:1fr}.welcome h1{font-size:34px}.dashboard{padding:0 18px 36px}.requirement-card{align-items:flex-start;flex-direction:column}.row-actions{width:100%;justify-content:space-between}.match-link{white-space:normal}.form-grid{grid-template-columns:1fr}.form-grid .wide{grid-column:auto}.section-head{align-items:start;flex-direction:column}.consultation-summary,.engagement-summary{grid-template-columns:1fr 1fr}.consultation-card,.engagement-card{align-items:flex-start;flex-direction:column}.consultation-status-wrap,.engagement-status-wrap{align-items:flex-start}.secondary-link{margin-top:4px}}@media(max-width:520px){.consultation-summary,.engagement-summary{grid-template-columns:1fr}.consultation-meta,.engagement-meta{flex-direction:column;gap:5px}}
  `]
})
export class CustomerDashboardComponent {
  private readonly auth = inject(AuthService);
  private readonly customerService = inject(CustomerService);
  private readonly consultationService = inject(ConsultationRequestService);
  private readonly router = inject(Router);

  customer: CustomerProfile | null = null;
  requirements: CustomerRequirement[] = [];
  consultations: ConsultationRequest[] = [];
  engagements: CustomerEngagement[] = [];
  loading = true;
  requirementsLoading = true;
  consultationsLoading = true;
  engagementsLoading = true;
  errorMessage = '';
  consultationError = '';
  engagementError = '';
  successMessage = '';
  showCreate = false;
  saving = false;
  formError = '';
  skillsText = '';
  form: Partial<CustomerRequirementRequest> = { title:'', description:'', technology:'', requiredExperienceYears:0, estimatedHours:1, preferredStartDate:'', priority:'MEDIUM', budget:0, currencyCode:'USD' };

  get displayName(): string { return this.customer?.contactName ?? ''; }
  get acceptedConsultations(): number { return this.consultations.filter(r => r.status === 'ACCEPTED').length; }
  get pendingConsultations(): number { return this.consultations.filter(r => r.status === 'PENDING').length; }
  get rejectedConsultations(): number { return this.consultations.filter(r => r.status === 'REJECTED').length; }
  get recentConsultations(): ConsultationRequest[] { return this.consultations.slice(0, 3); }
  get readyEngagements(): number { return this.engagements.filter(e => e.status === 'READY').length; }
  get activeEngagements(): number { return this.engagements.filter(e => e.status === 'ACTIVE').length; }
  get completedEngagements(): number { return this.engagements.filter(e => e.status === 'COMPLETED').length; }

  constructor() {
    const user = this.auth.getCurrentUser();
    if (!user) { this.router.navigateByUrl('/login'); return; }
    this.loadCustomer(user.userId);
    this.loadConsultations(user.userId);
  }

  private loadCustomer(userId:number) {
    this.customerService.getCustomer(userId).subscribe({
      next:c=>{ this.customer=c; this.loading=false; this.loadRequirements(); this.loadEngagements(); },
      error:e=>{ this.loading=false; this.errorMessage=e?.error?.error||'Unable to load your customer profile.'; }
    });
  }

  private loadRequirements() {
    if(!this.customer)return;
    this.requirementsLoading=true;
    this.customerService.getRequirements(this.customer.customerId).subscribe({
      next:p=>{this.requirements=p.content??[];this.requirementsLoading=false;},
      error:()=>{this.requirements=[];this.requirementsLoading=false;}
    });
  }

  private loadConsultations(userId:number) {
    this.consultationsLoading=true;
    this.consultationError='';
    this.consultationService.listMine(userId).subscribe({
      next:p=>{this.consultations=p.content??[];this.consultationsLoading=false;},
      error:e=>{this.consultations=[];this.consultationsLoading=false;this.consultationError=e?.error?.message||e?.error?.error||'Unable to load consultation requests.';}
    });
  }

  loadEngagements() {
    if(!this.customer)return;
    this.engagementsLoading=true;
    this.engagementError='';
    this.customerService.getEngagements(this.customer.customerId).subscribe({
      next:p=>{this.engagements=p.content??[];this.engagementsLoading=false;},
      error:e=>{this.engagements=[];this.engagementsLoading=false;this.engagementError=e?.error?.message||e?.error?.error||'Unable to load engagements.';}
    });
  }

  openCreate(){this.successMessage='';this.formError='';this.skillsText='';this.form={title:'',description:'',technology:'',requiredExperienceYears:0,estimatedHours:1,preferredStartDate:'',priority:'MEDIUM',budget:0,currencyCode:'USD'};this.showCreate=true;}
  closeCreate(){if(!this.saving)this.showCreate=false;}

  createRequirement(){
    if(!this.customer||this.saving)return;
    this.formError='';
    if(!this.form.title?.trim()||!this.form.description?.trim()){this.formError='Title and description are required.';return;}
    const request:CustomerRequirementRequest={customerId:this.customer.customerId,companyName:this.customer.companyName,contactName:this.customer.contactName,email:this.customer.email,phone:this.customer.phone,country:this.customer.country,city:this.customer.city,title:this.form.title.trim(),description:this.form.description.trim(),technology:this.form.technology?.trim(),requiredExperienceYears:Number(this.form.requiredExperienceYears||0),estimatedHours:Number(this.form.estimatedHours||1),preferredStartDate:this.form.preferredStartDate||undefined,priority:this.form.priority||'MEDIUM',budget:Number(this.form.budget||0),currencyCode:this.form.currencyCode||'USD',skills:this.skillsText.split(',').map(s=>s.trim()).filter(Boolean).map((skillName,i)=>({skillName,priorityOrder:i+1,mandatory:true}))};
    this.saving=true;
    this.customerService.createRequirement(request).subscribe({
      next:()=>{this.saving=false;this.showCreate=false;this.successMessage='Requirement created successfully.';this.loadRequirements();},
      error:e=>{this.saving=false;this.formError=e?.error?.message||e?.error?.error||'Unable to create requirement.';}
    });
  }

  canDelete(r:CustomerRequirement){return r.status==='DRAFT'||r.status==='SUBMITTED';}
  deleteRequirement(r:CustomerRequirement){if(!this.canDelete(r)||!confirm(`Delete requirement \"${r.title}\"?`))return;this.customerService.deleteRequirement(r.id).subscribe({next:()=>{this.successMessage='Requirement deleted successfully.';this.loadRequirements();},error:e=>{this.errorMessage=e?.error?.message||e?.error?.error||'Unable to delete requirement.';}});}
  logout(){this.auth.logout();this.router.navigateByUrl('/login');}
}
