import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FinancialLedgerEntry { id:number; customerId:number; expertId:number|null; engagementId:number|null; invoiceId:number|null; paymentId:number|null; settlementId:number|null; replacementRequestId:number|null; parentEntryId:number|null; entryType:string; direction:string; amount:number; currencyCode:string; sourceType:string; sourceId:string; description:string; occurredAt:string; createdAt:string; createdBy:string|null; }
export interface FinancialLedgerPage { content:FinancialLedgerEntry[]; totalElements:number; totalPages:number; number:number; size:number; }
export interface FinancialReconciliation { customerId:number|null; expertId:number|null; customerDebits:number|null; customerCredits:number|null; customerNet:number|null; expertEarnings:number|null; expertPayouts:number|null; expertOutstanding:number|null; balanced:boolean; }

@Injectable({providedIn:'root'})
export class FinancialLedgerService {
 private readonly http=inject(HttpClient); private readonly base='http://localhost:8080/api/v1/financial-ledger';
 customer(id:number,page=0,size=50):Observable<FinancialLedgerPage>{return this.http.get<FinancialLedgerPage>(`${this.base}/customer/${id}?page=${page}&size=${size}`);}
 expert(id:number,page=0,size=50):Observable<FinancialLedgerPage>{return this.http.get<FinancialLedgerPage>(`${this.base}/expert/${id}?page=${page}&size=${size}`);}
 all(page=0,size=50):Observable<FinancialLedgerPage>{return this.http.get<FinancialLedgerPage>(`${this.base}?page=${page}&size=${size}`);}
 customerReconciliation(id:number):Observable<FinancialReconciliation>{return this.http.get<FinancialReconciliation>(`${this.base}/customer/${id}/reconciliation`);}
 expertReconciliation(id:number):Observable<FinancialReconciliation>{return this.http.get<FinancialReconciliation>(`${this.base}/expert/${id}/reconciliation`);}
}
