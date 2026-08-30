import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ExpertReplacement {
  id:number; engagementId:number; requirementId:number; requirementTitle:string;
  expertId:number; expertName:string; status:string; reasonCode:string; comments:string;
  requestedAt:string; workCutoffAt:string; reviewedAt:string|null; reviewerComment:string|null;
  approvedHours:number; eligibleAmount:number; paidAmount:number; balanceDue:number; refundOrCreditDue:number; currencyCode:string;
  newExpertId:number|null; newExpertName:string|null; newEngagementId:number|null;
}

export interface ExpertMatch {
  expertId:number; firstName:string; lastName:string; city:string; timezone:string;
  totalExperienceYears:number; hourlyRate:number; currencyCode:string; matchScore:number;
  matchedSkills:number; requiredSkills:number; matchedSkillNames:string[];
  mandatorySkillsMatched:number; mandatorySkillsRequired:number; optionalSkillsMatched:number; optionalSkillsRequired:number;
  missingMandatorySkills:string[]; missingOptionalSkills:string[]; mandatorySkillsSatisfied:boolean;
  experienceMatch:boolean; availabilityMatch:boolean; technologyMatch:boolean; matchLevel:string; recommendation:string;
}

@Injectable({providedIn:'root'})
export class ExpertReplacementService {
  private readonly http=inject(HttpClient); private readonly base='http://localhost:8080/api/v1/expert-replacements';
  pending():Observable<ExpertReplacement[]>{return this.http.get<ExpertReplacement[]>(`${this.base}/pending`);}
  byEngagement(id:number):Observable<ExpertReplacement[]>{return this.http.get<ExpertReplacement[]>(`${this.base}/engagements/${id}`);}
  request(id:number, reasonCode:string, comments:string):Observable<ExpertReplacement>{return this.http.post<ExpertReplacement>(`${this.base}/engagements/${id}`,{reasonCode,comments});}
  approve(id:number,comment=''):Observable<ExpertReplacement>{const params=comment?new HttpParams().set('comment',comment):undefined;return this.http.post<ExpertReplacement>(`${this.base}/${id}/approve`,{},params?{params}:{});}
  reject(id:number,reason:string):Observable<ExpertReplacement>{return this.http.post<ExpertReplacement>(`${this.base}/${id}/reject`,{}, {params:new HttpParams().set('reason',reason)});}
  cancel(id:number):Observable<ExpertReplacement>{return this.http.post<ExpertReplacement>(`${this.base}/${id}/cancel`,{});}
  matches(id:number,limit=10):Observable<ExpertMatch[]>{return this.http.get<ExpertMatch[]>(`${this.base}/${id}/matches`,{params:new HttpParams().set('limit',limit)});}
  assign(id:number,newExpertId:number):Observable<ExpertReplacement>{return this.http.post<ExpertReplacement>(`${this.base}/${id}/assign`,{}, {params:new HttpParams().set('newExpertId',newExpertId)});}
}
