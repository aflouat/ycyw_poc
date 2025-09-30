import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';
import { SessionService } from '../../services/session.service';
import { IntercomService } from '../../services/intercom.service';
import { BehaviorSubject } from 'rxjs';
class SessionServiceMock {
  private logged$ = new BehaviorSubject<boolean>(false);
  $isLogged() {
    return this.logged$.asObservable();
  }
  /** Permet de changer l'état dans les tests */
  emit(isLogged: boolean) {
    this.logged$.next(isLogged);
  }
}

class IntercomServiceMock {
  boot = jasmine.createSpy('boot');
}
describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HeaderComponent],
      providers: [
        { provide: SessionService, useClass: SessionServiceMock },
        { provide: IntercomService, useClass: IntercomServiceMock },]
    })
      .compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
