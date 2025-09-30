import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      imports: [RouterTestingModule]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});


// Additional tests to increase coverage

describe('AppComponent header visibility', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      imports: [RouterTestingModule]
    }).compileComponents();
  });

  it('should hide header on "/" and "/home"', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance as any;
    app.updateHeaderVisibility('/');
    expect(app.showHeader).toBeFalse();
    app.updateHeaderVisibility('/home');
    expect(app.showHeader).toBeFalse();
  });

  it('should show header on other routes', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance as any;
    app.updateHeaderVisibility('/auth/login');
    expect(app.showHeader).toBeTrue();
    app.updateHeaderVisibility('/something');
    expect(app.showHeader).toBeTrue();
  });

  it('ngOnInit should set showHeader based on initial router.url - home hidden', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance as AppComponent;
    const router = TestBed.inject(Router);
    spyOnProperty(router, 'url', 'get').and.returnValue('/home');
    app.ngOnInit();
    expect(app.showHeader).toBeFalse();
  });

  it('ngOnInit should set showHeader based on initial router.url - other shown', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance as AppComponent;
    const router = TestBed.inject(Router);
    spyOnProperty(router, 'url', 'get').and.returnValue('/auth/login');
    app.ngOnInit();
    expect(app.showHeader).toBeTrue();
  });
});
