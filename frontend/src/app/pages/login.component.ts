import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <main class="page">
      <section class="card">
        <a routerLink="/" class="brand">NeshTek Expert Connect</a>
        <div class="eyebrow">Secure sign in</div>
        <h1>Welcome back</h1>
        <p class="intro">Sign in to manage your requirements, expert matches and consultations.</p>
        <form [formGroup]="form" (ngSubmit)="submit()" novalidate>
          <label for="email">Email</label>
          <input id="email" type="email" formControlName="email" autocomplete="email" placeholder="you@example.com" />
          @if (form.controls.email.touched && form.controls.email.invalid) { <small class="field-error">Enter a valid email address.</small> }
          <label for="password">Password</label>
          <input id="password" type="password" formControlName="password" autocomplete="current-password" placeholder="Enter your password" />
          @if (form.controls.password.touched && form.controls.password.invalid) { <small class="field-error">Password is required.</small> }
          @if (errorMessage) { <div class="error" role="alert">{{ errorMessage }}</div> }
          <button type="submit" [disabled]="form.invalid || loading">{{ loading ? 'Signing in…' : 'Sign in' }}</button>
        </form>
        <p class="back"><a routerLink="/">← Back to home</a></p>
      </section>
    </main>
  `,
  styles: [`
    .page{min-height:100vh;display:grid;place-items:center;padding:24px;box-sizing:border-box;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif}.card{width:min(440px,100%);background:#fff;border:1px solid #e2e7f0;border-radius:18px;padding:36px;box-shadow:0 18px 50px rgba(23,32,51,.08);box-sizing:border-box}.brand{display:inline-block;text-decoration:none;color:#315ea8;font-weight:800;font-size:14px;margin-bottom:42px}.eyebrow{text-transform:uppercase;letter-spacing:.12em;font-size:12px;font-weight:800;color:#315ea8}h1{font-size:36px;margin:10px 0}.intro{color:#667085;line-height:1.55;margin:0 0 28px}form{display:flex;flex-direction:column;gap:8px}label{font-weight:700;font-size:14px;margin-top:8px}input{width:100%;box-sizing:border-box;border:1px solid #d6dce7;border-radius:10px;padding:13px 14px;font:inherit;outline:none}input:focus{border-color:#315ea8;box-shadow:0 0 0 3px rgba(49,94,168,.1)}button{margin-top:14px;border:0;border-radius:10px;padding:14px 18px;background:#172033;color:#fff;font-weight:800;font-size:15px;cursor:pointer}button:disabled{opacity:.55;cursor:not-allowed}.field-error{color:#b42318}.error{margin-top:12px;padding:12px;border-radius:9px;background:#fff1f0;color:#b42318;font-size:14px;line-height:1.4}.back{text-align:center;margin:24px 0 0}.back a{color:#315ea8;text-decoration:none;font-weight:700}
  `]
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly form = this.fb.nonNullable.group({ email: ['', [Validators.required, Validators.email]], password: ['', Validators.required] });
  loading = false;
  errorMessage = '';

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.errorMessage = '';
    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.loading = false;
        const user = this.auth.getCurrentUser();
        switch (user?.role) {
          case 'CUSTOMER':
          case 'ROLE_CUSTOMER':
            this.router.navigateByUrl('/customer/dashboard');
            break;
          case 'EXPERT':
          case 'ROLE_EXPERT':
            this.router.navigateByUrl('/expert/dashboard');
            break;
          default:
            this.router.navigateByUrl('/');
        }
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error?.error?.error || 'Unable to sign in. Please check your email and password.';
      }
    });
  }
}
