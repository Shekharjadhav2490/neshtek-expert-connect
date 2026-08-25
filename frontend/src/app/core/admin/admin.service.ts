import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdminPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface AdminCustomer {
  customerId: number;
  companyName: string;
  contactName: string;
  email: string;
  status: string;
}

export interface AdminExpert {
  expertId: number;
  firstName: string;
  lastName: string;
  email: string;
  status: string;
}

export interface AdminRequirement {
  id: number;
  customerId: number;
  title: string;
  status: string;
  priority: string;
  createdAt: string;
}

export interface AdminConsultation {
  id: number;
  customerId: number;
  requirementId: number;
  expertId: number;
  expertName: string;
  requirementTitle: string;
  message: string;
  requestedStartDate: string;
  estimatedHours: number;
  proposedRate: number;
  currencyCode: string;
  status: string;
  rejectionReason: string | null;
  respondedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/v1';

  getCustomers(page = 0, size = 20): Observable<AdminPage<AdminCustomer>> {
    return this.http.get<AdminPage<AdminCustomer>>(`${this.baseUrl}/customers?page=${page}&size=${size}`);
  }

  getExperts(page = 0, size = 20): Observable<AdminPage<AdminExpert>> {
    return this.http.get<AdminPage<AdminExpert>>(`${this.baseUrl}/experts?page=${page}&size=${size}`);
  }

  getRequirements(page = 0, size = 20): Observable<AdminPage<AdminRequirement>> {
    return this.http.get<AdminPage<AdminRequirement>>(`${this.baseUrl}/requirements?page=${page}&size=${size}`);
  }

  getConsultations(page = 0, size = 20): Observable<AdminPage<AdminConsultation>> {
    return this.http.get<AdminPage<AdminConsultation>>(`${this.baseUrl}/admin/consultation-requests?page=${page}&size=${size}`);
  }
}
