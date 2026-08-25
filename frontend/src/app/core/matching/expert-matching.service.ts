import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ExpertMatch {
  expertId: number;
  firstName: string;
  lastName: string;
  city: string;
  timezone: string;
  totalExperienceYears: number;
  hourlyRate: number;
  currencyCode: string;
  matchScore: number;
  matchedSkills: number;
  requiredSkills: number;
  matchedSkillNames: string[];
  mandatorySkillsMatched: number;
  mandatorySkillsRequired: number;
  optionalSkillsMatched: number;
  optionalSkillsRequired: number;
  missingMandatorySkills: string[];
  missingOptionalSkills: string[];
  mandatorySkillsSatisfied: boolean;
  experienceMatch: boolean;
  availabilityMatch: boolean;
  technologyMatch: boolean;
  matchLevel: string;
  recommendation: string;
}

@Injectable({ providedIn: 'root' })
export class ExpertMatchingService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/v1/requirements';

  findMatches(requirementId: number, limit = 10): Observable<ExpertMatch[]> {
    return this.http.get<ExpertMatch[]>(`${this.apiUrl}/${requirementId}/matches?limit=${limit}`);
  }
}
