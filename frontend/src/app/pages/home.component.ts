import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  template: `
    <main class="shell">
      <section class="hero">
        <div class="eyebrow">NeshTek Expert Connect</div>
        <h1>Connect with the right expert for your requirement.</h1>
        <p>Find verified experts, compare matches, and manage consultation requests from one secure platform.</p>
        <div class="actions">
          <a routerLink="/login" class="button">Sign in</a>
          <button type="button" class="secondary">Create account</button>
        </div>
      </section>
      <section class="cards" aria-label="Platform capabilities">
        <article><strong>Verified experts</strong><span>Profiles, skills and verification status.</span></article>
        <article><strong>Smart matching</strong><span>Match requirements with relevant expertise.</span></article>
        <article><strong>Secure consultations</strong><span>Role-based and ownership-aware workflows.</span></article>
      </section>
    </main>
  `,
  styles: [`
    .shell{min-height:100vh;padding:48px;box-sizing:border-box;background:#f7f9fc;color:#172033;font-family:Inter,system-ui,sans-serif}
    .hero{max-width:900px;margin:0 auto;padding:72px 0 44px}.eyebrow{text-transform:uppercase;letter-spacing:.14em;font-size:13px;font-weight:700;color:#315ea8}h1{font-size:clamp(40px,6vw,68px);line-height:1.03;margin:16px 0 22px;max-width:820px}p{font-size:19px;line-height:1.6;max-width:720px;color:#536078}.actions{display:flex;gap:12px;margin-top:30px}.button,button{border:0;border-radius:10px;padding:13px 20px;font-weight:700;background:#172033;color:white;cursor:pointer;text-decoration:none;font-family:inherit;font-size:14px}.secondary{background:white;color:#172033;border:1px solid #d9dfeb}.cards{max-width:900px;margin:0 auto;display:grid;grid-template-columns:repeat(3,1fr);gap:16px}.cards article{background:white;border:1px solid #e2e7f0;border-radius:14px;padding:22px;display:flex;flex-direction:column;gap:8px;box-shadow:0 8px 25px rgba(23,32,51,.05)}.cards span{color:#667085;line-height:1.5}@media(max-width:700px){.shell{padding:24px}.hero{padding-top:42px}.cards{grid-template-columns:1fr}}
  `]
})
export class HomeComponent {}
