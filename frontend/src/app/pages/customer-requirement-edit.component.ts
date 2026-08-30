import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CustomerRequirement, CustomerRequirementRequest, CustomerService } from '../core/customer/customer.service';

@Component({
  selector: 'app-customer-requirement-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <main class="page">
      <header class="topbar">
        <div><div class="brand">NeshTek Expert Connect</div><div class="subtitle">Requirement management</div></div>
        <a routerLink="/customer/dashboard" class="back">Customer dashboard</a>
      </header>

      <section class="content">
        <div class="eyebrow">Requirement</div>
        <h1>Edit requirement</h1>
        <p class="intro">Update your requirement before expert matching or engagement activity begins.</p>

        @if (loading) { <section class="state">Loading requirement…</section> }
        @else if (loadError) { <section class="state error">{{ loadError }}</section> }
        @else if (requirement) {
          <section class="card">
            <div class="status-row"><span>Status</span><strong>{{ requirement.status }}</strong></div>
            @if (!canEdit) {
              <div class="notice warning">This requirement has already entered the matching or engagement process and can no longer be edited.</div>
            }

            <form (ngSubmit)="save()">
              <div class="form-grid">
                <label>Title *<input name="title" [(ngModel)]="form.title" required maxlength="250" [disabled]="!canEdit"></label>
                <label>Technology<input name="technology" [(ngModel)]="form.technology" [disabled]="!canEdit"></label>
                <label class="wide">Description *<textarea name="description" [(ngModel)]="form.description" required rows="5" [disabled]="!canEdit"></textarea></label>
                <label>Experience (years)<input type="number" name="experience" min="0" step="0.5" [(ngModel)]="form.requiredExperienceYears" [disabled]="!canEdit"></label>
                <label>Estimated hours<input type="number" name="hours" min="0.1" step="0.5" [(ngModel)]="form.estimatedHours" [disabled]="!canEdit"></label>
                <label>Preferred start date<input type="date" name="startDate" [(ngModel)]="form.preferredStartDate" [disabled]="!canEdit"></label>
                <label>Priority<select name="priority" [(ngModel)]="form.priority" [disabled]="!canEdit"><option>LOW</option><option>MEDIUM</option><option>HIGH</option><option>URGENT</option></select></label>
                <label>Budget<input type="number" name="budget" min="0" step="0.01" [(ngModel)]="form.budget" [disabled]="!canEdit"></label>
                <label>Currency<select name="currency" [(ngModel)]="form.currencyCode" [disabled]="!canEdit"><option>USD</option><option>INR</option><option>EUR</option><option>GBP</option><option>AED</option></select></label>
                <label class="wide">Skills<input name="skills" [(ngModel)]="skillsText" [disabled]="!canEdit" placeholder="Oracle WebCenter Content, Oracle Database, Java"></label>
              </div>

              @if (errorMessage) { <div class="notice error-box">{{ errorMessage }}</div> }
              @if (successMessage) { <div class="notice success">{{ successMessage }}</div> }

              <div class="actions">
                <a class="secondary" routerLink="/customer/dashboard">Back</a>
                @if (canEdit) { <button type="submit" class="primary" [disabled]="saving">{{ saving ? 'Saving…' : 'Save changes' }}</button> }
              </div>
            </form>
          </section>
        }
      </section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif;padding:0 32px 56px;box-sizing:border-box}.topbar{max-width:1000px;margin:0 auto;padding:22px 0;border-bottom:1px solid #e3e8f0;display:flex;justify-content:space-between;align-items:center}.brand{font-weight:850}.subtitle{margin-top:4px;color:#667085;font-size:14px}.back{color:#315ea8;text-decoration:none;font-weight:750;border:1px solid #d5dbe6;background:#fff;padding:10px 14px;border-radius:9px}.content{max-width:1000px;margin:0 auto;padding-top:48px}.eyebrow{text-transform:uppercase;letter-spacing:.1em;font-size:11px;font-weight:850;color:#315ea8}.content h1{font-size:40px;margin:9px 0}.intro{color:#667085;line-height:1.6}.card{margin-top:28px;background:#fff;border:1px solid #e1e6ef;border-radius:16px;padding:26px;box-shadow:0 12px 32px rgba(23,32,51,.05)}.status-row{display:flex;justify-content:space-between;align-items:center;padding-bottom:18px;margin-bottom:18px;border-bottom:1px solid #edf0f5;color:#667085;font-size:13px}.status-row strong{color:#315ea8}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:18px}.form-grid label{display:flex;flex-direction:column;gap:7px;font-size:12px;font-weight:800;color:#475467}.form-grid .wide{grid-column:1/-1}.form-grid input,.form-grid textarea,.form-grid select{font:inherit;border:1px solid #d5dbe6;border-radius:9px;padding:10px 11px;color:#172033;background:#fff;box-sizing:border-box}.form-grid input:disabled,.form-grid textarea:disabled,.form-grid select:disabled{background:#f5f7fa;color:#667085}.form-grid input:focus,.form-grid textarea:focus,.form-grid select:focus{outline:2px solid #dce7f8;border-color:#315ea8}.notice{padding:12px 14px;border-radius:9px;margin-top:18px;font-size:13px;line-height:1.5}.warning{background:#fff8e8;color:#8a5a00;border:1px solid #f2d08a}.error-box{background:#fff5f4;color:#b42318}.success{background:#ecfdf3;color:#067647}.actions{display:flex;justify-content:flex-end;gap:10px;margin-top:24px}.primary,.secondary{display:inline-block;border-radius:9px;padding:11px 17px;font-weight:800;text-decoration:none;cursor:pointer}.primary{border:0;background:#172033;color:#fff}.primary:disabled{opacity:.55;cursor:not-allowed}.secondary{border:1px solid #d5dbe6;background:#fff;color:#172033}.state{padding:22px;margin-top:24px;background:#fff;border:1px solid #e1e6ef;border-radius:16px}.state.error{color:#b42318;background:#fff5f4}@media(max-width:700px){.page{padding:0 18px 40px}.form-grid{grid-template-columns:1fr}.form-grid .wide{grid-column:auto}.topbar{align-items:flex-start;gap:15px;flex-direction:column}}
  `]
})
export class CustomerRequirementEditComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly service = inject(CustomerService);

  requirement: CustomerRequirement | null = null;
  loading = true;
  saving = false;
  loadError = '';
  errorMessage = '';
  successMessage = '';
  skillsText = '';

  form = {
    title: '', description: '', technology: '', requiredExperienceYears: null as number | null,
    estimatedHours: null as number | null, preferredStartDate: '', priority: 'MEDIUM',
    budget: null as number | null, currencyCode: 'USD'
  };

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('requirementId'));
    if (!Number.isFinite(id) || id <= 0) {
      this.loading = false;
      this.loadError = 'A valid requirement ID is required.';
      return;
    }
    this.service.getRequirement(id).subscribe({
      next: requirement => {
        this.requirement = requirement;
        this.form = {
          title: requirement.title || '', description: requirement.description || '', technology: requirement.technology || '',
          requiredExperienceYears: requirement.requiredExperienceYears ?? null, estimatedHours: requirement.estimatedHours ?? null,
          preferredStartDate: requirement.preferredStartDate || '', priority: requirement.priority || 'MEDIUM',
          budget: requirement.budget ?? null, currencyCode: requirement.currencyCode || 'USD'
        };
        this.skillsText = (requirement.skills || []).map(s => s.skillName).join(', ');
        this.loading = false;
      },
      error: error => {
        this.loading = false;
        this.loadError = error?.error?.error || 'Unable to load the requirement.';
      }
    });
  }

  get canEdit(): boolean {
    return !!this.requirement && (this.requirement.status === 'DRAFT' || this.requirement.status === 'SUBMITTED');
  }

  save(): void {
    if (!this.requirement || !this.canEdit) return;
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';
    const skills = this.skillsText.split(',').map(v => v.trim()).filter(Boolean).map((skillName, index) => ({
      skillName, priorityOrder: index + 1, mandatory: true
    }));
    const request: CustomerRequirementRequest = {
      customerId: this.requirement.customerId,
      companyName: this.requirement.companyName,
      contactName: this.requirement.contactName,
      email: this.requirement.email,
      phone: this.requirement.phone || undefined,
      country: this.requirement.country || undefined,
      city: this.requirement.city || undefined,
      title: this.form.title,
      description: this.form.description,
      technology: this.form.technology || undefined,
      requiredExperienceYears: this.form.requiredExperienceYears ?? undefined,
      estimatedHours: this.form.estimatedHours ?? undefined,
      preferredStartDate: this.form.preferredStartDate || undefined,
      priority: this.form.priority,
      budget: this.form.budget ?? undefined,
      currencyCode: this.form.currencyCode,
      skills
    };
    this.service.updateRequirement(this.requirement.id, request).subscribe({
      next: updated => {
        this.requirement = updated;
        this.successMessage = 'Requirement updated successfully.';
        this.saving = false;
      },
      error: error => {
        this.saving = false;
        this.errorMessage = error?.error?.error || 'Unable to update the requirement.';
      }
    });
  }
}
