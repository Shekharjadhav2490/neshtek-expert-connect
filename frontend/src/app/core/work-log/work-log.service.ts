import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WorkLog {
  id:number; engagementId:number; customerId:number; expertId:number; expertName:string;
  requirementId:number; requirementTitle:string; workDate:string; hours:number; description:string;
  status:'DRAFT'|'SUBMITTED'|'APPROVED'|'REJECTED'|string; submittedAt:string|null; reviewedAt:string|null;
  reviewerComment:string|null; createdAt:string; updatedAt:string;
}
export interface WorkLogPage { content:WorkLog[]; totalElements:number; totalPages:number; number:number; size:number; }
export interface EngagementUsage {
  engagementId:number; status:string; requirementTitle:string; estimatedHours:number|null; loggedHours:number;
  approvedHours:number; remainingHours:number|null; utilizationPercentage:number|null; proposedRate:number|null;
  currencyCode:string|null; estimatedAmount:number|null; approvedAmount:number|null; requestedStartDate:string|null;
}
export interface EngagementBillingSummary {
  engagementId:number; status:string; requirementTitle:string; contractHours:number|null; loggedHours:number;
  submittedHours:number; approvedHours:number; rejectedHours:number; pendingHours:number; remainingHours:number|null;
  utilizationPercentage:number|null; hourlyRate:number|null; currencyCode:string|null; contractValue:number|null;
  approvedBilling:number|null; pendingBilling:number|null; rejectedValue:number|null;
}

@Injectable({providedIn:'root'})
export class WorkLogService {
  private readonly http=inject(HttpClient); private readonly url='http://localhost:8080/api/v1/work-logs';
  private readonly billingUrl='http://localhost:8080/api/v1/billing';
  listForEngagement(id:number):Observable<WorkLogPage>{return this.http.get<WorkLogPage>(`${this.url}/engagements/${id}?page=0&size=50`);}
  getUsage(id:number):Observable<EngagementUsage>{return this.http.get<EngagementUsage>(`${this.url}/engagements/${id}/usage`);}
  getBillingSummary(id:number):Observable<EngagementBillingSummary>{return this.http.get<EngagementBillingSummary>(`${this.billingUrl}/engagements/${id}`);}
  create(id:number,payload:{workDate:string;hours:number;description:string}):Observable<WorkLog>{return this.http.post<WorkLog>(`${this.url}/engagements/${id}`,payload);}
  submit(id:number):Observable<WorkLog>{return this.http.post<WorkLog>(`${this.url}/${id}/submit`,{});}
  approve(id:number,comment=''):Observable<WorkLog>{return this.http.post<WorkLog>(`${this.url}/${id}/approve?comment=${encodeURIComponent(comment)}`,{});}
  reject(id:number,comment=''):Observable<WorkLog>{return this.http.post<WorkLog>(`${this.url}/${id}/reject?comment=${encodeURIComponent(comment)}`,{});}
}
