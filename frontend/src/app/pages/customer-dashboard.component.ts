import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService, CustomerProfile, CustomerRequirement, CustomerRequirementRequest } from '../core/customer/customer.service';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <main class="dashboard">
      <header class="topbar"><div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Customer dashboard</div></div><button class="logout" type="button" (click)="logout()">Sign out</button></header>
      <section class="welcome"><div><div class="eyebrow">Customer workspace</div><h1>Welcome back{{ displayName ? ', ' + displayName : '' }}.</h1><p>Manage your company profile and continue finding the right experts for your requirements.</p></div></section>
      @if (loading) { <section class="state-card">Loading your customer profile…</section> }
      @else if (errorMessage) { <section class="state-card error">{{ errorMessage }}</section> }
      @else if (customer) {
        <section class="grid">
          <article class="card profile"><div class="card-title">Company profile</div><div class="profile-name">{{ customer.companyName }}</div><div class="muted">{{ customer.contactName }} · {{ customer.email }}</div><dl><div><dt>Location</dt><dd>{{ customer.city }}, {{ customer.country }}</dd></div><div><dt>Industry</dt><dd>{{ customer.industry || 'Not specified' }}</dd></div><div><dt>Company size</dt><dd>{{ customer.companySize || 'Not specified' }}</dd></div><div><dt>Status</dt><dd>{{ customer.status }}</dd></div></dl></article>
          <article class="card stat"><div class="card-title">Requirements</div><div class="number">{{ requirements.length }}</div><div class="muted">Requirements currently available to your account.</div></article>
          <article class="card next"><div class="card-title">Next step</div><h2>Find expert matches</h2><p>Select a requirement below to compare the best available experts.</p>@if (requirements[0]) { <a class="primary-link" [routerLink]="['/customer/requirements', requirements[0].id, 'matches']">Open latest requirement</a> }</article>
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
        <div class="overlay"><section class="modal"><div class="modal-head"><div><div class="eyebrow">New requirement</div><h2>Create a requirement</h2><p>Tell us what expertise you need and we'll match verified experts.</p></div><button class="icon" (click)="closeCreate()">×</button></div>
          <form (ngSubmit)="createRequirement()">
            <div class="form-grid"><label>Title *<input name="title" [(ngModel)]="form.title" required maxlength="250" placeholder="e.g. Oracle WebCenter Content Expert"></label><label>Technology<input name="technology" [(ngModel)]="form.technology" placeholder="e.g. Oracle WebCenter Content"></label><label class="wide">Description *<textarea name="description" [(ngModel)]="form.description" required rows="4" placeholder="Describe the implementation, support or project requirement"></textarea></label><label>Experience (years)<input type="number" name="experience" min="0" step="0.5" [(ngModel)]="form.requiredExperienceYears"></label><label>Estimated hours<input type="number" name="hours" min="0.1" step="0.5" [(ngModel)]="form.estimatedHours"></label><label>Preferred start date<input type="date" name="startDate" [(ngModel)]="form.preferredStartDate"></label><label>Priority<select name="priority" [(ngModel)]="form.priority"><option>LOW</option><option selected>MEDIUM</option><option>HIGH</option><option>URGENT</option></select></label><label>Budget<input type="number" name="budget" min="0" step="0.01" [(ngModel)]="form.budget"></label><label>Currency<select name="currency" [(ngModel)]="form.currencyCode"><option>USD</option><option>INR</option><option>EUR</option><option>GBP</option><option>AED</option></select></label><label class="wide">Skills <input name="skills" [(ngModel)]="skillsText" placeholder="Oracle WebCenter Content, Oracle Database, Java"></label></div>
            @if (formError) { <div class="notice error-box">{{ formError }}</div> }
            <div class="modal-actions"><button type="button" class="secondary" (click)="closeCreate()">Cancel</button><button type="submit" class="primary" [disabled]="saving">{{ saving ? 'Creating…' : 'Create requirement' }}</button></div>
          </form>
        </section></div>
      }
    </main>
  `,
  styles: [`
    .dashboard{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 48px;box-sizing:border-box}.topbar{max-width:1180px;margin:0 auto;padding:22px 0;display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #e3e8f0}.brand{font-weight:850}.subtitle,.muted{color:#667085;font-size:14px}.subtitle{margin-top:4px}.logout,.secondary,.icon{border:1px solid #d5dbe6;background:#fff;color:#172033;border-radius:9px;padding:10px 16px;font-weight:750;cursor:pointer}.welcome{max-width:1180px;margin:0 auto;padding:56px 0 28px}.eyebrow{text-transform:uppercase;letter-spacing:.12em;font-size:11px;font-weight:850;color:#315ea8}.welcome h1{font-size:42px;line-height:1.1;margin:9px 0 12px}.welcome p{max-width:720px;color:#667085;line-height:1.6;margin:0}.grid{max-width:1180px;margin:0 auto;display:grid;grid-template-columns:2fr 1fr 1fr;gap:18px}.card,.state-card{background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:24px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.card-title{font-size:13px;font-weight:800;color:#667085;text-transform:uppercase;letter-spacing:.07em}.profile-name{font-size:24px;font-weight:850;margin:18px 0 5px}.profile dl{margin:24px 0 0;display:grid;grid-template-columns:1fr 1fr;gap:16px}.profile dt{font-size:12px;color:#98a2b3;margin-bottom:4px}.profile dd{margin:0;font-weight:700}.number{font-size:46px;font-weight:850;margin:25px 0 5px}.next h2{margin:18px 0 8px;font-size:22px}.next p{color:#667085;line-height:1.55}.primary-link,.match-link{display:inline-block;margin-top:12px;color:#315ea8;font-weight:800;text-decoration:none}.state-card{max-width:1180px;margin:0 auto}.error{color:#b42318;background:#fff5f4}.stat,.next{min-height:180px}.requirements-section{max-width:1180px;margin:42px auto 0}.section-head{display:flex;justify-content:space-between;align-items:end;gap:20px}.section-head h2{margin:8px 0 18px}.primary{border:0;background:#17233a;color:white;border-radius:9px;padding:11px 15px;font-weight:800;cursor:pointer}.primary:disabled{opacity:.55;cursor:not-allowed}.requirements-list{display:grid;gap:12px}.requirement-card{background:#fff;border:1px solid #e1e6ef;border-radius:14px;padding:20px;display:flex;align-items:center;justify-content:space-between;gap:20px}.requirement-title{font-size:18px;font-weight:850;margin-bottom:5px}.skill-list{display:flex;flex-wrap:wrap;gap:7px;margin-top:12px}.skill-list span{background:#eef2f7;border-radius:999px;padding:5px 9px;font-size:11px;font-weight:750}.row-actions{display:flex;align-items:center;gap:14px}.match-link{white-space:nowrap}.delete{border:0;background:none;color:#b42318;font-weight:800;cursor:pointer}.notice{padding:12px 14px;border-radius:9px;margin:0 0 14px}.success{background:#ecfdf3;color:#176b3a;border:1px solid #b7ebca}.empty{text-align:center}.overlay{position:fixed;inset:0;background:rgba(15,23,42,.42);display:flex;align-items:center;justify-content:center;padding:20px;z-index:20}.modal{width:min(780px,100%);max-height:90vh;overflow:auto;background:white;border-radius:16px;padding:24px;box-shadow:0 24px 70px rgba(0,0,0,.25)}.modal-head{display:flex;justify-content:space-between;gap:20px}.modal-head h2{font-size:28px;margin:8px 0}.modal-head p{color:#667085;margin:0 0 20px}.icon{font-size:24px;width:42px;height:42px;padding:0}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:14px}.form-grid label{font-size:12px;font-weight:800;color:#475467}.form-grid .wide{grid-column:1/-1}.form-grid input,.form-grid textarea,.form-grid select{display:block;width:100%;box-sizing:border-box;margin-top:6px;border:1px solid #d5dbe6;border-radius:8px;padding:10px 11px;font:inherit;color:#172033;background:#fff}.form-grid textarea{resize:vertical}.modal-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:20px}.error-box{background:#fff5f4;color:#b42318;border:1px solid #f3b7b2}@media(max-width:900px){.grid{grid-template-columns:1fr}.welcome h1{font-size:34px}.dashboard{padding:0 18px 36px}.requirement-card{align-items:flex-start;flex-direction:column}.row-actions{width:100%;justify-content:space-between}.match-link{white-space:normal}.form-grid{grid-template-columns:1fr}.form-grid .wide{grid-column:auto}.section-head{align-items:start;flex-direction:column}}
  `]
})
export class CustomerDashboardComponent {
  private readonly auth = inject(AuthService);
  private readonly customerService = inject(CustomerService);
  private readonly router = inject(Router);
  customer: CustomerProfile | null = null;
  requirements: CustomerRequirement[] = [];
  loading = true; requirementsLoading = true; errorMessage = ''; successMessage = ''; showCreate = false; saving = false; formError = ''; skillsText = '';
  form: Partial<CustomerRequirementRequest> = { title:'', description:'', technology:'', requiredExperienceYears:0, estimatedHours:1, preferredStartDate:'', priority:'MEDIUM', budget:0, currencyCode:'USD' };
  get displayName(): string { return this.customer?.contactName ?? ''; }
  constructor() { const user=this.auth.getCurrentUser(); if(!user){this.router.navigateByUrl('/login');return;} this.loadCustomer(user.userId); }
  private loadCustomer(userId:number){this.customerService.getCustomer(userId).subscribe({next:c=>{this.customer=c;this.loading=false;this.loadRequirements();},error:e=>{this.loading=false;this.errorMessage=e?.error?.error||'Unable to load your customer profile.';}});}
  private loadRequirements(){if(!this.customer)return;this.requirementsLoading=true;this.customerService.getRequirements(this.customer.customerId).subscribe({next:p=>{this.requirements=p.content??[];this.requirementsLoading=false;},error:()=>{this.requirements=[];this.requirementsLoading=false;}});}
  openCreate(){this.successMessage='';this.formError='';this.skillsText='';this.form={title:'',description:'',technology:'',requiredExperienceYears:0,estimatedHours:1,preferredStartDate:'',priority:'MEDIUM',budget:0,currencyCode:'USD'};this.showCreate=true;}
  closeCreate(){if(!this.saving)this.showCreate=false;}
  createRequirement(){if(!this.customer||this.saving)return;this.formError='';if(!this.form.title?.trim()||!this.form.description?.trim()){this.formError='Title and description are required.';return;}const request:CustomerRequirementRequest={customerId:this.customer.customerId,companyName:this.customer.companyName,contactName:this.customer.contactName,email:this.customer.email,phone:this.customer.phone,country:this.customer.country,city:this.customer.city,title:this.form.title.trim(),description:this.form.description.trim(),technology:this.form.technology?.trim(),requiredExperienceYears:Number(this.form.requiredExperienceYears||0),estimatedHours:Number(this.form.estimatedHours||1),preferredStartDate:this.form.preferredStartDate||undefined,priority:this.form.priority||'MEDIUM',budget:Number(this.form.budget||0),currencyCode:this.form.currencyCode||'USD',skills:this.skillsText.split(',').map(s=>s.trim()).filter(Boolean).map((skillName,i)=>({skillName,priorityOrder:i+1,mandatory:true}))};this.saving=true;this.customerService.createRequirement(request).subscribe({next:()=>{this.saving=false;this.showCreate=false;this.successMessage='Requirement created successfully.';this.loadRequirements();},error:e=>{this.saving=false;this.formError=e?.error?.message||e?.error?.error||'Unable to create requirement.';}});}
  canDelete(r:CustomerRequirement){return r.status==='DRAFT'||r.status==='SUBMITTED';}
  deleteRequirement(r:CustomerRequirement){if(!this.canDelete(r)||!confirm(`Delete requirement "${r.title}"?`))return;this.customerService.deleteRequirement(r.id).subscribe({next:()=>{this.successMessage='Requirement deleted successfully.';this.loadRequirements();},error:e=>{this.errorMessage=e?.error?.message||e?.error?.error||'Unable to delete requirement.';}});}
  logout(){this.auth.logout();this.router.navigateByUrl('/login');}
}
