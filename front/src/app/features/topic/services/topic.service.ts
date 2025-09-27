import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Topic } from '../interfaces/topic.interface';
import { environment } from '../../../../environments/environment';
import { HttpHeadersService } from 'src/app/shared/services/http.headers.service';

@Injectable({
  providedIn: 'root',
})
export class TopicService {
  private baseUrl = environment.baseUrl;

  private apiUrl = this.baseUrl+'topic';

  constructor(private http: HttpClient, private httpHeadersService:HttpHeadersService) {}

  // Liste des topics (GET)
  getTopics(): Observable<Topic[]> {
    return this.http.get<Topic[]>(this.apiUrl, { headers: this.httpHeadersService.getHeaders() });

  }
    // Liste des topics (GET)
    getSubscribedTopics(): Observable<Topic[]> {
      return this.http.get<Topic[]>(this.baseUrl+'subscription', { headers: this.httpHeadersService.getHeaders() });
  
    }

  // Abonnement à un topic
  subscribeUserToTopic(idTopic: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}subscription/${idTopic}`, {}, { headers: this.httpHeadersService.getHeaders() });
  }

    // Désabonnement à un topic
    unsubscribeUserToTopic(idTopic: number): Observable<void> {
      return this.http.delete<void>(`${this.baseUrl}subscription/${idTopic}`, { headers: this.httpHeadersService.getHeaders() });
    }
  
}
