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

export interface Engagement {
  id: number;
  consultationRequestId: number;
  customerId: number;
  expertId: number;
  expertName: string;
  requirementId: number;
  requirementTitle: string;
  status: 'READY' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED' | string;
  requestedStartDate: string | null;
  estimatedHours: number | null;
  agreedRate: number | null;
  currencyCode: string | null;
  startedAt: string | null;
  completedAt: string | null;
  cancelledAt: string | null;
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
  private readonly engagementUrl = 'http://localhost:8080/api/v1/engagements';

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

  getEngagements(expertId: number): Observable<PageResponse<Engagement>> {
    return this.http.get<PageResponse<Engagement>>(`${this.engagementUrl}/experts/${expertId}?page=0&size=20`);
  }

  startEngagement(id: number): Observable<Engagement> {
    return this.http.post<Engagement>(`${this.engagementUrl}/${id}/start`, {});
  }

  completeEngagement(id: number): Observable<Engagement> {
    return this.http.post<Engagement>(`${this.engagementUrl}/${id}/complete`, {});
  }

  cancelEngagement(id: number): Observable<Engagement> {
    return this.http.post<Engagement>(`${this.engagementUrl}/${id}/cancel`, {});
  }
}
