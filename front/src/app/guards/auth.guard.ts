import {Injectable} from "@angular/core";
import {CanActivate, Router} from "@angular/router"; 
import { SessionService } from "../shared/services/session.service";

@Injectable({providedIn: 'root'})
export class AuthGuard implements CanActivate {
 
  constructor( 
    private router: Router,
    private sessionService: SessionService,
  ) {
  }

  public canActivate(): boolean {
    console.log("token:", JSON.stringify(this.sessionService.sessionInformation, null, 2));

      if (!this.sessionService.isLogged  ) {

      this.router.navigate(['/auth/login']).then(() => {
      });      
      return false;
    }

    return true;
  }
}