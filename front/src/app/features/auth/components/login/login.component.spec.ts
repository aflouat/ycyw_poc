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
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';



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
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        NoopAnimationsModule
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


// Additional tests to increase coverage

describe('LoginComponent additional', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: AuthService, useClass: AuthServiceMock },
        { provide: SessionService, useClass: SessionServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should call SessionService.logIn and navigate on success', () => {
    const sessionMock = TestBed.inject(SessionService) as unknown as SessionServiceMock;
    const auth = TestBed.inject(AuthService) as unknown as AuthServiceMock;
    const router = TestBed.inject(Router);
    const navSpy = spyOn(router, 'navigate');

    component.form.setValue({ identifier: 'john', password: '123456' });
    component.onSubmit();

    auth.emitSuccess({ username: 'john', email: 'john@example.com', token: 't' } as any);

    expect(sessionMock.logIn).toHaveBeenCalled();
    expect(navSpy).toHaveBeenCalledWith(['auth', 'profile']);
  });
});


// Extra tests for coverage

describe('LoginComponent navigation helpers', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: AuthService, useClass: AuthServiceMock },
        { provide: SessionService, useClass: SessionServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('goBack should navigate to root', () => {
    const router = TestBed.inject(Router);
    const spy = spyOn(router, 'navigate');
    component.goBack();
    expect(spy).toHaveBeenCalledWith(['/']);
  });
});
