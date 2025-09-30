import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterComponent } from './register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../../services/auth.service';
import { Subject } from 'rxjs';
import { RegisterRequest } from '../../interfaces/register-request.interface';
import { SessionService } from 'src/app/shared/services/session.service';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
// ---- Mocks ----
class AuthServiceMock {
  private reg$ = new Subject<void>();
  register(_req: RegisterRequest) {
    // on renvoie un flux pilotable depuis le test
    return this.reg$.asObservable();
  }
  emitSuccess() {
    this.reg$.next();
    this.reg$.complete();
    this.reg$ = new Subject<void>();
  }
  emitError() {
    this.reg$.error(new Error('register failed'));
    this.reg$ = new Subject<void>();
  }
}

class SessionServiceMock {
  // Ajoute ici des méthodes si le composant les utilise plus tard
}
describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        NoopAnimationsModule
      ], providers: [
        { provide: AuthService, useClass: AuthServiceMock },
        { provide: SessionService, useClass: SessionServiceMock },
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.form).toBeDefined();

  });
  it('should submit and navigate on success', () => {
    // Remplir le formulaire (peu importe la validité vu que code a "|| true")
    component.form.setValue({
      email: 'john@example.com',
      username: 'john',
      password: 'AnyPass123!'
    });

    const router = TestBed.inject(Router);
    const navigateSpy = spyOn(router, 'navigate');
    const auth = TestBed.inject(AuthService) as unknown as AuthServiceMock;

    component.onSubmit();

    // Simule la réussite de l'appel HTTP
    auth.emitSuccess();

    expect(navigateSpy).toHaveBeenCalledWith(['/auth/login']);
  });
});


// Additional tests to increase coverage

describe('RegisterComponent additional', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
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

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should validate password strength - invalid cases', () => {
    const ctrl = component.form.controls['password'];
    ctrl.setValue('short');
    expect(ctrl.errors).toBeTruthy();
    ctrl.setValue('longbutnoSpecial1A'); // missing special char
    expect(ctrl.errors).toBeTruthy();
  });

  it('should validate password strength - valid case', () => {
    const ctrl = component.form.controls['password'];
    ctrl.setValue('Valid#123');
    expect(ctrl.errors).toBeNull();
  });

  it('goBack should navigate to root', () => {
    const router = TestBed.inject(Router);
    const spy = spyOn(router, 'navigate');
    component.goBack();
    expect(spy).toHaveBeenCalledWith(['/']);
  });

  it('should set onError=true on register error', () => {
    component.form.setValue({ email: 'a@b.c', username: 'u', password: 'Valid#123' });
    const auth = TestBed.inject(AuthService) as unknown as AuthServiceMock;
    component.onSubmit();
    auth.emitError();
    expect(component.onError).toBeTrue();
  });
});


// Extra validator tests for coverage

describe('RegisterComponent validators', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
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

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('email control should be required and validate email format', () => {
    const email = component.form.controls['email'];
    email.setValue('');
    expect(email.hasError('required')).toBeTrue();
    email.setValue('not-an-email');
    expect(email.hasError('email')).toBeTrue();
  });

  it('username control should be required', () => {
    const username = component.form.controls['username'];
    username.setValue('');
    expect(username.hasError('required')).toBeTrue();
  });
});
