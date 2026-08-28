import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Settlement {
  id:number; engagementId:number; expertId:number; expertName:string; customerId:number; companyName:string;
  requirementTitle:string; approvedHours:number; hourlyRate:number; grossAmount:number; currencyCode:string;
  status:'REQUESTED'|'APPROVED_FOR_PAYOUT'|'PAID'|'REJECTED'|string; requestedAt:string; approvedAt:string|null;
  paidAt:string|null; rejectedAt:string|null; adminComment:string|null; paymentReference:string|null;
}
export interface SettlementPage { content:Settlement[]; totalElements:number; totalPages:number; number:number; size:number; }

@Injectable({providedIn:'root'})
export class SettlementService {
  private readonly http=inject(HttpClient);
  private readonly url='http://localhost:8080/api/v1/settlements';
  byExpert(expertId:number):Observable<SettlementPage>{return this.http.get<SettlementPage>(`${this.url}/experts/${expertId}?page=0&size=50`);}
  all():Observable<SettlementPage>{return this.http.get<SettlementPage>(`${this.url}?page=0&size=100`);}
  request(engagementId:number):Observable<Settlement>{return this.http.post<Settlement>(`${this.url}/engagements/${engagementId}/request`,{});}
  approve(id:number,comment=''):Observable<Settlement>{return this.http.post<Settlement>(`${this.url}/${id}/approve?comment=${encodeURIComponent(comment)}`,{});}
  reject(id:number,reason:string):Observable<Settlement>{return this.http.post<Settlement>(`${this.url}/${id}/reject?reason=${encodeURIComponent(reason)}`,{});}
  markPaid(id:number,paymentReference:string):Observable<Settlement>{return this.http.post<Settlement>(`${this.url}/${id}/paid?paymentReference=${encodeURIComponent(paymentReference)}`,{});}
}
