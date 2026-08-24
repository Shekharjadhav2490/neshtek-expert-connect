import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CustomerProfile {
  customerId: number;
  companyName: string;
  contactName: string;
  email: string;
  phone: string;
  country: string;
  city: string;
  timezone: string;
  industry: string;
  companySize: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/v1/customers';

  getCustomer(customerId: number): Observable<CustomerProfile> {
    return this.http.get<CustomerProfile>(`${this.apiUrl}/${customerId}`);
  }

  getRequirements(customerId: number): Observable<PageResponse<unknown>> {
    return this.http.get<PageResponse<unknown>>(`${this.apiUrl}/${customerId}/requirements?page=0&size=5`);
  }
}
