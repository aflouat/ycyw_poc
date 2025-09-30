import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { SessionInformation } from 'src/app/shared/interfaces/session-information.interface';
import { Subject } from 'rxjs';
import { LoginRequest } from '../../interfaces/login-request';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/shared/services/session.service';
import { RouterTestingModule } from '@angular/router/testing';
import { MatIcon } from '@angular/material/icon';
import { MatFormField } from '@angular/material/form-field';



// ---------- Mocks ----------
class AuthServiceMock {
  // on utilise un Subject pour pouvoir piloter l’émission depuis le test
  private login$ = new Subject<SessionInformation>();

  login(req: LoginRequest) {
    // Par défaut, on renvoie un flux pilotable
    return this.login$.asObservable();
  }

  /** Utilitaire: fait réussir le login en émettant une session */
  emitSuccess(session: SessionInformation) {
    this.login$.next(session);
    this.login$.complete();
    // recrée un Subject pour les tests suivants éventuels
    this.login$ = new Subject<SessionInformation>();
  }
}

class SessionServiceMock {
  logIn = jasmine.createSpy('logIn');
}


describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authMock: AuthServiceMock;
  let sessionMock: SessionServiceMock;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent], // ✅ non-standalone
      imports: [
        ReactiveFormsModule,         // ✅ nécessaire pour FormBuilder/FormGroup
        RouterTestingModule, // ✅ fournit un Router de test
        MatIcon,
        MatFormField
      ],
      providers: [
        { provide: AuthService, useClass: AuthServiceMock },
        { provide: SessionService, useClass: SessionServiceMock },
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
