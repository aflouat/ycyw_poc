import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { IntercomService } from '../../services/intercom.service';



@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  standalone: false
})
export class HomeComponent {

  constructor(private router: Router, private intercom: IntercomService) {

  }

  login() {
    console.log('login.....');
    this.router.navigate(['/auth/login']);

  }
  register() {
    console.log('register.....');
    this.router.navigate(['/auth/register']);

  }

  ngAfterViewInit(): void {
    console.log('chat intercom boot..');

    this.intercom.boot(localStorage.getItem('token'));
  }

}
