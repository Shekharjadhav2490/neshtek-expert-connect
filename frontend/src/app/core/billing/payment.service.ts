import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PaymentRequest { paymentReference:string; amount:number; paymentMethod:string; paymentDate?:string; notes?:string; }
export interface Payment { id:number; invoiceId:number; invoiceNumber:string; paymentReference:string; paymentDate:string; amount:number; currencyCode:string; paymentMethod:string; status:string; notes:string|null; }
export interface PaymentPage { content:Payment[]; totalElements:number; totalPages:number; number:number; size:number; }

@Injectable({providedIn:'root'})
export class PaymentService {
 private readonly http=inject(HttpClient); private readonly base='http://localhost:8080/api/v1/payments';
 byInvoice(invoiceId:number,page=0,size=50):Observable<PaymentPage>{return this.http.get<PaymentPage>(`${this.base}/invoices/${invoiceId}?page=${page}&size=${size}`);}
 record(invoiceId:number,request:PaymentRequest):Observable<Payment>{return this.http.post<Payment>(`${this.base}/invoices/${invoiceId}`,request);}
 refund(paymentId:number):Observable<Payment>{return this.http.post<Payment>(`${this.base}/${paymentId}/refund`,{});}
}
