import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SessionInformation } from '../interfaces/session-information.interface';
import { HttpHeadersService } from './http.headers.service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
      private baseUrl = environment.baseUrl;

  private pathService = this.baseUrl+'auth';
  

  constructor(private httpClient: HttpClient, private httpHeadersService:HttpHeadersService) { }

  public loadSessionInformation(token: string): Observable<SessionInformation> {
    return this.httpClient.get<SessionInformation>(`${this.pathService}/me`, {
      headers: this.httpHeadersService.getHeaders(),
    });
}
}
