import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { SessionService } from '../../../../shared/services/session.service';

import { RegisterRequest } from '../../interfaces/register-request.interface';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  standalone: false
})

export class ProfileComponent implements OnInit {
  public profileForm: FormGroup;

  errorMessage: string = '';
  public onError = false;


  constructor(
    private fb: FormBuilder,
    private sessionService: SessionService,
    private router: Router,
    private authService: AuthService
  ) {

    this.profileForm = this.fb.group({
      username: [''],
      email: [''],
      password: [''],
    });
  }

  ngOnInit(): void {
    console.log('ProfileComponent initialized');
    console.log('sessionInformation:', this.sessionService);
    if (this.sessionService.sessionInformation) {
      this.profileForm.patchValue({
        username: this.sessionService.sessionInformation.username,
        email: this.sessionService.sessionInformation.email

      });
    }
  }

  onSubmit(): void {
    console.log('Profil mis à jour :', this.profileForm.value);
    // Logique pour enregistrer les modifications
    if (this.profileForm.valid) {
      const registerRequest = this.profileForm.value as RegisterRequest;
      this.authService.update(registerRequest).subscribe({
        error: () => this.onError = true,
      });
    }
  }

  logout(): void {
    this.sessionService.logOut();
    this.router.navigate(['/']);
  }




}
