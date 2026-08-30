import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Invoice {
  id:number; invoiceNumber:string; customerId:number; companyName:string; engagementId:number; requirementTitle:string;
  expertId:number; expertName:string; invoiceDate:string; dueDate:string; currencyCode:string;
  subtotalAmount:number; taxAmount:number; totalAmount:number; paidAmount:number; balanceDue:number; status:string;
  notes:string|null; issuedAt:string|null; paidAt:string|null;
}
export interface InvoicePage {content:Invoice[];totalElements:number;totalPages:number;number:number;size:number;}

@Injectable({providedIn:'root'})
export class InvoiceService {
  private readonly http=inject(HttpClient); private readonly base='http://localhost:8080/api/v1/invoices';
  all(page=0,size=50):Observable<InvoicePage>{return this.http.get<InvoicePage>(`${this.base}?page=${page}&size=${size}`);}
  byCustomer(customerId:number,page=0,size=50):Observable<InvoicePage>{return this.http.get<InvoicePage>(`${this.base}/customers/${customerId}?page=${page}&size=${size}`);}
  get(id:number):Observable<Invoice>{return this.http.get<Invoice>(`${this.base}/${id}`);}
  generate(engagementId:number):Observable<Invoice>{return this.http.post<Invoice>(`${this.base}/engagements/${engagementId}/generate`,{});}
  issue(id:number):Observable<Invoice>{return this.http.post<Invoice>(`${this.base}/${id}/issue`,{});}
  cancel(id:number):Observable<Invoice>{return this.http.post<Invoice>(`${this.base}/${id}/cancel`,{});}
}
