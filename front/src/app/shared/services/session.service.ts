import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { SessionInformation } from '../interfaces/session-information.interface';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  public isLogged = false
  public sessionInformation: SessionInformation | undefined;
  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLogged);

  constructor(private userService: UserService) {
    console.log('SessionService.constructor');
    const token = localStorage.getItem('token');
    console.log('token2:', token);

    if (token) {
      this.isLogged = true;
      this.userService.loadSessionInformation(token).subscribe({
        next: (sessionInformation) => {
          console.log('token3:', token);
          this.sessionInformation = sessionInformation;
          this.next();
        },
        error: (error) => {
          console.error('Failed to load session information:', error);
        }
      });
    }
  }

  public $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  public logIn(sessionInformation: SessionInformation): void {
    localStorage.setItem('token', sessionInformation.token);
    localStorage.setItem('intercomJwt', sessionInformation.intercomJwt);
    localStorage.setItem('intercomUserHash', sessionInformation.intercomUserHash);
    this.sessionInformation = sessionInformation;
    this.isLogged = true;
    this.next();
  }

  public logOut(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('intercomJwt');
    localStorage.removeItem('intercomUserHash');
    this.sessionInformation = undefined;
    this.isLogged = false;
    this.next();
  }

  private next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }
}
