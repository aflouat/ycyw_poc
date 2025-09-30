import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileComponent } from './profile.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionService } from 'src/app/shared/services/session.service';
import { AuthService } from '../../services/auth.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';

class SessionServiceMock {
  sessionInformation: any = { username: 'john', email: 'john@example.com' };
  logOut = jasmine.createSpy('logOut');
}

class AuthServiceMock {
  update = jasmine.createSpy('update').and.returnValue({ subscribe: () => {} });
}

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProfileComponent],
      imports: [ReactiveFormsModule, RouterTestingModule, MatFormFieldModule, MatInputModule, NoopAnimationsModule],
      providers: [
        { provide: SessionService, useClass: SessionServiceMock },
        { provide: AuthService, useClass: AuthServiceMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('onSubmit should call authService.update with form value when valid', () => {
    const auth = TestBed.inject(AuthService) as unknown as AuthServiceMock;
    component.profileForm.setValue({ username: 'john', email: 'john@example.com', password: 'x' });
    component.onSubmit();
    expect(auth.update).toHaveBeenCalled();
  });
});


// Additional tests to increase coverage

describe('ProfileComponent additional', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  class SessionServiceMock2 extends SessionServiceMock {}

  class AuthServiceErrMock {
    private subj = new Subject<void>();
    update = jasmine.createSpy('update').and.callFake((_req: any) => this.subj.asObservable());
    emitError() {
      this.subj.error(new Error('fail'));
      this.subj = new Subject<void>();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProfileComponent],
      imports: [ReactiveFormsModule, RouterTestingModule, MatFormFieldModule, MatInputModule, NoopAnimationsModule],
      providers: [
        { provide: SessionService, useClass: SessionServiceMock2 },
        { provide: AuthService, useClass: AuthServiceErrMock },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should patch form with sessionInformation on init', () => {
    expect(component.profileForm.value.username).toBe('john');
    expect(component.profileForm.value.email).toBe('john@example.com');
  });

  it('onSubmit should set onError=true on update error', () => {
    component.profileForm.setValue({ username: 'john', email: 'john@example.com', password: 'x' });
    const auth = TestBed.inject(AuthService) as unknown as AuthServiceErrMock;
    component.onSubmit();
    auth.emitError();
    expect(component.onError).toBeTrue();
  });

  it('logout should call SessionService.logOut and navigate to /', () => {
    const router = TestBed.inject(Router);
    const navSpy = spyOn(router, 'navigate');
    const session = TestBed.inject(SessionService) as unknown as SessionServiceMock;
    component.logout();
    expect(session.logOut).toHaveBeenCalled();
    expect(navSpy).toHaveBeenCalledWith(['/']);
  });
});
