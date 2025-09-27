import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginRequest } from '../../interfaces/login-request';
import { AuthService } from '../../services/auth.service';
import { SessionInformation } from '../../../../shared/interfaces/session-information.interface';
import { SessionService } from '../../../../shared/services/session.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  standalone: false
})
export class LoginComponent implements OnInit, OnDestroy {

  form!: FormGroup;
  private sessionInformationSubscription: Subscription | undefined;

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService,
    private sessionService: SessionService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      identifier: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      console.log('Form Submitted', this.form.value);
      const loginRequest = this.form.value as LoginRequest;
      this.sessionInformationSubscription = this.authService.login(loginRequest).subscribe({
        next: (response: SessionInformation) => {
          this.sessionService.logIn(response);
          this.router.navigate(['auth', 'profile']);
        },
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/']); // Retourne à la page précédente ou d'accueil
  }

  /**
 *  Unsubscribe when component is destroyed so that it can't produce memory leaks or side effects
 */
  ngOnDestroy(): void {
    if (this.sessionInformationSubscription) {
      this.sessionInformationSubscription.unsubscribe();
    }
  }
}
