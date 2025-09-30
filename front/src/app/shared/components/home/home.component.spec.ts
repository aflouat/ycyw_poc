import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeComponent } from './home.component';
import { RouterTestingModule } from '@angular/router/testing';
import { IntercomService } from '../../services/intercom.service';
import { Router } from '@angular/router';

class IntercomServiceMock {
  boot = jasmine.createSpy('boot');
}

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HomeComponent],
      imports: [RouterTestingModule],
      providers: [{ provide: IntercomService, useClass: IntercomServiceMock }]
    })
      .compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});


// Additional tests to increase coverage

describe('HomeComponent additional', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let intercom: IntercomServiceMock;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HomeComponent],
      imports: [RouterTestingModule],
      providers: [{ provide: IntercomService, useClass: IntercomServiceMock }]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    intercom = TestBed.inject(IntercomService) as unknown as IntercomServiceMock;
    fixture.detectChanges();
  });

  it('login() should navigate to /auth/login', () => {
    const router = TestBed.inject(Router);
    const spy = spyOn(router, 'navigate');
    component.login();
    expect(spy).toHaveBeenCalledWith(['/auth/login']);
  });

  it('register() should navigate to /auth/register', () => {
    const router = TestBed.inject(Router);
    const spy = spyOn(router, 'navigate');
    component.register();
    expect(spy).toHaveBeenCalledWith(['/auth/register']);
  });

  it('ngAfterViewInit should boot intercom with token from localStorage', () => {
    localStorage.setItem('token', 'abc');
    component.ngAfterViewInit();
    expect(intercom.boot).toHaveBeenCalledWith('abc');
  });
});
