import { Component, HostListener, OnInit } from '@angular/core';
import { SessionService } from '../../services/session.service';
import { Subscription } from 'rxjs';
import { IntercomService } from '../../services/intercom.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
  standalone: false
})
export class HeaderComponent implements OnInit {
  showRightMenu: boolean = false;
  private subscription: Subscription = new Subscription();
  isMobile: boolean = false;

  constructor(private sessionService: SessionService, private intercom: IntercomService) { }


  ngOnInit(): void {
    this.subscription = this.sessionService.$isLogged().subscribe({
      next: (isLogged) => (this.showRightMenu = isLogged),
    });

    this.checkScreenSize();
  }
  ngAfterViewInit(): void {
    console.log('chat intercom boot..');

    this.intercom.boot(localStorage.getItem('intercomJwt'));
  }

  // Écoute les changements de taille d'écran
  @HostListener('window:resize', [])
  onResize() {
    this.checkScreenSize();
  }

  // Détermine si l'écran est de type mobile
  private checkScreenSize() {
    this.isMobile = window.innerWidth <= 768; // Par exemple, 768px est la largeur max pour mobile
  }
}