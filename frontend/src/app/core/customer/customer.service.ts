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

export interface CustomerRequirement {
  id: number;
  customerId: number;
  companyName: string;
  contactName: string;
  email: string;
  phone: string;
  country: string;
  city: string;
  title: string;
  description: string;
  technology: string;
  requiredExperienceYears: number;
  estimatedHours: number;
  preferredStartDate: string;
  priority: string;
  budget: number;
  currencyCode: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  skills: { id: number; skillName: string; priorityOrder: number; mandatory: boolean }[];
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
  private readonly requirementsUrl = 'http://localhost:8080/api/v1/requirements';

  getCustomer(customerId: number): Observable<CustomerProfile> {
    return this.http.get<CustomerProfile>(`${this.apiUrl}/${customerId}`);
  }

  getRequirements(customerId: number): Observable<PageResponse<CustomerRequirement>> {
    return this.http.get<PageResponse<CustomerRequirement>>(`${this.apiUrl}/${customerId}/requirements?page=0&size=20`);
  }

  getRequirement(requirementId: number): Observable<CustomerRequirement> {
    return this.http.get<CustomerRequirement>(`${this.requirementsUrl}/${requirementId}`);
  }
}
