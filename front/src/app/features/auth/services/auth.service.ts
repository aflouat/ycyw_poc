import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginRequest } from '../interfaces/login-request';
import { RegisterRequest } from '../interfaces/register-request.interface';
import { SessionInformation } from '../../../shared/interfaces/session-information.interface';
import { environment } from '../../../../environments/environment';
import { HttpHeadersService } from 'src/app/shared/services/http.headers.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = environment.baseUrl;

  private pathModule = this.baseUrl+'auth';

  constructor(private httpClient: HttpClient, private httpHeadersService:HttpHeadersService) { }

  public register(registerRequest: RegisterRequest): Observable<void> {
    return this.httpClient.post<void>(`${this.pathModule}/register`, registerRequest);
  }

  public login(loginRequest: LoginRequest): Observable<SessionInformation> {
    return this.httpClient.post<SessionInformation>(`${this.pathModule}/login`, loginRequest);
  }
  public update(registerRequest: RegisterRequest): Observable<void> {
    console.log(registerRequest);
    return this.httpClient.post<void>(`${this.pathModule}/update`, registerRequest, { headers: this.httpHeadersService.getHeaders() });
  }
}
