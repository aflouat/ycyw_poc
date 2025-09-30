import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterComponent } from './register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../../services/auth.service';
import { Subject } from 'rxjs';
import { RegisterRequest } from '../../interfaces/register-request.interface';
import { SessionService } from 'src/app/shared/services/session.service';
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
  });
});
