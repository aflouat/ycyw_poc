// intercom.service.ts
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import Intercom from '@intercom/messenger-js-sdk';
import { UserService } from './user.service';
import { SessionService } from './session.service';

@Injectable({ providedIn: 'root' })
export class IntercomService {
    constructor(@Inject(PLATFORM_ID) private platformId: Object, private sessionService: SessionService) { }
    boot(token?: string | null) {
        if (!isPlatformBrowser(this.platformId)) return; // SSR-safe

        Intercom({
            app_id: 'w73nmkfb',
            intercom_user_jwt: token ?? undefined,
            name: this.sessionService.sessionInformation?.lastName ?? undefined, // Full name
            email: this.sessionService.sessionInformation?.email ?? undefined, // Email address
            user_id: this.sessionService.sessionInformation?.id.toString() ?? undefined, // User ID
            created_at: undefined, // Signup date as a Unix timestamp
            session_duration: 86400000,
        });
    }
}
