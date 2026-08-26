import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ConsultationRequestPayload {
  customerId: number;
  requirementId: number;
  expertId: number;
  message?: string;
  requestedStartDate?: string;
  estimatedHours?: number;
  proposedRate?: number;
  currencyCode?: string;
}

export interface ConsultationRequest {
  id: number;
  customerId: number;
  requirementId: number;
  expertId: number;
  expertName?: string;
  requirementTitle?: string;
  message?: string;
  requestedStartDate?: string;
  estimatedHours?: number;
  proposedRate?: number;
  currencyCode?: string;
  status: string;
  rejectionReason?: string | null;
  respondedAt?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface ConsultationRequestPage {
  content: ConsultationRequest[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class ConsultationRequestService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/v1/consultation-requests';

  create(request: ConsultationRequestPayload): Observable<ConsultationRequest> {
    return this.http.post<ConsultationRequest>(this.apiUrl, request);
  }

  listMine(customerId: number, page = 0, size = 20): Observable<ConsultationRequestPage> {
    return this.http.get<ConsultationRequestPage>(`${this.apiUrl}/customers/${customerId}?page=${page}&size=${size}`);
  }
}
