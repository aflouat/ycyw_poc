import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';
import { SessionService } from '../../services/session.service';
import { IntercomService } from '../../services/intercom.service';
import { BehaviorSubject } from 'rxjs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
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
      imports: [MatToolbarModule, RouterTestingModule, MatMenuModule, MatButtonModule, MatIconModule],
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


// Additional tests to increase coverage

describe('HeaderComponent additional', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let session: SessionServiceMock;
  let intercom: IntercomServiceMock;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HeaderComponent],
      imports: [MatToolbarModule, RouterTestingModule, MatMenuModule, MatButtonModule, MatIconModule],
      providers: [
        { provide: SessionService, useClass: SessionServiceMock },
        { provide: IntercomService, useClass: IntercomServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    session = TestBed.inject(SessionService) as unknown as SessionServiceMock;
    intercom = TestBed.inject(IntercomService) as unknown as IntercomServiceMock;
    fixture.detectChanges();
  });

  it('should toggle showRightMenu based on session $isLogged()', () => {
    expect(component.showRightMenu).toBeFalse();
    session.emit(true);
    fixture.detectChanges();
    expect(component.showRightMenu).toBeTrue();
  });

  it('should set isMobile based on window size on init and on resize', () => {
    (window as any).innerWidth = 500;
    // re-create component to apply initial check
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component.isMobile).toBeTrue();

    (window as any).innerWidth = 1024;
    window.dispatchEvent(new Event('resize'));
    expect(component.isMobile).toBeFalse();
  });

  it('ngAfterViewInit should boot intercom with intercomJwt', () => {
    localStorage.setItem('intercomJwt', 'jwt-token');
    component.ngAfterViewInit();
    expect(intercom.boot).toHaveBeenCalledWith('jwt-token');
  });
});
