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

export interface ConsultationRequestResponse {
  id: number;
  customerId: number;
  requirementId: number;
  expertId: number;
  status: string;
  message?: string;
  requestedStartDate?: string;
  estimatedHours?: number;
  proposedRate?: number;
  currencyCode?: string;
  createdAt?: string;
  updatedAt?: string;
}

@Injectable({ providedIn: 'root' })
export class ConsultationRequestService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/v1/consultation-requests';

  create(request: ConsultationRequestPayload): Observable<ConsultationRequestResponse> {
    return this.http.post<ConsultationRequestResponse>(this.apiUrl, request);
  }
}
