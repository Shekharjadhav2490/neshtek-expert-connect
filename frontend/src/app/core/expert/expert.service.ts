import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface ExpertProfile {
  expertId: number;
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber: string;
  status: string;
  skillCount: number;
  expertiseWordCount: number;
  createdAt: string;
}

export interface ConsultationRequest {
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

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class ExpertService {
  private readonly http = inject(HttpClient);
  private readonly expertsUrl = 'http://localhost:8080/api/v1/experts';
  private readonly consultationUrl = 'http://localhost:8080/api/v1/consultation-requests';

  findByEmail(email: string): Observable<ExpertProfile> {
    return this.http.get<PageResponse<ExpertProfile>>(`${this.expertsUrl}?page=0&size=100`).pipe(
      map(page => {
        const expert = page.content.find(item => item.email?.toLowerCase() === email.toLowerCase());
        if (!expert) {
          throw new Error('Expert profile is not available for this account.');
        }
        return expert;
      })
    );
  }

  getConsultationRequests(expertId: number): Observable<PageResponse<ConsultationRequest>> {
    return this.http.get<PageResponse<ConsultationRequest>>(`${this.consultationUrl}/experts/${expertId}?page=0&size=20`);
  }

  acceptConsultation(id: number): Observable<ConsultationRequest> {
    return this.http.post<ConsultationRequest>(`${this.consultationUrl}/${id}/accept`, {});
  }

  rejectConsultation(id: number, reason: string): Observable<ConsultationRequest> {
    return this.http.post<ConsultationRequest>(`${this.consultationUrl}/${id}/reject`, { reason });
  }
}
