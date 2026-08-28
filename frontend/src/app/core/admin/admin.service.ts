import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdminPage<T> { content: T[]; totalElements: number; totalPages: number; number: number; size: number; }
export interface AdminCustomer { customerId: number; companyName: string; contactName: string; email: string; phone?: string; country?: string; city?: string; timezone?: string; industry?: string; companySize?: string; status: string; createdAt?: string; updatedAt?: string; }
export interface AdminExpert { expertId: number; firstName: string; lastName: string; email: string; mobileNumber?: string; status: string; skillCount: number; expertiseWordCount: number; createdAt: string; }
export interface AdminRequirement { id: number; customerId: number; title: string; status: string; priority: string; createdAt: string; }
export interface AdminConsultation { id: number; customerId: number; requirementId: number; expertId: number; expertName: string; requirementTitle: string; message: string; requestedStartDate: string; estimatedHours: number; proposedRate: number; currencyCode: string; status: string; rejectionReason: string | null; respondedAt: string | null; createdAt: string; updatedAt: string; }
export interface AdminEngagement { id: number; consultationRequestId: number; customerId: number; companyName: string; expertId: number; expertName: string; requirementId: number; requirementTitle: string; status: string; requestedStartDate: string | null; estimatedHours: number | null; agreedRate: number | null; currencyCode: string | null; startedAt: string | null; completedAt: string | null; cancelledAt: string | null; createdAt: string; updatedAt: string; }

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/v1';
  getCustomers(page = 0, size = 20, search = ''): Observable<AdminPage<AdminCustomer>> { const params = new URLSearchParams({page:String(page),size:String(size)}); if(search.trim()) params.set('search',search.trim()); return this.http.get<AdminPage<AdminCustomer>>(`${this.baseUrl}/admin/customers?${params.toString()}`); }
  getCustomer(id:number): Observable<AdminCustomer> { return this.http.get<AdminCustomer>(`${this.baseUrl}/admin/customers/${id}`); }
  getExperts(page = 0, size = 20, status?: string): Observable<AdminPage<AdminExpert>> { const params = new URLSearchParams({page:String(page),size:String(size)}); if(status) params.set('status',status); return this.http.get<AdminPage<AdminExpert>>(`${this.baseUrl}/admin/experts?${params.toString()}`); }
  getRequirements(page = 0, size = 20): Observable<AdminPage<AdminRequirement>> { return this.http.get<AdminPage<AdminRequirement>>(`${this.baseUrl}/requirements?page=${page}&size=${size}`); }
  getConsultations(page = 0, size = 20): Observable<AdminPage<AdminConsultation>> { return this.http.get<AdminPage<AdminConsultation>>(`${this.baseUrl}/admin/consultation-requests?page=${page}&size=${size}`); }
  getEngagements(page = 0, size = 20): Observable<AdminPage<AdminEngagement>> { return this.http.get<AdminPage<AdminEngagement>>(`${this.baseUrl}/admin/engagements?page=${page}&size=${size}`); }
  getExpert(id:number): Observable<AdminExpert> { return this.http.get<AdminExpert>(`${this.baseUrl}/admin/experts/${id}`); }
  startExpertReview(id:number): Observable<AdminExpert> { return this.http.post<AdminExpert>(`${this.baseUrl}/admin/experts/${id}/review`, {}); }
  startExpertVerification(id:number): Observable<AdminExpert> { return this.http.post<AdminExpert>(`${this.baseUrl}/admin/experts/${id}/verification`, {}); }
  verifyExpert(id:number, payload:any): Observable<AdminExpert> { return this.http.post<AdminExpert>(`${this.baseUrl}/admin/experts/${id}/verify`, payload); }
  activateExpert(id:number): Observable<AdminExpert> { return this.http.post<AdminExpert>(`${this.baseUrl}/admin/experts/${id}/activate`, {}); }
  rejectExpert(id:number, reason:string): Observable<AdminExpert> { return this.http.post<AdminExpert>(`${this.baseUrl}/admin/experts/${id}/reject`, {reason}); }
}
